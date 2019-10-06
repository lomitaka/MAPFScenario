package mapfScenario.simulation;

import mapfScenario.Data.MapData;
import mapfScenario.agents.Agent;
import org.apache.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** picat solution loaded from file */
public class Solution implements java.io.Serializable {

    public boolean hasNoSoluiton = false;
    List<List<AgentAction>> solutionData;
    List<Agent> agentList = null;
    MapData md = null;
    String solutionFileName;
    String solutionName;

    public Solution(int agentCount) {
        //initialize solution
        solutionData = new ArrayList<>();
        for (int i = 0; i < agentCount; i++) {
            solutionData.add(new ArrayList<AgentAction>());
        }

    }

    public List<Agent> getAglist() {
        return agentList;
    }

    public MapData getMapData() {
        return md;
    }

    public void addActions(int agentID, List<AgentAction> aaList) {

        List<AgentAction> agA = solutionData.get(agentID - 1);

        for (AgentAction aa : aaList) {
            agA.add(aa);

        }
    }


    public void setSolutionFileName(String solutionFileName) {
        this.solutionFileName = solutionFileName;

    }

    public String getSolutionFileName() {
        return solutionFileName;
    }

    public void setSolutionName(String solutionName) {
         this.solutionName =solutionName;
    }

    public String getSolutionName() { return solutionName; }

     public void addStageData(List<Agent> agents, MapData md){
         this.md = md;


         List<Agent> data = new ArrayList<Agent>();
         for(Agent a : agents ){
             data.add(a);
         }
         agentList = data;

     }

    public List<List<AgentAction>> getSolutionActions(){ return solutionData; }



}
