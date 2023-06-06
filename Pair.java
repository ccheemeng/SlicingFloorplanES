/**
 * This utility class stores two items of the same type together in a pair.
 * It could be used, for instance, to faciliate returning of two values in a function.
 *
 * derived from
 * @author cs2030
 * @param <T> the type of the items
 **/
public class Pair<T> {
    private final T t1;
    private final T t2;

    public Pair(T t1, T t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T first() {
        return this.t1;
    }

    public T second() {
        return this.t2;
    }

    @Override
    public String toString() {
        return "(" + this.t1 + ", " + this.t2 + ")";
    }
}
