package leesche.smartrecycling.base.http.subscribers;


public interface SubscriberOnNextListener<T> {
    void onNext(T t);
}
