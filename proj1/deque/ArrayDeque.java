package deque;

import java.util.Arrays;

public class ArrayDeque<T> {
    private T[] items;
    private int headIndex;
    private int tailIndex;
    private int size;
    private int headInitial;
    private int tailInitial;
    private double usageFactor = 0.25;

    /** Creates an empty list. */
    public ArrayDeque() {
        items = (T []) new Object[8];
        size = 0;
        headIndex = 4;  //point to the current head, actually start with 3
        tailIndex = 3;  //point to the current tail, actually start with 4
    }

    public void addFirst(T item) {
        if(size == items.length){
            resize(1/usageFactor);
        }
        headIndex -= 1;
        if(headIndex < 0 ) {
            // go to the very end
            headIndex = headIndex + items.length;
        }
        items[headIndex] = item;
        size += 1;
    }

    /** Inserts X into the back of the list. */
    public void addLast(T item) {
        if(size == items.length){
            resize(1/usageFactor);
        }
        tailIndex += 1;
        if(tailIndex == items.length ) {
            // go to the very front
            tailIndex = 0;
        }
        items[tailIndex] = item;
        size += 1;
    }


    public boolean isEmpty() {
        if(size == 0){
            return true;
        }else{
            return false;
        }
    }

    public void printDeque() {
        if(headIndex > tailIndex){
            for(int i = headIndex; i <items.length; i++){
                System.out.print(items[i]+" ");
            }
            for(int i = 0; i < tailIndex + 1; i++){
                if(i == tailIndex){
                    System.out.print(items[i]+"\n");
                }else{
                    System.out.print(items[i]+" ");
                }
            }
        }else{
            for(int i = headIndex; i < tailIndex + 1; i++){
                if(i == tailIndex){
                    System.out.print(items[i]+"\n");
                }else{
                    System.out.print(items[i]+" ");
                }
            }
        }
    }

    public void printInternal() {
        System.out.print('|');
        for(int i = 0; i < items.length; i++){
            System.out.print(i);
            System.out.print(' ');
        }
        System.out.println();

        System.out.print('|');
        for(int i = 0; i < items.length; i++){
            if(items[i]==null){
                System.out.print(' ');
            }else {
                System.out.print(items[i]);
            }
            System.out.print(' ');
        }
        System.out.println();
    }
    public void resize(double factor){
        T[] newItems = (T []) new Object[(int)Math.round(items.length * factor)];
        int midPoint = newItems.length/2;
        int halfSize = size/2;

        if(headIndex > tailIndex){
            System.arraycopy(items,headIndex,newItems,midPoint - halfSize + 1,items.length - headIndex);
            System.arraycopy(items,0,newItems,midPoint - halfSize + 1 + items.length - headIndex,tailIndex + 1);
        }else{
            System.arraycopy(items,headIndex,newItems,midPoint - halfSize + 1,size);
        }
        headIndex =midPoint - halfSize + 1;
        tailIndex =midPoint - halfSize + items.length;
        items = newItems;

    }


    /** Gets the ith item in the list (0 is the front). */
    public T get(int i) {
        if(i >= size){
            return(null);
        }
        if(headIndex > tailIndex){
            if(headIndex + i >= items.length){
                return(items[headIndex + i - items.length]);
            }
            return(items[headIndex + i]);
        }else{
            return(items[headIndex + i]);
        }
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }

    public T removeFirst() {
        if(size == 0){
            return null;
        }
        if(size < (items.length * usageFactor) && size >= 16){
            resize(0.5);
            //TODO resize();
        }
        T itemRemoved =  items[headIndex];
        items[headIndex] = null;
        headIndex += 1;
        size -= 1;
        if(headIndex > items.length - 1 ) {
            // go to the very front
            headIndex = 0;
        }
        if(size ==0){
            headIndex = 4;
            tailIndex = 3;
        }
        return itemRemoved;

    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public T removeLast() {
        if(size == 0){
            return null;
        }
        if(size < (items.length * usageFactor) && size >= 16){
            resize(0.5);
            //TODO test resize();
        }
        T itemRemoved = items[tailIndex];
        items[tailIndex] = null;
        tailIndex -= 1;
        if(tailIndex < 0 ) {
            // go to the very end
            tailIndex = items.length - 1;
        }
        if(size ==0){
            headIndex = 4;
            tailIndex = 3;
        }
        return itemRemoved;
    }
}
