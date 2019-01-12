package mapfScenario.simulation;

import graphics.GraphicManager;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import mapfScenario.Consts;
import mapfScenario.agents.Agent;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

import static mapfScenario.simulation.SimulationMap.SimulationMode.Normal;

/** Takes care about drawing agent positions on the map. Used in simulation in main window,
 * and in simulation on Realmap. For this purpose is added swich Draw mode, Normal  which draws on main form. and
 * Soft  which draws only slightly and is used to show where agent should be in real situation*/
public class SimulationMap {

    /** pane where to draw elements */
    Pane mapPane;
    /** drawing manager */
    GraphicManager gm;
    private final Logger logger = Logger.getLogger(SimulationMap.class);

    /** list for agents, that are shown (when want to emphasize them */
    HashMap<Agent,Node> displayedElements = new HashMap<>();
    /** node which is currently emphasized */
    Node emphasizedActionPairNode = null;

   // private Node emphasizedObject = null;

    public SimulationMap(Pane mapPane, GraphicManager gm){
        this.mapPane = mapPane;
        this.gm = gm;
        this.sm = Normal;

    }


    public void emphasizeAgentPos(Agent a){
        logger.debug("emphasizeAgent: " + a.name);
        if (displayedElements.containsKey(a)){
            logger.debug("emphasizeAgent success");
            Node n = displayedElements.get(a);
            n.setScaleX(1.2f);
            n.setScaleY(1.2f);
        } else {
            logger.debug("emphasizeAgent: nothing found");
        }

    }

    public void unEmphasizeAgentPos(Agent a){
        logger.debug("UNemphasize: " + a.name);
        if (displayedElements.containsKey(a)){
            logger.debug("Unemphasize success");
            Node n = displayedElements.get(a);
            n.setScaleX(1.0f);
            n.setScaleY(1.0f);
        }
    }

    public void emphasizeActionPair(AgentActionPair aap){
        UnEmphasizeActionPair();
        Node n = gm.emphasizeAgentActionPairNode(aap);
        gm.moveNodeByCoords(n,Consts.mapLineMove,Consts.mapLineMove);
        mapPane.getChildren().add(n);
        emphasizedActionPairNode = n;

    }


    public void UnEmphasizeActionPair(){
        if (emphasizedActionPairNode != null){
            mapPane.getChildren().remove(emphasizedActionPairNode);
        }
    }


    public void clear(){
        mapPane.getChildren().clear();
        displayedElements.clear();
        emphasizedActionPairNode = null;
    }

    /** which mode is used to draw agents */
    public enum SimulationMode { Normal, Soft };
    /** current simulation mode */
    private SimulationMode sm;


    public void setDrawMode(SimulationMode sm){
        this.sm = sm;
    }

    /** gets position ifrormation of agents, and time mark, and draw agents on mapPane. */
   public void redrawAgentPositions(List<List<AgentActionPair>> aapList,double timeMark){
        logger.debug("RedrawAgent Position: time mark:" + timeMark);
        mapPane.getChildren().clear();
        displayedElements.clear();
        for (List<AgentActionPair> aaList : aapList){
           // for (int i = 0; i < aaList.size()-1;i++){
               for ( AgentActionPair aap :aaList){
                if (aap.startTimeMark < timeMark &&
                    (aap.startTimeMark+aap.current.duration) >= timeMark ){

                    double transition = (timeMark -  aap.startTimeMark)/ aap.current.duration;
                    Node n = drawPosition(aap,transition);
                    displayedElements.put(aap.agent,n);
                    //gm.moveCircleByCoords(n, Consts.mapLineMove,Consts.mapLineMove );
                    gm.moveNodeByCoords(n, Consts.mapLineMove,Consts.mapLineMove );
                    mapPane.getChildren().add(n);
                }
                continue;
            }
        }
    }

    /** there is used simulation switch to deside how drawed position will look. */
    private Node drawPosition(AgentActionPair aap, double transition){
        switch (sm){
            case Normal:
                return gm.getInterleavePoint(aap,transition);
            case Soft:
                //used in real map
                return gm.getInterleavePointSoft(aap,transition);
        }
        return null;

    }








    //public Node getInterleavePoint(AgentAction act, AgentAction next, float transition){

}
