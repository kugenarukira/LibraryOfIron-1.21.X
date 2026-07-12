package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseMath;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.ironedge.libraryofiron.render.umr.UMRModelInstance;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class UMRBridgeInstance {

    private final UMRBridgeDef def;

    private final List<UMRModelInstance> segments = new ArrayList<>();
    private final List<String> lastVariantIds = new ArrayList<>();

    // Global selection plan (variant index per segment)
    private final int[] plan;

    // DP discretization
    private static final float UNIT = 0.001f;

    // Replan bucket (prevents smooth endpoint lerps from replanning every frame)
    private int lastDistKey = Integer.MIN_VALUE;
    private static final float DIST_BUCKET = 0.05f; // 5cm buckets

    // --- Organic animation state ---
    private final int[] organicPlan;
    private boolean organicInitialized;
    private final Random organicRng = new Random();

    public UMRBridgeInstance(UMRBridgeDef def) {
        this.def = def;

        if (def.variants() == null || def.variants().isEmpty()) {
            throw new IllegalArgumentException("BridgeDef variants must not be empty");
        }

        // Pre-create placeholder segment instances
        for (int s = 0; s < def.segmentCount(); s++) {
            SegmentVariant v = def.variants().get(0);
            segments.add(makeInstance(s, v));
            lastVariantIds.add(v.id());
        }

        this.plan = new int[def.segmentCount()];
        for (int s = 0; s < plan.length; s++) plan[s] = 0;

        this.organicPlan = new int[def.segmentCount()];
        System.arraycopy(plan, 0, organicPlan, 0, plan.length);
    }

    public String id() { return def.id(); }
    public int segmentCount() { return segments.size(); }

    public String segmentSourceId(int segIndex) {
        return "umr:bridge:" + def.id() + ":" + lastVariantIds.get(segIndex) + ":seg" + segIndex;
    }

    public SegmentVariant variantAt(int segIndex) {
        String vid = lastVariantIds.get(segIndex);
        for (SegmentVariant v : def.variants()) {
            if (v.id().equals(vid)) return v;
        }
        return def.variants().get(0);
    }

    public void solveAndPublish(FrameContext frame, PoseGraph graph) {
        PoseTransform start = def.start().resolve(frame);
        PoseTransform end = def.end().resolve(frame);
        if (start == null || end == null) return;

        float targetDist = new Vector3f(end.translation()).sub(start.translation()).length();

        // Bucket distance -> only replan when the bucket changes
        int distKey = Math.round(targetDist / DIST_BUCKET);
        if (distKey != lastDistKey) {
            computeGlobalPlan(targetDist);
            applyOrdering(plan, def.ordering(), targetDist);

            lastDistKey = distKey;

            if (def.ordering() == SegmentOrdering.ORGANIC) {
                int sig = planSignatureCounts(plan);
                if (!organicInitialized || sig != lastPlanSig) {
                    initOrganicPlan(plan, distKey);
                    lastPlanSig = sig;
                }
            } else {
                organicInitialized = false;
            }
        }

        // Organic motion runs every frame after init/replan
        if (def.ordering() == SegmentOrdering.ORGANIC && organicInitialized) {
            stepOrganicMotion(frame, def.organicMotion());
        }

        // Glue chain
        PoseTransform currentRoot = start;

        for (int s = 0; s < segments.size(); s++) {
            Vector3f refUp = new Vector3f(0, 1, 0).rotate(currentRoot.rotation()).normalize();
            PoseTransform aimedRoot = aimRootToward(currentRoot, end, refUp);

            int vi = (def.ordering() == SegmentOrdering.ORGANIC) ? organicPlan[s] : plan[s];
            if (vi < 0 || vi >= def.variants().size()) vi = 0;

            SegmentVariant chosen = def.variants().get(vi);

            if (!chosen.id().equals(lastVariantIds.get(s))) {
                segments.set(s, makeInstance(s, chosen));
                lastVariantIds.set(s, chosen.id());
            }

            segments.get(s).publishWithRoot(graph, aimedRoot);

            PoseTransform tipPose = getPublishedTipPose(graph, s, chosen);
            currentRoot = (tipPose != null)
                    ? tipPose
                    : PoseMath.compose(aimedRoot, chosen.segment().restTipFromRoot());
        }
    }

    // ---------------- DP global best-fit ----------------

    private void computeGlobalPlan(float targetDist) {
        int n = segments.size();
        int k = def.variants().size();

        int[] L = new int[k];
        int maxL = 0;

        for (int v = 0; v < k; v++) {
            int li = Math.max(1, Math.round(def.variants().get(v).length() / UNIT));
            L[v] = li;
            if (li > maxL) maxL = li;
        }

        int target = Math.max(0, Math.round(targetDist / UNIT));
        int maxSum = n * maxL;

        boolean[][] dp = new boolean[n + 1][maxSum + 1];
        int[][] parent = new int[n + 1][maxSum + 1];
        int[][] prevSum = new int[n + 1][maxSum + 1];

        for (int i = 0; i <= n; i++) {
            for (int s = 0; s <= maxSum; s++) {
                parent[i][s] = -1;
                prevSum[i][s] = -1;
            }
        }

        dp[0][0] = true;

        for (int i = 0; i < n; i++) {
            for (int s = 0; s <= maxSum; s++) {
                if (!dp[i][s]) continue;

                for (int v = 0; v < k; v++) {
                    int ns = s + L[v];
                    if (ns > maxSum) continue;
                    if (!dp[i + 1][ns]) {
                        dp[i + 1][ns] = true;
                        parent[i + 1][ns] = v;
                        prevSum[i + 1][ns] = s;
                    }
                }
            }
        }

        int bestSum = -1;
        int bestErr = Integer.MAX_VALUE;

        for (int s = 0; s <= maxSum; s++) {
            if (!dp[n][s]) continue;
            int err = Math.abs(s - target);
            if (err < bestErr) {
                bestErr = err;
                bestSum = s;
            }
        }

        if (bestSum < 0) {
            for (int i = 0; i < n; i++) plan[i] = 0;
            return;
        }

        int s = bestSum;
        for (int i = n; i >= 1; i--) {
            int v = parent[i][s];
            if (v < 0) v = 0;
            plan[i - 1] = v;
            s = prevSum[i][s];
            if (s < 0) s = 0;
        }
    }

    // ---------------- Ordering modes ----------------

    private void applyOrdering(int[] plan, SegmentOrdering mode, float targetDist) {
        if (mode == null) return;

        switch (mode) {
            case DEFAULT -> { /* no-op */ }
            case MECHANICAL -> mechanicalArrange(plan);
            case ORGANIC -> { /* organic uses init + wave */ }
        }
    }

    private void mechanicalArrange(int[] plan) {
        // short at ends, long near center, while preserving counts
        int n = plan.length;

        Integer[] tmp = new Integer[n];
        for (int i = 0; i < n; i++) tmp[i] = plan[i];

        java.util.Arrays.sort(tmp, java.util.Comparator.comparingDouble(vIdx -> def.variants().get(vIdx).length()));

        int[] out = new int[n];
        int L = 0, R = n - 1;
        boolean leftTurn = true;

        for (int k = 0; k < n; k++) {
            if (leftTurn) out[L++] = tmp[k];
            else out[R--] = tmp[k];
            leftTurn = !leftTurn;
        }

        System.arraycopy(out, 0, plan, 0, n);
    }

    // ---------------- Organic (animated, count-preserving) ----------------

    // fields
    private int lastPlanSig = Integer.MIN_VALUE;

    private int planSignatureCounts(int[] plan) {
        int k = def.variants().size();
        int[] counts = new int[k];
        for (int v : plan) {
            if (v >= 0 && v < k) counts[v]++;
        }
        return java.util.Arrays.hashCode(counts);
    }

    private void initOrganicPlan(int[] basePlan, int distKey) {
        System.arraycopy(basePlan, 0, organicPlan, 0, basePlan.length);

        // Stable seed: changes only when distKey changes
        long seed = ((long) def.id().hashCode() << 32) ^ (long) distKey;
        organicRng.setSeed(seed);

        // Shuffle once at init
        for (int i = organicPlan.length - 1; i > 0; i--) {
            int j = organicRng.nextInt(i + 1);
            int t = organicPlan[i]; organicPlan[i] = organicPlan[j]; organicPlan[j] = t;
        }

        organicInitialized = true;
    }

    private void stepOrganicMotion(FrameContext frame, OrganicMotion mode) {
        if (mode == null) mode = OrganicMotion.PING_PONG;

        var level = frame.attachment("level", net.minecraft.world.level.Level.class);
        float t = (level != null ? level.getGameTime() : 0L) + frame.partialTicks();

        float speed = 2.5f; // tweak
        int wave = (int) (t * speed);

        int span = organicPlan.length - 1;
        if (span <= 0) return;

        switch (mode) {
            case BUBBLE -> {
                int head = mod(wave, span);
                swapWaveAt(head, +1);
            }
            case PING_PONG -> {
                int period = span * 2;
                int p = mod(wave, period);
                int head = (p <= span) ? p : (period - p);
                int dir = (p <= span) ? +1 : -1;
                swapWaveAt(head, dir);
            }
            case INTERFERENCE -> {
                int headA = mod(wave, span);
                int headB = (span - 1) - headA;
                swapWaveAt(headA, +1);
                swapWaveAt(headB, -1);
            }
        }
    }

    private boolean swapWaveAt(int head, int dir) {
        int n = organicPlan.length;
        if (n < 2) return false;

        int limit = n - 1;

        // Find a differing adjacent pair by scanning around the ring
        for (int step = 0; step < limit; step++) {
            int i = head + dir * step;
            i = mod(i, limit); // wraps into [0..n-2]

            int a = organicPlan[i];
            int b = organicPlan[i + 1];

            if (a != b) {
                organicPlan[i] = b;
                organicPlan[i + 1] = a;
                //System.out.println("[Organic] swap at " + i);
                return true;
            }
        }

        return false;
    }

    private static int mod(int x, int m) {
        int r = x % m;
        return (r < 0) ? (r + m) : r;
    }

    // ---------------- Glue / pose helpers ----------------

    private PoseTransform getPublishedTipPose(PoseGraph graph, int segIndex, SegmentVariant chosen) {
        String src = segmentSourceId(segIndex);
        String tipId = chosen.segment().tipNodeId();
        return graph.frame().get(new PoseKey(src, tipId));
    }

    private UMRModelInstance makeInstance(int segIndex, SegmentVariant v) {
        return new UMRModelInstance(
                "bridge:" + def.id() + ":" + v.id() + ":seg" + segIndex,
                v.segment().model(),
                "player",
                null,
                net.ironedge.libraryofiron.render.pose.PlayerAnchorMap.INSTANCE,
                PoseTransform.identity()
        );
    }

    private static PoseTransform aimRootToward(PoseTransform root, PoseTransform end, Vector3f refUp) {
        Vector3f from = root.translation();
        Vector3f to = end.translation();

        Vector3f dir = new Vector3f(to).sub(from);
        if (dir.lengthSquared() < 1.0e-6f) return root;
        dir.normalize();

        Quaternionf qForward = new Quaternionf().rotationTo(new Vector3f(0, 0, 1), dir);
        Vector3f upAfter = new Vector3f(0, 1, 0).rotate(qForward);

        Vector3f refUpProj = new Vector3f(refUp).sub(new Vector3f(dir).mul(refUp.dot(dir)));
        Vector3f upProj = new Vector3f(upAfter).sub(new Vector3f(dir).mul(upAfter.dot(dir)));

        if (upProj.lengthSquared() > 1.0e-8f && refUpProj.lengthSquared() > 1.0e-8f) {
            upProj.normalize();
            refUpProj.normalize();

            float cos = clamp(upProj.dot(refUpProj), -1f, 1f);
            float angle = (float) Math.acos(cos);

            Vector3f cross = new Vector3f(upProj).cross(refUpProj);
            float sign = Math.signum(cross.dot(dir));
            angle *= sign;

            Quaternionf qTwist = new Quaternionf().fromAxisAngleRad(dir.x, dir.y, dir.z, angle);
            Quaternionf q = new Quaternionf(qTwist).mul(qForward);

            return new PoseTransform(new Vector3f(root.translation()), q, new Vector3f(1, 1, 1));
        }

        return new PoseTransform(new Vector3f(root.translation()), qForward, new Vector3f(1, 1, 1));
    }

    private static float clamp(float v, float lo, float hi) {
        return (v < lo) ? lo : Math.min(v, hi);
    }
}