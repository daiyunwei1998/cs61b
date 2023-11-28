package deque;
import org.junit.Test;

import java.util.Arrays;

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
        System.out.print("Ground Truth: ");
        groundTruth.printDeque();
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

        int testSize = 0 + (int)(Math.random() * ((32 - 0) + 1));
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
        for(int i =0; i < 100000; i++){
            int index = 0 + (int)(Math.random() * ((32 - 0) + 1));
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
    public void testResizeOnlyAddFirst(){
        //if condition 1
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> groundTruth = new LinkedListDeque<>();
        for(int i = 1; i < 18; i++){
            L.addFirst(i);
            groundTruth.addFirst(i);

        }
        L.printInternal();
        L.printDeque();
        System.out.print("Ground Truth: ");
        groundTruth.printDeque();
    }

    @Test
    public void testResizeOnlyAddLast(){
        //if condition 1
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> groundTruth = new LinkedListDeque<>();
        for(int i = 1; i < 18; i++){
            L.addLast(i);
            groundTruth.addLast(i);

        }
        L.printInternal();
        L.printDeque();
        System.out.print("Ground Truth: ");
        groundTruth.printDeque();
    }


    public void randLargeSample() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> groundTruth = new LinkedListDeque<>();

        int totalSize = 32;
        int[] record = new int[totalSize];
        for (int i = 1; i < totalSize + 1; i++) {
            double randomValue = Math.random();
            if (randomValue < 0.5) {
                L.addFirst(i);
                groundTruth.addFirst(i);
                record[i-1] = 1;
            } else {
                L.addLast(i);
                groundTruth.addLast(i);
                record[i-1] = 0;
            }
        }
        L.printInternal();
        L.printDeque();
        System.out.print("GroundTruth: ");
        groundTruth.printDeque();
        System.out.println("case code: "+Arrays.toString(record));
    }


    @Test
    public void testResizeSample(){
        for(int i = 0; i < 10; i++) {
            System.out.print("Test "+i+"\n");
            randLargeSample();
            System.out.println();
        }
    }

    public int[] string2array(String code){
        String[] stringArray = code.split(", ");

        // Convert the array of strings to an array of integers
        int[] intArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i]);
        }

        // Print the resulting array of integers
        //for (int num : intArray) {
        //    System.out.print(num + " ");
        //}
        return intArray;
    }



    @Test
    public void debug(){
        String code = "0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1";

        ArrayDeque<Integer> L = new ArrayDeque<>();
        int[] intArray = string2array(code);

        int i = 0;
        for (int num : intArray) {
            if(num==1){
                L.addFirst(i);
                i++;
            }else{
                L.addLast(i);
                i++;
            }
        }
        L.printInternal();
        L.printDeque();
        //problem, headInitial is changed after resize!!
    }


@Test
    public void testRemove() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> groundTruth = new LinkedListDeque<>();

        int totalSize = 0 + (int)(Math.random() * ((32 - 0) + 1));
        int[] record = new int[totalSize];
        for (int i = 1; i < totalSize + 1; i++) {
            double randomValue = Math.random();
            if (randomValue < 0.5) {
                L.addFirst(i);
                groundTruth.addFirst(i);
                record[i-1] = 1;
            } else {
                L.addLast(i);
                groundTruth.addLast(i);
                record[i-1] = 0;
            }
        }

        for(int i = 0; i < totalSize; i++){
            double randomValue = Math.random();
            if (randomValue < 0.5) {
                assertEquals(groundTruth.removeFirst(),L.removeFirst());
            } else {
                assertEquals(groundTruth.removeLast(),L.removeLast());
            }
        }
    }


    @Test
    public void testRemoveSample(){
        for(int i = 0; i < 10; i++) {
            System.out.print("Test "+i+"\n");
            randLargeSample();
            System.out.println();
        }
    }





}//test class ends


