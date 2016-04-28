import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;

//The game Handler
class MazeGame extends World {
    
    
    static final int MAZE_WIDTH = 35;
    static final int MAZE_HEIGHT = 35;
    static final int MAZE_SIZE = MAZE_HEIGHT * MAZE_WIDTH;
    static final int CELL_SIZE = 20;
    static final int CANVAS_WIDTH = MAZE_WIDTH * CELL_SIZE + 350;
    static final int DISPLAY_PANEL_X_CUTOFF = CANVAS_WIDTH - 350;
    static final int CANVAS_HEIGHT = MAZE_HEIGHT * CELL_SIZE;

    
    //Class data for MazeGame
    ArrayList<Node> nodeList;
    ArrayList<Edge> edgeList;
    ICollection<Node> pathWorkList;
    MyUtil util;
    Posn player1;   
    Node startNode; //the starting block
    Node endNode;   // the finish block
    boolean endGame; // is the game over?
    boolean dfs;    
    boolean bfs;
    boolean building;
    boolean endScreen = false; //should the end screen be shown?  //TODO class data
    HashMap<Integer, Integer> repS;
    ArrayList<Edge> workListBuildMaze;
    int countPlayerMoves = 0;

    // Constructor for MazeGame
    MazeGame() {
        util = new MyUtil();
        this.resetMaze();
        System.out.println(this.endNode.location());
        
    }
    
    /*  --------------------------------------------------------------------------
     *  Maze Construction
     */

    
    
    
    
    /**
     * EFFECT: build this.nodeList for the board
     */
    void buildNodeList() {
        this.nodeList = new ArrayList<Node>();
        for (int y = 0; y < MAZE_HEIGHT; y += 1) {
            for (int x = 0; x < MAZE_WIDTH; x += 1) {
                nodeList.add(new Node(x, y));
            }
        }
    }
    
    
    
    
    /**
     * EFFECT: Builds all possible Edges for the board
     */
    void buildEdgeList() {
        this.edgeList = new ArrayList<Edge>();
        for (Node target: this.nodeList) {
            Posn p = target.getPosn();
            
            //Creating edge from target to the node above
            if (p.y > 0) {
                this.edgeList.add(new Edge(target,
                        this.util.matrixGet(this.nodeList, p.x, p.y - 1, MAZE_WIDTH)));
            }
            //Creating edge from target to the node below
            if (p.y < MAZE_HEIGHT - 1) {
                this.edgeList.add(new Edge(target,
                        this.util.matrixGet(this.nodeList, p.x, p.y + 1, MAZE_WIDTH)));
            }
            //Creating edge from target to the node left
            if (p.x > 0) {
                this.edgeList.add(new Edge(target,
                        this.util.matrixGet(this.nodeList, p.x - 1, p.y, MAZE_WIDTH)));
            }
            //Creating edge from target to the node below
            if (p.x < MAZE_WIDTH - 1) {
                this.edgeList.add(new Edge(target,
                        this.util.matrixGet(this.nodeList, p.x + 1, p.y, MAZE_WIDTH)));
            }
        }
    }
    
    
    
    /**
     * Builds the Maze based on Kruskal's Algorithm
     */
    void buildMaze() {
        HashMap<Integer, Integer> repQ = new HashMap<Integer, Integer>();
        ArrayList<Edge> edgesInTreeQ = new ArrayList<Edge>();
        ArrayList<Edge> workList = this.edgeList;

        //initialize every node's representative to itself
        this.util.heapSort(workList, new CompareEdge());
        this.util.makeSet(repQ, this.nodeList);

        while (!workList.isEmpty()) {
            this.buildMazeHelp(repQ, edgesInTreeQ, workList);
        }
        
        this.edgeList =  edgesInTreeQ;
        this.updateNodeWithEdges();
    }  
    
    
    
    /**
     * Helper for the while Loop in BuildMaze
     * EFFECT: changes rep, edgesInTree and workList
     * @param rep
     * @param edgesInTree
     * @param workList
     */
    void buildMazeHelp(HashMap<Integer, Integer> rep, ArrayList<Edge> edgesInTree, ArrayList<Edge> workList) {
        Edge target = workList.get(0);
        workList.remove(0);
        Node x = target.start;
        Node y = target.end;
        if (this.util.find(rep, x) == this.util.find(rep, y)) {
            //do nothing
            return;
        }
        else {
            edgesInTree.add(target);
            this.util.union(rep, x, y);
        }
        return;
    }
    

    
    /**
     * Update Nodes to Draw
     * Allows Nodes to know how to draw their walls
     * EFFECT: Sets the noCollision hashmap
     */
    void updateNodeWithEdges() {
        for(Node n: this.nodeList) {
            n.updateConnect(this.edgeList);
        }
    }
   
    /* ---------------------------------------------------------------------------
     * Reset Methods
     */

    void resetGame() {
        this.player1 = new Posn(0,0);
        this.endGame = false;
        this.dfs = false;
        this.bfs = false;
        this.startNode = this.nodeList.get(0);
        this.endNode = this.nodeList.get(this.nodeList.size() - 1);
        this.resetNodePath();   
        this.startNode.setDistance(0);
        this.countPlayerMoves = 0;
        
    }
    
    void resetMaze() {
        this.buildNodeList();
        this.buildEdgeList();
        this.building = false;
        this.repS = new HashMap<Integer, Integer>();
        this.workListBuildMaze = this.edgeList;
        this.edgeList = new ArrayList<Edge>();
        this.util.heapSort(this.workListBuildMaze, new CompareEdge());
        this.util.makeSet(this.repS, this.nodeList);
        this.resetGame();
        this.countPlayerMoves = 0;
    }
    
    void resetQMaze() {
        this.buildNodeList();
        this.buildEdgeList();
        this.building = false;
        this.repS = new HashMap<Integer, Integer>();
        this.workListBuildMaze = this.edgeList;
        this.resetGame();
        this.countPlayerMoves = 0;
    }
    
    void resetNodePath() {
        for(Node n : this.nodeList) {
            n.setMarked(false);
            n.setPath(false);
            n.setDistance(Integer.MAX_VALUE);
        }
    }
    
    /*  --------------------------------------------------------------------------
     *  Depth First Search and Breadth First Search
     */

    /**
     * Finds the Path Node to 
     * using BFS or DFS depending on pathWorklist 
     * @param to Node target node passing by reference
     */
    
    
    void findPathHelp(Node to) {
        Node next = this.pathWorkList.remove();

        if (next == to) {
            next.setPath(true);
            this.reconstruct(next);
            this.dfs = false;
            this.bfs = false;
            this.endGame = true;
            return;
        }
        else {
            next.setMarked(true);
            for (Edge e : next.adjacentTo) {
                if(e.end == to) {
                    this.pathWorkList.add(e.end);
                    break;
                }
                if(e.start == to) {
                    this.pathWorkList.add(e.start);
                    break;
                }
                if(!e.end.marked) {
                    this.pathWorkList.add(e.end);
                    e.end.setDistance(next.distance + 1);
                }
                if(!e.start.marked) {
                    this.pathWorkList.add(e.start);
                    e.start.setDistance(next.distance + 1);
                }
            }
        }
    }
    
    
    
    /*
     * EFFECT: Resets maze on key event "S"
     * @param Node: next
     */
    void reconstruct(Node next) {
        next.setPath(true);
        if (next == this.startNode) {
            return;
        }
        else {
            int dist = next.distance;
            Node n = null;
            for (Edge ed : next.adjacentTo) {
                if(ed.start.distance < dist) {
                    n = ed.start;
                    break;
                }
                if(ed.end.distance < dist) {
                    n = ed.end;
                    break;
                }
            }
            if(n == null) {
                throw new NullPointerException("e is null");
            }
            this.reconstruct(n);
        }
    }
    
    /*  --------------------------------------------------------------------------
     *  Player methods
     */

    private Posn playerMove(String k, Posn dir) {
        
        Node currentNode = this.util.matrixGet(this.nodeList, dir.x,  dir.y, MAZE_WIDTH);
        currentNode.setMarked(true);
        Node nextNode;
        
        
        if (k.equals("up") && currentNode.noCollision.get("top")) {
            dir = this.util.movePosn(dir, 0, -1);
            countPlayerMoves++;
        }
        
        if (k.equals("down") && currentNode.noCollision.get("bot")) {
            dir = this.util.movePosn(dir, 0, 1);
            countPlayerMoves++;
        }
        
        if (k.equals("left") && currentNode.noCollision.get("left")) {
            dir = this.util.movePosn(dir, -1, 0);
            countPlayerMoves++;
        }
        
        if (k.equals("right") && currentNode.noCollision.get("right")) {
            dir = this.util.movePosn(dir, 1, 0);
            countPlayerMoves++;
        }
        

        nextNode = this.util.matrixGet(this.nodeList, dir.x,  dir.y, MAZE_WIDTH);
        
        if (!nextNode.marked) {
            nextNode.setMarked(true);
            nextNode.setDistance(currentNode.distance + 1);
        }
        
        if (nextNode == endNode) {
            nextNode.setPath(true);
            this.reconstruct(endNode);
            this.endGame = true;
            
        }
        
        return dir;
    }

    void printCell(WorldScene scene, Posn p, Color color) {
        
        scene.placeImageXY(new RectangleImage(
                MazeGame.CELL_SIZE - 2, 
                MazeGame.CELL_SIZE - 2,
                OutlineMode.SOLID,
                color), (p.x * MazeGame.CELL_SIZE) + MazeGame.CELL_SIZE / 2, 
                        (p.y * MazeGame.CELL_SIZE) + MazeGame.CELL_SIZE / 2);
        
    }
    
    /*  --------------------------------------------------------------------------
     *  Big Bang methods
     */

    //on-tick mehtod
    public void onTick() {
        if ((this.bfs || this.dfs) && this.building) {
            this.findPathHelp(this.endNode);
        }
        else if (this.workListBuildMaze.isEmpty()) {
            this.building = true;
            this.repS = new HashMap<Integer, Integer>();
        }
        else if (!this.building && this.edgeList.size() <= this.nodeList.size()){
            this.buildMazeHelp(this.repS, this.edgeList, this.workListBuildMaze);
            this.updateNodeWithEdges();
        }
    }

    //world ends 
    public WorldEnd worldEnds() {

        //TODO
        if (endScreen) {
            return new WorldEnd(true, this.makeFinalScene());
        } 
        
        else {
            return new WorldEnd(false, this.makeScene());
        }
    }
    
    //TODO
    public WorldScene makeFinalScene() {
        
        

        
        WorldScene scene = new WorldScene(CANVAS_WIDTH, CANVAS_HEIGHT);
        scene.placeImageXY(new TextImage("The game is over", new Color(0, 0, 0)), CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2);
        
        return scene;
    }



    /**
     * public method that handles user input
     */
    public void onKeyEvent(String k) {
        if (k.equals("s")) {
            this.resetMaze();
        }
        if (k.equals("f") && !this.building) {
            while(!this.workListBuildMaze.isEmpty()) {
                this.buildMazeHelp(this.repS, this.edgeList, this.workListBuildMaze);
            }
            this.updateNodeWithEdges();
            this.building = true;
            this.repS = new HashMap<Integer, Integer>();
        }
        if (k.equals("f") && this.building && (this.bfs || this.dfs)) {
            while (!this.pathWorkList.isEmpty() && (this.bfs || this.dfs)) {
                this.findPathHelp(this.endNode);
            }
        }
        if (k.equals("q")) {
            this.resetQMaze();
            this.buildMaze();
            this.building = true;
        }
        if (this.building) {

            //TODO
            if (k.equalsIgnoreCase(" ")) {
                endScreen = true;
            }
            
            if (k.equals("r")) {
                this.resetGame();
                this.endGame = false;
            }
            if (k.equals("b")) {
                this.resetGame();
                this.pathWorkList = new Queue<Node>();
                this.pathWorkList.add(this.nodeList.get(0));
                this.bfs = true;
                this.dfs = false;
            }
            if (k.equals("v") ) {
                this.resetGame();
                this.pathWorkList = new Stack<Node>();
                this.pathWorkList.add(this.nodeList.get(0));
                this.dfs = true;
                this.bfs = false;
            }
            this.player1 = this.playerMove(k, this.player1);
        }
    }
    
    
    
    /**
     * public method to instantiate and draw on a scene
     */
    public WorldScene makeScene() {
        
        WorldScene scene = new WorldScene(CANVAS_WIDTH, CANVAS_HEIGHT);
         scene.placeImageXY(new RectangleImage(
                CANVAS_WIDTH, 
                CANVAS_HEIGHT,
                OutlineMode.SOLID,
                new Color(255, 255, 255)), 
                 CANVAS_WIDTH / 2, 
                 CANVAS_HEIGHT / 2);
         
         //TODO
         scene.placeImageXY(new TextImage("THE MAZE GAMEª", 17, FontStyle.BOLD_ITALIC,
                 new Color(10, 50, 120)), 
                 (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 50);
         

        TextImage ready = new TextImage("The game is ready to play when the"
                + " yellow start block appears.", 10, FontStyle.BOLD, new Color(160, 0, 160));
        TextImage aK = new TextImage("Press the arrow keys to control "
                + "the player", 10, FontStyle.ITALIC, new Color(190, 0, 170));
        TextImage iS = new TextImage("Press S to generate a new maze", 10, 
                FontStyle.ITALIC, new Color(190, 0, 170));
        TextImage iF = new TextImage("Press F to skip the"
                + " drawing animation during drawing and begin game", 
                10, FontStyle.ITALIC, new Color(255, 10, 10));
        TextImage iQ = new TextImage("Press Q to generate a new maze without"
                + " animation", 10, FontStyle.ITALIC, new Color(190, 0, 170));
        TextImage iSp = new TextImage("Press the spacebar to quit the game", 
                10, FontStyle.ITALIC, new Color(190, 0, 170));
        TextImage iR = new TextImage("Press R to reset the player's path", 10, 
                FontStyle.ITALIC, new Color(190, 0, 170));
        TextImage iB = new TextImage("Press B to show the Breadth First search "
                + "solution", 10, FontStyle.ITALIC, new Color(190, 0, 170));
        TextImage iV = new TextImage("Press V to show the Depth First search solution",
                10, FontStyle.ITALIC, new Color(190, 0, 170));
        TextImage author = new TextImage("MAZEGAMEª by Jameson O'Connor and Brendan Rejevich.",
                10, FontStyle.ITALIC, new Color(30, 80, 80));
        
        scene.placeImageXY(ready, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 100);
        scene.placeImageXY(aK, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 120);
        scene.placeImageXY(iS, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 140);
        scene.placeImageXY(iF, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 160);
        scene.placeImageXY(iQ, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 180);
        scene.placeImageXY(iSp, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 200);
        scene.placeImageXY(iR, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 220);
        scene.placeImageXY(iB, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 240);
        scene.placeImageXY(iV, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 260);
        
        scene.placeImageXY(author, (CANVAS_WIDTH + DISPLAY_PANEL_X_CUTOFF) / 2, 580);

        
        // Drawing the Cells
        for (int y = 0; y < MAZE_HEIGHT; y += 1) {
            for (int x = 0; x < MAZE_WIDTH; x += 1) {
                scene = this.util.matrixGet(this.nodeList, x, y, MAZE_WIDTH).drawNode(scene);
            }
        }
        

        if (this.building && !this.endGame) {
            this.printCell(scene, this.startNode.getPosn(), new Color(51, 255, 51));
            this.printCell(scene, this.endNode.getPosn(), new Color(160, 32, 240));
        }
        if (!this.bfs && !this.dfs && this.building && !this.endGame) {
            this.printCell(scene, this.player1, new Color(255, 255, 0));
        }
        
        return scene;
    }
}




//EXAMPLES CLASS TO RUN MAZEGAME
class ExamplesMazeGame {
    void initWorld() {
        MazeGame w1 = new MazeGame();
        w1.bigBang(MazeGame.CANVAS_WIDTH,
                MazeGame.CANVAS_HEIGHT, .0001);
    }
    
    void testInit(Tester T) {
        this.initWorld();
    }
}



