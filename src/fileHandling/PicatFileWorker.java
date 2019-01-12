package fileHandling;

import graphics.MVPoint;
import mapfScenario.Data.MapData;
import mapfScenario.mapView.MapView;
import mapfScenario.agents.Agent;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 *  Creates input file for picat. Also encodes needed data.
 * */
public class PicatFileWorker {

    private final Logger logger = Logger.getLogger(PicatFileWorker.class);
    // innver values
    private final String nodePoint = "<NODE>";
    private final String nodeNeibrs = "<NEIBRS>";
    private final String agentStartRep = "<START>";
    private final String agentEndRep = "<END>";

    // values that might need to be exported
//    private final String firstRow = "ins(Rel, As) => Rel = [";
    //   private final String mapAgSeparator = "], As = [";
    //   private final String lastRow = "].";
    //now it will behave as agent term.
    private final String firstRow = "ins([";
    private final String mapAgSeparator = "],[";
    private final String lastRow = "]).";


    private final String agentRep = "(<START>,<END>)";
    private final String mapNodeRep = "neibs(<NODE>,[<NEIBRS>])";
    private final String neibsSeparator = ",";
    private final String nodeSeparator = ",";
    private final String agentSeparator = ",";

    private HashMap<MVPoint,Integer> pointToIDMap = new HashMap<>();
    private String nl = System.getProperty("line.separator");

    private MapData md;
    private List<Agent> aglist;
    private HashMap<String,String> options;


    private File picatProblemTarget;
    private FileWriter writer;

    public PicatFileWorker(MapData mv, List<Agent> agentList, File targetFile, HashMap<String,String> options){
        this.md = mv;
        this.aglist = agentList;
        this.picatProblemTarget = targetFile;
        this.options = options;
    }

    /*public void setTargetFile(File picatProblemTarget){
        this.picatProblemTarget = picatProblemTarget;
    }*/



    public void writeFile(){

        try {

            writer = new FileWriter(picatProblemTarget);

            //get string representation of nodes
            List<String> strNodes= getMapViewNodes(md);

            // get mapping info (just for debugging purposses
            List<String> cmtMappings = getMappingToInfoPrint(pointToIDMap);

            //writess comment for node mapping
            writer.write("% Map num mapping. (0,0) is upper left. 1.cord is x. second y");
            writer.write(nl);
            for (int i = 0; i < cmtMappings.size();i++){
                writer.write(cmtMappings.get(i));
                writer.write(nl);
            }
            writer.write(nl);


            writer.write(firstRow);
            writer.write(nl);

            //writing nodes
            for (int i = 0; i < strNodes.size()-1;i++){
                writer.write(strNodes.get(i));
                writer.write(nodeSeparator);
                writer.write(nl);
            }
            //write last node (without separator
            writer.write(strNodes.get(strNodes.size()-1));
            writer.write(nl);


            writer.write(mapAgSeparator);

            String agentStr = getAgentsStringRep(aglist);

            writer.write(agentStr);
            writer.write(lastRow);
            writer.write(nl);

            //write node data for map.
            List<String> helpMappings = getMappingToMapPrint(pointToIDMap);
            writer.write("% Map from ID to coords (x, y) top left is (1,1) ");
            writer.write(nl);
            writer.write("idPosMap([");
            writer.write(nl);
            for (int i = 0; i < helpMappings.size()-1;i++){
                writer.write(helpMappings.get(i));
                writer.write(",");
            }
            if (helpMappings.size() > 0) {
                writer.write(helpMappings.get(helpMappings.size() - 1));
            }
            writer.write("]).");
            writer.write(nl);

            //write optios
            //write node data for map.
            List<String> optionMap = getOptionMapToArray(options);
            writer.write("% Map from options to values ");
            writer.write(nl);
            writer.write("options([");
            writer.write(nl);
            for (int i = 0; i < optionMap.size()-1;i++){
                writer.write(optionMap.get(i));
                writer.write(",");
            }
            if (optionMap.size() > 0) {
                writer.write(optionMap.get(optionMap.size() - 1));
            }
            writer.write("]).");


            writer.close();


        } catch (IOException e) {
            logger.error("Write exception:" + e.getLocalizedMessage());
            // e.printStackTrace();
        }


    }

    /** gets map from pointsID to points.  */
    public HashMap<Integer,MVPoint> getIDToPointMap(){

         HashMap<Integer,MVPoint> IDMapToMVPoint = new HashMap<>();
         for (Map.Entry<MVPoint,Integer> kv :  pointToIDMap.entrySet()){
             IDMapToMVPoint.put(kv.getValue(),kv.getKey());
         }
         return IDMapToMVPoint;
    }

    private ArrayList<String> getMappingToInfoPrint(HashMap<MVPoint,Integer> map){
        Set<Map.Entry<MVPoint,Integer>> esmap = map.entrySet();
        ArrayList<Map.Entry<MVPoint,Integer>> arr = new ArrayList<>(esmap);

        Collections.sort(arr, new Comparator<Map.Entry<MVPoint,Integer>>(){

            public int compare(Map.Entry<MVPoint,Integer> o1, Map.Entry<MVPoint,Integer> o2)
            {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<MVPoint,Integer> val: arr){
            result.add("%" +val.getKey().toString() + " => " + val.getValue());
        }
        return result;

    }

    /**
     * want to print array that can be converted to map in picat
     * [1 = (1,2),2 = (2,2),3 = (4,5)] (something like this
     * */
    private ArrayList<String> getMappingToMapPrint(HashMap<MVPoint,Integer> map){
        Set<Map.Entry<MVPoint,Integer>> esmap = map.entrySet();
        ArrayList<Map.Entry<MVPoint,Integer>> arr = new ArrayList<>(esmap);

        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<MVPoint,Integer> val: arr){
            result.add("" +val.getValue() + " ="  +val.getKey());
        }
        return result;

    }

    /**
     * want to print array for options
     * [option1 = value1,option2 = value2,opion3 = value3] (something like this
     * */
    private ArrayList<String> getOptionMapToArray(HashMap<String,String> map){
        Set<Map.Entry<String,String>> esmap = map.entrySet();
        ArrayList<Map.Entry<String,String>> arr = new ArrayList<>(esmap);

        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String,String> val: arr){
            result.add("" +val.getKey() + " ="  +val.getValue());
        }
        return result;

    }



    private ArrayList<String> getMapViewNodes(MapData md){


        logger.debug("Generating neighbours: ");

        ArrayList<String> result = new ArrayList<>();

        Set<MVPoint> obstacles = md.obstacles;

        int widthRefined = md.getSizeXRefined();// mv.getSizeXRefined();
        int heightRefined = md.getSizeYRefined();

        // JUST GO Over every point and make it inexed (to have pleasant indexing.)
        logger.debug("Pregenerating indexes");
        for (int y = 0; y < heightRefined; y++ ){
            for (int x = 0; x < widthRefined; x++){
                MVPoint basePoint = new MVPoint(x,y);

                logger.debug("base point: " + basePoint.toString());
                if (!MapView.isInGridAndOnGridCross(basePoint,widthRefined,heightRefined)){
                    logger.debug("not in grid on line");
                    continue;
                }

                if (obstacles.contains(basePoint)){
                    logger.debug("there is obstacle");
                    continue;
                }
                getPointID(basePoint);
            }
        }

        // Iterating again and now creating string representation of vertex and neighbours
        logger.debug("Generating string representation");
        for (int y = 0; y < heightRefined; y++ ){
            for (int x = 0; x < widthRefined; x++){
                MVPoint basePoint = new MVPoint(x,y);

                logger.debug("base point: " + basePoint.toString());
                if (!MapView.isInGridAndOnGridCross(basePoint,widthRefined,heightRefined)){
                    logger.debug("not in grid on line");
                    continue;
                }

                if (obstacles.contains(basePoint)){
                    logger.debug("there is obstacle");
                    continue;
                }

                Set<MVPoint> neibrs = getNeighbourhood(basePoint,widthRefined, heightRefined,  obstacles);
                String nodeStr= getNodeStringRep(basePoint,neibrs);
                result.add(nodeStr);
            }
        }

        return result;
    }





    /**
     * gets point agent and list of neibrhoods and returns
     * ($neibrs (agent,[list neibrs]),
     * */
    private String getAgentsStringRep(List<Agent> agentList ){

        StringBuilder result = new StringBuilder();

        for (Agent ag: agentList) {
            StringBuilder agstr = new StringBuilder(agentRep);
            stringBuilderReplaceAll(agstr,agentStartRep,getPointID(ag.start)+"");
            stringBuilderReplaceAll(agstr,agentEndRep,getPointID(ag.end)+"");
            agstr.append(agentSeparator);
            result.append(agstr);
        }
        if (result.length() > agentSeparator.length()) {
            //removes last neibs separator
            result.delete(result.length() - agentSeparator.length(), result.length());
        }



        return result.toString();
    }


    /**
     * gets point agent and list of neibrhoods and returns
     * ($neibrs (agent,[list neibrs]),
     * */
    private String getNodeStringRep(MVPoint base, Set<MVPoint> neigbours){

        StringBuilder neibrs = new StringBuilder();
        for (MVPoint neigh: neigbours) {
            neibrs.append(getPointID(neigh));
            neibrs.append(neibsSeparator);
        }

        if (neibrs.length() > neibsSeparator.length()) {
            //removes last neibs separator
            neibrs.delete(neibrs.length() - neibsSeparator.length(), neibrs.length());
        }
        StringBuilder row = new StringBuilder(mapNodeRep);
        stringBuilderReplaceAll(row,nodePoint,getPointID(base)+"");
        stringBuilderReplaceAll(row,nodeNeibrs,neibrs);

        return row.toString();
    }

    /**
     * WARNING PICAT GETS INDEXES FROM ONE
     * */
    private Integer getPointID(MVPoint pt){

        if (pointToIDMap.containsKey(pt)){
            return pointToIDMap.get(pt);
        } else {
            int ptmapsize =pointToIDMap.size()+1;
            pointToIDMap.put(pt,ptmapsize);
            return ptmapsize;
        }
    }

    private Set<MVPoint> getNeighbourhood(MVPoint pt,int width, int height, Set<MVPoint> filter){
        Set<MVPoint> result = new HashSet<>();

        MVPoint candidate,preCandidate;

        candidate = new MVPoint(pt.x-2,pt.y); //left
        preCandidate = new MVPoint(pt.x-1,pt.y); //preleft
        if (!filter.contains(preCandidate)) {
            addIfMatch(candidate, width, height, filter, result);
        }

        candidate = new MVPoint(pt.x+2,pt.y); //right
        preCandidate = new MVPoint(pt.x+1,pt.y); //pre right
        if (!filter.contains(preCandidate)) {
            addIfMatch(candidate, width, height, filter, result);
        }

        candidate = new MVPoint(pt.x,pt.y-2); //up
        preCandidate = new MVPoint(pt.x,pt.y-1); //preup
        if (!filter.contains(preCandidate)) {
            addIfMatch(candidate, width, height, filter, result);
        }

        candidate = new MVPoint(pt.x,pt.y+2); //bottom
        preCandidate = new MVPoint(pt.x,pt.y+1); //prebottom
        if (!filter.contains(preCandidate)) {
            addIfMatch(candidate, width, height, filter, result);
        }

        return result;
    }

    private void addIfMatch(MVPoint pt,int width, int height, Set<MVPoint> filter, Set<MVPoint> addTo){
        if (MapView.isInGridAndOnGridCross(pt,width,height)){
            if (!filter.contains(pt)){
                addTo.add(pt);
            }
        }
    }


    private static void stringBuilderReplaceAll(StringBuilder builder, String from, StringBuilder to){
        stringBuilderReplaceAll(builder, from, to.toString());
    }
    private static void stringBuilderReplaceAll(StringBuilder builder, String from, String to)
    {
        int index = builder.indexOf(from);
        while (index != -1)
        {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }


}
