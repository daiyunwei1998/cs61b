package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> c;
    public MaxArrayDeque(Comparator<T> c) {
        this.c = c;
    }
    public T max() {
        if (this.isEmpty()) {
            return null;
        } else {
            T max = this.get(0);
            for (T element : this) {
                if (c.compare(element, max) > 0) {
                    max = element;
                }
            }
            return max;
        }
    }
    public T max(Comparator<T> comparator) {
        if (this.isEmpty()) {
            return null;
        } else {
            T max = this.get(0);
            for (T element : this) {
                if (comparator.compare(element, max) > 0) {
                    max = element;
                }
            }
            return max;
        }
    }
}
