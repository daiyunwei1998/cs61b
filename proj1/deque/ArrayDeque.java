package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int headIndex;
    private int tailIndex;
    private int size;
    private int headInitial;
    private int tailInitial;

    /** Creates an empty list. */
    public ArrayDeque() {
        items = (T []) new Object[8];
        size = 0;
        headIndex = 3;
        headInitial = 3;
        tailIndex = 4;
        tailInitial = 4;
    }

    public void addFirst(T item) {
        if(size == items.length){
            System.out.println("We don't do that here");
            // TODO: resize if array is full

        }else{
            items[headIndex] = item;
            size += 1;
            headIndex -= 1;
            if(headIndex < 0 ) {
                headIndex = headIndex + items.length;
            }
        }
    }

    /** Inserts X into the back of the list. */
    public void addLast(T item) {
        if(size == items.length){
            System.out.println("We don't do that here");
            // TODO: resize if array is full

        }else{
            items[tailIndex] = item;
            size += 1;
            tailIndex += 1;
            if(tailIndex > items.length - 1 ) {
                tailIndex = tailIndex - items.length;
            }
        }
    }

    public boolean isEmpty() {
        if(size == 0){
            return true;
        }else{
            return false;
        }
    }

    public void printDeque() {
        if(headIndex > headInitial) {
            for(int i = headIndex + 1; i < items.length; i++){
                System.out.print(items[i]+" ");
            }
            for(int i = 0; i < headInitial + 1; i++) {
                System.out.print(items[i]+" ");
            }
            for(int i = tailInitial; i < headIndex + 1; i++ ) {
                System.out.print(items[i]+" ");
            }
        }else if(tailIndex < tailInitial){
            for(int i = headIndex + 1; i < headInitial + 1; i++) {
                System.out.print(items[i]+" ");
            }
            for(int i = tailInitial; i < items.length; i++){
                System.out.print(items[i]+" ");
            }
            for(int i = 0; i < tailIndex; i++){
                System.out.print(items[i]+" ");
            }
        }else{
            for (T item : items) {
                System.out.print(item + " ");
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
    public void resize(){

    }


    /** Gets the ith item in the list (0 is the front). */
    public T get(int i) {
        if(i >= size){
            return(null);
        }
        //what happens if the array not full? condition 1 and 2 might break
        if(headIndex >= headInitial) {
            int actualIndex = i + headIndex + 1;
            if(actualIndex >= items.length){
                actualIndex -= items.length;
            }
            return items[actualIndex];

        }else if(tailIndex <= tailInitial){
            int actualIndex = i + 1 + headIndex;
            if(actualIndex >= items.length){
                actualIndex -= items.length;
            }
            return items[actualIndex];
        }else{
            int actualIndex = i + 1 + headIndex;
            return items[actualIndex];
        }

    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }

    public T removeFirst() {
        return null;
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public int removeLast() {
        return 0;
    }
}
