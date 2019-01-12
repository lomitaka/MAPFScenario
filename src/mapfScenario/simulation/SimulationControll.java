package mapfScenario.simulation;

import graphics.GraphicSimulationManager;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import mapfScenario.Consts;
import mapfScenario.agents.Agent;
import org.apache.log4j.Logger;

import java.util.List;
/**
 *  Draws simulation controlls, displays each agent on botom panel and draws timeline for each action.
 * */
public class SimulationControll {

    private final Logger logger = Logger.getLogger(SimulationControll.class);

    /** pane to display agnet actions on timeline */
    private Pane controllPane;
    /** pane to display agents and their colors on left side */
    private Pane agentPane;
    /** drawed time line on top of agent actions  */
    private Pane timeMarkPane;
    /** display settings for simulation controll */
    private SimulatorDisplaySettings sds;
    /** class managing elements drawn in simulation controll */
    private GraphicSimulationManager gsm;
    /** managing drawing elements in mMpView. */
    private SimulationMap simulationMap;
    /** Main class that takes care of simulation */
    private Simulator simulator;



    public SimulationControll(Pane p, SimulatorDisplaySettings sds,Pane timeMarkPane,Pane agentPane,SimulationMap sm,Simulator sim){
        controllPane = p;
        this.sds = sds;
        this.timeMarkPane = timeMarkPane;
        this.gsm = new GraphicSimulationManager(sds);
        this.agentPane = agentPane;
        this.simulationMap = sm;
        this.simulator = sim;

    }

    Node timeMark = null;



    /** repopulate agnets and agentActions with new sizes */
    public void redrawPane(List<List<AgentActionPair>> aapList, List<Agent> agList){
        int agentCount = agList.size();

        /** populate agents info */
        agentPane.getChildren().clear();
        VBox agentInfoBox = new VBox();


        double prefWidth = agentPane.getWidth();
        Node timeLine  = gsm.makeInfoText(Consts.timeLine,  prefWidth);
        agentInfoBox.getChildren().add(timeLine);
        for (Agent a : agList){

            Node n = gsm.makeInfoAgent(a,prefWidth );
            n.setUserData(a);
            addAgentsMouseListeners(n,simulationMap);
            agentInfoBox.getChildren().add(n);
        }
        agentPane.getChildren().add(agentInfoBox);
        controllPane.getChildren().clear();



        /* populate agent actions */
        VBox agentBox = new VBox();

        Node timeLineNode = gsm.drawTimeLine(sds.totalTimeLength);
        addInnterTimeLineListener(timeLineNode);
        agentBox.getChildren().add(timeLineNode);
         for(List<AgentActionPair> agentPath : aapList){
            HBox agentI = new HBox();
             for (AgentActionPair aap: agentPath){
                Node actionNode = gsm.makeControlAgentAction(aap.current);
                actionNode.setUserData(aap);
                addAgentActionMouseListeners(actionNode,simulationMap);
                agentI.getChildren().add(actionNode);
            }
            agentBox.getChildren().add(agentI);
        }

        controllPane.getChildren().add(agentBox);

    }


    /** hangs listener on entering upper timeline, and clicking and moving it.
     * By entering disables update botom slider. ( and avoids update cascade) */
    private void addInnterTimeLineListener(Node n){
       /* n.addEventFilter(MouseEvent.MOUSE_CLICKED, e-> {
            drawTimeMark(e.getX()/sds.constrollStretch);
        });*/

        n.addEventFilter(MouseEvent.MOUSE_ENTERED, e-> {
                simulator.doScrollUPdate = false;
                });
        n.addEventFilter(MouseEvent.ANY, e-> {
            logger.debug("TimeLine MouseMoving");
            logger.debug(e.getButton().toString());
            if (e.getButton()== MouseButton.PRIMARY) {

                drawTimeMark(e.getX() / sds.constrollStretch);
                double time = e.getX()/sds.constrollStretch;
                simulator.timeSliderUpdateInnerTimeLine(time);
            }
        });
    }

    /** when mouse enters over agent box, emphasize agent on map */
    private void addAgentsMouseListeners(Node n,SimulationMap sm){
        n.addEventFilter(MouseEvent.MOUSE_ENTERED, e-> {
            logger.debug("Agent Mouse entered");

            Agent a = (Agent) n.getUserData();
            sm.emphasizeAgentPos(a);

            });
        n.addEventFilter(MouseEvent.MOUSE_EXITED, e-> {
            logger.debug("Agent Mouse exited");
            Agent a = (Agent) n.getUserData();
            sm.unEmphasizeAgentPos(a);

        });
    }

    private  void addAgentActionMouseListeners(Node n,SimulationMap sm){
        n.addEventFilter(MouseEvent.MOUSE_ENTERED, e-> {
            logger.debug("Agent Action Mouse entered");
            AgentActionPair aap = (AgentActionPair)n.getUserData();
            sm.emphasizeActionPair(aap);

        });
        n.addEventFilter(MouseEvent.MOUSE_EXITED, e-> {
            logger.debug("Agent Action Mouse exited");
            //AgentActionPair aap = (AgentActionPair) n.getUserData();
            sm.UnEmphasizeActionPair();
        });
    }

    /** if first draws node otherwise updates */
    public void drawTimeMark(double time) {
        if (timeMark == null) {
            timeMarkPane.getChildren().clear();
            Node timeMark = gsm.drawTimeMark(time);
            this.timeMark = timeMark;
            timeMarkPane.getChildren().add(timeMark);
        } else {
            gsm.updateTimeMark(timeMark,time);
        }

    }

    public void clear(){
         controllPane.getChildren().clear();
         agentPane.getChildren().clear();
         timeMarkPane.getChildren().clear();
    }



}
