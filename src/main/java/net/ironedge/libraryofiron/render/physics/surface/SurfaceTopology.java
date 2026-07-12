package net.ironedge.libraryofiron.render.physics.surface;

public final class SurfaceTopology {

    private final int rows;
    private final int cols;

    public SurfaceTopology(int rows, int cols) {
        if (rows < 2) throw new IllegalArgumentException("rows must be >= 2");
        if (cols < 2) throw new IllegalArgumentException("cols must be >= 2");
        this.rows = rows;
        this.cols = cols;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public int pointCount() {
        return rows * cols;
    }

    public int index(int row, int col) {
        if (!isValid(row, col)) {
            throw new IndexOutOfBoundsException("row=" + row + ", col=" + col);
        }
        return row * cols + col;
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}