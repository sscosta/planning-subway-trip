package planningsubwaytrip;

import planningsubwaytrip.model.Edge;
import planningsubwaytrip.model.Line;
import planningsubwaytrip.model.Pair;
import planningsubwaytrip.model.Station;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Loader {
    private static int                     numberOfStations;
    public static Pair<String,Station> [] graph;

    Loader(String[] filenames){
        String lineDataFileName = filenames[0];
        String stationDataFileName = filenames[1];
        loadStationsDataFromFile(stationDataFileName);
        loadLineDataFromFile(lineDataFileName);
    }
    protected static void loadStationsDataFromFile(String stFileName) {

        try(BufferedReader brStations = new BufferedReader(
                new InputStreamReader(
                        planningsubwaytrip.class.getClassLoader().getResourceAsStream(stFileName)))){

            initGraph(brStations);
            pairStationWithItsLines(brStations);
            fillStationEdges(brStations);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initGraph(BufferedReader br)throws IOException{
        numberOfStations = Integer.valueOf(br.readLine());
        graph= (Pair<String,Station>[]) new Pair[numberOfStations];
    }

    private static void pairStationWithItsLines(BufferedReader br)throws IOException{
        String stationLine;
        for(int i = 0; i < numberOfStations; i++){
            stationLine = br.readLine();
            Pair<String,Station> newP = buildStationFromLine(stationLine,i);
            insertPairInGraph(newP);
        }
    }

    private static Pair<String,Station> buildStationFromLine(String stationLine,int id){
        String stationName = ServiceUtilities.fetchNameOfStationFromLine(stationLine);
        return new Pair<String,Station>(ServiceUtilities.fetchNameOfStationFromLine(stationLine),new Station(id,index(stationName),stationName,ServiceUtilities.fetchLinesFromString(stationLine)));
    }

    private static void insertPairInGraph(Pair<String,Station> pair){
        int index = pair.getValue().getHashCode();
        if(graph[index]==null)
            graph[index] = pair;
        else{
            pair.next = graph[index];
            graph[index]= pair;
        }
    }
    //21linhas
    private static void fillStationEdges(BufferedReader br) throws IOException{
        String textLine;
        while((textLine=br.readLine())!=null){
            String [] stationsSplittedFromTime = textLine.split("-");
            String [] namesOfStations = stationsSplittedFromTime[0].split(":");
            int indexOfStation1 =index(namesOfStations[0]);
            int indexOfStation2 = index(namesOfStations[1]);

            Pair<String,Station> st1 = graph[indexOfStation1];
            while(isNotTheEndOfListAndWasntFound(st1,namesOfStations[0]))
                st1=st1.getNext();

            Pair<String,Station> st2 = graph[indexOfStation2];
            while(isNotTheEndOfListAndWasntFound(st2,namesOfStations[1]))
                st2=st2.getNext();

            int transitTimeInSeconds = getTransitTimeInSeconds(stationsSplittedFromTime[1]);
            st1.getValue().adjList = new Edge(transitTimeInSeconds,st2,st1.getValue().adjList);

            st2.getValue().adjList = new Edge(transitTimeInSeconds,st1,st2.getValue().adjList);
        }
    }

    private static boolean isNotTheEndOfListAndWasntFound(Pair<String,Station> station,String nameOfStation) {
        return station!=null && !station.getKey().equals(nameOfStation);
    }

    private static int getTransitTimeInSeconds(String timeString){
        String [] timeMinSec = removeTrailingSpacesAndSplitMinFromSeconds(timeString);
        return minSecToSeconds(timeMinSec);
    }

    private static String[] removeTrailingSpacesAndSplitMinFromSeconds(String timeString){
        return timeString.replace(" ","").split(":");
    }

    private static void loadLineDataFromFile(String arg) {
        String textFileLine;
        try( BufferedReader brLines = new BufferedReader(new
                InputStreamReader(planningsubwaytrip.class.getClassLoader().getResourceAsStream(arg)))){

            int numberOfLines = Integer.valueOf(brLines.readLine());
            for(int j = 0;j<numberOfLines;++j){
                textFileLineToFillLineWeight(textFileLine = brLines.readLine());
            }
            fillLineEdges(brLines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void textFileLineToFillLineWeight(String textFileLine) {
        String [] lineAux = textFileLine.split(" ");
        String subwayLine = lineAux[0];
        String [] timeAux = lineAux[1].split(":");
        int weight = minSecToSeconds(timeAux);
        fillAllStationsInLineWithWeight(subwayLine,weight);
    }

    private static void fillAllStationsInLineWithWeight(String subwayLine,int weight) {
        for(int i = 0; i< numberOfStations; ++i){
            Pair<String,Station> pairToIterate = graph[i];
            while(pairToIterate!=null){
                if(isInTheSameLine(pairToIterate,subwayLine))
                    setLineWeight(pairToIterate,subwayLine,weight);
                pairToIterate = pairToIterate.getNext();
            }
        }
    }

    private static boolean isInTheSameLine(Pair<String,Station> pair,String subwayLine){
        Line [] lines = pair.getValue().line;
        for(int i =0;i<lines.length;++i){
            if(lines[i].equals(subwayLine))
                return true;
        }
        return false;
    }

    private static void setLineWeight(Pair<String,Station> pair, String subwayLine, int weight){
        Line [] lines = pair.getValue().line;
        for(int i =0;i<lines.length;++i){
            if(lines[i].equals(subwayLine))
                lines[i].setWeight(weight);
        }
    }

    //19linhas
    private static void fillLineEdges(BufferedReader brLines)throws IOException {
        String textFileLine,lineAux[];
        while((textFileLine=brLines.readLine())!=null){

            lineAux = textFileLine.split(" ");
            String nameOfLine1 = lineAux[0];
            String nameOfLine2 = lineAux[1];
            int weight = minSecToSeconds(lineAux[2].split(":"));

            for(int i = 0; i< numberOfStations; ++i){
                Pair<String,Station> pairUsedToIterate = graph[i];
                while(pairUsedToIterate!=null){
                    if(hasMoreThanOneLine(pairUsedToIterate) && stationHasBothLines(pairUsedToIterate,nameOfLine1,nameOfLine2)) {
                        insertLineEdge(pairUsedToIterate,nameOfLine1,nameOfLine2,weight);
                    }
                    pairUsedToIterate=pairUsedToIterate.getNext();
                }
            }
        }
    }

    private static void insertLineEdge(Pair<String, Station> pairUsedToIterate, String line1, String line2, int weight) {
        Line l1 = pairUsedToIterate.getValue().getLine(0);
        Line l2 = pairUsedToIterate.getValue().getLine(1);
        if(l1.getName().equals(line1)&& l2.getName().equals(line2)) {
            l1.edge = new Edge(weight, pairUsedToIterate.getValue().getLine(1), l1.getEdge());
            l2.edge = new Edge(weight, pairUsedToIterate.getValue().getLine(0), l2.getEdge());
        }
        else{
            l1.edge = new Edge(weight, pairUsedToIterate.getValue().getLine(0), l1.getEdge());
            l2.edge = new Edge(weight, pairUsedToIterate.getValue().getLine(1), l2.getEdge());
        }
    }

    private static boolean stationHasBothLines(Pair<String,Station> pair, String line1, String line2){
        return (pair.getValue().line[0].name.equals(line1) && pair.getValue().line[1].name.equals(line2))
                || (pair.getValue().line[1].name.equals(line1) && pair.getValue().line[0].name.equals(line2));
    }

    private static boolean hasMoreThanOneLine(Pair<String,Station> pair){
        return pair.getValue().getLine().length!=1;
    }

    private static int minSecToSeconds(String[] timeAux) {
        return Integer.parseInt(timeAux[0])*60 + Integer.parseInt(timeAux[1]);
    }

    protected static int index(Object k){
        int hc = k.hashCode();
        int m = hc% numberOfStations;
        return m<0?m+ numberOfStations :m;
    }
    public static Pair<String,Station>[] getGraph(){
        return graph;
    }
}