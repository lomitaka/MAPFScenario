package mapfScenario.mapView;

import graphics.MVPoint;
import javafx.scene.input.MouseEvent;
import mapfScenario.Consts;
import mapfScenario.MainFormDelegate;
import mapfScenario.agents.Agent;
import mapfScenario.agents.AgentManager;
import org.apache.log4j.Logger;

/** Class containing state logic for mapView click events.  */
public class MapViewState {

    private final Logger logger = Logger.getLogger(MapViewState.class);

    /** Action Interface */
    public static interface AbstractMapHandler{
        void performAction(MouseEvent event);
        //public void setMapView(MapView mv);
        void cancelAction();
    }

    /** state when nothing happens */
   public static class  NullMapHandler implements  AbstractMapHandler{
        private final Logger logger = Logger.getLogger(NullMapHandler.class);


        @Override
        public void performAction(MouseEvent event) {
            logger.debug(String.format("Null MapHandler: X: %f, Y: %f", event.getX(),event.getY() ));
        }

        @Override
        public void cancelAction() {
            logger.debug("Cancel called on nullhandler");
        }


    }



    /** state when new obstacle will be added by clicking on map */
    public static class ObstacleAddMapHandler implements  AbstractMapHandler{
        private final Logger logger = Logger.getLogger(ObstacleAddMapHandler.class);
        MapView mv;

        public ObstacleAddMapHandler(MapView mv) {
            this.mv = mv;
        }
        @Override
        public void performAction(MouseEvent event) {
            logger.debug(String.format("ObstacleAddMapHandler: X: %f, Y: %f", event.getX(),event.getY() ));
            mv.addObstacleRequest((float)event.getX(),(float)event.getY());
        }

        @Override
        public void cancelAction() {
            logger.debug("Cancel called on obstacel add");
        }
    }




    /** state when obstacle shall be removed */
    public static class ObstacleRemoveMapHandler implements  AbstractMapHandler{
        private final Logger logger = Logger.getLogger(ObstacleRemoveMapHandler.class);
        MapView mv;

        public ObstacleRemoveMapHandler(MapView mv) {
            this.mv = mv;
        }

        @Override
        public void performAction(MouseEvent event) {
            logger.debug(String.format("ObstacleRemoveMapHandler: X: %f, Y: %f", event.getX(),event.getY() ));
            mv.removeObstacleRequest((float)event.getX(),(float)event.getY());
        }
        @Override
        public void cancelAction() {
            logger.debug("Cancel called on obstacle remove");
        }
    }



/** state when new start point of agent will be set. */
    public static class AgentSetStartMapHandler implements MapViewState.AbstractMapHandler {
        private final Logger logger = Logger.getLogger(AgentSetStartMapHandler.class);
        MapView mv;
        Agent a;
        AgentManager agmag;
        MainFormDelegate mfd;
        boolean callSetAgentEnd;

        public AgentSetStartMapHandler(Agent a,  MapView mv,AgentManager agmag,  boolean callSetAgentEnd,MainFormDelegate mfd){
            this.a = a;
            this.mv = mv;
            this.agmag = agmag;
            this.callSetAgentEnd = callSetAgentEnd;
            this.mfd = mfd;
            mv.setInfoLabel(Consts.setAgentStartText);
        }
        @Override
        public void performAction(MouseEvent event) {
            logger.debug(String.format("Agent Set Start on pos: X: %f, Y: %f", event.getX(),event.getY() ));

            MVPoint cord = mv.translateScrCoordsToMVPoint((float)event.getX(),(float)event.getY());

            if (cord == null){ logger.debug("invalid location clicked"); return;  }

            mv.setInfoLabel("");
            boolean success = agmag.setStartFlag(a,cord);

            if (success){
                if (callSetAgentEnd) {
                    logger.debug("change state to AgentSetEndMapHandler");
                    mv.mapActionState = new AgentSetEndMapHandler(a,mv,agmag,mfd);
                } else {
                    logger.debug("change state to NullMapHandler");
                    mv.mapActionState = new NullMapHandler();
                }
            } else {
                logger.debug("failed to set flag, try again");
            }
        }

    @Override
    public void cancelAction() {
        logger.debug("Cancel called on agentsetstart");
        mv.setInfoLabel("");
        mfd.UnlockButtons();
    }
    }

/** state when end point of agent will be set */
    public static class AgentSetEndMapHandler implements MapViewState.AbstractMapHandler {
        private final Logger logger = Logger.getLogger(AgentSetStartMapHandler.class);
        MapView mv;
        Agent a;
        MainFormDelegate mfd;
        AgentManager agmag;
        public AgentSetEndMapHandler(Agent a,  MapView mv,AgentManager agmag,MainFormDelegate mfd){
            this.a = a;
            this.mv = mv;
            this.agmag = agmag;
            this.mfd = mfd;
            mv.setInfoLabel(Consts.setAgentEndText);
        }
        @Override
        public void performAction(MouseEvent event) {
            logger.debug(String.format("Agent Set End on pos: X: %f, Y: %f", event.getX(),event.getY() ));

            MVPoint cord = mv.translateScrCoordsToMVPoint((float)event.getX(),(float)event.getY());

            if (cord == null){ logger.debug("invalid location clicked"); return;  }

            //agent.end = cord;
            boolean success = agmag.setEndFlag(a,cord);
            // NEEDED 1. Add position inside of agent
            if (success) {
                logger.debug("change state to NullMapHandler");
                mv.mapActionState = new NullMapHandler();
                mv.setInfoLabel("");
                mfd.UnlockButtons();
            }else  {
                logger.debug("failed to set flag, try again");
            }
            // NEEDED 2. Add position to the map.
        }

        @Override
        public void cancelAction() {
            logger.debug("Cancel called on nullhandleragent set end");
            mv.setInfoLabel("");
            mfd.UnlockButtons();

        }

    }
}
