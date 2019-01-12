package fileHandling;


import graphics.MVPoint;
import javafx.stage.FileChooser;
import mapfScenario.Consts;
import mapfScenario.Data.SolutionPacket;
import mapfScenario.DataStore;
import org.apache.log4j.Logger;
import mapfScenario.simulation.AgentAction;
import mapfScenario.simulation.Solution;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** takes care of reading result of parsing file. */
public class SolutionFileWorker {

    private final Logger logger = Logger.getLogger(SolutionFileWorker.class);
    public Solution loadSolutionFile(File file, HashMap<Integer,MVPoint> idToMVPointMap, int agentCount){

        Solution s = new Solution(agentCount);
        s.setSolutionFileName(file.getAbsolutePath());

        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            //line = br.readLine();
            //if (agentCount < 0) { return null;}
            int lastAgentId = -1;
            List<AgentAction> result = new ArrayList<>();
            while ((line = br.readLine()) != null){

                // no solution found.
                if (line.trim().toUpperCase().equals(Consts.solutionNotFoundPicatString.trim().toUpperCase())){
                    s.hasNoSoluiton = true;
                    br.close();
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
                if (solData.length >= 6) { note = solData[5];}
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
                br.close();
              return null;
            }
            s.addActions(lastAgentId,result);

            br.close();

            return s;

        } catch (IOException e) {
            logger.error("cant read agentfile " + e.getMessage());
            e.printStackTrace();
        }
        return null;
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
        fileChooser.setTitle("Choose whereto save file");

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

    public SolutionPacket loadSolutionPacket(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose whereto save file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("sol", "*.sol")
        );
        try {

            File f   = fileChooser.showOpenDialog(DataStore.mainWindow);
            if (f == null) { return null;}

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

}



