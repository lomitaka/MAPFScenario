package fileHandling;

import helpout.methods;
import mapfScenario.DataStore;
import mapfScenario.picat.SolverOption;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PicatInterfaceParser {

    private final Logger logger = Logger.getLogger(PicatInterfaceParser.class);

    public List<SolverOption> loadPiatInterfaceOptions(String picatInterfaceFilePath) {
        logger.debug("parsing picat interface options");
        //trying to edit file
        ArrayList<SolverOption> result = new ArrayList<>();
        File file = methods.fileToAbsolute(picatInterfaceFilePath);
        //File file = new File(picatInterfaceFilePath);
        if (!file.exists()){
            logger.warn("file " + picatInterfaceFilePath + " does not exists");
            return result;
        }


        try {
            Scanner scan =  new Scanner(file);
            String  pis =DataStore.settings.picatAnotationString;
            while (scan.hasNextLine()){
                String line = scan.nextLine();

                if (line.contains(pis)){
                    logger.debug("annotation string found");
                    //remove spaces
                    line = line.replace("\r","");
                    line = line.replace("\n","");
                    int pas = line.indexOf(pis);
                    line = line.substring(pas+pis.length()).trim();
                    //If n is zero then the pattern will be applied as many times as possible,
                    // the array can have any length, and trailing empty strings will be discarded.
                    String[] arr = line.split(" ");
                    SolverOption so = new SolverOption();
                    if (arr.length > 0){

                        so.predicatName = arr[0];
                        so.targetFilePath = file.getAbsolutePath();
                        if (arr.length > 1)
                            { so.displayName = arr[1];}
                        else {
                            so.displayName = "";}
                        result.add(so);
                    }
                }

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return result;

    }
}
