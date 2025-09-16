//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package leesche.smartrecycling.base.utils;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;

public class Size implements Parcelable {
    public int type;
    public int frame_type;
    public int index;
    public int width;
    public int height;
    public int frameIntervalType;
    public int frameIntervalIndex;
    public int[] intervals;
    public float[] fps;
    private String frameRates;
    public static final Parcelable.Creator<Size> CREATOR = new Parcelable.Creator<Size>() {
        public Size createFromParcel(final Parcel source) {
            return new Size(source);
        }

        public Size[] newArray(final int size) {
            return new Size[size];
        }
    };

    public Size(final int _type, final int _frame_type, final int _index, final int _width, final int _height) {
        this.type = _type;
        this.frame_type = _frame_type;
        this.index = _index;
        this.width = _width;
        this.height = _height;
        this.frameIntervalType = -1;
        this.frameIntervalIndex = 0;
        this.intervals = null;
        this.updateFrameRate();
    }

    public Size(final int _type, final int _frame_type, final int _index, final int _width, final int _height, final int _min_intervals, final int _max_intervals, final int _step) {
        this.type = _type;
        this.frame_type = _frame_type;
        this.index = _index;
        this.width = _width;
        this.height = _height;
        this.frameIntervalType = 0;
        this.frameIntervalIndex = 0;
        this.intervals = new int[3];
        this.intervals[0] = _min_intervals;
        this.intervals[1] = _max_intervals;
        this.intervals[2] = _step;
        this.updateFrameRate();
    }

    public Size(final int _type, final int _frame_type, final int _index, final int _width, final int _height, final int[] _intervals) {
        this.type = _type;
        this.frame_type = _frame_type;
        this.index = _index;
        this.width = _width;
        this.height = _height;
        int n = _intervals != null ? _intervals.length : -1;
        if (n > 0) {
            this.frameIntervalType = n;
            this.intervals = new int[n];
            System.arraycopy(_intervals, 0, this.intervals, 0, n);
        } else {
            this.frameIntervalType = -1;
            this.intervals = null;
        }

        this.frameIntervalIndex = 0;
        this.updateFrameRate();
    }

    public Size(final Size other) {
        this.type = other.type;
        this.frame_type = other.frame_type;
        this.index = other.index;
        this.width = other.width;
        this.height = other.height;
        this.frameIntervalType = other.frameIntervalType;
        this.frameIntervalIndex = other.frameIntervalIndex;
        int n = other.intervals != null ? other.intervals.length : -1;
        if (n > 0) {
            this.intervals = new int[n];
            System.arraycopy(other.intervals, 0, this.intervals, 0, n);
        } else {
            this.intervals = null;
        }

        this.updateFrameRate();
    }

    private Size(final Parcel source) {
        this.type = source.readInt();
        this.frame_type = source.readInt();
        this.index = source.readInt();
        this.width = source.readInt();
        this.height = source.readInt();
        this.frameIntervalType = source.readInt();
        this.frameIntervalIndex = source.readInt();
        if (this.frameIntervalType >= 0) {
            if (this.frameIntervalType > 0) {
                this.intervals = new int[this.frameIntervalType];
            } else {
                this.intervals = new int[3];
            }

            source.readIntArray(this.intervals);
        } else {
            this.intervals = null;
        }

        this.updateFrameRate();
    }

    public Size set(final Size other) {
        if (other != null) {
            this.type = other.type;
            this.frame_type = other.frame_type;
            this.index = other.index;
            this.width = other.width;
            this.height = other.height;
            this.frameIntervalType = other.frameIntervalType;
            this.frameIntervalIndex = other.frameIntervalIndex;
            int n = other.intervals != null ? other.intervals.length : -1;
            if (n > 0) {
                this.intervals = new int[n];
                System.arraycopy(other.intervals, 0, this.intervals, 0, n);
            } else {
                this.intervals = null;
            }

            this.updateFrameRate();
        }

        return this;
    }

    public float getCurrentFrameRate() throws IllegalStateException {
        int n = this.fps != null ? this.fps.length : 0;
        if (this.frameIntervalIndex >= 0 && this.frameIntervalIndex < n) {
            return this.fps[this.frameIntervalIndex];
        } else {
            throw new IllegalStateException("unknown frame rate or not ready");
        }
    }

    public void setCurrentFrameRate(final float frameRate) {
        int index = -1;
        int n = this.fps != null ? this.fps.length : 0;

        for(int i = 0; i < n; ++i) {
            if (this.fps[i] <= frameRate) {
                index = i;
                break;
            }
        }

        this.frameIntervalIndex = index;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.frame_type);
        dest.writeInt(this.index);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.frameIntervalType);
        dest.writeInt(this.frameIntervalIndex);
        if (this.intervals != null) {
            dest.writeIntArray(this.intervals);
        }

    }

    public void updateFrameRate() {
        int n = this.frameIntervalType;
        int min;
        int step;
        if (n > 0) {
            this.fps = new float[n];

            for(min = 0; min < n; ++min) {
                float var3 = this.fps[min] = 1.0E7F / (float)this.intervals[min];
            }
        } else if (n == 0) {
            try {
                min = Math.min(this.intervals[0], this.intervals[1]);
                int max = Math.max(this.intervals[0], this.intervals[1]);
                step = this.intervals[2];
                int i;
                float fps;
                if (step > 0) {
                    int m = 0;

                    for(i = min; i <= max; i += step) {
                        ++m;
                    }

                    this.fps = new float[m];
                    m = 0;

                    for(i = min; i <= max; i += step) {
                        fps = this.fps[m++] = 1.0E7F / (float)i;
                    }
                } else {
                    float max_fps = 1.0E7F / (float)min;
                    i = 0;

                    for(fps = 1.0E7F / (float)min; fps <= max_fps; ++fps) {
                        ++i;
                    }

                    this.fps = new float[i];
                    i = 0;

                    for(fps = 1.0E7F / (float)min; fps <= max_fps; this.fps[i++] = fps++) {
                    }
                }
            } catch (Exception var8) {
                this.fps = null;
            }
        }

        min = this.fps != null ? this.fps.length : 0;
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for(step = 0; step < min; ++step) {
            sb.append(String.format(Locale.US, "%4.1f", this.fps[step]));
            if (step < min - 1) {
                sb.append(",");
            }
        }

        sb.append("]");
        this.frameRates = sb.toString();
        if (this.frameIntervalIndex > min) {
            this.frameIntervalIndex = 0;
        }

    }

    public String toString() {
        float frame_rate = 0.0F;

        try {
            frame_rate = this.getCurrentFrameRate();
        } catch (Exception var3) {
        }

        return String.format(Locale.US, "Size(%dx%d@%4.1f,type:%d,frame:%d,index:%d,%s)", this.width, this.height, frame_rate, this.type, this.frame_type, this.index, this.frameRates);
    }
}
