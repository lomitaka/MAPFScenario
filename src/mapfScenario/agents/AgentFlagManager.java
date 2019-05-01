package mapfScenario.agents;

import graphics.GraphicManager;
import graphics.MVPoint;
import helpout.methods;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import mapfScenario.Consts;
import mapfScenario.mapView.MapView;
import mapfScenario.mapView.MapViewSettings;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

/** Manages displaying, moving removing agent flags ie start and end positions.  */
public class AgentFlagManager {

    private final Logger logger = Logger.getLogger(AgentFlagManager.class);

    /** current settings of environment, used to determine flag position */
    MapViewSettings mvs;
    /** pane that is used to draw flag */
    Pane flagPane;
    /** Used to draw graphic elements */
    GraphicManager gm;

    /** used to index which agent which Node currently uses (for start positions) */
    HashMap<Agent,Node> startFlagsNodes;
    /** used to index which agent which Node currently uses (for end positions)*/
    HashMap<Agent,Node> endFlagsNodes;

    /** used to index which agent currently which node uses (start position) */
    HashMap<Agent, MVPoint> startFlagsMVPoints;
    /*used to index which agent currently which node uses (end positions) */
    HashMap<Agent,MVPoint> endFlagsMVPoints;

    public AgentFlagManager(Pane basePane, MapViewSettings mvs){
        flagPane = basePane;
        this.mvs = mvs;
        gm = new GraphicManager(mvs);
        startFlagsNodes = new HashMap<>();
        endFlagsNodes = new HashMap<>();

        startFlagsMVPoints = new HashMap<>();
        endFlagsMVPoints = new HashMap<>();
    }

    /** tests if current point is already occupied by some start flag */
    public boolean canSetStartFlagThere(MVPoint a){
        logger.debug(String.format("Testing set start flag (%d %d)",a.x,a.y));

        if (!MapView.isInGridAndOnGridCross(a,mvs.getSizeXRefined(),mvs.getSizeYRefined())){
            logger.warn("clicked not on cross position");
            return false;
        }

        if (startFlagsMVPoints.values().contains(a)){
            logger.warn("setting flag on already filled position not possible");
            methods.showDialog(Consts.warningPointForFlagAlredyTaken);
            return false;
        }

        return  true;
    }

    /** tests if current point is already occupied by some end flag */
    public boolean canSetEndFlagThere(MVPoint a){
        logger.debug(String.format("Testing set end flag (%d %d)",a.x,a.y));

        if (!MapView.isInGridAndOnGridCross(a,mvs.getSizeXRefined(),mvs.getSizeYRefined())){
            logger.warn("clicked not on cross position");
            return false;
        }

        if (endFlagsMVPoints.values().contains(a)){
            logger.warn("setting flag on already filled position not possible");
            methods.showDialog(Consts.warningPointForFlagAlredyTaken);
            return false;
        }

        return  true;
    }

    /** Agent a, currently have set start position, sets agnet start position on pane */
    public void addStartFlag(Agent a){


        if (startFlagsNodes.containsKey(a)) {
            logger.debug("Start Flag Already defined, setting new");
            //cannot simply remove flag, there flag already is
            removeStartFlagUns(a);
            setStartFlag(a);


        } else {
            logger.debug("Setting new start flag");
            setStartFlag(a);

        }

    }


    /** Agent a, currently have set end position, sets agnet end position on pane */
    public void addEndFlag(Agent a){
        logger.debug("Adding end flag");

        if (endFlagsNodes.containsKey(a)) {
            //cannot simply remove flag, there flag already is
            logger.debug("End Flag Already defined, setting new");
            removeEndFlagUns(a);
            setEndFlag(a);

        } else {
            //setting new flag agent
            logger.debug("Setting new end flag");
            setEndFlag(a);

        }

    }

    public void removeStartFlag(Agent a){
        if (startFlagsNodes.containsKey(a)){
            removeStartFlagUns(a);
        }
    }

    public void removeEndFlag(Agent a){
        if (endFlagsNodes.containsKey(a)){
            removeEndFlagUns(a);
        }
    }

    /* unsafe operations. simple removes, do not check if Node exist */
    private void removeStartFlagUns(Agent a){
        Node n = startFlagsNodes.get(a);
        startFlagsNodes.remove(n);
        startFlagsMVPoints.remove(a);
        flagPane.getChildren().remove(n);

    }
    /* unsafe operations. simple removes, do not check if Node exist */
    private void removeEndFlagUns(Agent a){
        Node n = endFlagsNodes.get(a);
        endFlagsNodes.remove(n);
        endFlagsMVPoints.remove(a);
        flagPane.getChildren().remove(n);
    }

    private void setStartFlag(Agent a){
        //creates flag at given cords
        Node img = gm.makeFlag(a.start,a.color,GraphicManager.FlagType.Start);
        //moves flag of the topleft corner by contst
        gm.moveImgViewByCoords(img, Consts.mapLineMove,Consts.mapLineMove );
        // move flag off the grid
        gm.moveImgViewByCoords(img, Consts.mapFlagOffsetStartX,Consts.mapFlagOffsetStartY);
        //adjust star flag position
        gm.moveImgViewByPixels(img,(int)(-mvs.edgeLength*Consts.mapFlagCircleRadius),(int)(-mvs.edgeLength*Consts.mapFlagCircleRadius) );
        startFlagsNodes.put(a,img);
        flagPane.getChildren().add(img);

        startFlagsMVPoints.put(a,a.start);

    }

    private void setEndFlag(Agent a){
        //creates flag at given cords
        Node img = gm.makeFlag(a.end,a.color,GraphicManager.FlagType.End);
        //moves flag of the topleft corner by contst
        gm.moveImgViewByCoords(img, Consts.mapLineMove,Consts.mapLineMove );
        // move flag off the grid
        gm.moveImgViewByCoords(img,Consts.mapFlagOffsetFinishX,Consts.mapFlagOffsetFinishY);
        // adjust end position of the flag
        //gm.moveImgViewByPixels(img,0,-Consts.mapFlagFlagHeight );
        gm.moveImgViewByPixels(img,(int)(-mvs.edgeLength*Consts.mapFlagCircleRadius),(int)(-mvs.edgeLength*Consts.mapFlagCircleRadius) );
        endFlagsNodes.put(a,img);
        flagPane.getChildren().add(img);

        endFlagsMVPoints.put(a,a.end);
    }

    /** removes every flag */
    public void clearFlags(){
        flagPane.getChildren().clear();
        startFlagsNodes.clear();
        endFlagsNodes.clear();
        startFlagsMVPoints.clear();
        endFlagsMVPoints.clear();
    }

    /** set every flag for each agent */
    public void setFlags(List<Agent> aglist){
        clearFlags();
        for (Agent a : aglist){
            if (canSetStartFlagThere(a.start)) {
                addStartFlag(a);
            } else {
                logger.warn(String.format("wrong agent '%s' start position %s ",a.name,a.end.toString()));
            }

            if (canSetEndFlagThere(a.end)) {
                addEndFlag(a);
            } else {
                logger.warn(String.format("wrong agent '%s' end position %s ",a.name,a.end.toString()));
            }

        }
    }

}
