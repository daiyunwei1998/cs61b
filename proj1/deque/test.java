package deque;
import org.junit.Test;
import static org.junit.Assert.*;

public class test {
    @Test
    public  void case1(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addFirst(1);
        L.addFirst(2);
        L.addFirst(3);
        L.addFirst(4);
        L.addFirst(5);
        L.addFirst(6);
        L.printInternal();
        L.printDeque();
    }

    @Test
    public  void case2(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addFirst(1);
        L.addFirst(2);
        L.addFirst(3);
        L.addFirst(4);
        L.printInternal();
        L.printDeque();
    }

    @Test
    public  void testAddLast(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(1);
        L.addLast(2);
        L.addLast(3);
        L.addLast(4);
        L.addLast(5);


        L.printInternal();
        L.printDeque();
    }

    @Test
    public void testConflict(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(1);
        L.addLast(2);
        L.addLast(3);
        L.addLast(4);
        L.addLast(5);
        L.addFirst(6);
        L.addFirst(7);
        L.addFirst(8);
        L.printInternal();
        L.printDeque();
    }

    public void randSample() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> groundTruth = new LinkedListDeque<>();

        for (int i = 1; i < 9; i++) {
            double randomValue = Math.random();
            if (randomValue < 0.5) {
                L.addFirst(i);
                groundTruth.addFirst(i);
            } else {
                L.addLast(i);
                groundTruth.addLast(i);
            }
        }
        L.printInternal();
        L.printDeque();
    }


@Test
    public void MultipleSample(){
        for(int i = 0; i < 10; i++) {
            System.out.print("Test "+i+"\n");
            randSample();
            System.out.println();
        }
}


    public void testGet(int index){
    ArrayDeque<Integer> L = new ArrayDeque<>();
    LinkedListDeque<Integer> groundTruth = new LinkedListDeque<>();

    for (int i = 1; i < 9; i++) {
        double randomValue = Math.random();
        if (randomValue < 0.5) {
            L.addFirst(i);
            groundTruth.addFirst(i);
        } else {
            L.addLast(i);
            groundTruth.addLast(i);
        }
    }
    L.printInternal();
    L.printDeque();
    System.out.print('\n');
    groundTruth.printDeque();
    System.out.print("groundTruth get"+groundTruth.get(index)+';'+"L get"+L.get(index)+"\n");
    assertEquals(groundTruth.get(index),L.get(index));

}

    public void testGetSparse(int index){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> groundTruth = new LinkedListDeque<>();

        int testSize = 0 + (int)(Math.random() * ((8 - 0) + 1));
        for (int i = 1; i < testSize + 1; i++) {
            double randomValue = Math.random();
            if (randomValue < 0.5) {
                L.addFirst(i);
                groundTruth.addFirst(i);
            } else {
                L.addLast(i);
                groundTruth.addLast(i);
            }
        }
        L.printInternal();
        L.printDeque();
        System.out.print('\n');
        groundTruth.printDeque();
        System.out.print("Trying to get "+index+",groundTruth get "+groundTruth.get(index)+';'+"L get "+L.get(index)+"\n");
        assertEquals(groundTruth.get(index),L.get(index));

    }

    @Test
    public void multipleTestGet(){
        for(int i =0; i < 10000; i++){
            int index = 0 + (int)(Math.random() * ((8 - 0) + 1));
            testGetSparse(index);
        }

    }

    @Test
    public void case5(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(1);
        L.addLast(2);
        L.addLast(3);
        L.addLast(4);
        L.addFirst(5);
        L.printInternal();
        L.printDeque();
        System.out.println();
        System.out.println(L.get(1));
    }

    @Test
    public void testResize(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.resize();

    }
@Test
    public void testResize1(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addFirst(1);
        L.addFirst(2);
        L.addFirst(3);
        L.addFirst(4);
        L.addFirst(5);
        L.printInternal();
        L.printDeque();
        L.resize();
        System.out.println();
        L.printInternal();
        L.printDeque();
    }
    @Test
    public void testResize2(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(1);
        L.addLast(2);
        L.addLast(3);
        L.addLast(4);
        L.addLast(5);
        L.printInternal();
        L.printDeque();
        L.resize();
        System.out.println();
        L.printInternal();
        L.printDeque();
    }

    @Test
    public void testResize3(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(1);
        L.addLast(2);
        L.addFirst(3);
        L.addLast(4);
        L.addFirst(5);
        L.addFirst(6);
        L.addFirst(7);
        L.printInternal();
        L.printDeque();
        L.resize();
        System.out.println();
        L.printInternal();
        L.printDeque();
    }

    @Test
    public void testResize4(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(1);
        L.addLast(2);
        L.addLast(3);
        L.addLast(4);
        L.addLast(5);
        L.addLast(6);
        L.addLast(7);
        L.addLast(8);
        L.addLast(9);
        L.addLast(10);
        L.addLast(11);
        L.printInternal();
        L.printDeque();
    }

    public void randLargeSample() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> groundTruth = new LinkedListDeque<>();

        for (int i = 1; i < 33; i++) {
            double randomValue = Math.random();
            if (randomValue < 0.5) {
                L.addFirst(i);
                groundTruth.addFirst(i);
            } else {
                L.addLast(i);
                groundTruth.addLast(i);
            }
        }
        L.printInternal();
        L.printDeque();
        System.out.println();
        groundTruth.printDeque();
    }


    @Test
    public void testResizeSample(){
        for(int i = 0; i < 10; i++) {
            System.out.print("Test "+i+"\n");
            randLargeSample();
            System.out.println();
        }
    }





}//test class ends


