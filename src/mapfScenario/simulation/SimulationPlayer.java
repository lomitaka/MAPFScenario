package mapfScenario.simulation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/** Simple class that take care about playing simulation */
public class SimulationPlayer {

    /** simulation object that handles playing */
    private Simulator sim;
    private int timeStepMs =100;
    Timeline timeLine;

    public SimulationPlayer(Simulator sim){
        this.sim = sim;
    }



    private boolean isPlaying = false;

    /** initialize play action */
    public void play(){

        if (!isPlaying) {
            isPlaying = true;

            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(timeStepMs),
                    ae -> playStep()));
            timeLine.setCycleCount(Animation.INDEFINITE);
            timeLine.play();
        }


    }
    /** stop playing */
    public void stop(){
        if (timeLine != null){
            timeLine.stop();
            isPlaying = false;
        }
    }



    /** execution one step of playing. */
    private void playStep(){

        double timeCurrent = sim.currentTimeMark;
        //playing end
        if (timeCurrent >= sim.sds.totalTimeLength) {

            sim.timeSliderUpdateByPlayer(0);
            isPlaying = false;
            timeLine.stop();
            return;
         }

        sim.timeSliderUpdateByPlayer(timeCurrent+timeStepMs);


    }

}
