package mapfScenario;

import fileHandling.SettingsFileWorker;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mapfScenario.solutionPack.SolutionPack;
import org.apache.log4j.Logger;

/**
 * This class creates Main MapfScenario window.
 * And gives control to MainFormController.
 * */

public class EntryPoint extends Application {
    private final Logger logger = Logger.getLogger(EntryPoint.class);
    @Override
    public void start(Stage primaryStage) throws Exception{
        logger.info("starting app");

        DataStore.mainWindow = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("MainForm.fxml"));
        primaryStage.setTitle("MAPF Scenario v1.06");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
       /* VBox hb = Temp.getRange();
        primaryStage.setScene(new Scene(hb,600,600));
        primaryStage.show();*/

        //on close listnerer
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                logger.info("stage is closing");
                onCloseRequest();
            }
        });
        //primaryStage.close();
    }


    public static void main(String[] args) {

        DataStore.args = args;
        loadFileData();
        consoleArgsProcessTrueIfExit(args);
        //Latter to use as javaFX application
        launch(args);
    }


    private void onCloseRequest(){
        try {
            saveFileData();
        } catch (Exception e){
            logger.error("cannot save settings. Some error");
        }
    }


    //load settings.
    public static void loadFileData(){
        //logger.info("loading settings");
        Settings s = SettingsFileWorker.loadSettings(Consts.settingsFile);

        DataStore.settings = s;



    }

    public void saveFileData(){

        SettingsFileWorker.saveSettings(DataStore.settings);

    }


    public static boolean consoleArgsProcessTrueIfExit(String[] args){

        if (args.length == 0) { return false;}

        if (args.length > 0 && args[0].trim().toLowerCase().equals("solpack")){
            return SolutionPack.processSolutionPackTrueIfExit(args);

        }



        return false;

    }



}
