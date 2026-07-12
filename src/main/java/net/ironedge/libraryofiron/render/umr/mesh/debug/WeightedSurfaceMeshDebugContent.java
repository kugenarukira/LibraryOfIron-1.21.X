package net.ironedge.libraryofiron.render.umr.mesh.debug;

import net.ironedge.libraryofiron.render.core.RenderEngine;
import net.ironedge.libraryofiron.render.umar.material.UMaterialDebugInstances;
import net.ironedge.libraryofiron.render.physics.debug.PhysicsCapeSurfaceTestContent;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAssetRegistry;
import net.ironedge.libraryofiron.render.umr.mesh.MeshInstance;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import net.ironedge.libraryofiron.render.umr.mesh.deform.DeformedMeshRenderNode;
import net.ironedge.libraryofiron.render.umr.mesh.deform.SurfaceBindingFactory;
import net.ironedge.libraryofiron.render.umr.mesh.deform.WeightedSurfaceMeshBinding;

public final class WeightedSurfaceMeshDebugContent {

    private static boolean installed = false;

    public static final String ASSET_ID = "debug_weighted_surface_mesh";
    public static final String SURFACE_NAME = "weighted_panel";

    private WeightedSurfaceMeshDebugContent() {}

    public static void install(RenderEngine engine) {
        if (installed) return;
        installed = true;

        // Make sure the sim exists first.
        PhysicsCapeSurfaceTestContent.install();

        // Higher-res mesh than the sim topology.
        // Example: sim might be 8x8, mesh is 16x24.
        MeshAsset asset = GridMeshAssetBuilder.buildGrid(
                ASSET_ID,
                SURFACE_NAME,
                16,
                24,
                1.2f,
                1.8f
        );

        MeshAssetRegistry.register(asset);

        MeshInstance instance = new MeshInstance("debug_weighted_surface_mesh_instance", asset);

        MeshSurface surface = asset.surfacesView().get(0);

        WeightedSurfaceMeshBinding binding = SurfaceBindingFactory.fromUVs(
                PhysicsCapeSurfaceTestContent.SIM_ID,
                PhysicsCapeSurfaceTestContent.TOPO,
                surface
        );

        engine.graph().addNode(new DeformedMeshRenderNode(
                instance,
                UMaterialDebugInstances.FLAT_WHITE_INSTANCE,
                binding
        ));

        SurfaceBindingFactory.fromUVs(
                PhysicsCapeSurfaceTestContent.SIM_ID,
                PhysicsCapeSurfaceTestContent.TOPO,
                surface,
                false,
                false,
                true
        );
    }
}