import java.util.*;


//Used to represent a collection of multiple items
interface ICollection<T> {
    
    boolean isEmpty(); // Is this collection empty?
    // Returns the first item of the collection
    // EFFECT: removes that first item from the collection
    T remove();
    
    void add(T item); // EFFECT: add the given item to the collection
    
    int size();
}



/*  ///////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ 
 * Abstraction for stack and queue
 * Stack and queue are our two different ways as compiling the collections
 *  Both implement ICollection and the above methods
 */

class Stack<T> implements ICollection<T> {
    Deque<T> contents;
    Stack() {
      this.contents = new Deque<T>();
    }
    public int size() {
        return this.contents.size();
    }
    
    // Is this collection empty?
    public boolean isEmpty() {
      return this.contents.isEmpty();
    }
    // Returns the first item of the collection
    // EFFECT: removes that first item from the collection
    public T remove() {
      return this.contents.removeFromHead();
    }
    // EFFECT: add the given item to the collection
    public void add(T item) {
      this.contents.addAtHead(item);
    }
  }

class Queue<T> implements ICollection<T> {
    Deque<T> contents;
    Queue() {
      this.contents = new Deque<T>();
    }
    
    public int size() {
        return this.contents.size();
    }
    
    // Is this collection empty?
    public boolean isEmpty() {
      return this.contents.isEmpty();
    }
    
     // Returns the first item of the collection
    // EFFECT: removes that first item from the collection
    public T remove() {
      return this.contents.removeFromHead();
    }
    // EFFECT: add the given item to the collection
    public void add(T item) {
      this.contents.addAtTail(item); // NOTE: Different from Stack!
    }
  } 

