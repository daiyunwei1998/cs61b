package deque;
import org.junit.Test;
import java.util.Comparator;
public class testMaxArrayDeque {

    public class ReverseOrderComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            // Compare in reverse order
            return b.compareTo(a);
        }
    }

    @Test
    public  void case1(){

        MaxArrayDeque<Integer> L = new MaxArrayDeque<Integer>(Comparator.naturalOrder());
        L.addFirst(1);
        L.addFirst(2);
        L.addFirst(3);
        L.addFirst(4);
        L.addFirst(5);
        L.addFirst(6);
        L.printDeque();
        int ans = L.max();
        System.out.print(ans);
    }
}
