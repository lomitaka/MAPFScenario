package fileHandling;


import mapfScenario.solutionPack.SolutionAutogenItem;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** takes care of reading result of parsing file. */
/**   NAME, MAP,AGS, SOLVER, TEMPLATE, ACTIONCAT  */
public class SolutionPackFileWorker {

    private static final Logger logger = Logger.getLogger(SolutionPackFileWorker.class);
    public static List<SolutionAutogenItem> loadSolutionPackFile(File file){


        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            //line = br.readLine();
            //if (agentCount < 0) { return null;}
            int lastAgentId = -1;
            List<SolutionAutogenItem> result = new ArrayList<>();
            while ((line = br.readLine()) != null){


                if (line.length() == 0) { continue;}
                if (line.trim().charAt(0) == '#') { continue;}
                String[] solData = line.split(";",6);

                if (solData.length < 5) {
                    logger.warn("unexpected file end");
                    return null; }

                File map = helpout.methods.fileToAbsolute(solData[1]);
                File ags = helpout.methods.fileToAbsolute(solData[2]);

                SolutionAutogenItem spi = new SolutionAutogenItem(solData[0],map,ags,solData[3],solData[4]);
                result.add(spi);
            }


            br.close();

            return result;

        } catch (IOException e) {
            logger.error("cant read agentfile " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
