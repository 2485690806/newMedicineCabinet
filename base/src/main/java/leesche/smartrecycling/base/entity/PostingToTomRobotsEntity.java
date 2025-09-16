package leesche.smartrecycling.base.entity;

import java.util.List;

import leesche.smartrecycling.base.entity.Containers;

public class PostingToTomRobotsEntity {

    private List<Containers> containers;

    public List<Containers> getContainers() {
        return containers;
    }

    public void setContainers(List<Containers> containers) {
        this.containers = containers;
    }

    public List<String> getMonitorImg() {
        return monitorImg;
    }

    public void setMonitorImg(List<String> monitorImg) {
        this.monitorImg = monitorImg;
    }

    private List<String> monitorImg;

}
