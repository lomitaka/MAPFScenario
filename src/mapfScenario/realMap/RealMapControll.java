package mapfScenario.realMap;

import graphics.GraphicManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mapfScenario.*;
import mapfScenario.agents.Agent;
import mapfScenario.mapView.MapViewSettings;
import mapfScenario.picat.PicatManager;
import mapfScenario.picat.SolverProcess;
import mapfScenario.simulation.AgentActionPair;
import mapfScenario.simulation.SimulationMap;
import mapfScenario.simulation.Simulator;
import mapfScenario.simulation.Solution;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *  Represents form which controlls Real map testing.
 *  Allows to set start position, start test, stop test, chose solution to draw while testing,
 *  and scripts to execute on start and on end.
 *
 * */
public class RealMapControll {

    private final Logger logger = Logger.getLogger(RealMapControll.class);

    /** Represents form that with controll */
    private Stage s;
    /** reference to picat manager, to obtain list of solution. */
    private PicatManager pm;
    /** took modified version of simulation process, to show simulated robot locations on real map. */
    private SimulationMap smFront;
    /** pane used to draw agent positions, and current location of simulation. */
    private Pane displayControllPane;
    /** label of timer. used to update timer positions. */
    private Label displayTimer;

    private GraphicManager gm;
    /** manages assigning id to robots. */
    private AgentIDBlink agBlink;
    private List<Agent> agentList;
    /** manages simulation playng. */
    private RealMapPlayer rmp;
    /** timer  */
    private PlayerTimerOnly pto;
    /** window with timer. */
    private Stage timer;
    /** reference to MainForm, used to check if agent action times needs to be adjusted. */
    private MainFormDelegate mfd;
    /** choicebox for available solutions to be payed. */
    private ChoiceBox<SolverProcess> solverChoices;

    /** textield to execute command when start button is pressed */
    private TextField execOnStart;
    /** textield to execute command when stop button is pressed */
    private TextField execOnEnd;
    /** when start button is clicked, perfroms stop button automaticly before start. */
    private CheckBox stopBeforeCall;

    /** task called on starat */
    private Thread startTask;
    /** task called on stop */
    private Thread endTask;

    private ObservableList<SolverProcess> solverOptions;


    public RealMapControll(Pane realMapPane, MapViewSettings mvs, List<Agent> ags, PicatManager pm, MainFormDelegate mfd){
        gm = new GraphicManager(mvs);
        smFront = new SimulationMap(realMapPane,gm);

        displayControllPane = realMapPane;
        agBlink = new AgentIDBlink(realMapPane,gm,ags);
        agBlink.setAfterPlay(this::playAction);
        this.agentList = ags;

        smFront.setDrawMode(SimulationMap.SimulationMode.Soft);
        this.pm = pm;
        this.mfd = mfd;


    }

    /** in case of new display appeared reinints controll for new display */
    public void reinit(Pane realMapPane, MapViewSettings mvs, List<Agent> ags){
        displayControllPane = realMapPane;
        gm = new GraphicManager(mvs);
        smFront = new SimulationMap(realMapPane,gm);
        agBlink = new AgentIDBlink(realMapPane,gm,ags);
        agBlink.setAfterPlay(this::playAction);
        smFront.setDrawMode(SimulationMap.SimulationMode.Soft);
        agentList = ags;

        //reset loaded solutions
        SolverProcess dummySp = SolverProcess.getDummyProcess();
        solverOptions.clear();
        solverOptions.add(dummySp);
        solverOptions.addAll(pm.getSolverProcessNotErrorList());

    }


    public void drawAgentStartPositions(List<Agent> agList){
        displayControllPane.getChildren().clear();
        for (Agent ag : agList){
            Node n = gm.makeCircle(ag.start,ag.color,Consts.mapAgentRealMapStartPosCircleSize);
            gm.moveNodeByCoords(n, Consts.mapLineMove,Consts.mapLineMove );
            gm.moveNodeByCoords(n, Consts.mapAgentRealMapStartPosCircleXOffset,Consts.mapAgentRealMapStartPosCircleYOffset );
            displayControllPane.getChildren().add(n);
        }

    }


    public void show(){
        initWindow();
        s.show();
    }


    /** draws window, and assings code to controls */
    private void initWindow(){
        final Stage dialog = new Stage();
        s = dialog;

        int buttonWidth = 120;
        VBox controlls = new VBox();
        controlls.setSpacing(10);
        controlls.setPadding(new Insets(10));
        Button showStartPoints = new Button("Show Start");
        showStartPoints.setPrefWidth(buttonWidth);

        showStartPoints.setOnAction(a-> {
            drawAgentStartPositions(agentList);
            }
        );

        Button start = new Button("Start");
        start.setPrefWidth(120);

        start.setOnAction(a-> {
            agBlink.invokeBlink();
            //after that, is inited after action (added in constructor and calls playaction
        });

        Button stop = new Button("Stop");

        stop.setOnAction(a-> {

            execEndAction(execOnEnd.getText());

            if (rmp !=null) rmp.stop();
            if (timer!= null && pto != null) {
                pto.stop();
            }

        });
        stop.setPrefWidth(buttonWidth);

        Button clear = new Button("Clear");

        clear.setOnAction(a-> {

            if (rmp !=null) rmp.stop();
            smFront.clear();
            if (timer!= null && pto != null) {
                pto.stop();
                pto.reset();
            }

        });
        clear.setPrefWidth(buttonWidth);


        ChoiceBox<SolverProcess> solutionBox = new ChoiceBox<>();
        solutionBox.setPrefWidth(buttonWidth);
        solverChoices = solutionBox;
        ObservableList<SolverProcess> sol = FXCollections.observableArrayList();
        solverOptions = sol;
        SolverProcess dummySp = SolverProcess.getDummyProcess();

        solutionBox.setItems(sol);
        sol.add(dummySp);
        sol.addAll(pm.getSolverProcessNotErrorList());

        //hook to select files
        solutionBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            logger.debug("selection changed");
            if (newSelection != null  ) {
                SolverProcess sp  = (SolverProcess) newSelection;
                if (sp != null ){
                    agBlink.reinitBlink(sp.agentStartBlinkNumber,sp.getSolution().getAglist());
                }
            }
        });



        Button showTimer = new Button("Timer");

        showTimer.setOnAction(a-> {
            showTimerWindow();

        });
        showTimer.setPrefWidth(buttonWidth);

        execOnStart = new TextField();
        Label execOnStartLbl = new Label(Consts.textExecOnStart);
        execOnEnd = new TextField();
        Label execOnEndLbl = new Label(Consts.textExecOnEnd);
        stopBeforeCall = new CheckBox();
        stopBeforeCall.setText(Consts.callStopBeforeStart);



        controlls.getChildren().add(showStartPoints);
        controlls.getChildren().add(start);
        VBox cb = new VBox();

        cb.getChildren().add(new Label("Solution to play:"));
        cb.getChildren().addAll(solutionBox);
        controlls.getChildren().add(cb);
        //controlls.getChildren().add(start)
        controlls.getChildren().addAll(stop);;
        controlls.getChildren().add(clear);
        controlls.getChildren().add(showTimer);

        VBox vbox2 = new VBox();
        vbox2.getChildren().addAll(execOnStartLbl,execOnStart,execOnEndLbl,execOnEnd,stopBeforeCall);

        controlls.getChildren().addAll(vbox2);

        dialog.setTitle("Real Map Controll");



        Scene scene = new Scene(controlls,180,350);

        s.setOnCloseRequest((WindowEvent event1) -> {
                if (DataStore.rmm != null) {
                    DataStore.rmm.clearControllPane();;
                    DataStore.rmc = null;
                }
                if (pto != null) {
                    pto.stop();
                    pto = null;
                }

                if (endTask != null) { endTask.interrupt(); }
                if (startTask != null) { startTask.interrupt();}
        });

        dialog.setScene(scene);
        s.show();
    }


    /** after blink ends, it initialize this action */
    private void playAction(){

        if (stopBeforeCall.isSelected()) { execEndAction(execOnEnd.getText()); }
        execStartAction(execOnStart.getText());

        //if timer was inicializated
        if (timer != null) {
            if (pto == null) {
                pto = new PlayerTimerOnly(displayTimer);
                pto.play();
            } else {
                pto.stop();
                pto.play();
            }
        }
        //check if play is needed
        SolverProcess sp = solverChoices.getSelectionModel().getSelectedItem();
        if (sp == null || sp.isDummyProcess()){
            return;
        }

         Solution s = sp.getSolution();

        s = mfd.transofrmSolutionTimeIfChecked(s);

        List<List<AgentActionPair>> aapList = Simulator.getAAPfromSolution(s);
        rmp = new RealMapPlayer(aapList,smFront);
        rmp.play();


    }

    /** handles execution of action performed on start  */
    private void execStartAction(String action){

        if (action.length() == 0) { return; }

        if (startTask != null ){
         startTask.interrupt();
         startTask = null;
        }

        RunningTask rt = new RunningTask(this,action.split(" "),true);
        startTask = new Thread(rt);
        startTask.start();
    }


    /** handles execution of action performed on stop  */
    private void execEndAction(String action){

        if (action.length() == 0) { return; }

        if (endTask != null ){
            endTask.interrupt();
            endTask = null;
        }
        RunningTask rt = new RunningTask(this,action.split(" "),false);
        endTask = new Thread(rt);
        endTask.start();
    }


    private void showTimerWindow(){

        if (timer != null) { return;}

        final Stage timerDialog = new Stage();
        Label l = new Label();
        l.setFont(Font.font(21));
        timerDialog.setTitle("Timer");
        StackPane displayTimerPane = new StackPane();
        displayTimerPane.getChildren().add(l);
        Scene stimer = new Scene(displayTimerPane,200,100);
        timerDialog.setScene(stimer);

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->{
                //System.out.println("Height: " + stage.getHeight() + " Width: " + stage.getWidth()
                double Xstretchh = timerDialog.getWidth()/100f;
                double Ystretch = timerDialog.getHeight()/35f;
                l.setScaleX(Xstretchh);
                l.setScaleY(Ystretch);
                };

        timerDialog.widthProperty().addListener(stageSizeListener);
        timerDialog.heightProperty().addListener(stageSizeListener);

        l.setText("0");
        displayTimer = l;

        timer = timerDialog;

        timer.setOnCloseRequest((WindowEvent event1) -> {
            if (pto != null) {
                pto.stop();
                pto = null;
            }
            timer = null;
        });


        timerDialog.show();

    }


    /** code support for calling custom start, stop commands */
    private class RunningTask implements  Runnable{
        RealMapControll rpc;
        String calledProg[];
        boolean isStart;
        public RunningTask(RealMapControll rpc,String[] calledProg,boolean isStart){
            this.rpc = rpc;
            this.calledProg = calledProg;
            this.isStart = isStart;
        }

        @Override
        public void run() {
            runInBackground(calledProg);
            if (isStart) {
                Platform.runLater(() -> rpc.taskStartFinished());
            } else {
                Platform.runLater(() -> rpc.taskEndFinished());
            }
        }
    }

    private void taskStartFinished(){
        startTask = null;
    }

    private void taskEndFinished(){
        endTask = null;
    }


    private  void runInBackground(String[] calledProg){
        try {


            Process proc = Runtime.getRuntime().exec(calledProg);
            InputStream in = proc.getInputStream();

            proc.waitFor();
        } catch (InterruptedException e) {
            logger.error("exectuion interrupted");
            logger.error(e.toString());
        } catch (IOException e){
            logger.error("read write exception");
            logger.error(e.toString());
        }

    }


}
