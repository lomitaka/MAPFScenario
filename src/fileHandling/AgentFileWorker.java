package fileHandling;

import graphics.MVPoint;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import mapfScenario.DataStore;
import mapfScenario.agents.Agent;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AgentFileWorker {

    private final Logger logger = Logger.getLogger(AgentFileWorker.class);

    public void SaveToFile(List<Agent> aglist) {
        logger.debug("saving agents to file");
        //trying to edit file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Agent File");
        //fileChooser.showOpenDialog(null);
        //fileChooser.setSelectedExtensionFilter();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("AGENTS", "*.ags")
        );
        File file = fileChooser.showSaveDialog(DataStore.mainWindow);

        if (file == null) {
            logger.debug("cant write to file");
            return;
        }

        if (!file.getName().toLowerCase().endsWith(".ags")){
            file = new File(file.getAbsolutePath()+".ags");
        }
        logger.debug(String.format("file chosen: %s ", file.getAbsolutePath()));

        //ALWAYS WRITING REFINED. (unless someone asks for for not refined
        WriteAglist(aglist, file);
       /* if (mvNeedsRefine(mv)) {
            logger.info("Saving refined map");
            WriteRefinedMv(mv, file);
        } else {
            logger.info("Saving rough map");
            WriteRoughMv(mv,file);
        }*/

    }

    private void WriteAglist(List<Agent> aglist, File file){

        String nl = System.getProperty("line.separator");

        try {
            FileWriter fw = new FileWriter(file);
            //writes agent count
            fw.write(aglist.size()+"");
            fw.write(nl);
            //Iterate over file and write obstacles.
            for (Agent a : aglist) {
                String aColorString = helpout.methods.colorToWebString(a.color);
                fw.write(String.format("%s;%s;%s;%s", a.name, aColorString,a.start.toString(),a.end) );
                fw.write(nl);
            }

            fw.close();

        } catch (IOException e) {
            logger.error("cannot read file" + e.getMessage());
        }
    }



    public List<Agent> LoadFromFile() {
        logger.debug("loading agents to file");
        //trying to edit file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Agent File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("AGENTS", "*.ags")
        );

        File file  = fileChooser.showOpenDialog(DataStore.mainWindow);

        if (file == null ) {
            logger.debug("cant read file.");
            return null;
        }
        logger.debug(String.format("file chosen: %s ", file.getName()));

        //always laod refined map.
        return LoadAgents(file);

    }


    /* reads data from file into preparsed format */
    public List<Agent> LoadAgents(File file){

        String readLine = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            readLine = br.readLine();
            int agentCount = Integer.parseInt(readLine);
            if (agentCount < 0) { return null;}

            List<Agent> result = new ArrayList<>();
            for (int i = 0; i <agentCount;i++){
                readLine = br.readLine();
                if (readLine == null) {
                    logger.warn("unexpected file end");
                    return null;
                }
                String[] agData = readLine.split(";");
                String agName = agData[0];
                Color c = Color.web(agData[1]);
                MVPoint start = MVPoint.mvPointFromString(agData[2]);
                MVPoint end = MVPoint.mvPointFromString(agData[3]);
                Agent a = new Agent(agName,c,start,end,i);
                result.add(a);
            }

            br.close();

            return result;

        } catch (IOException e) {
            logger.error("cant read agentfile " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    //mvPointFromString

}
