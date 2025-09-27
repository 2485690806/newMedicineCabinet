package leesche.smartrecycling.base.entity;

public class GridRegion {
    public int x, y, width, height;
    public String gridNumber;

    public GridRegion(int x, int y, int width, int height, String gridNumber) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gridNumber = gridNumber;
    }
}