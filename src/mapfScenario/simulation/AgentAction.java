package mapfScenario.simulation;

import graphics.MVPoint;
import mapfScenario.agents.Agent;

/** AgentAction represents one action of agents that can be performed. Contains action start position, action length, action type.
 *  */
public class AgentAction implements java.io.Serializable  {
    /** position from where action will start */
    public MVPoint position;
    /** rotation of current robot.. can be null.  */
    public Integer rotation;
    /** action represents Ozobot action that wil be performed */
    public String action;
    /** duration of the action */
    public int duration;
    /** some text for debug or another uses */
    public String note;

    public AgentAction(MVPoint position,Integer rotation, String action,int duration, String note){
        this.position = position;
        this.rotation = rotation;
        this.action = action;
        this.duration = duration;
        this.note = note;
    }


    //public int startTimeMark;
    //public Agent agent;

    //public void Enrich(Agent a,int time){
    //    agent = a;
    //    startTimeMark = time;
    //}

    public AgentAction clone(){
        return new AgentAction(position,rotation,action,duration,note);
    }

}
