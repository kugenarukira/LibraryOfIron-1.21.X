package net.ironedge.libraryofiron.render.umr.mesh.deform;

import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;

import java.util.List;

public final class WeightedSurfaceMeshBinding {
    private final String simulationId;
    private final SurfaceTopology topology;
    private final String meshSurfaceName;
    private final List<WeightedSurfaceVertexBinding> vertexBindings;

    public WeightedSurfaceMeshBinding(
            String simulationId,
            SurfaceTopology topology,
            String meshSurfaceName,
            List<WeightedSurfaceVertexBinding> vertexBindings
    ) {
        this.simulationId = simulationId;
        this.topology = topology;
        this.meshSurfaceName = meshSurfaceName;
        this.vertexBindings = List.copyOf(vertexBindings);
    }

    public String simulationId() { return simulationId; }
    public SurfaceTopology topology() { return topology; }
    public String meshSurfaceName() { return meshSurfaceName; }
    public List<WeightedSurfaceVertexBinding> vertexBindings() { return vertexBindings; }
}