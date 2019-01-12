package mapfScenario;

import graphics.MVPoint;
import helpout.QuestionName;
import helpout.methods;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import mapfScenario.Data.MapData;
import mapfScenario.mapView.MapView;
import mapfScenario.realMap.RealMapManager;
import mapfScenario.agents.Agent;
import mapfScenario.simulation.Solution;
import mapfScenario.solutionPack.SolutionPack;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Set;
import javafx.scene.input.KeyEvent;
/**
 * Some methods too long to be added to MainFormController
 * */
public class MainFormControllerLogic {

    private final Logger logger = Logger.getLogger(MainFormControllerLogic.class);




    /** checks if agents are well placed on map. Also means that solver can be called on current data */
    public static boolean problemIsValidCheck(MapData mv, List<Agent> agentList ){
        StringBuilder answ = new StringBuilder();
        boolean error = false;
        Set<MVPoint> obst = mv.obstacles;



        // check if
        for (Agent a : agentList) {
            if (a.start == null ||  obst.contains(a.start) ||
                !MapView.isInGridAndOnGridCross(a.start,mv.getSizeXRefined(),mv.getSizeYRefined())) {

                answ.append(String.format(Consts.infoAgentStartPositionInvalidStrF,a.name));
                error = true;
            }

            if (a.end == null || obst.contains(a.end) ||
                !MapView.isInGridAndOnGridCross(a.end,mv.getSizeXRefined(),mv.getSizeYRefined())) {
                answ.append(String.format(Consts.infoAgentEndPositionInvalidStrF,a.name));
                error = true;
            }

        }

        if (agentList.size() == 0){
            methods.showDialog(Consts.infoNoAgentsDefined);
            return false;
        }

        if (error){
            methods.showDialog(answ.toString());
            return false;
        }
        return true;
    }


    /** creates new realMapManager set its properties to print, and prints  */
    public void realMapPrintLogic(MapView mv, int lengthRealMm,int widthRealMm ){

                //MapViewSettings printSettings;
        //float lentghRealMm = printSettings.edgeLength;
        //float widthRealmm = printSettings.edgeWidth;


        int lengthRealDpi = Math.round(lengthRealMm*Consts.mmPerInch *Consts.printResolutionDpi);
        int widthRealDpi = Math.round(widthRealMm*Consts.mmPerInch *Consts.printResolutionDpi);

        //mvsPrint.edgeLength = lengthRealDpi;
        //mvsPrint.edgeWidth = widthRealDpi;

        RealMapManager rm = new RealMapManager(mv);
        rm.setEdgeLength(lengthRealDpi);
        rm.setEdgeWidth(widthRealDpi);
        rm.setForPrint();
        rm.reinit();
        rm.print();;

    }

    /** some logic to invoke buttons on form. like Esc, F2,F3 */
    public void initKeyPressHandler(MainFormDelegate mfd){

        final MainFormDelegate mfdx = mfd;
        EventHandler handler = new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                MainFormDelegate mfd =  mfdx ;
                if(event.getCode() ==KeyCode.ESCAPE) {
                    logger.debug("Escape called");
                    mfd.escapeAction();
                } else if(event.getCode() == KeyCode.F2){
                    mfd.debugLoad();
                } else if (event.getCode() == KeyCode.F3){
                    initCommandFromF3(mfd);
                }
                else {
                    event.consume();
                }
            }

        };

        DataStore.mainWindow.addEventHandler(KeyEvent.KEY_PRESSED,handler);

    }

    /**  Currently invokes prompt windows from where SolutionPack can be processed  */
    public void initCommandFromF3(MainFormDelegate mfd){
        QuestionName qn = new QuestionName();qn.setQuestionData("Input command:","");
        methods.showDialogQuestion(qn);
        String command = qn.getAnswer();


        if (!qn.getConfirmed()) {
            return;
        }

        SolutionPack.processSolutionPackTrueIfExit(command.split(" "));
        mfd.loadAutoloadedSols();

    }

    /** checks if data from solution and current one are the same. */
    public boolean currentMapAndSolutionMatch(Solution s, MapData md, List<Agent> ags){

        List<Agent> solAglist = s.getAglist();

        if (solAglist.size() != ags.size()){return false;}
        for(int i = 0; i < solAglist.size();i++ ){
            if (!solAglist.get(i).equals(ags.get(i))){return false;}
        }

        MapData solMD = s.getMapData();
        if (solMD.sizeX != md.sizeX || solMD.sizeY != md.sizeY){ return false;}

        if (solMD.obstacles.size() != md.obstacles.size()){return false;}
        for (MVPoint mp : solMD.obstacles){
            if (!md.obstacles.contains(mp)){
                return false;
            }
        }
        return true;

    }







}
