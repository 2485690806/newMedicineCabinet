package leesche.smartrecycling.base.entity;

public class UploadSignal {
    public String signalType;
    public int signalLattice;
    public int signalIntensity;

    public String getSignalType() {
        return signalType;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public int getSignalLattice() {
        return signalLattice;
    }

    public void setSignalLattice(int signalLattice) {
        this.signalLattice = signalLattice;
    }

    public int getSignalIntensity() {
        return signalIntensity;
    }

    public void setSignalIntensity(int signalIntensity) {
        this.signalIntensity = signalIntensity;
    }
}
