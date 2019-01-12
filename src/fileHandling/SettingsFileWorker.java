package fileHandling;

import helpout.methods;
import mapfScenario.Settings;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class SettingsFileWorker {

    private static final Logger logger = Logger.getLogger(SettingsFileWorker.class);

    public static Settings defaultSettings(){

           Settings s = new Settings();
            s.workDirectoryPath = "workdir";
            s.defaultPicatInterfaceFileName =  "picat"+ File.separator +"picat_iface.pi";
            s.picatAnotationString = "[SCENARIO_EXPORT]";
            //s.libDirectoryPath = "lib";
            s.customFilePredicat = "run";
            s.ozobotTemplateFile = "template.ozocode";
        return  s;
    }

    public static Settings loadSettings(String configFile){

        Settings s = new Settings();

        Properties prop = new Properties();
        InputStream input = null;
        File cf = methods.fileToAbsolute(configFile);
        if (!cf.exists() || !cf.canRead()){
            logger.info("config file was not found");
            return defaultSettings();
        }

        try {


            input = new FileInputStream(cf);

            // load agent properties file
            prop.load(input);

            s.defaultPicatInterfaceFileName = prop.getProperty("defaultSolverFileName");
            s.workDirectoryPath = prop.getProperty("workDirectoryPath");
            s.picatAnotationString = prop.getProperty("picatAnotationString");
            //s.libDirectoryPath = prop.getProperty("libDirectoryPath");
            s.customFilePredicat = prop.getProperty("customFilePredicat");
            s.ozobotTemplateFile = prop.getProperty("ozobotTemplateFile");
            // get the property value and print it out
            //System.out.println(prop.getProperty("database"));
            //System.out.println(prop.getProperty("dbuser"));
            //System.out.println(prop.getProperty("dbpassword"));



        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("cant read config file");
            e.printStackTrace();
            return null;
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


        //IF nothing is readed, writes default properties and returns them.
        if (prop.size() == 0) {

            Settings def = defaultSettings();
            SettingsFileWorker.saveSettings(def);
            return def;
        }

        return s;
    }


    public static void saveSettings(Settings s){
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            File outputFile = methods.fileToAbsolute("config.properties");
            output = new FileOutputStream(outputFile);

            // set the properties value
            prop.setProperty("defaultSolverFileName" , s.defaultPicatInterfaceFileName);
            prop.setProperty("workDirectoryPath" , s.workDirectoryPath);
            prop.setProperty("picatAnotationString", s.picatAnotationString);
            //prop.setProperty("libDirectoryPath",s.libDirectoryPath);
            prop.setProperty("customFilePredicat",s.customFilePredicat);
            prop.setProperty("ozobotTemplateFile",s.ozobotTemplateFile);
            // save properties to project root folder
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



}
