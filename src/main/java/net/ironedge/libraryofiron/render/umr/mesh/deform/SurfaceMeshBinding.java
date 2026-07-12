package net.ironedge.libraryofiron.render.umr.mesh.deform;

import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;

public final class SurfaceMeshBinding {

    private final String simulationId;
    private final SurfaceTopology topology;
    private final String meshSurfaceName;

    public SurfaceMeshBinding(
            String simulationId,
            SurfaceTopology topology,
            String meshSurfaceName
    ) {
        this.simulationId = simulationId;
        this.topology = topology;
        this.meshSurfaceName = meshSurfaceName;
    }

    public String simulationId() {
        return simulationId;
    }

    public SurfaceTopology topology() {
        return topology;
    }

    public String meshSurfaceName() {
        return meshSurfaceName;
    }
}