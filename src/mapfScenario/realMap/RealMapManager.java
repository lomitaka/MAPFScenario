package mapfScenario.realMap;

import helpout.methods;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mapfScenario.Consts;
import mapfScenario.DataStore;
import mapfScenario.mapView.MapView;
import mapfScenario.mapView.MapViewSettings;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** class derived from MapView it goals are to realMapDisplay map with changeable style and to save it to file.
 * Also creates new window, where shows map, and allow RealMapControll draw item to it */
public class RealMapManager {

    public interface Command{
        void execute();
    }

    /** reference to mapview, from which this instance was created */
    private MapView source;
    /** modidied map view, for printing or display reasons */
    private MapView myMv;
    /*reprezents current window.*/
    public Stage s;
    private final Logger logger = Logger.getLogger(RealMapManager.class);

    /** pane where grid is drawn */
    private Pane gridPane;
    /** pane over grid pane where obstacles are drawn */
    private Pane obstaclePane;

    /** pane which contains gridPane and Obstacle pane, and is used for printing*/
    private Pane printPane;

    /** used to display elements ( like agnet start position, and so on. ) */
    private Pane controllPane;

    public RealMapManager(MapView source){
        int sizeXPt = (source.mvs.sizeX +1)* source.mvs.edgeLength;
        int sizeYPt = (source.mvs.sizeY +1)* source.mvs.edgeLength;
        int border = source.mvs.edgeLength /2;
        initWindow(sizeXPt,sizeYPt,border);

        this.source = source;

        myMv = new MapView(gridPane,obstaclePane,  source.mvs.sizeX,source.mvs.sizeY);
        //myMv.mvs.mapBackGroundColor = JavaFx;
        myMv.setObstacles(source.getObstacles());

        //backgroundColor white;
        myMv.mvs.mapBackGroundColor = Color.web(Consts.mapBackGroundColorForDisplay);;


        logger.debug(String.format("BackGroundColor set: %s","-fx-background-color: " + methods.colorToWebString(myMv.mvs.mapBackGroundColor)));
        //gridPane.setStyle("-fx-background-color: " +methods.colorToWebString( myMv.mvs.mapBackGroundColor));
        s.getScene().setFill(Color.web(Consts.mapBackGroundColorForDisplay));
    }

    /** returns pane for drawing additional information */
    public Pane getControllPane(){
         return controllPane;
    }

    /** clears every element from controll pane */
    public void clearControllPane(){
        controllPane.getChildren().clear();
    }

    /** gets currently assign draw properties of  map */
    public MapViewSettings getMVS(){
        return myMv.mvs;
    }

    private void initWindow(int sizeXpt, int sizeYpt,int borderSize){
        final Stage dialog = new Stage();
        s = dialog;

        Color backgrColor = Color.web(Consts.mapBackGroundColorForDisplay);

        ScrollPane sp = new ScrollPane();

        Insets i = new Insets(borderSize);
        sp.setPadding(i);
        StackPane stackpane = new StackPane();
        StackPane stackPanePrint = new StackPane();
        printPane = stackPanePrint;

        Pane grid = new Pane();
        gridPane = grid;
        Pane holes = new Pane();
        obstaclePane = holes;


        Pane controll = new Pane();
        controllPane = controll;
        sp.setContent(stackpane);

        printPane.getChildren().add(grid);
        printPane.getChildren().add(holes);
        stackpane.getChildren().add(printPane);
        stackpane.getChildren().add(controll);

        //sp.setStyle("-fx-background-color: " +Consts.mapBackGroundColorForDisplay);
        //stackpane.setStyle("-fx-background-color: " +Consts.mapBackGroundColorForDisplay);;
        //grid.setStyle("-fx-background-color: " +Consts.mapBackGroundColorForDisplay);;

        //-fx-background-color does not do what i really want
        sp.setStyle("-fx-background: " + Consts.mapBackGroundColorForDisplay);



        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        logger.debug(String.format("ScreenBounds %f %f", primaryScreenBounds.getWidth(),primaryScreenBounds.getHeight()));
        double width = sizeXpt;
        double height = sizeYpt;
        if (primaryScreenBounds.getWidth() < sizeXpt){
            width = primaryScreenBounds.getWidth();
        }
        if (primaryScreenBounds.getHeight() < sizeYpt){
            height = primaryScreenBounds.getHeight();
        }
        //set Stage boundaries to visible bounds of the main screen
       // stage.setX(primaryScreenBounds.getMinX());
       // stage.setY(primaryScreenBounds.getMinY());
       // stage.setWidth(primaryScreenBounds.getWidth());
       // stage.setHeight(primaryScreenBounds.getHeight());

        dialog.setTitle("Real Map");
        Scene scene = new Scene(sp,width,height,backgrColor);


        dialog.setOnCloseRequest((WindowEvent event1) -> {
            logger.debug("Closing and setting rmm to null");
            executeOnExit();
            DataStore.rmm = null;
        });


        dialog.setScene(scene);

    }

    /** command rutine */
    List<Command> exitCommands= new ArrayList<>();
    public void addExecuteOnExit(Command cmd){
        exitCommands.add(cmd);
    }

    private void executeOnExit(){
        for(Command cmd : exitCommands){
            cmd.execute();
        }
    }

    /*private Pane getPaneToPrint(){

        Pane p = new Pane();
        p.setStyle("-fx-background-color: " +methods.colorToWebString( myMv.mvs.mapBackGroundColor));
        p.getChildren().add(gridPane);
        p.getChildren().add(obstaclePane);
        return p;
    }*/

    public void showWindow(){
        s.show();
    }

    /** only updates map display properties, do not redraws anything */
    public void setEdgeWidth(int edgeWidth){myMv.mvs.edgeWidth = edgeWidth;}
    /*only updates map display properties, do not redraws anything */
    public void setEdgeLength(int edgeLength){myMv.mvs.edgeLength =  edgeLength;}

    /**
     * updates width and length from source
     * updates grid size and obstacle positions and sizes based on edgeWidth and edgeLength */
    public void reinitFromSource(){

        myMv.mvs.sizeY = source.mvs.sizeY;
        myMv.mvs.sizeX = source.mvs.sizeX;
        myMv.reloadMapsize();
        myMv.setObstacles(source.getObstacles());
    }
    /** updates grid size and obstacle positions and sizes based on edgeWidth and edgeLength */
    public void reinit(){

        //myMv.mvs.sizeY = source.mvs.sizeY;
        //myMv.mvs.sizeX = source.mvs.sizeX;
        myMv.reloadMapsize();
        myMv.setObstacles(source.getObstacles());
    }

    /** initialize save map to file */
    public void SaveMap(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        //fileChooser.showOpenDialog(null);

        File file = fileChooser.showSaveDialog(DataStore.mainWindow);

        if (file != null){

            if (!file.getName().toLowerCase().endsWith(".png")){
                file = new File(file.getAbsolutePath()+".png");
            }
            saveAsPng(printPane,file.getAbsolutePath());
        }



        /*PrinterJob prob = PrinterJob.createPrinterJob();
        prob.showPrintDialog(null);
        prob.printPage(basePane);
        prob.endJob();*/



    }


    /** initialize map printing. */
    public void print(){
        PrinterJob job = PrinterJob.createPrinterJob();


        if (job == null){
            methods.showDialog(Consts.errorCannotCreatePrinterJob);
            return;
        }

        /* This code sould iterate over possible resolutions, and find fitting one.
         * but printer ignores resolution settings and always print on default settings which is 72 dpi */


        /*Printer printer = job.getPrinter();
        logger.debug("possible printResolutions:");
        PrintResolution selectedRes = printer.getPrinterAttributes().getDefaultPrintResolution();
        Set<PrintResolution> res = printer.getPrinterAttributes().getSupportedPrintResolutions();
        for (PrintResolution pr : res) {
            logger.debug(String.format("Resolution: %d %d",pr.getCrossFeedResolution(),pr.getFeedResolution()));

            if (pr.getCrossFeedResolution() == Consts.printResolutionDpi && pr.getFeedResolution() ==  Consts.printResolutionDpi){
                selectedRes  = pr;
            }
        }

        logger.debug(String.format("Printing with: %d %d",selectedRes.getCrossFeedResolution(),selectedRes.getFeedResolution()));
        JobSettings js = job.getJobSettings();
        js.setPrintResolution(selectedRes);*/




        if (job.showPrintDialog(null)) {


            job.printPage(printPane);
            job.endJob();


        }

    }

    /** set current map background color to be printed */
    public void setForPrint(){
        myMv.mvs.mapBackGroundColor = Color.web(Consts.mapBackGroundColorForPrint);
    }



    public static final void saveAsPng(Node node, final String fileName) {
        //SnapshotParameters sp; sp.
        final WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
        //final String        NAME     = fileName.replace("\\.[agent-zA-Z]{3,4}", "");
        final File          FILE     = new File(fileName);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", FILE);
        } catch (IOException exception) {
            // handle exception here
        }
    }

    public void closeWindow(){
        s.close();
    }


}
