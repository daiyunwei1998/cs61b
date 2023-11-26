package deque;

public class LinkedListDeque {

    private Node sentinel;
    public int size;
    public class Node{
        //helper class
        public Node prev;
        public int item;
        public Node next;

        public Node(Node previous, int i, Node n) {
            prev = previous;
            item = i;
            next = n;
        }

    }

    public LinkedListDeque() {
        //instantiate an empty list
        sentinel= new Node(null, 999,null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public boolean isEmpty(){
        if(size ==0){
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

    public void addFirst(int x){
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
    public void addLast(int x){
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

    public void removeFirst(){
        if(size > 0){
            sentinel.next = sentinel.next.next;
            size -= 1;
        }
    }

    public void removeLast(){
        if(size > 0){
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
            size -= 1;
        }
    }
    public Integer get(int index){
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
