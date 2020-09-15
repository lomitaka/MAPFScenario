package fileHandling;


import graphics.MVPoint;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import mapfScenario.Consts;
import mapfScenario.Data.MapData;
import mapfScenario.Data.SolutionPacket;
import mapfScenario.Data.SolutionPacketReadable;
import mapfScenario.DataStore;
import mapfScenario.agents.Agent;
import mapfScenario.picat.SolverOption;
import mapfScenario.solutionPack.SolutionPack;
import org.apache.log4j.Logger;
import mapfScenario.simulation.AgentAction;
import mapfScenario.simulation.Solution;

import java.io.*;
import java.util.*;

/** takes care of reading result of parsing file. */
public class SolutionFileWorker {

    private final Logger logger = Logger.getLogger(SolutionFileWorker.class);
    private static String nl = System.getProperty("line.separator");

    String solutionName = "#SolutionName";
    String timeStamp = "#Timestamp";
    String solverOptionDisplayName = "#SolverOptionDisplayName";
    String solverOptionPredicatName = "#SolverOptionPredicatName";
    String solverOptiontargetFilePath = "#SolverOptiontargetFilePath";
    String solutionFileName = "#SolutionFileName";
    String agentTag = "#Agents:";
    String agentTagFull = "#Agents: AgentNO AgentName AgentColor (startX,startY) (endX,endY)";
    String mapSizeTag = "#MapSizeX,MapSizeY";
    String mapObstacleTag = "#Obstacles: (MapObstacleX,MapObstacleY)";
    String idVerToPosTag = "#PicatIdToVertexNum: VertexNum->(PositionX,PositionY)";
    String answFileTag = "#AgentNO VertexNo";
    String answFileTagFull = "#AgentNO VertexNo Rotation Action Duration SomeText ";
    String sectionEnd = "#SectionEnd";

    public Solution loadSolutionFile(File file, HashMap<Integer,MVPoint> idToMVPointMap, int agentCount){

        List<String> input = readToList(file);

        return readSolutionFromStringList(input,file.getAbsolutePath(),idToMVPointMap,agentCount);

    }

    public Solution readSolutionFromStringList(List<String> input, String solutionFileName, HashMap<Integer,MVPoint> idToMVPointMap, int agentCount){

        if (input == null){
            return null;
        }

        Solution s = new Solution(agentCount);
        s.setSolutionFileName(solutionFileName);

        int lastAgentId = -1;
        List<AgentAction> result = new ArrayList<>();
        for (String line : input){

            // no solution found.
            if (line.trim().toUpperCase().equals(Consts.solutionNotFoundPicatString.trim().toUpperCase())){
                s.hasNoSoluiton = true;
                return s;
            }

            if (line.length() == 0) { continue;}
            if (line.trim().charAt(0) == '#') { continue;}
            String[] solData = line.split(" ",6);

            if (solData.length < 5) {
                logger.warn("unexpected file end");
                return null; }

            int agentNo = Integer.parseInt(solData[0]);

            int vertexNo = Integer.parseInt(solData[1]);
            MVPoint agPos = idToMVPointMap.get(vertexNo);
            int rotation = 0;
            if (!solData[2].trim().toUpperCase().equals("NULL")){
                rotation = Integer.parseInt(solData[2]);
            }

            String action = solData[3];
            int duration = Integer.parseInt(solData[4]);
            String note = "";
            if (solData.length >= 6) { note = solData[5].trim();}
            if (note.length() > 0 && note.charAt(0) =='"'){ note = note.substring(1);}
            if (note.length() > 0 && note.charAt(note.length()-1) =='"'){ note = note.substring(0,note.length()-1);}

            AgentAction aa = new AgentAction(agPos,rotation,action,duration,note);

            if (lastAgentId == -1) { lastAgentId = agentNo;}

            //flush agents
            if (lastAgentId != agentNo){
                s.addActions(lastAgentId,result);
                result.clear();
                lastAgentId = agentNo;
            }

            result.add(aa);
        }

        if (lastAgentId == -1){
            logger.error("solution do not contains any object");
            return null;
        }
        s.addActions(lastAgentId,result);
        return s;

    }


    public List<String> readToList(File file){

            List<String> result = new ArrayList<>();

            String line = "";
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                while ((line = br.readLine()) != null) {
                    result.add(line);
                }
                br.close();
            } catch (IOException e) {
                logger.error("cant read file " + e.getMessage());
                e.printStackTrace();
            }

            return result;
    }

    public void writeListToFile(List<String> data ,File file){


        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            for (String line : data) {
                out.println(line);
            }
            out.close();
        } catch (IOException e) {
            logger.error("cant write file " + e.getMessage());
            e.printStackTrace();
        }

    }


    public void saveSolutionPacket(SolutionPacket sol){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose where to save file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("sol", "*.sol")
        );
        File file = fileChooser.showSaveDialog(DataStore.mainWindow);

        if (!file.getName().toLowerCase().endsWith(".sol")){
            file = new File(file.getAbsolutePath()+".sol");
        }

        try {

            FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(sol);
            out.close();
            fileOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void saveSolutionPacketReadable(SolutionPacketReadable solr){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose where to save file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("solr", "*.solr")
        );
        File file = fileChooser.showSaveDialog(DataStore.mainWindow);

        if (!file.getName().toLowerCase().endsWith(".solr")){
            file = new File(file.getAbsolutePath()+".solr");
        }

        try {
        FileWriter out = new FileWriter(file);
        out.write(solutionName);out.write(nl);
        out.write(solr.solutionName);out.write(nl);

        out.write(timeStamp);out.write(nl);
        out.write(solr.timeStamp);out.write(nl);

        out.write(solverOptionDisplayName);out.write(nl);
        out.write(solr.so.displayName);out.write(nl);

        out.write(solverOptionPredicatName);out.write(nl);
        out.write(solr.so.predicatName);out.write(nl);

        out.write(solverOptiontargetFilePath);out.write(nl);
        out.write(solr.so.targetFilePath);out.write(nl);

        out.write(solutionFileName);out.write(nl);
        out.write(solr.solutionFileName);out.write(nl);

        out.write(nl);
        out.write(agentTagFull);out.write(nl);

        //Iterating over agnets and prints some info about them.
        List<Agent> agentList= solr.agentList;
        for (int i = 0 ; i < agentList.size();i++) {
            Agent agent = agentList.get(i);
            out.write(String.format("%d %s %s (%d,%d) (%d,%d)", i, escape(agent.name), agent.color, agent.start.x, agent.start.y, agent.end.x, agent.end.y ));
            out.write(nl);
        }

        out.write(nl);
        out.write(mapSizeTag);out.write(nl);
        //Prints information about map (map is made by its size and holes.)
        MapData mapData= solr.md;
        out.write(String.format("(%d,%d)",mapData.sizeX,mapData.sizeY));out.write(nl);

        out.write(mapObstacleTag);out.write(nl);
        Set<MVPoint> obstacleSet = mapData.obstacles; //obstacles are holes
        for (MVPoint obstacle : obstacleSet) {
            out.write(String.format("(%d,%d)", obstacle.x, obstacle.y));
            out.write(nl);
        }
        out.write(nl);


        out.write(idVerToPosTag);out.write(nl);
        Set<Map.Entry<Integer,MVPoint>> idToPints = solr.idToPointMap.entrySet();
        for (Map.Entry<Integer,MVPoint> idToPnt : idToPints){
            out.write(String.format("%d->(%d,%d)",idToPnt.getKey(),idToPnt.getValue().x,idToPnt.getValue().y));
            out.write(nl);
        }
        /** Answ file already contains description */
        for(String line : solr.answFile){
            out.write(line);
            out.write(nl);
        }

        out.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }



}

    public SolutionPacket loadSolutionPacket(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to load");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("sol", "*.sol", "*.solr")
        );

        File f   = fileChooser.showOpenDialog(DataStore.mainWindow);
        if (f == null) { return null;}

        if (f.getName().endsWith("sol")){
            return loadSolutionPacketSerialized(f);
        } else {
            SolutionPacketReadable packet = loadSolutionPacketReadable(f);
            SolutionPacket solPacket = new SolutionPacket();
            solPacket.s = readSolutionFromStringList(packet.answFile,packet.solutionName,packet.idToPointMap,packet.agentList.size());
            solPacket.s.addStageData(packet.agentList,packet.md);
            solPacket.answFile = packet.answFile;
            solPacket.idToPointMap = packet.idToPointMap;
            solPacket.so = packet.so;
            solPacket.solutionName = packet.solutionName;
            solPacket.timeStamp = packet.timeStamp;

            return solPacket;
        }

    }

    public SolutionPacket loadSolutionPacketSerialized(File f){

        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose whereto save file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("sol", "*.sol")
        );*/
        try {

            //File f   = fileChooser.showOpenDialog(DataStore.mainWindow);
            //if (f == null) { return null;}

            FileInputStream fileIn = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            SolutionPacket sp = (SolutionPacket)in.readObject();
            in.close();
            fileIn.close();
            return sp;
        }
         catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return null;
        }

    }

    enum SolNextData {
        None,
        Agents,
        MapSize,
        MabObstacle,
        idToVertMap,
        answFile};

    public SolutionPacketReadable loadSolutionPacketReadable(File f){

        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to load");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("solr", "*.solr", "*.sol")
        );*/
        try {

            //File f   = fileChooser.showOpenDialog(DataStore.mainWindow);
           // if (f == null) { return null;}

            BufferedReader br = new BufferedReader(new FileReader(f));

            SolutionPacketReadable spr = new SolutionPacketReadable();
            spr.so = new SolverOption();
            spr.idToPointMap = new HashMap<>();
            spr.agentList = new ArrayList<>();
            spr.answFile = new ArrayList<>();
            spr.md = new MapData( new HashSet<>(),0,0);

            //Set<MVPoint> obstacles = new HashSet<>();
            String line = "";
            SolNextData snd = SolNextData.None;
            while ((line = br.readLine()) != null){
                //solname
                if (line.startsWith(solutionName)){
                    String next = br.readLine();
                    if (next == null){ next = "";}
                    spr.solutionName = next;
                    continue;
                }
                //timeStamp
                if (line.startsWith(timeStamp)){
                    String next = br.readLine();
                    if (next == null){ next = "000";logger.warn("No timestamp found");}
                    spr.timeStamp = next;
                    continue;
                }
                if (line.startsWith(solverOptionDisplayName)){
                    String next = br.readLine();
                    if (next == null){ next = "null";logger.warn("No solverOptionDisplayName found");}
                    spr.so.displayName = next;
                    continue;
                }
                if (line.startsWith(solverOptionPredicatName)){
                    String next = br.readLine();
                    if (next == null){ next = "null";logger.warn("No solverOptionPredicatName found");}
                    spr.so.predicatName = next;
                    continue;
                }
                if (line.startsWith(solverOptiontargetFilePath)){
                    String next = br.readLine();
                    if (next == null){ next = "null";logger.warn("No solverOptiontargetFilePath found");}
                    spr.so.targetFilePath = next;
                    continue;
                }
                if (line.startsWith(solutionFileName)){
                    String next = br.readLine();
                    if (next == null){ next = "null";logger.warn("No solutionFileName found");}
                    spr.solutionFileName = next;
                    continue;
                }

                if (line.startsWith(agentTag)){
                    snd = SolNextData.Agents;
                    continue;
                }

                if (line.startsWith(mapSizeTag)){
                    String next = br.readLine();
                    if (next == null){ next = "null";logger.warn("No solutionFileName found");}
                    MVPoint size = MVPoint.mvPointFromString(next);
                    spr.md.sizeX = size.x;
                    spr.md.sizeY = size.y;
                    snd = SolNextData.None;
                    continue;
                }
                if (line.startsWith(mapObstacleTag)){
                    snd = SolNextData.MabObstacle;
                    continue;
                }

                if (line.startsWith(idVerToPosTag)){
                    snd = SolNextData.idToVertMap;
                    continue;
                }

                if (line.startsWith(answFileTag)){
                    snd = SolNextData.answFile;
                    spr.answFile.add("#AgentNO VertexNo Rotation Action Duration SomeText");
                    continue;
                }

                if (snd == SolNextData.Agents){
                    if (line.trim().length() == 0) { continue;}
                    String[] agentData = line.split(" ");
                    Integer agentNo = Integer.parseInt(agentData[0]);
                    String agentName = agentData[1];
                    Color c = Color.web(agentData[2]);
                    MVPoint start = MVPoint.mvPointFromString(agentData[3]);
                    MVPoint end = MVPoint.mvPointFromString(agentData[4]);
                    Agent a = new Agent(agentName,c,start,end,agentNo);
                    spr.agentList.add(a);
                }
                if (snd == SolNextData.MabObstacle) {
                    if (line.trim().length() == 0) { continue;}
                    MVPoint obstaclePoint = MVPoint.mvPointFromString(line);
                    spr.md.obstacles.add(obstaclePoint);
                    continue;
                }

                if (snd == SolNextData.idToVertMap){
                    if (line.trim().length() == 0) { continue;}
                    String[] data = line.split("->");
                    if (data.length != 2) {logger.error("vertexNum to map position invalid syntax, expected: Num->(Num,Num)"); return null; }
                    Integer id = Integer.parseInt(data[0]);
                    MVPoint pos = MVPoint.mvPointFromString(data[1]);
                    spr.idToPointMap.put(id,pos);
                    continue;
                }

                if (snd == SolNextData.answFile) {
                    spr.answFile.add(line);
                }
            }


            return spr;
        }
        catch (IOException i) {
            i.printStackTrace();
            return null;
        }
    }

    public void createOzoGeneratorInputFile (List<List<String>> inputActions, File writeFile){

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(writeFile)));

            for (int i =0 ; i <inputActions.size();i++){
                for (String line : inputActions.get(i)) {
                    //"2 14 180 turnLeft 2000 \"turnLeft\""

                    out.println(String.format("%d 0 0 %s null null",i+1,line));
                }
            }
            out.close();
        } catch (IOException e) {
            logger.error("cant write file " + e.getMessage());
            e.printStackTrace();
        }
    }


    static private String escape(String input){
        return input.replaceAll(" ","%20;");
    }

    static private String unescape(String input){
        String r1 = input.replaceAll("%20;"," ");
        return r1;
    }

}



