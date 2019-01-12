package mapfScenario.realMap;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;
import mapfScenario.Consts;
import mapfScenario.simulation.AgentActionPair;
import mapfScenario.simulation.SimulationMap;
import mapfScenario.simulation.Simulator;
import org.apache.log4j.Logger;

import java.util.List;

/** represent timer. which displays current time.  */
public class PlayerTimerOnly {
    private final Logger logger = Logger.getLogger(PlayerTimerOnly.class);

        private int timeStepMs =100;

        Timeline timeLine;
        Label timerLabel;

        int currentTimeMark = 0;

        public PlayerTimerOnly(Label timerLabel){
            this.timerLabel = timerLabel;
        }

        private boolean isPlaying = false;

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

        public void stop(){
            logger.debug("Stop");
            if (timeLine != null){
                timeLine.stop();
                isPlaying = false;
                currentTimeMark = 0;
            }
        }


        private void playStep(){
            //double timeCurrent = sim.currentTimeMark;
            currentTimeMark+= timeStepMs;
            timerLabel.setText(String.format(java.util.Locale.US, Consts.timerFormatString,currentTimeMark/1000f));
            //playing end
            /*if (currentTimeMark >= ) {

                currentTimeMark = 0;
                isPlaying = false;
                timeLine.stop();
                sm.clear();
                return;
            }*/
            logger.debug("timer step position");
           //  sm.redrawAgentPositions(aap,currentTimeMark);

        }




    public void reset(){
        //double timeCurrent = sim.currentTimeMark;
        currentTimeMark = 0;
        timerLabel.setText(String.format(java.util.Locale.US, Consts.timerFormatString,currentTimeMark/1000f));

        logger.debug("timer reset");
        //  sm.redrawAgentPositions(aap,currentTimeMark);

    }



}
