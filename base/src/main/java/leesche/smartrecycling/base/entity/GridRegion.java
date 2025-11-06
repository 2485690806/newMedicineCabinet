package leesche.smartrecycling.base.entity;

public class GridRegion {
    public int x;
    public int y;
    public int width;
    public int height;
    public String gridNumber;

    public int getCameraNum() {
        return cameraNum;
    }

    public void setCameraNum(int cameraNum) {
        this.cameraNum = cameraNum;
    }

    public int cameraNum;

    public String getGridNumber() {
        return gridNumber;
    }

    public void setGridNumber(String gridNumber) {
        this.gridNumber = gridNumber;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public GridRegion(int x, int y, int width, int height, String gridNumber) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gridNumber = gridNumber;
    }
    public GridRegion() {
    }
}