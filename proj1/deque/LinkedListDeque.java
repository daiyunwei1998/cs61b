package deque;

public class LinkedListDeque<T> {

    private Node sentinel;
    public int size;
    public class Node{
        //helper class
        public Node prev;
        public T item;
        public Node next;

        public Node(Node previous, T i, Node n) {
            prev = previous;
            item = i;
            next = n;
        }

    }

    public LinkedListDeque() {
        //instantiate an empty list
        sentinel= new Node(null, null,null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public boolean isEmpty(){
        if(size == 0){
            return true;
        }else{
            return false;
        }
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        //Prints the items in the deque from first to last, separated by a space.
        // Once all the items have been printed, print out a new line.
        if(!isEmpty()){
            Node p = sentinel.next;
            for(int i = 0; i < size; i++){
                System.out.print(p.item);
                System.out.print(" ");
                p = p.next;
            }
            System.out.print("\n");
        }else{
            System.out.println("Empty List");
        }
    }

    public void addFirst(T x){
        Node newNode = new Node(sentinel,x,sentinel.next);
        if(size == 0){
            //if start from fresh
            sentinel.next = newNode;
            sentinel.prev = newNode;
        }else{
            sentinel.next.prev = newNode;
            sentinel.next = newNode;
        }
        size += 1;
    }
    public void addLast(T x){
        Node newNode = new Node(sentinel.prev,x,sentinel);
        if(size == 0){
            //if start from fresh
            sentinel.next = newNode;
            sentinel.prev = newNode;
        }else{
            sentinel.prev.next = newNode;
            sentinel.prev = newNode;

        }
        size += 1;
    }

    public T removeFirst(){
        if(size > 0){
            T item = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            size -= 1;
            return item;
        }
        return null;
    }

    public T removeLast(){
        if(size > 0){
            T item = sentinel.prev.prev.next.item;
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
            size -= 1;
            return item;
        }
        return null;
    }
    public T get(int index){
        if(index < 0 ||index > size-1){
            return null; //should return null
        }else{
            Node p = sentinel.next;
            for(int i = 0; i < index; i++) {
                p = p.next;
            }
            return p.item;
        }
    }


}
