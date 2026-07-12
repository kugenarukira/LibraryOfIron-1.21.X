package net.ironedge.libraryofiron.render.physics.strip;

public final class StripTopology {

    private final int rows;
    private final int cols;

    public StripTopology(int rows, int cols) {
        if (rows < 2) throw new IllegalArgumentException("StripTopology rows must be >= 2");
        if (cols < 2) throw new IllegalArgumentException("StripTopology cols must be >= 2");
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
        if (row < 0 || row >= rows) throw new IndexOutOfBoundsException("row=" + row);
        if (col < 0 || col >= cols) throw new IndexOutOfBoundsException("col=" + col);
        return row * cols + col;
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}