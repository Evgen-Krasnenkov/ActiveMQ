package bootcamp;

public interface Publisher<T> {
    T send(T t);
}
