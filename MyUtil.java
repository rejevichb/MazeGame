import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javalib.worldimages.Posn;
import tester.*;


class MyUtil {
    /**
     * Returns the posn moved by x and y
     * @param p
     * @param x
     * @param y
     * @return POSN
     */
    Posn movePosn(Posn p, int x, int y) {
        Posn result = new Posn(p.x + x, p.y +y);
        return result;
    }
    /**
     * Returns item as if it is a 2 matrix
     * @param Alist
     * @param x
     * @param y
     * @param widthSize
     * @return T
     */
    <T> T matrixGet(ArrayList<T> AList, int x, int y, int widthSize) {
        if (x >= widthSize) {
            throw new RuntimeException("Trying to access x that is bigger than row length");
        }
        if (y >= (AList.size() / widthSize)) {
            throw new RuntimeException("Trying to access y that is bigger than col length");
        }
        int result = x + (y * widthSize);
        return AList.get(result);
    }
    
    //Filter this arrayList and retunr a new arrayList
    <T> ArrayList<T> arrayFilter(ArrayList<T> aList, IPredicate<T> pred) {
        ArrayList<T> result = new ArrayList<T>();
        for(T t : aList) {
            if (pred.apply(t)) {
                result.add(t);
            }
        }
        return result;
    }
    /**
     * Assumes there are no duplicates in aList, 
     * and item is in Arraylist,  find this Nodes data that 
     * is accepted by this IPredicate
     * @param aList
     * @param pred
     * @return T -- data
     */
    <T> T arrayFind(ArrayList<T> aList, IPredicate<T> pred) {
        for (T t : aList) {
            if (pred.apply(t)) {
                return t;
            }
        }
        throw new RuntimeException("Target not found in arrayFind");
    }

    /**
     * Sorts the given list using a heapSort
     * EFFECT: sorts the given ArrayList
     * @param AList
     * @param com
     * @return void
     */
    <T> void heapSort(ArrayList<T> AList, IComparator<T> com) {

        this.heapify(AList, AList.size(), com);
        int end = AList.size() - 1;
        while (end > 0) {
            this.swap(AList, end, 0);
            end -= 1;
            this.siftDown(AList, 0, end, com);
        }
        return;
    }

    /**
     * EFFECT: Swaps items in given list 
     * Swaps the array lists
     * @param AList >>(given ArrayList to swap)
     * @param i index 
     * @param j index
     * @return void
     */
    <T> void swap(ArrayList<T> AList, int i, int j) {
        if (i >= AList.size() || j >= AList.size()) {
            throw new RuntimeException("Tried to Swap i: " +
                    i + " or j: " +
                    j + " while size: " + AList.size());
        }
        T temp = AList.get(i);
        AList.set(i, AList.get(j));
        AList.set(j, temp);

        return;
    }
    
    
    /**
     * EFFECT: Heaps the given AList 
     * heaps the Alist 
     * @param AList
     * @param count 
     * @param com
     * @return void
     */
    <T> void heapify(ArrayList<T> AList, int count, IComparator<T> com) {
        int start = ((count - 2) / 2);
        while (start >= 0) {
            this.siftDown(AList, start, count - 1, com);
            start -= 1;
        }
        return;
    }
    <T> void siftDown(ArrayList<T> AList, int start, int end, IComparator<T> com) {
        int root = start;

        while ((root * 2 + 1) <= end) {
            int child = (root * 2) + 1;
            int swap = root;

            if (com.compare(AList.get(swap), AList.get(child))  < 0) {
                swap = child;
            }
            if ((child + 1) <= end && 
                    com.compare(AList.get(swap), AList.get(child + 1)) < 0) {
                swap = child + 1;
            }
            if (swap == root) {
                return;
            }
            else {
                swap(AList, root, swap);
                root = swap;
            }
        }
    }

    <T> int find(HashMap<Integer, Integer> map, T x) {
        if (map.get(x.hashCode()) != x.hashCode()) {
            return find(map, map.get(x.hashCode()));
        }
        return map.get(x.hashCode());
    }
    <T> void union(HashMap<Integer, Integer> map, T x, T y) {
        int xRoot = this.find(map, x);
        int yRoot = this.find(map, y);
        if (xRoot == yRoot) {
            return;
        }
        
        // x and y are not already in same set. Merge them.
        map.put(map.get(xRoot), yRoot);
    }
    <T> void makeSet(HashMap<Integer, Integer> map, ArrayList<T> tList) {
        for(T t: tList) {
            map.put(t.hashCode(), t.hashCode());
        }
    }

}

interface IComparator<T> {
    
    int compare(T t1, T t2);
}
class CompareInts implements IComparator<Integer> {

    public int compare(Integer t1, Integer t2) {
        return t1 - t2;
    }
}

interface IPredicate<T> {
    //Returns true if predicate is true
    boolean apply(T t);
    
}

// Test the my utils 
class ExamplesUtil {
    ArrayList<Integer> unsort = new ArrayList<Integer>();
    ArrayList<Integer> expect = new ArrayList<Integer>();
    Random rand = new Random();
    MyUtil util = new MyUtil();
    
    
    
    void testHeapSort(Tester t) {
        for (int i = 0; i < 10; i += 1) {
            this.expect.add(0);
            this.unsort.add(rand.nextInt(10));
        }
        
        t.checkExpect(this.unsort, this.expect);
        this.util.heapSort(this.unsort, new CompareInts());
        t.checkExpect(this.unsort, this.expect);
    }

}