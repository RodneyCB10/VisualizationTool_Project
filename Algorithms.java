package VisualizationTool_Project;

public class Algorithms {

    private static final double INF = Double.POSITIVE_INFINITY;

    /**
     * FloydWarshall- creates a matrix of shortest distances from each node to each node
     * @param graph- (Graph)- contains all information on nodes and edges
     * @return - a double array of all the shortest distances.
     */
    public static double[][] FloydWarshall(Graph graph){
        //make adjacency matrix
        double matrix[][] = new double[graph.getNumNodes()][graph.getNumNodes()];

        //Initialize matrix distances
        for (int i = 0; i < graph.getNumNodes(); ++i) {
            for (int j = 0; j < graph.getNumNodes(); ++j) {
                matrix[i][j] = INF;
            }
        }

        //fill matrix with graph data
        for(int i = 0; i < graph.getNumNodes(); ++i) {
            for (int j = 0; j < graph.getNumLinks(); ++j) {

                int node1 = graph.getLinks().get(j).getNode1().getId();
                int node2 = graph.getLinks().get(j).getNode2().getId();
                if(i == node1){

                    matrix[node1][node2] = graph.getLinks().get(j).getValue();

                    //mirror since it is an undirected graph
                    matrix[node2][node1]= graph.getLinks().get(j).getValue();
                }
            }
        }
        //print adjacency matrix for testing purposes
        System.out.println("User Input as Adjacency Matrix:");
        printMatrix(matrix, graph.getNumNodes());

        int V = matrix.length;
        double dist[][] = new double[V][V]; //output matrix

        //initialize output matrix as input matrix
        for (int i = 0; i < V; ++i) {
            for (int j = 0; j < V; ++j) {
                dist[i][j] = matrix[i][j];
            }
        }

        //run core algorithm ( used code from https://www.geeksforgeeks.org/floyd-warshall-algorithm-dp-16/)
        for (int k = 0; k < V; ++k) {
            for (int i = 0; i < V; ++i) {
                for (int j = 0; j < V; ++j) {

                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        //print distances matrix for testing and return dist for future use
        System.out.println("Shortest Distance Matrix:");
        printMatrix(dist, V);
        return dist;
    }

    /**
     * printMatrix- prints a double array in the form of a readable matrix (each line is a new node, each column is the distance to that node)
     * @param matrix - array to print
     * @param numNodes - length of array
     */
    private static void printMatrix(double[][] matrix, int numNodes){
        for (int i = 0; i < numNodes; ++i) {
            for (int j = 0; j < numNodes; ++j) {
                if (matrix[i][j] == INF) {
                    System.out.print("INF\t");
                } else {
                    System.out.printf("%.1f\t", matrix[i][j]);
                }
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    /**
     * dijkstra- creates a graph of the links in the shortest path between startNode and endNode
     * @param graph - graph in which to find the path
     * @param startNode - beginning of the path
     * @param endNode - end of the path
     * @return - a graph containing the links for the path
     * @throws Exception
     */
    public static Graph dijkstra(Graph graph, int startNode, int endNode) throws Exception {

        int numNodes = graph.getNumNodes();
        int[] reverse = new int[numNodes];
        double[] distance = new double[numNodes];
        int[] parent = new int[numNodes];
        boolean[] visited = new boolean[numNodes];
        int visitedNodes = 0;
        int minV;
        int minI = 0;
        int cur;
        int pathSize = 0;
        Graph shortest = new Graph();

        for (int i = 0; i < numNodes; i++) {
            distance[i] = INF;
            parent[i] = startNode;
            visited[i] = false;
        }
        distance[startNode] = 0;

        while(visitedNodes < numNodes){

            //Find minimun node not visited yet
            minV = (int) INF;
            for (int i = 1; i < numNodes; i++) {
                if ((distance[i] < minV) && !visited[i]){
                    minI = i;
                    minV = (int) distance[i];
                }
            }

            //Mark as visited
            visited[minI] = true;
            visitedNodes++;

            //Find and update adjacent nodes
            for (Node node:graph.getNodes()) {
                for (Link link:graph.getLinks()) {
                    if( ((link.getNode1() == graph.getNodes().get(minI) && link.getNode2() == node) || (link.getNode1() == node && link.getNode2() == graph.getNodes().get(minI))) && (distance[minI] + link.getValue() < distance[node.getId()]) ){
                        parent[node.getId()] = minI;
                        distance[node.getId()] = distance[minI] + link.getValue();
                        break;
                    }
                }
            }
        }

        cur = endNode;
        for (int i = 0; i < numNodes; i++) {
            reverse[i] = cur;
            cur = parent[cur];
            if (cur == startNode){
                reverse[i + 1] = cur;
                pathSize = i + 2;
                break;
            }
        }

        int[] path = new int[pathSize];
        for (int i = 0; i < pathSize; i++) {
            path[i] = reverse[pathSize - 1 - i];
        }

        for (int i = 0; i < pathSize - 1; i++) {
            for (Link link:graph.getLinks()) {
                if ( ((link.getNode1().getId() == path[i] && link.getNode2().getId() == path[i + 1]) || (link.getNode1().getId() == path[i + 1] && link.getNode2().getId() == path[i])) ){
                    shortest.addLink(link);
                    break;
                }
            }
        }

        return shortest;

    }


    /**
     * bellmanFord - Finds all the shortest paths from a start node to all other nodes.  Deletes edges that are not a part of this graph
     * @param graph- (Graph) contains all node and edge info
     * @param source- Starting node to find shortest paths
     * @return - Graph that contains all shortest paths
     * @throws Exception
     */
    public static Graph bellmanFord(Graph graph, Node source) throws Exception {
        double[] distance = new double[graph.getNumNodes()];


        Link[] links = new Link[graph.getNumNodes() - 1];

        //Set all weights to "infinite" except for source to source
        for(int i = 0; i< graph.getNumNodes(); i++){
            if(graph.getNode(i).equals(source)){
                distance[i] = 0;
            }
            else {
                distance[i] = INF;
            }
        }

        int sourcePassed = 0;

        //max of numNodes - 1 iterations
        for(int x = 1; x < graph.getNumNodes(); x++){

            sourcePassed = 0;

            //loops through every node each time
            for(int i = 0; i < graph.getNumNodes(); i++) {
                //ensures only if there is already a path to it
                if(distance[i] < INF-1) {

                    if(graph.getNode(i).equals(source)){
                        sourcePassed = 1;
                    }

                    for (int j = 0; j < graph.getNumLinks(); j++) {
                        //checks if the directed link starts at specified node
                        if (graph.getLinks().get(j).getNode1().equals(graph.getNode(i))) {
                            //if current distance from source to node2 is greater than link of node1 and 2 plus distance from source to node1
                            if (distance[graph.getLinks().get(j).getNode2().getId()] > (graph.getLinks().get(j).getValue() + distance[graph.getLinks().get(j).getNode1().getId()])){
                                //sets equal as the same thing
                                distance[graph.getLinks().get(j).getNode2().getId()] = (graph.getLinks().get(j).getValue() + distance[graph.getLinks().get(j).getNode1().getId()]);

                                //doesn't set destination links for source
                                if(!graph.getLinks().get(j).getNode2().equals(source)) {
                                    //getting the links for shortest paths
                                    links[graph.getLinks().get(j).getNode2().getId() - sourcePassed] = graph.getLinks().get(j);
                                }


                            }
                        }


                    }


                }
            }

        }


        Graph temp = new Graph();

        //creating new graph to return
        for(int i = 0; i < graph.getNumNodes(); i++){
            //adds all the nodes
            temp.addNode(graph.getNode(i).getName());
        }
        for(int i = 0; i < graph.getNumNodes() - 1; i++) {
            //adds all the links
            temp.addLink(links[i]);
        }

        //distance[] contains the values of the shortest paths

        //distance should be an array of the shortest path from the source to each corresponding node on the graph
        return temp;
    }
}
