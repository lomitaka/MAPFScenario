package mapfScenario.realMap;

import graphics.GraphicManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import mapfScenario.Consts;
import mapfScenario.agents.Agent;

import java.util.ArrayList;
import java.util.List;

/** handles sequence of blick, which tells each robot, from real map, which id they have.
 * Each agent has its ID, which tells what path he have to follow. AgentIDBlink passes that information from display
 * screen to Agent.
 * */
public class AgentIDBlink {

    private boolean isPlaying = false;

    /** timer like class. generates tick events */
    private Timeline timeLine;
    /** pane when used to draw color spots for agents */
    private Pane drawPane;
    /** how long one color will blick before another will be shown. robot can recognize min 45. but 200 is ok. */
    private int timeStepMs = 200;
    /** represents number of color to blink */
    private int finalStep;

    private List<Agent> agentList;
    /** for each agent list of it color.  One list of color represents one agent ID, which is number encoded
     * in ternary logic, where Red = 0, Green = 1, Blue = 2.  */
    private List<List<Color>> codeColors;

    private GraphicManager gm;


    public AgentIDBlink(Pane drawPane, GraphicManager gm,List<Agent> agents){

        this.drawPane = drawPane;
        agentList = agents;
        codeColors = getColorsFromNums(agents.size(),0);
        finalStep = codeColors.get(0).size();
        this.gm = gm;



    }

    /** initializes new blick sequence.  */
    public void reinitBlink(int initialNumber, List<Agent> agentCount){
        this.initialNumber = initialNumber;
        agentList = agentCount;
        codeColors = getColorsFromNums(agentCount.size(),initialNumber);
        finalStep = codeColors.get(0).size();
    }


    private int initialNumber = 0;
    public void setInitialNumber(int initialNumber){
        this.initialNumber = initialNumber;
    }

    /** number of color that will be blinked next */
    private int blinkStepNum;

    /** initializes blinkin sequence */
    public void invokeBlink(){

        if (!isPlaying) {
            blinkStepNum = 0;
            isPlaying = true;

            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(timeStepMs),
                    ae -> playStep()));
            timeLine.setCycleCount(Animation.INDEFINITE);
            timeLine.play();
        }


    }


    /** performs one blink step */
    private void playStep(){

       drawPane.getChildren().clear();
        //playing end
        if (blinkStepNum >= finalStep) {
            isPlaying = false;
            timeLine.stop();
            //runs action after play
            if (afterPlay!= null){
                afterPlay.run();
            }

            return;
        }

        for (int i =0 ; i < agentList.size();i++) {
            Agent a = agentList.get(i);
            Color c = codeColors.get(i).get(blinkStepNum);
            Node n = gm.makeCircle(a.start,c,Consts.mapAgentRealMapStartPosCircleSize);
            gm.moveNodeByCoords(n, Consts.mapLineMove,Consts.mapLineMove );
            gm.moveNodeByCoords(n,Consts.mapAgentRealMapStartPosCircleXOffset,Consts.mapAgentRealMapStartPosCircleYOffset);


            drawPane.getChildren().add(n);
        }


        blinkStepNum++;

    }

    /** represents action that will be called after play finishes. */
    private Runnable afterPlay;

    /** sets acton that will be performed after blinking */
    public void setAfterPlay(Runnable action){
        afterPlay = action;
    }

  /*  public void blinkRobotID(List<Agent> agList){
        displayControllPane.getChildren().clear();
        for (Agent ag : agList){
            Node n = gm.makeCircle(ag.start,);
            gm.moveNodeByCoords(n, Consts.mapLineMove,Consts.mapLineMove );
            displayControllPane.getChildren().add(n);
        }

    }*/


    /** for each agent creates list of colors, which represnts numbers, and starting from initial number
     *  As first color is added white, which indicates that color sequence is started and ended with black, which
     *  indicates that color sequence ended.
     *  */
    public static List<List<Color>> getColorsFromNums(int agentCount,int initialNumber){

        // The smallest .. to the biggest
        List<List<Color>> resultReverse = new ArrayList<>();

        for (int i = initialNumber ; i < initialNumber+agentCount;i++){
            List<Color> current = new ArrayList<>();
            int num = i;
            if (num ==0) { current.add(numToColor(0)); }
            while (num >0) {
                current.add(numToColor(num % 3));
                num = num /3;
            }
            resultReverse.add(current);
        }
        // get max length
        int maxLen =0;
        for (List<Color> c: resultReverse){ if (c.size() > maxLen){maxLen = c.size();}}


        List<List<Color>> result = new ArrayList<>();
        for (int i = 0 ; i < agentCount;i++){
            List<Color> currRev = resultReverse.get(i);
            List<Color> current = new ArrayList<>();
            while (currRev.size() <= maxLen){currRev.add(Color.color(1,1,1));}

            Color last = Color.color(1,1,1);
            for(int j = currRev.size()-1; j >= 0;j--){
                //if next color is same as the one before, make next white
                if (last.equals(currRev.get(j))) {
                    current.add(Color.color(1,1,1));
                    last = Color.color(1,1,1);
                }else {
                    current.add(currRev.get(j));
                    last = currRev.get(j);
                }

            }
            //Adds black color.
            current.add(Color.color(0,0,0));
            result.add(current);
        }

        return result;
    }

    /** translates ternary logic values to colors. Red = 0, Green = 1, Blue = 2, Yellow = Otherwise */
    private static Color numToColor(int num){
        switch (num){
            case 0:
                return Color.color(1,0,0);
            case 1:
                return Color.color(0,1,0);
            case 2:
                return Color.color(0,0,1);
            default:
                return Color.color(1,1,0);
        }
    }

}
