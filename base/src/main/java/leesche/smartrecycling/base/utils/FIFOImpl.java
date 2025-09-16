package leesche.smartrecycling.base.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FIFOImpl<T> extends LinkedList<T> {

    private int maxSize = Integer.MAX_VALUE;
    private final Object synObj = new Object();

    public FIFOImpl() {
        super();
    }

    public FIFOImpl(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    public T addLastSafe(T addLast) {
        synchronized (synObj) {
            T head = null;
            while (size() >= maxSize) {
                head = poll();
            }
            addLast(addLast);
            return head;
        }
    }

    public T pollSafe() {
        synchronized (synObj) {
            return poll();
        }
    }

    public List<T> setMaxSize(int maxSize) {
        List<T> list = null;
        if (maxSize < this.maxSize) {
            list = new ArrayList<T>();
            synchronized (synObj) {
                while (size() > maxSize) {
                    list.add(poll());
                }
            }
        }
        this.maxSize = maxSize;
        return list;
    }

    public int getMaxSize() {
        return this.maxSize;
    }
}