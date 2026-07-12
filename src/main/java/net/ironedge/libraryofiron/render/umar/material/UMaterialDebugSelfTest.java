package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.state.UMaterialStateContext;
import org.joml.Vector3f;

public final class UMaterialDebugSelfTest {

    private UMaterialDebugSelfTest() {
    }

    public static void run() {
        //System.out.println("[UMaR] ===== Running material self-test =====");

        UMaterialStateContext testContext = UMaterialStateContext.builder()
                .ageInTicks(120.0f)
                .partialTick(0.5f)
                .healthPercent(0.35f)
                .movementSpeed(0.28f)
                .limbSwing(2.0f)
                .limbSwingAmount(0.8f)
                .powerLevel(0.6f)
                .hurt(true)
                .sprinting(true)
                .airborne(false)
                .glowing(false)
                .velocity(new Vector3f(0.2f, 0.0f, 0.1f))
                .cameraDirection(new Vector3f(0.0f, 0.0f, 1.0f))
                .build();

        testResolveParams(testContext);
        testResolver(testContext);

       // System.out.println("[UMaR] ===== Material self-test complete =====");
    }

    private static void testResolveParams(UMaterialStateContext testContext) {
        UMaterialInstance emissiveInst = UMaterialDebugInstances.EMISSIVE_HOT;
        UMaterialLayer emissiveLayer = emissiveInst.definition().layers().get(1);

        UMaterialParamSet emissiveResolved = UMaterialRuntime.resolveParams(
                emissiveInst,
                emissiveLayer,
                testContext
        );

        float emissiveStrength = emissiveResolved.getFloat(UMaterialParams.EMISSIVE_STRENGTH, 1.0f);
        //System.out.println("[UMaR] Resolved emissive strength: " + emissiveStrength);

        UMaterialInstance visorInst = UMaterialDebugInstances.VISOR_DARK;
        UMaterialLayer visorBaseLayer = visorInst.definition().layers().get(0);
        UMaterialLayer visorNoiseLayer = visorInst.definition().layers().get(1);

        UMaterialParamSet visorBaseResolved = UMaterialRuntime.resolveParams(
                visorInst,
                visorBaseLayer,
                testContext
        );

        UMaterialParamSet visorNoiseResolved = UMaterialRuntime.resolveParams(
                visorInst,
                visorNoiseLayer,
                testContext
        );

        float alpha = visorBaseResolved.getFloat(UMaterialParams.ALPHA, 0.5f);
        float uvScrollV = visorNoiseResolved.getFloat(UMaterialParams.UV_SCROLL_V, 0.0f);
        float noiseStrength = visorNoiseResolved.getFloat(UMaterialParams.NOISE_STRENGTH, 1.0f);

        //System.out.println("[UMaR] Resolved visor alpha: " + alpha);
        //System.out.println("[UMaR] Resolved visor UV scroll V: " + uvScrollV);
        //System.out.println("[UMaR] Resolved visor noise strength: " + noiseStrength);
    }

    private static void testResolver(UMaterialStateContext context) {
        UMaterialResolver resolver = new UMaterialResolver();

        UMaterialPassPlan plan = resolver.resolve(
                UMaterialDebugInstances.EMISSIVE_HOT,
                context
        );

        for (UMaterialRenderData pass : plan.passes()) {
            //System.out.println("[UMaR] Pass: " + pass.layerName());
            //System.out.println("  RenderMode: " + pass.renderMode());
            //System.out.println("  BlendMode: " + pass.blendMode());
            //System.out.println("  Color: 0x" + Integer.toHexString(pass.color()));
            //System.out.println("  Alpha: " + pass.alpha());
            //System.out.println("  EmissiveStrength: " + pass.emissiveStrength());
            //System.out.println("  UV Scroll: " + pass.uvScrollU() + ", " + pass.uvScrollV());
        }
    }
}