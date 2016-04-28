import tester.*;

interface IPred<T> {
    boolean apply(T t);
}


class Deque<T> {
    Sentinel<T> header;
    
    Deque() {
        this.header = new Sentinel<T>();
    }
    
    Deque(Sentinel<T> header) {
        this.header = header;
    }
    // is this dequeue empty?
    boolean isEmpty() {
        return 
                this.header.next.isSentinel() &&
                this.header.prev.isSentinel();
    }
    
    // count the number of Nodes in this Dequeue
    int size() {
        return header.countNodes(0);
    }
    // EFFECT: add the given T at the head of this Deque
    void addAtHead(T t) {
        header.addAtHead(t);
    }
    // EFFECT: add the given T at the tail of this Deque
    void addAtTail(T t) {
        header.addAtTail(t);
    }
    // remove the first node that is not a sentinel from this Deque
    T removeFromHead() {
        return header.removeFromHead();
    }
    // remove the tail node (the last Node) from this Deque
    T removeFromTail() {
        return header.removeFromTail();
    }
    // return the first node in this Deque that satisfies the given IPred
    ANode<T> find(IPred<T> pred) {
        return this.header.find(pred);
    }
    
    // EFFECT: remove the node with the given data from the list
    void removeNode(T t) {
        this.find(new IsTargetNode<T>(t)).removeThis();
    }
}

abstract class ANode<T> {
    ANode<T> next;
    ANode<T> prev;
    
    
    
 // is this ANode a Sentinel?
    boolean isSentinel() {
        return false;
    }
    
    // count all the non-Sentinel nodes connected to this ANode
    int countNodes(int acc) {
        if (this.next.isSentinel()) { return acc; }
        else { return this.next.countNodes(acc + 1); }
    }
    
    // change this ANode's next to the given ANode
    void changeNext(ANode<T> n) {
        this.next = n;
    }
    // change this ANode's prev to the given ANode
    void changePrev(ANode<T> n) {
        this.prev = n;
    }
    // remove this ANode from the list
    abstract T removeThis();
    
    //return the node that satisfies the given Ipred 
    ANode<T> findHelp(IPred<T> pred) {
        return this;
    }
}

class Sentinel<T> extends ANode<T> {
    
    Sentinel() {
        this.next = this;
        this.prev = this;
    }
    
    // is this Sentinel a Sentinel?
    boolean isSentinel() {
        return true;
    }
    // add the given T to the beginning of this list
    void addAtHead(T t) {
        newNode<T> tNode = new newNode<T>(t, this.next, this);
        tNode.next.changePrev(tNode);
        this.changeNext(tNode);
    }
    
    // add the given T to the end of this list
    void addAtTail(T t) {
        newNode<T> tNode = new newNode<T>(t, this, this.prev);
        tNode.prev.changeNext(tNode);
        this.changePrev(tNode);
    }
    // remove the first node of this list
    T removeFromHead() {
        if (this.next.isSentinel()) { 
            throw new RuntimeException("This list is empty");
            }
        else { 
            return this.next.removeThis(); 
            }
    }
    // remove the last node of this list
    T removeFromTail() {
        if (this.prev.isSentinel()) { 
            throw new RuntimeException("This list is empty");
        }
        else { 
            return this.prev.removeThis();
        }
    }
    // remove this Sentinel from the list
    T removeThis() {
        throw new RuntimeException("Can't remove Sentinel.");
    }
    // find the Node that satisfies the given IPred
    ANode<T> find(IPred<T> pred) {
        if (this.next.isSentinel()) { 
            return this; 
        }
        else {
            return this.next.findHelp(pred);
        }
    }
    //return the node that satisfies the given Ipred 
    ANode<T> findHelp(IPred<T> pred) {
        return this;
    }
}


class newNode<T> extends ANode<T> {
    T data;
    
    newNode(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
    
    newNode(T data, ANode<T> next, ANode<T> prev) {
        this(data);
        if (next == null || prev == null) {
            throw new IllegalArgumentException("The given node has no value");
        }
        else {
            this.next = next;
            next.prev = this;
            this.prev = prev;
            prev.next = this;
        }
    }
    
    ANode<T> findHelp(IPred<T> pred) {
        if (pred.apply(this.data)) { return this; }
        else { return this.next.findHelp(pred); }
    }
    
    T removeThis() {
        this.prev.changeNext(this.next);
        this.next.changePrev(this.prev);
        return this.data;
    }
}

class ShortString implements IPred<String> {
    // is the given string shorter than 5 characters?
    public boolean apply(String s) {
        return s.length() < 5;
    }
}

class BigInt implements IPred<Integer> {
    // is the given integer bigger than 50?
    public boolean apply(Integer n) {
        return n > 100;
    }
}

class IsTargetNode<T> implements IPred<T> {
    T targetData;
    
    IsTargetNode(T targetData) {
        this.targetData = targetData;
    }
    
    // is the given T the targetData?
    public boolean apply(T t) {
        return t.equals(targetData);
    }
}


class ExamplesDeque {
    
    Sentinel<String> sent1 = new Sentinel<String>();
    Deque<String> deque1 = new Deque<String>();
    
    Sentinel<String> sent2 = new Sentinel<String>();
    newNode<String> nodeA = new newNode<String>("abc", sent2, sent2);
    newNode<String> nodeB = new newNode<String>("bcd", sent2, nodeA);
    newNode<String> nodeC = new newNode<String>("cde", sent2, nodeB);
    newNode<String> nodeD = new newNode<String>("def", sent2, nodeC);
    Deque<String> deque2 = new Deque<String>(sent2);
    
    Sentinel<Integer> sent3 = new Sentinel<Integer>();
    newNode<Integer> node1 = new newNode<Integer>(53, sent3, sent3);
    newNode<Integer> node2 = new newNode<Integer>(8, sent3, node1);
    newNode<Integer> node3 = new newNode<Integer>(367, sent3, node2);
    newNode<Integer> node4 = new newNode<Integer>(-13, sent3, node3);
    newNode<Integer> node5 = new newNode<Integer>(0, sent3, node4);
    newNode<Integer> node6 = new newNode<Integer>(1868, sent3, node5);
    Deque<Integer> deque3 = new Deque<Integer>(sent3);
    
    void init() {
        deque1 = new Deque<String>();
        
        sent2 = new Sentinel<String>();
        nodeA = new newNode<String>("abc", sent2, sent2);
        nodeB = new newNode<String>("bcd", sent2, nodeA);
        nodeC = new newNode<String>("cde", sent2, nodeB);
        nodeD = new newNode<String>("def", sent2, nodeC);
        deque2 = new Deque<String>(sent2);
        
        sent3 = new Sentinel<Integer>();
        node1 = new newNode<Integer>(53, sent3, sent3);
        node2 = new newNode<Integer>(8, sent3, node1);
        node3 = new newNode<Integer>(367, sent3, node2);
        node4 = new newNode<Integer>(-13, sent3, node3);
        node5 = new newNode<Integer>(0, sent3, node4);
        node6 = new newNode<Integer>(1868, sent3, node5);
        deque3 = new Deque<Integer>(sent3);
    }
    
    void testSize(Tester t) {
        t.checkExpect(deque1.size(), 0);
        t.checkExpect(deque2.size(), 4);
        t.checkExpect(deque3.size(), 6);
    }
     
    void testAddAtHead(Tester t) {
        this.init();
        newNode<String> nodeX = new newNode<String>("abcdefg", sent1, sent1);
        Deque<String> deque1AddX = new Deque<String>(sent1);
        newNode<String> nodeY = new newNode<String>("xyz", nodeA, sent2);
        Deque<String> deque2AddY = new Deque<String>(sent2);
        deque1.addAtHead("abcdefg");
        t.checkExpect(deque1, deque1AddX);
        deque2.addAtHead("xyz");
        t.checkExpect(deque2, deque2AddY);
        this.init();
    }
    
    void testAddAtTail(Tester t) {
        this.init();
        newNode<String> nodeX = new newNode<String>("lmnopqrs", sent1, sent1);
        Deque<String> deque1TailX = new Deque<String>(sent1);
        newNode<String> nodeY = new newNode<String>("zyx", sent2, nodeD);
        Deque<String> deque2TailY = new Deque<String>(sent2);
        deque1.addAtTail("lmnopqrs");
        t.checkExpect(deque1, deque1TailX);
        deque2.addAtTail("zyx");
        t.checkExpect(deque2, deque2TailY);
        this.init();
    }
    
    void testRemoveFromHead(Tester t) {
        this.init();
        t.checkException(new RuntimeException("This list is empty"), deque1, "removeFromHead");
        Sentinel<String> sent2Rem = new Sentinel<String>();
        newNode<String> nodeBR = new newNode<String>("bcd", sent2Rem, sent2Rem);
        newNode<String> nodeCR = new newNode<String>("cde", sent2Rem, nodeBR);
        newNode<String> nodeDR = new newNode<String>("def", sent2Rem, nodeCR);
        Deque<String> deque2Rem = new Deque<String>(sent2Rem);
        deque2.removeFromHead();
        t.checkExpect(deque2, deque2Rem);
        this.init();
    }
    
    void testRemoveFromTail(Tester t) {
        this.init();
        t.checkException(new RuntimeException("This list is empty"), deque1, "removeFromTail");
        Sentinel<Integer> sent3Rem = new Sentinel<Integer>();
        newNode<Integer> node1R = new newNode<Integer>(53, sent3Rem, sent3Rem);
        newNode<Integer> node2R = new newNode<Integer>(8, sent3Rem, node1R);
        newNode<Integer> node3R = new newNode<Integer>(367, sent3Rem, node2R);
        newNode<Integer> node4R = new newNode<Integer>(-13, sent3Rem, node3R);
        newNode<Integer> node5R = new newNode<Integer>(0, sent3Rem, node4R);
        Deque<Integer> deque3Rem = new Deque<Integer>(sent3Rem);
        deque3.removeFromTail();
        t.checkExpect(deque3, deque3Rem);
        this.init();
    }
    
    void testFind(Tester t) {
        this.init();
        t.checkExpect(deque1.find(new ShortString()), new Sentinel<String>());
        t.checkExpect(deque2.find(new ShortString()), sent2.next);
        t.checkExpect(deque3.find(new BigInt()), sent3.next.next.next);
    }
    
    void testRemovenewNode(Tester t) {
        this.init();
        Deque<String> deque1Copy = deque1;
        
        t.checkExpect(deque1, deque1Copy);
        Sentinel<String> sent2RN = new Sentinel<String>();
        newNode<String> nodeARN = new newNode<String>("abc", sent2RN, sent2RN);
        newNode<String> nodeBRN = new newNode<String>("bcd", sent2RN, nodeARN);
        newNode<String> nodeDRN = new newNode<String>("def", sent2RN, nodeBRN);
        Deque<String> deque2RN = new Deque<String>(sent2RN);
        deque2.removeNode("cde");
        t.checkExpect(deque2, deque2RN);
    }
    
    
    
    
}
