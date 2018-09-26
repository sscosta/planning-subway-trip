package planningsubwaytrip;

import planningsubwaytrip.model.Edge;
import planningsubwaytrip.model.Line;
import planningsubwaytrip.model.Pair;
import planningsubwaytrip.model.Station;

import java.util.*;

public class SubwayService {
    public static String                   DESTINATION="";
    public static Pair<String,Station>[] graph;
    protected static int numberOfStations;


    public SubwayService(){
        graph = Loader.getGraph();
        numberOfStations = graph.length;

    }
    public void allPathsFromTo( LinkedList<Station> visited) {
        LinkedList<Station> stations = visited.getLast().getAdjacent();
        // examine adjacent nodes
        for (Station station : stations) {
            if (visited.contains(station)) {
                continue;
            }
            if (station.getNome().equals(DESTINATION)) {
                visited.add(station);
                printPath(visited);
                visited.removeLast();
                break;
            }
        }
        for (Station station : stations) {
            if (visited.contains(station) || station.getNome().equals(DESTINATION)) {
                continue;
            }
            visited.addLast(station);
            allPathsFromTo(visited);
            visited.removeLast();
        }
    }
    private void printPath(LinkedList<Station> visited) {
        for (Station st : visited) {
            System.out.print(st);
            System.out.print(" ");
        }
        System.out.println();
    }



    public void dijkstra ( Station src){
        src.setDistance(src.line[0].weight);

        Set<Station> settledNodes   = new HashSet<>();
        Set<Station> unsettledNodes = new HashSet<>();

            unsettledNodes.add(src);

            while (unsettledNodes.size() != 0) {
                Station currentNode = getLowestDistanceNode(unsettledNodes);
                unsettledNodes.remove(currentNode);
                Edge e = currentNode.adjList;
                while(e!=null){
                    Station adjacentNode = ((Station)((Pair<String,Station>)e.connectsTo).getValue());
                    Integer edgeWeight =e.weight + swapLinesWeight(currentNode,adjacentNode);
                    if(!settledNodes.contains(adjacentNode)){
                        calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                        unsettledNodes.add(adjacentNode);
                    }
                    e= e.next;
                }
                settledNodes.add(currentNode);
            }
            //print Path
            Station dst = ServiceUtilities.fetchStationFromNameOfStation(graph,DESTINATION);
            printShortestPath(dst);
        }

    private void printShortestPath(Station dst) {
        if(dst==null)return;
        printShortestPath(dst.predecessor);
        System.out.println(dst);
    }


    private static Station getLowestDistanceNode(Set < Station > unsettledNodes) {
        Station lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Station node: unsettledNodes) {
            int nodeDistance = node.distance;
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }


    private static void calculateMinimumDistance(Station evaluationNode,
                                                 Integer edgeWeigh, Station sourceNode) {
        Integer sourceDistance = sourceNode.distance;
        if (sourceDistance + edgeWeigh < evaluationNode.distance) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            evaluationNode.predecessor = sourceNode;
            }
    }

       private int swapLinesWeight(Station currentNode, Station adjacentNode) {
        //find the swappedLine
        Line[] linesFrst = currentNode.line;
        Line[] linesScnd = adjacentNode.line;
        for(int i=0;i<linesFrst.length;++i){
            for(int j=0;j<linesScnd.length;++j){
                Edge e= linesFrst[i].edge;
                while(e!=null){
                    if(e.connectsTo.equals(linesScnd[j]))//return waiting time + swapping time
                        return e.getWeight()+linesScnd[j].weight;
                    e=e.next;
                }
            }
        }
        return 0;
    }

    public void lessChanges(Station station) {
        station.nHops=0;

        Set<Station> settledNodes = new HashSet<>();
        Set<Station> unsettledNodes = new HashSet<>();

        unsettledNodes.add(station);

        while (unsettledNodes.size() != 0) {
            Station currentNode = getMinHopsNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            Edge e = currentNode.adjList;
            while(e!=null){
                Station adjacentNode = ((Station)((Pair<String,Station>)e.connectsTo).getValue());
                Integer hopNotHop = ServiceUtilities.isInTheSameLine(currentNode.predecessor,adjacentNode)?0:1;
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumHopDistance(adjacentNode, hopNotHop, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
                e=e.next;
            }
            settledNodes.add(currentNode);
        }
        //print Path
        Station dst = ServiceUtilities.fetchStationFromNameOfStation(graph,DESTINATION);
        printShortestPath(dst);
    }

    private static Station getMinHopsNode(Set < Station > unsettledNodes) {
        Station lowestDistanceNode = null;
        int lowestHopCount = Integer.MAX_VALUE;
        for (Station node: unsettledNodes) {
            int nodeHops = node.nHops;
            if (nodeHops < lowestHopCount) {
                lowestHopCount = nodeHops;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }
    private static void calculateMinimumHopDistance(Station evaluationNode,int hopNotHop,Station sourceNode){
        Integer sourceHops = sourceNode.nHops;
        if (sourceHops + hopNotHop <= evaluationNode.nHops) {
            evaluationNode.nHops = sourceHops + hopNotHop;
            evaluationNode.predecessor = sourceNode;
        }
    }
}
