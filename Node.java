import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;

//to represent a node
class Node {
    
    MyUtil util;
    int x;
    int y;
    int distance;
    boolean marked;
    boolean path;
    ArrayList<Edge> adjacentTo;
    HashMap<String, Boolean> noCollision; //if it is true for that String key than there is NO wall
    
    //constructor for node
    Node(int x, int y) {
        this.util = new MyUtil();
        this.x = x;
        this.y = y;
        this.distance = Integer.MAX_VALUE;
        this.marked = false;
        this.path = false;
        this.adjacentTo = new ArrayList<Edge>();
        this.noCollision = new HashMap<String, Boolean>();
        this.noCollision.put("top", false);
        this.noCollision.put("bot", false);
        this.noCollision.put("left", false);
        this.noCollision.put("right", false);

    }
    /*
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.location().hashCode();
    }
    
    String location() {
        return this.intToString(this.x)
                .concat(" : ")
                .concat(this.intToString(this.y));
    }
    
    String intToString(int num) {
        return new Integer(num).toString();
    }
    
    public boolean equals(Object other) {
        if (!(other instanceof Node)) {
            return false;
        }
        Node that = (Node)other;
        return 
                this.x == that.x &&
                this.y == that.y;
    }

    
    /**
     * getPosn
     * @return this Node as a Posn
     */
    Posn getPosn() {
        return new Posn(this.x, this.y);
    }

    
    /**
     * getPosnDraw
     * @return this Node as a Posn properly set to allow this Node to be drawn on the correct spot
     */
    Posn getPosnDraw() {
        return new Posn((this.x * MazeGame.CELL_SIZE) + MazeGame.CELL_SIZE / 2 , 
                this.y * MazeGame.CELL_SIZE + MazeGame.CELL_SIZE / 2);
    }
    
    /**
     * EFFECT: set this.marked to b
     * @param b boolean
     */
    void setMarked(boolean b) {
        this.marked = b;
    }
    
    
    /**
     * EFFECT: set this.path to b
     * @param b boolean
     */
    void setPath(boolean b) {
        this.path = b;
    }
    
    
    /**
     * EFFECT: set this.distance to w
     * @param w integer
     */
    void setDistance(int w) {
        this.distance = w;
    }
    
    
    

    /**
     * EFFECT updated this.noCollision with a list of Edges that start from this Node
     * @param edgeList Full list of edges
     */
    void updateConnect(ArrayList<Edge> edgeList) {
        this.adjacentTo = new ArrayList<Edge>();
        ArrayList<Edge> workList = this.util.arrayFilter(edgeList, new EdgeContainsNode(this));
        this.adjacentTo = workList;
        if (workList.size() > 4) {
            System.out.println("Tried Connecting more Node = " + workList.size() + " From node= " + this.location());
            //throw new RuntimeException("Tried Connecting more Node = " + workList.size() + " From node= " + this.location());
        }
        this.setCollision(workList);
    }

    /**
     * Based on the given workList of Edge that has this Node as the end or start node 
     * EFFECT: sets this collision
     * @param workList
     */
    void setCollision(ArrayList<Edge> workList) {
        for (Edge e: workList) {
            Node end;
            if(e.start == this) {
                end = e.end;
            }
            else {
                end = e.start;
            }
            //Is there top wall?
            if (this.x == end.x && this.y > end.y) {
                this.noCollision.put("top", true);
            }
            //Is there bot wall?
            if (this.x == end.x && this.y < end.y) {
                this.noCollision.put("bot", true);
            }
            //Is there left wall?
            if (this.x > end.x && this.y == end.y) {
                this.noCollision.put("left", true);
            }
            //Is there right wall?
            if (this.x < end.x && this.y == end.y) { 
                this.noCollision.put("right", true);
            }
        }
    }
    /**
     * Public method that draws this
     * @param WorldScene
     * @return WorldScene
     */
    WorldScene drawNode(WorldScene scene) {
        
        int cSize = MazeGame.CELL_SIZE;
        int xPosn = this.x * cSize;
        int yPosn = this.y * cSize;
 


        scene.placeImageXY(new RectangleImage(cSize, cSize,
                OutlineMode.SOLID, this.nodeColor()),
                xPosn + cSize / 2, yPosn + cSize / 2);
        this.drawWalls(scene);

        return scene;
    }
    
    /**
     * Private methods that draws this on the result with the walls 
     * @param scene
     * @return void
     */
    private void drawWalls(WorldScene scene) {
        int lineThickness = MazeGame.CELL_SIZE / 15;
        if (lineThickness == 0) {
            lineThickness = 1;
        }
        int lineLength = MazeGame.CELL_SIZE;
        Posn top = this.util.movePosn(this.getPosnDraw(), 0, - (MazeGame.CELL_SIZE / 2));
        Posn bot = this.util.movePosn(this.getPosnDraw(), 0, (MazeGame.CELL_SIZE / 2));
        Posn left = this.util.movePosn(this.getPosnDraw(), - (MazeGame.CELL_SIZE / 2), 0);
        Posn right = this.util.movePosn(this.getPosnDraw(), (MazeGame.CELL_SIZE / 2), 0);
       
        //Draw top wall
        if(!this.noCollision.get("top")) {
            scene.placeImageXY(new RectangleImage(lineLength, lineThickness, OutlineMode.SOLID, new Color(0, 0, 0)),
                    top.x, top.y);
        }
        //Draw bottom wall
        if(!this.noCollision.get("bot")) {
            scene.placeImageXY(new RectangleImage(lineLength, lineThickness, OutlineMode.SOLID, new Color(0, 0, 0)), 
                    bot.x, bot.y);
        }
        //Draw left wall
        if(!this.noCollision.get("left")) {
            scene.placeImageXY(new RectangleImage(lineThickness, lineLength, OutlineMode.SOLID, new Color(0, 0, 0)), 
                    left.x, left.y);
        }
        //Draw right wall
        if(!this.noCollision.get("right")) {
            scene.placeImageXY( new RectangleImage(lineThickness, lineLength, OutlineMode.SOLID, new Color(0, 0, 0)), 
                    right.x, right.y);
        }
    }
    
    
    /**
     * Gives the Color depending on the status of the Node
     * @return this node's Color
     */
    private Color nodeColor() {
        //Gonna need to tweak to produce proper image
        if(this.path) {
            return new Color(0, 100, 150);
        }
        if(this.marked) {
            return new Color(0, 191, 255);
        }
        else {
            return new Color(200, 200, 200);
        }
    }
    
}

