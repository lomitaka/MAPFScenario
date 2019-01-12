package mapfScenario.simulation;

import mapfScenario.agents.Agent;

/** for simulation purposes it is better to work with tuple of actions, current and next. */
public class AgentActionPair {

    /** this is action that action pair represents */
    public AgentAction current;
    /** to know where current action leads is used action next. */
    public AgentAction next;
    /** were in time action start */
    public int startTimeMark;
    /** agent to which action belongs */
    public Agent agent;


    public AgentActionPair(AgentAction current,AgentAction next,Agent agent, int startTimeMark){
        this.current = current;
        this.next = next;
        this.startTimeMark = startTimeMark;
        this.agent = agent;

    }
}
