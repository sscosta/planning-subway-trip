package planningsubwaytrip.model;

import java.util.LinkedList;

public class Station implements Comparable<Station> {
    //base info of the station
    int id;
    int hashCode;
    String nome;
        public Line [] line;
    public Edge adjList;
    public int distance;
    public Station predecessor;
    public int nHops;

    public Station(int theId,int theHashCode,String theName,Line [] theLines){
        id=theId;
        hashCode=theHashCode;
        nome = theName;
        line = theLines;
        distance = Integer.MAX_VALUE;
        adjList=null;
        predecessor=null;
        nHops=Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "[Station - nome:" + nome + ", distance: " + distance + ", nHops: " + nHops + "];";
    }

    public LinkedList<Station> getAdjacent(){
        LinkedList<Station> adj = new LinkedList<>();
        adj.add(this);
        Edge e = adjList;
        while(e!=null){
            adj.add(((Pair<String,Station>)e.connectsTo).v);
            e=e.next;
        }
        return adj;
    }

    public Line[] getLine() {
        return line;
    }
    public Line getLine(int i){
        return line[i];
    }

    public String getNome() {
        return nome;
    }
    public void setDistance(int i){distance=i;}
    public int getHashCode(){return hashCode;}
    @Override
    public int compareTo(Station o) {
        return this.distance-o.distance;
    }
}
