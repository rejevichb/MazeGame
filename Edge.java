import java.util.Random;
import java.awt.Color;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;

//representation of an edge, with a start and end node, and a weight
class Edge {
    Node start;
    Node end;
    int weight;
    
    //constructor for edge
    Edge (Node start, Node end) {
        this.start = start;
        this.end = end;
        this.weight = this.randomWeight();
    }
    
    //get a pseudorandom number
    int randomWeight() {
        Random rand = new Random();
        int result = rand.nextInt(MazeGame.MAZE_SIZE * 10 - 1 + 1) + 1;
        if (result < 0) {
            throw new RuntimeException("Random Gave neg weight");
        }
        return result;
    }
    
    //return location as string
    String location() {
        return start.location() + " to " + end.location();
    }
    
    public boolean equals(Object other) {
        if (!(other instanceof Edge)) {
            return false;
        }
        Edge that = (Edge)other;
        return 
                this.start == that.start &&
                this.end == that.end &&
                this.weight == that.weight;
    }
    /*Write drawEdge to draw the connected nodes IF they are marked or if they are the path
     * Need to check for stack overflow error when drawing nodes
     *
     */
    void drawEdge(WorldScene scene) {
        WorldImage result;
        result = new LineImage(this.start.getPosnDraw(), 
                                   new Color(0, 128, 128));
        if (start.marked || start.path) {
            start.drawNode(scene).placeImageXY(result, this.end.getPosnDraw().x, this.end.getPosnDraw().y);
        }
        if (end.marked || end.path) {
            end.drawNode(scene).placeImageXY(result, this.end.getPosnDraw().x, this.end.getPosnDraw().y);
        }
    }
}


/* \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
 * \/\/\/\/\/\/\/\/\/\/\/\/ FUNCTION OBJECTS /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\
 * \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
 */ 


//EdgeComparator
class CompareEdge implements IComparator<Edge> {
    public int compare(Edge t1, Edge t2) {
        return t1.weight - t2.weight;
    }
}



class EdgeContainsNode implements IPredicate<Edge> {
    Node n;
    EdgeContainsNode(Node n) {
        this.n = n;
    }
    public boolean apply(Edge t) {
        return this.n.equals(t.start) || this.n.equals(t.end);
    }
}



class EdgeContainsNodeFrom implements IPredicate<Edge> {
    Node n;
    EdgeContainsNodeFrom(Node n) {
        this.n = n;
    }
    public boolean apply(Edge t) {
        return this.n.equals(t.start);
    }
}






class nodePosnFind implements IPredicate<Node> {
    Posn p;
    nodePosnFind(Posn p) {
        this.p = p;
    }
    public boolean apply(Node n) {
        Posn result = n.getPosn();
        return 
                this.p.x == result.x &&
                this.p.y == result.y;
    }
}