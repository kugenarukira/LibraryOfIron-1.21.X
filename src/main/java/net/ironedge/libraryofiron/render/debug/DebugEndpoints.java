package net.ironedge.libraryofiron.render.debug;

import net.ironedge.libraryofiron.render.bridge.Endpoint;
import net.ironedge.libraryofiron.render.bridge.FunctionEndpoint;
import net.ironedge.libraryofiron.render.bridge.SegmentVariant;
import net.ironedge.libraryofiron.render.bridge.TipDistanceMode;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public final class DebugEndpoints {
    private DebugEndpoints() {}

    public static Endpoint playerForwardWithTipModes(
            PoseKey torsoKey,
            int segmentCount,
            List<SegmentVariant> variants,
            TipDistanceMode contracted,
            TipDistanceMode extended
    ) {
        final float[] debugLen = new float[]{ 1.0f };

        final float[] chosenContractTarget = new float[]{ -1f };
        final float[] chosenExtendTarget   = new float[]{ -1f };
        final boolean[] wasExtended        = new boolean[]{ false };

        // Stable seed so it doesn’t re-randomize from class reload weirdness
        final Random rng = new Random(12345L);

        return new FunctionEndpoint(frame -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.player == null || mc.level == null) return null;

            PoseTransform torso = PoseGraph.get().frame().get(torsoKey);
            if (torso == null) return null;

            float minLen = variants.stream().map(SegmentVariant::length).min(Float::compare).orElse(0.2f);
            float maxLen = variants.stream().map(SegmentVariant::length).max(Float::compare).orElse(0.45f);

            float minDist = segmentCount * minLen;
            float maxDist = segmentCount * maxLen;

            boolean extendedState = mc.player.isShiftKeyDown();
            TipDistanceMode mode = extendedState ? extended : contracted;

            boolean justBecameExtended   = extendedState && !wasExtended[0];
            boolean justBecameContracted = !extendedState && wasExtended[0];
            wasExtended[0] = extendedState;

            float targetDist;

            switch (mode) {
                case STATIC -> {
                    targetDist = extendedState ? maxDist : minDist;
                }

                case RANDOM_ONCE -> {
                    if (extendedState) {
                        if (justBecameExtended || chosenExtendTarget[0] < 0f) {
                            chosenExtendTarget[0] = minDist + rng.nextFloat() * (maxDist - minDist);
                        }
                        targetDist = chosenExtendTarget[0];
                    } else {
                        if (justBecameContracted || chosenContractTarget[0] < 0f) {
                            chosenContractTarget[0] = minDist + rng.nextFloat() * (maxDist - minDist);
                        }
                        targetDist = chosenContractTarget[0];
                    }
                }

                case RANDOM_CONTINUOUS -> {
                    float time = frame.partialTicks();
                    long gt = mc.level.getGameTime();

                    float noise = (float) Math.sin((gt + time) * 0.3f);
                    float t = noise * 0.5f + 0.5f;

                    targetDist = minDist + t * (maxDist - minDist);
                }

                default -> targetDist = extendedState ? maxDist : minDist;
            }

            // Smooth
            debugLen[0] += (targetDist - debugLen[0]) * 0.12f;

            Vector3f forward = new Vector3f(0, 0, 1).rotate(torso.rotation()).normalize();
            Vector3f target = new Vector3f(torso.translation()).add(forward.mul(debugLen[0]));

            return new PoseTransform(
                    target,
                    new Quaternionf(),
                    new Vector3f(1, 1, 1)
            );
        });
    }
}