package mapfScenario.simulation;

import graphics.GraphicManager;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import mapfScenario.Consts;
import mapfScenario.mapView.MapViewSettings;
import mapfScenario.agents.Agent;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/** Main Simulation class there are generated simulations from Solutions. Handles controlls and sliders.
 * Use support classes for drawing on MapView and drawing timeline (SimulationControll).
 * */
public class Simulator {

    private final Logger logger = Logger.getLogger(Simulator.class);
    /** used to show results on map */
    Pane mapPane;
    /** top time line */
    Pane timeMarkPane;
    /** pane to draw agents ( bottom left) */
    Pane agentPane;


    /* used to draw timeline with agent actions. */
    Pane controlPane;
    /** used to draw agent paths.  */
    Pane infoMapPane;

    Slider timeSlider;
    Slider scaleSlider;
    Slider pathDisplaySlider;
    /** pane in which is timeline with agent aciton enclosed */
    ScrollPane scrollPane;
    /** settings of current timeline controls */
    SimulatorDisplaySettings sds = new SimulatorDisplaySettings();

    /** currently loaded solution */
    //Solution solution;

    /** time in which current simulation is */
    double currentTimeMark =0;
    /** value indicating how long path to display before agent and after agent */
    int displaySliderRangeValue = 0;

    //current simulation data
    List<List<AgentActionPair>> aapList;
    List<Agent> agList;

    SimulationControll sc;
    SimulationMap sm;
    SimulationMapPath smp;
    SimulationPlayer sp;



    /** drawed elements */

    /** enables or disables update of bottom time slider. ( have two sliders, when update one second is updated too,
     * and update of second updates first. and to avoid that that variable is used.  )  */
    public boolean doScrollUPdate = true;

    public Simulator(Slider slider, Pane mapPane, Pane controlPane, Slider scaleSlider,
                     Pane timeMarkPane, ScrollPane scrollPane, MapViewSettings mvs,Pane agentPane,
                    Slider pathDisplaySlider,Pane infoMapPane){
        this.timeSlider = slider;
        this.mapPane = mapPane;
        this.controlPane = controlPane;
        this.scaleSlider = scaleSlider;
        this.scrollPane = scrollPane;
        this.pathDisplaySlider = pathDisplaySlider;
        this.infoMapPane = infoMapPane;
        this.timeMarkPane = timeMarkPane;
        this.agentPane = agentPane;

        /** needs to be performed only once */
        initSliders();
        disableSliders();
    }



    public void loadSolution(Solution s,MapViewSettings mvs){


        aapList = getAAPfromSolution(s);
        agList = s.agentList;

        /** initialize structures */
        sds = new SimulatorDisplaySettings();
        sds.agentCount = s.solutionData.size();
        sds.agentLineHeight = Consts.controllPaneAgentLineHeight;
        sds.totalTimeLength = getLongestSolution(aapList);
        sds.actionCount  = getStepCount( aapList);
        sds.constrollStretch = 0.1f;

        GraphicManager gm = new GraphicManager(mvs);
        sm = new SimulationMap(mapPane,gm);
        sc = new SimulationControll(controlPane,sds,timeMarkPane,agentPane,sm,this);
        smp = new SimulationMapPath(infoMapPane,sds,gm);
        sp = new SimulationPlayer(this);
        timeSlider.setValue(0);


        enableSliders();


        sc.redrawPane(aapList,agList);
        reloadStretchSliderBoundaries();
        reloadTimeSlider();
        reloadDisplayPathSlider();

        //hook to always redrwas slider
        scaleSliderChange();

    }

    public void clearSolution(){

        disableSliders();
        aapList = null;
        agList = null;
        sds = null;
        if (smp != null) {
            smp.clear();
            smp = null;
        }
        if (sm != null){
            sm.clear();
            sm = null;

        }
        if (sc != null) {
            sc.clear();
            sc = null;
        }


    }



    public void initSliders(){
        timeSlider.setValue(0);

        scaleSlider.valueProperty().addListener(a -> {
            logger.debug("scaleSlider drag ended");
            scaleSliderChange();
        });

        timeSlider.addEventFilter(MouseEvent.MOUSE_ENTERED, e-> {
            doScrollUPdate = true;
        });

        timeSlider.valueProperty().addListener(a -> {
            logger.debug("timeSlider changed");
            timeSliderUpdateMainTimeLine(doScrollUPdate);

        });

        scrollPane.widthProperty().addListener(a-> {
            if (aapList != null && agList != null) {
                reloadStretchSliderBoundaries();
            }
            //reloadTimeSlider(scrollPane.getWidth());
        });

        pathDisplaySlider.valueProperty().addListener(a->{
            if (aapList != null && agList != null) {
                displaySliderRangeValue = (int)pathDisplaySlider.getValue();
                logger.debug("pathDisplaySlider changed done");
                timeSliderUpdateMainTimeLine(false);
                logger.debug(String.format("pathDisplayValue:(ms) %d",displaySliderRangeValue));
            }
        });

    }

    public void scaleSliderChange(){
        //sds.constrollStretch = scaleSlider.getValue()
        sds.constrollStretch = Math.floor(scaleSlider.getValue()*1000)/1000f;

        sc.redrawPane(aapList,agList);
        sc.drawTimeMark(currentTimeMark);
    }
    /** updated via timeLine */
    public void timeSliderUpdateByPlayer(double updateTime){
        logger.debug("time slider update by play");

        doScrollUPdate = true;
        currentTimeMark = Math.floor(updateTime*1000)/1000f;//timeSlider.getValue();
        //rest is handled by timeSliderUpdate
        timeSlider.setValue(currentTimeMark);

    }
    /** updated via timeLine */
    public void timeSliderUpdateInnerTimeLine(double updateTime){
        logger.debug("time slider update inner time");
        currentTimeMark = Math.floor(updateTime*1000)/1000f;//timeSlider.getValue();
        timeSlider.setValue(currentTimeMark);
    }

    /** action performed on change time slider */
    public void timeSliderUpdateMainTimeLine(boolean scrollUpdate){
        logger.debug("time slider update main time");

        currentTimeMark = Math.floor(timeSlider.getValue()*1000)/1000f;//timeSlider.getValue();
        logger.debug(String.format("CurrentTime: %f", currentTimeMark));
        if (scrollUpdate) {
            updateInnerSrollPane(currentTimeMark);
        }

        sc.drawTimeMark(currentTimeMark);
        sm.redrawAgentPositions(aapList, currentTimeMark);
        smp.displayPath(aapList,displaySliderRangeValue,(int)currentTimeMark);
    }


    private void disableSliders(){
        timeSlider.setDisable(true);
        scaleSlider.setDisable(true);
        pathDisplaySlider.setDisable(true);
    }

    private void enableSliders(){
        timeSlider.setDisable(false);
        scaleSlider.setDisable(false);
        pathDisplaySlider.setDisable(false);
    }

    /** gets AgentActionPairs ie. agent actions from solution. */
    public static List<List<AgentActionPair>> getAAPfromSolution(Solution s ){
        List<List<AgentActionPair>> result = new ArrayList<>();


        for (int agi =0; agi < s.solutionData.size();agi++){
            List<AgentAction> aaList = s.solutionData.get(agi);
            List<AgentActionPair> agResult = new ArrayList<>();
            Agent a = s.getAglist().get(agi);
            int timeMark = 0;
            for (int i = 0; i < aaList.size()-1;i++){
                AgentAction current = aaList.get(i);
                AgentAction next = aaList.get(i+1);
                AgentActionPair aap = new AgentActionPair(current,next,a,timeMark);
                timeMark += current.duration;
                agResult.add(aap);
            }
            // adding last element
            AgentAction last = aaList.get(aaList.size()-1);
            AgentActionPair aap = new AgentActionPair(last,last,a,timeMark);
            agResult.add(aap);
            result.add(agResult);
        }
        return result;
    }

    private void reloadStretchSliderBoundaries(){
        scaleSlider.setDisable(false);
        int solutionMaxLen = sds.totalTimeLength;
        double controlWidth = scrollPane.widthProperty().doubleValue()-2;
        //double controlWidth = DataStore.mainWindow.getWidth()+Consts.controllPaneWidthAdjust; //controlPane.getWidth();

        double sliderMinScale = controlWidth / solutionMaxLen;
        if (sliderMinScale > 1) { sliderMinScale = 1;}
        double sliderMaxScale = 0.1f;
        if (sliderMinScale > sliderMaxScale) { sliderMinScale = sliderMaxScale; scaleSlider.setDisable(true); }
        scaleSlider.setMin(sliderMinScale);
        scaleSlider.setMax(sliderMaxScale);
        scaleSlider.setValue(sliderMinScale);
        sds.constrollStretch = sliderMinScale;
        scaleSlider.setBlockIncrement((sliderMinScale + sliderMaxScale)/50);
        logger.debug(String.format("scaleSlider min %f max %f ",sliderMinScale,sliderMaxScale));

    }

    public void reloadTimeSlider(){
        timeSlider.setMin(0);
        timeSlider.setMax(sds.totalTimeLength);
        timeSlider.setValue(0);
        timeSlider.setBlockIncrement(sds.totalTimeLength/sds.actionCount);
    }

    public void reloadDisplayPathSlider(){
        pathDisplaySlider.setMin(0);
        pathDisplaySlider.setMax(sds.totalTimeLength);
        pathDisplaySlider.setValue(0);
        pathDisplaySlider.setBlockIncrement(sds.actionCount/2+2);

    }

    private void updateInnerSrollPane(double currentTime){
        double max = timeSlider.getMax();
        scrollPane.setHvalue(currentTime/max);
    }





    public void buttonPlayAction(){
        logger.debug("play button pressed");
        if (agList != null) {
            sp.play();
        }
    }

    public void buttonStopAction(){
        logger.debug("stop button pressed");
        if (agList != null) {
            sp.stop();
        }
    }


    /** compute time of the longest path */
    public static int getLongestSolution(List<List<AgentActionPair>> aapList){
        int maxSolutionLength = 0;
        for (List<AgentActionPair> ls : aapList){
            if (ls.size()==0) { continue;}
            AgentActionPair aap = ls.get(ls.size()-1);
            if (maxSolutionLength < aap.startTimeMark+aap.current.duration){
                maxSolutionLength = aap.startTimeMark+aap.current.duration;
            }
        }
        //logger.debug(String.format("Got maxSolutionLength %s", maxSolutionLength));
        return maxSolutionLength;
    }

    /** gets max action count of agent paths.  */
    public int getStepCount(List<List<AgentActionPair>> aapList){
        int maxActionCount = 0;
        for (List<AgentActionPair> ls : aapList){
            if (ls.size()==0) { continue;}

            if (maxActionCount < ls.size()){
                maxActionCount = ls.size();
            }
        }
        logger.debug(String.format("Got maxSolutionLength %s", maxActionCount));
        return maxActionCount;
    }

    public int getMakeSpan() {
        return sds.totalTimeLength ;
    }
}
