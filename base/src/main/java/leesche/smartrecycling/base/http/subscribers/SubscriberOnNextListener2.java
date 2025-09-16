package leesche.smartrecycling.base.http.subscribers;

public interface SubscriberOnNextListener2<T> {
    void onNext(T t);

    void onError(String e);
}
