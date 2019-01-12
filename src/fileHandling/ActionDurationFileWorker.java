package fileHandling;

import helpout.methods;
import mapfScenario.Consts;
import org.apache.log4j.Logger;
import mapfScenario.timeDuration.ActionCategory;
import mapfScenario.timeDuration.ActionDuration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/** handles saving and loading action to file */
public class ActionDurationFileWorker {


    private static final Logger logger = Logger.getLogger(SettingsFileWorker.class);


    public static List<ActionCategory> loadActionCategories(){



        List<ActionDuration> result = new ArrayList<ActionDuration>();


        Properties prop = new Properties();
        InputStream input = null;
        //File cf = methods.fileToAbsolute(configFile);
        //File cf = new File(sourceFile);
        File cf = helpout.methods.fileToAbsolute(Consts.actionDurationFile);
        if (!cf.exists() || !cf.canRead()){
            logger.info("no ActionDurations defined");
            return new ArrayList<ActionCategory>() ;
        }

        try {


            input = new FileInputStream(cf);

            // load agent properties file
            prop.load(input);

            for (String actionName: prop.stringPropertyNames()     ) {
                int intDuration = -1;
                 try {
                     intDuration = Integer.parseInt(prop.getProperty(actionName));
                 } catch (Exception e){}
                 if (intDuration <= 0 || actionName.length() == 0) { continue; }
                 ActionDuration ad = new ActionDuration(actionName,intDuration);
                 result.add(ad);
            }

            return actionDurationsListToMap(result);


        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("cant read action duration file");
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    logger.error("cant close config file");
                    e.printStackTrace();

                }
            }
        }
    }


    public static void saveSettings(List<ActionDuration> adList){
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            File outputFile = methods.fileToAbsolute(Consts.actionDurationFile);
            output = new FileOutputStream(outputFile);

            for (ActionDuration ad: adList) {
                prop.setProperty(ad.actionName,ad.actionDurations+"");
            }

            prop.store(output, null);

        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }

        }

    }


    private static List<ActionCategory> actionDurationsListToMap(List<ActionDuration> rawAD){
        HashMap<String,List<ActionDuration>> result = new HashMap<>();

        //want to split
        for (ActionDuration adRaw: rawAD) {
            List<String> adres = rawActionParse(adRaw.actionName);
            if (adres == null) { continue;}
            if (result.containsKey(adres.get(0))){
                result.get(adres.get(0)).add(new ActionDuration(adres.get(1),adRaw.actionDurations));
            } else {
                List<ActionDuration> newRec = new ArrayList<ActionDuration>();
                newRec.add(new ActionDuration(adres.get(1),adRaw.actionDurations));
                result.put(adres.get(0),newRec);
            }
        }

        List<ActionCategory> aclist = new ArrayList<>();

        for (java.util.Map.Entry<String, List<ActionDuration>> mdata: result.entrySet()) {
            aclist.add(new ActionCategory(mdata.getKey(),mdata.getValue()));
        }

        return aclist;
    }



    /** returns category, name*/
    private static List<String> rawActionParse(String rawDuration){

       /* String prefix = Consts.actionDurationPrefix;

        if (rawDuration.startsWith(prefix)){
            rawDuration = rawDuration.substring(prefix.length());
        } else {
            return null;
        }*/
        List<String> result = new ArrayList<>();
        int lastDot = rawDuration.lastIndexOf('.');
        if (lastDot <= 0) return null;
        String action = rawDuration.substring(lastDot+1);
        String category = rawDuration.substring(0,lastDot);

        result.add(category);result.add(action);

        return result;

    }

}
