/* *****************************************************************************
 *  Name:    Shohan Anthony Saverimuttu
 *  IIT_ID:   20191184
 *  Westminster_ID: w1790356
 *
 *  Description:  Finds max flow of Model built with input txt file.
 *                Add, remove and search for edges in model.
 *                Visualises graph model using JUNG library.
 *                Updates txt file if saved. Includes removing and adding data.
 *  Written:       25/03/2021
 *  Last updated:  08/04/2021
 *
 *  Computer: hp
 *  Ram: 12Gb
 *  Processor: Intel i5 7thGen
 *
 **************************************************************************** */

package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import javax.swing.JFrame;

import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import java.awt.Dimension;
import java.util.Scanner;

public class GraphMatrix  {
    static String in_files = "bridge_1";
    private int numOfNodes;  // Holds the number of Nodes needed for the model from the txt file.
    private boolean directed; // Checks if Graph is directed?
    private boolean weighted; // Checks if Graph is weighted.
    static float[][] matrix; // Creates a 2D array to store edges and weights.
    public static BufferedReader Rb;
    public static String str;
    public static int noOfVertices; // Initialise
    public static int sourceNode;
    public static int destinationNode;
    public static int weightOfEdge;
    public static GraphMatrix adjacencyMatrixGraph = new GraphMatrix( true, true); // Initialise an object adjacencyMatrixGraph of GraphMatrix type.
    public static String fileName = "";
    public static String fileNameTempHolder;
    public static DirectedSparseGraph<String, String> visualiserGraph = new DirectedSparseGraph<String, String>(); // Initialises an Object visualGraph of DirectSparseGraph type.
    public static int count = 0;
    public static ArrayList<String> addingList = new ArrayList<String>(); // Creates an array list to hold new edges to be added to the model.
    public static ArrayList<String> removeList = new ArrayList<String>(); // Creates an array list to hold new edges to be removed from the model.
    public static int pathFlowCounter; // Stores valid paths from source to sink node when finding path flow and Maximum flow.


    public static void initialiseFile(int Check){  // Takes first line from txt file and passes it onto variable noOfVertices to store the number of nodes required by the model.

            boolean InputValid = false;
            Scanner sc = new Scanner(System.in);
            /* While Loop to repeat if invalid file name has been entered.
               Check variable used to identify if initialiseFile method was called from the GraphMatrix constructor or Main method.
               If check = 0, method called from within GraphMatrix constructor. If Check = 1, method called from within Main method.
               Check is used to prevent the prompt for the name of the file when its not required.*/
            while (!InputValid) {
                if(Check == 0){
                    System.out.println("NAME OF THE FILE?");
                    fileName = sc.nextLine();
                    fileNameTempHolder = fileName;

                }
                fileName = fileNameTempHolder;
                try {
                    Rb = new BufferedReader(new FileReader(fileName +".txt")); // Initialises Buffer reader.
                    str = Rb.readLine();
                    str = str.trim();                      // Trims/ removes unwanted spaces
                    noOfVertices = Integer.parseInt(str); //  Converts from String type to Integer.
                    InputValid = true;                   // If file reading is successful, exits while Loop.

                }catch(NullPointerException et){
                    System.out.println(fileName+"File empty");
                }catch (FileNotFoundException ex){
                    System.out.println("File not Found");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return ;
    }

    /*
     This will allow us to safely add weighted graphs in our class since
     we will be able to check whether an edge exists without relying
     on specific special values (like 0)
    */
    private boolean[][] isSetMatrix;
    // Constructor for graph.
    public GraphMatrix( boolean directed, boolean weighted) {
        initialiseFile(0);
        this.directed = directed;
        this.weighted = weighted;
        this.numOfNodes = noOfVertices;

        // Simply initializes our adjacency matrix to the appropriate size
        matrix = new float[numOfNodes][numOfNodes];
        isSetMatrix = new boolean[numOfNodes][numOfNodes];
    }

    // adds egde to graph
    public void addEdge(int source, int destination, float weight) {

        float valueToAdd = weight;

        if (!weighted) {
            valueToAdd = 0;
        }
        
        matrix[source][destination] = valueToAdd;
        isSetMatrix[source][destination] = true;

        if (!directed) {
            matrix[destination][source] = valueToAdd;
            isSetMatrix[destination][source] = true;
        }
    }
    // Removes edge from graph
    public void removeEdge(int x, int y)
    {
        // Checks if the vertices exists
        if ((x < 0) || (x >= noOfVertices)) {
            System.out.printf("Vertex " + x
                    + " does not exist!");
        }
        if ((y < 0) || (y >= noOfVertices)) {
            System.out.printf("Vertex " + y
                    + " does not exist!");
        }

        // Checks if it is a self edge
        if (x == y) {
            System.out.println("Same Vertex!");
        }

        else {
            // Remove edge
            matrix[y][x] = 0;
            matrix[x][y] = 0;
            isSetMatrix[x][y] = false;
        }
    }
    //Prints matrix
    public void printDataStructureMatrix() {
        System.out.println("----Matrix----");
        for (int i = 0; i < numOfNodes; i++) {
            for (int j = 0; j < numOfNodes; j++) {
                // We only want to print the values of those positions that have been marked as set
                if (isSetMatrix[i][j])
                    System.out.format("%8s", String.valueOf(matrix[i][j]));
                else System.out.format("%8s", "0  ");
            }
            System.out.println();
        }

        System.out.println("-------END-----");
    }
    // Prints edges of data structure
    public void printDataStructureEdges() {
        System.out.println("----Edge description----");
        for (int i = 0; i < numOfNodes; i++) {
            System.out.print("Node " + i + " is connected to: ");
            for (int j = 0; j < numOfNodes; j++) {
                if (isSetMatrix[i][j]) {
                    System.out.print(j + " ");
                }
            }
            System.out.println();
        }

        System.out.println("--------END---------");
    }
    // Return Boolean to check if edge already exist.
    public boolean hasEdge(int source, int destination) {
        try{
            return isSetMatrix[source][destination];
        }catch(ArrayIndexOutOfBoundsException ee){
            System.out.println("Out of Bound, try smaller values. Array does not contain indexes");
        }
        return false;
    }
    // Method call to get value of a specific edge
    public Float getEdgeValue(int source, int destination) {
        if (!weighted || !isSetMatrix[source][destination]){
            System.out.print("Has no weight");
            return null;
        }
        return matrix[source][destination];
    }
    // Method call to construct data structure.
    private static void BuildDataStructure() throws Exception{
        while((str = Rb.readLine()) != null )
        {
            String temp[] = str.split(" ");
            sourceNode = Integer.valueOf(temp[0]);
            destinationNode = Integer.valueOf(temp[1]);
            weightOfEdge = Integer.valueOf(temp[2]);
            adjacencyMatrixGraph.addEdge(sourceNode, destinationNode, weightOfEdge);
        }
        Rb.close();
        System.out.println("COMPLETE! Data structure has been build based on the refered file: '"+ fileNameTempHolder +"' ! ");
        return;

    }
    // Adding egdes to the graph.
    public static void addingEdgeToGraph(){
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter Source node: ");
        sourceNode = sc.nextInt();
        System.out.print("Enter Destination node: ");
        destinationNode = sc.nextInt();
        System.out.print("Enter weight of edge: ");
        weightOfEdge = sc.nextInt();
        if(!adjacencyMatrixGraph.hasEdge(sourceNode, destinationNode)){
            addingList.add(String.valueOf(sourceNode)+" "+String.valueOf(destinationNode)+" "+String.valueOf(weightOfEdge));
            adjacencyMatrixGraph.addEdge(sourceNode, destinationNode, weightOfEdge);
            GraphMatrix.addEdgeToVisualGraph(sourceNode, destinationNode, weightOfEdge);
            adjacencyMatrixGraph.printDataStructureMatrix();
            adjacencyMatrixGraph.printDataStructureEdges();
        }else{
            System.out.println("Edge already exists. To update edge weight, delete exsisting edge and recreate with updated weight");
        }



    }
    public static void removingEdgeFromGraph(){
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter Source node: ");
        sourceNode = sc.nextInt();
        System.out.print("Enter Destination node: ");
        destinationNode = sc.nextInt();
        if(adjacencyMatrixGraph.hasEdge(sourceNode, destinationNode)){
            weightOfEdge = Math.round(adjacencyMatrixGraph.getEdgeValue(sourceNode, destinationNode));
            removeList.add(String.valueOf(sourceNode)+" "+String.valueOf(destinationNode)+" "+String.valueOf(weightOfEdge));
            adjacencyMatrixGraph.removeEdge(sourceNode, destinationNode);
            GraphMatrix.removeEdgeFromVisualGraph(sourceNode, destinationNode);
            adjacencyMatrixGraph.printDataStructureMatrix();
            adjacencyMatrixGraph.printDataStructureEdges();
        }else{
            System.out.println("Edge doesn't exists. Enter an existing edge to remove...");
        }

    }



    // -------------------------------------------------------------------------------------------------------


    // Using BFS as a searching algorithm
    public static boolean bfs(float Graph[][], int s, int t, int pathOfParentNode[]) {
        int V = matrix.length;
        boolean visited[] = new boolean[V];
        for (int i = 0; i < V; ++i)
            visited[i] = false;

        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(s);
        visited[s] = true;
        pathOfParentNode[s] = -1;

        while (queue.size() != 0) {
            int u = queue.poll();

            for (int v = 0; v < V; v++) {
                if (visited[v] == false && Graph[u][v] > 0) {
                    queue.add(v);
                    pathOfParentNode[v] = u;
                    visited[v] = true;
                }
            }
        }


        return (visited[t] == true);
    }

    // Applying fordfulkerson algorithm
    public static int fordFulkersonAlgorithm(float residualGraph[][], int sourceNode, int targetNode) {
        pathFlowCounter = 0;
        System.out.println("Paths from source to sink nodes: ");
        System.out.println();
        int V = matrix.length;
        int u, v;
        float Graph[][] = new float[V][V];

        for (u = 0; u < V; u++)
            for (v = 0; v < V; v++)
                Graph[u][v] = residualGraph[u][v];

        int p[] = new int[V];

        int max_flow = 0;


        while (bfs(Graph, sourceNode, targetNode, p)) {
            pathFlowCounter++;
            ArrayList<Integer> pathHolder = new ArrayList<Integer>();
            float path_flow = Integer.MAX_VALUE;

            for (v = targetNode; v != sourceNode; v = p[v]) {
                u = p[v];
                path_flow = Math.min(path_flow, Graph[u][v]);

                //System.out.println("         Node: "+v+" to Node: "+u +" with path flow of "+ path_flow);
            }
            for (v = targetNode; v != sourceNode; v = p[v]) {
                u = p[v];
                Graph[u][v] -= path_flow;
                Graph[v][u] += path_flow;
                pathHolder.add(v);
                pathHolder.add(u);
            }
            // Displaying valid paths and there path flow Value
            Collections.reverse(pathHolder);
            int counter=0;
            System.out.print(pathFlowCounter+") Path from Node "+pathHolder.get(counter)+" to ");
            counter++;
            while ( counter < (pathHolder.size()-1)){

                if (!pathHolder.get(counter).equals(pathHolder.get(counter+1)) && !(pathHolder.get(counter+1)).equals(null)){
                    System.out.print(pathHolder.get(counter)+" to ");
                }
                counter++;
            }
            System.out.print(pathHolder.get(counter));

            // Adding the path flows
            System.out.println(" has a Source to Sink path flow of: "+path_flow);
            max_flow += path_flow;
        }
//        System.out.println("Max Flow of the "+pathFlowCounter+" valid path's: " + max_flow+" ( Source "+0+" to Target " + (noOfVertices -1)+" )");
        System.out.printf("Max Flow of the %d valid paths: %d (Source %d to target %d)", pathFlowCounter, max_flow, 0, (noOfVertices - 1));
        return max_flow;
    }
    // ----------------------------------------------------------------------------------------------------
    // Uses Jung library to visualise txt file.
    private static void VisualizeGraph() throws Exception{


        int i = 1;
        while (i < noOfVertices) {
            visualiserGraph.addVertex(String.valueOf(i));
            i++;

        }
        while((str = Rb.readLine()) != null )
        {
            count++;
            String temp[] = str.split(" ");
            sourceNode = Integer.valueOf(temp[0]);
            destinationNode = Integer.valueOf(temp[1]);
            weightOfEdge = Integer.valueOf(temp[2]);
            visualiserGraph.addEdge("Edge "+String.valueOf(count)+" weight: "+String.valueOf(weightOfEdge), String.valueOf(sourceNode), String.valueOf(destinationNode));
        }
        Rb.close();
        VisualizationViewer<String, String> vv =
                new VisualizationViewer<String, String>(
                        new CircleLayout<String, String>(visualiserGraph), new Dimension(400,400));
        Transformer<String, String> transformer = new Transformer<String, String>() {
            @Override public String transform(String arg0) { return arg0; }
        };
        vv.getRenderContext().setVertexLabelTransformer(transformer);
        transformer = new Transformer<String, String>() {
            @Override public String transform(String arg0) { return arg0; }
        };
        vv.getRenderContext().setEdgeLabelTransformer(transformer);


        // The following code adds capability for mouse picking of vertices/edges. Vertices can even be moved!
        final DefaultModalGraphMouse<String,Number> graphMouse = new DefaultModalGraphMouse<String,Number>();
        vv.setGraphMouse(graphMouse);
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

        JFrame frame = new JFrame();
        frame.getContentPane().add(vv);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    // adds required data to visualise through Jung Library.
    public static void addEdgeToVisualGraph(int source, int destination, int weight){
        count++;
        visualiserGraph.addEdge("Edge "+String.valueOf(count)+" weight: "+String.valueOf(weight), String.valueOf(source), String.valueOf(destination));
        return;
    }
    // Required required data to visualise through Jung Library.
    public static void removeEdgeFromVisualGraph(int source, int destination){
        visualiserGraph.removeEdge("Edge "+String.valueOf(count)+" weight: "+String.valueOf(weightOfEdge));
        String edge = visualiserGraph.findEdge(String.valueOf(source),String.valueOf(destination));
        visualiserGraph.removeEdge(edge);
        return;
    }
    // Finds edge in the model.
    public static void Find(){
        System.out.println();
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter Source node: ");
        sourceNode = sc.nextInt();
        System.out.print("Enter Destination node: ");
        destinationNode = sc.nextInt();
        System.out.println("Does an edge from "+ sourceNode +" to "+ destinationNode +" exist?");

        long start = System.currentTimeMillis();
        if (adjacencyMatrixGraph.hasEdge(sourceNode, destinationNode)) {
            System.out.println("Yes");
            System.out.println(adjacencyMatrixGraph.getEdgeValue(sourceNode, destinationNode));
        }
        else System.out.println("No");
        long now = System.currentTimeMillis();
        double elapsed = (now - start) / 1000.0;
        System.out.println();
        System.out.println("Elapsed time = " + elapsed + " seconds");
    }
    // Saves changes to file, such as adding edges.
    public static void saveToFile(){
        if(!addingList.isEmpty()){
            int saveCount = 0;
            System.out.println(fileNameTempHolder);
            try {
                BufferedWriter writer = new BufferedWriter(
                        new FileWriter(fileNameTempHolder + ".txt", true));
                while  (saveCount < addingList.size()){
                    writer.write(addingList.get(saveCount));
                    writer.newLine();
                    saveCount++;
                }
                System.out.println("Test 02");
                writer.close();
                addingList.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    // Saves changes to file, such as removing edges.
    public static void removeFromFile() throws Exception  {
        if(!removeList.isEmpty()){
            System.out.println("Test 1");
            File inputFile = new File(fileNameTempHolder +".txt");
            File tempFile = new File("myTempFile.txt");

            boolean successful = false;
            boolean stringFound = false;

            try
            {
                BufferedReader reader = new BufferedReader
                        (new FileReader(inputFile));
                PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
                String lineToRemove;
                String currentLine;

                lineToRemove = "5 3 5";



                while((currentLine = reader.readLine()) != null && !currentLine.equals(" "))
                {
                    // trim newline when comparing with lineToRemove
                    String trimmedLine = currentLine.trim();
                    System.out.println(trimmedLine);
                    if(removeList.contains(trimmedLine)){

                        stringFound = true;
                        continue;
                    }
                    System.out.println("Gets in Trommspdksko");

                        writer.println(currentLine);
                        writer.flush();
                        System.out.println("Test next space empty");

                    System.out.println("blbalka");


                }

                if(stringFound)
                {
                    reader.close();
                    writer.close();
                    boolean deleteFile = inputFile.delete();
                    if(deleteFile){
                        successful = tempFile.renameTo(inputFile);
                    }else{
                        System.out.println("Unsuccessful");
                    }
                    if (!successful)
                    {
                        System.out.println("Sorry we couldn't complete your "
                                + "request due to an unexpected error");
                    }
                    else
                    {
                        System.out.println( " was deleted "
                                + "off your list!");
                        removeList.clear();
                    }
                }
                else
                {
                    System.out.println("Sorry this item was not found.");
                }

            }
            catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }





    public static void main(String[] args) throws Exception,
            java.lang.NullPointerException {
        Scanner sc= new Scanner(System.in);
        GraphMatrix.BuildDataStructure();
        while (true) {
            String UserChoice = "";
            System.out.println("            -------------------------PREMIER LEAGUE MANAGER------------\n" +
                    "          ||           [ENTER '1'] PRINT EDGE & MATRIX                 ||\n" +
                    "          ||           [ENTER '2'] FIND MAX FLOW                       ||\n" +
                    "          ||           [ENTER '3'] VISUALISE GRAPH                     ||\n" +
                    "          ||           [ENTER '4'] ADD EDGE                            ||\n" +
                    "          ||           [ENTER '5'] REM0VE EDGE                         ||\n" +
                    "          ||           [ENTER '6'] FIND EDGE & WEIGHT                  ||\n" +
                    "          ||           [ENTER '7'] SAVE CHANGES TO FILE                ||\n" +
                    "           ------------------------------------------------------------\n" +
                    "ENTER CHOICE(1-7):- "
            );
            UserChoice = sc.nextLine();
            switch (UserChoice) {
                case "1":
                    adjacencyMatrixGraph.printDataStructureMatrix();
                    adjacencyMatrixGraph.printDataStructureEdges();
                    break;
                case "2":
                    long start = System.currentTimeMillis();
                    fordFulkersonAlgorithm(matrix, 0, noOfVertices -1);
                    long now = System.currentTimeMillis();
                    double elapsed = (now - start) / 1000.0;
                    System.out.println();
                    System.out.println("Elapsed time = " + elapsed + " seconds");
                    break;
                case "3":
                    initialiseFile(1);
                    GraphMatrix.VisualizeGraph();
                    break;
                case "4":
                    GraphMatrix.addingEdgeToGraph();
                    break;
                case "5":
                    GraphMatrix.removingEdgeFromGraph();
                    break;
                case "6":
                    Find();
                    break;
                case "7":
                    GraphMatrix.saveToFile();
                    GraphMatrix.removeFromFile();
                    break;
                default:
                    System.out.println("Invalid input, Please try again.");
            }
        }
    }

}
