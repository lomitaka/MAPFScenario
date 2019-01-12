package mapfScenario.realMap;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import mapfScenario.Consts;
import mapfScenario.simulation.AgentActionPair;
import mapfScenario.simulation.SimulationMap;
import mapfScenario.simulation.Simulator;
import org.apache.log4j.Logger;

import java.util.List;

/** simple class, which contains timer, and in some time interval redraws theoretic agent positions on realMap */
public class RealMapPlayer {
    private final Logger logger = Logger.getLogger(RealMapPlayer.class);

        private int timeStepMs =Consts.realMapPlayerTimeStep;
        SimulationMap sm;
        Timeline timeLine;
        List<List<AgentActionPair>> aap;

        int currentTimeMark = 0;
        int totalTimeLength = 0;


        public RealMapPlayer (List<List<AgentActionPair>> aap,SimulationMap sm){
            this.sm = sm;
            this.aap = aap;

            totalTimeLength = Simulator.getLongestSolution(aap);
        }



        private boolean isPlaying = false;

        /** initializes playing sequence */
        public void play(){
            logger.debug("Play");
            currentTimeMark = 0;
            if (!isPlaying) {
                isPlaying = true;

                timeLine = new Timeline(new KeyFrame(
                        Duration.millis(timeStepMs),
                        ae -> playStep()));
                timeLine.setCycleCount(Animation.INDEFINITE);
                timeLine.play();
            }


        }

        /** stop playing sequence */
        public void stop(){
            logger.debug("Stop");
            if (timeLine != null){
                timeLine.stop();
                isPlaying = false;
            }
        }



        /** performs one playing step. */
        private void playStep(){
            logger.debug("Play Step");
            //double timeCurrent = sim.currentTimeMark;
            currentTimeMark+= timeStepMs;
            //playing end
            if (currentTimeMark >= totalTimeLength) {

                currentTimeMark = 0;
                isPlaying = false;
                timeLine.stop();
                sm.clear();
                return;
            }
            logger.debug("Redrawing position");
             sm.redrawAgentPositions(aap,currentTimeMark);

        }



}
