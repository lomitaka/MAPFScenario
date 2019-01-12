package mapfScenario.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Class that modifies action lengths of agents */
public class SolutionDurationModifier {

    /** get Solution, and HashMap with new action lengths, and return new solution with new action lengths.
     * if mapActionDuration do not contains current action duration, then action len is not changed*/
    public static Solution cloneSolution(Solution s, HashMap<String, Integer> mapActionDuration ){

        Solution answ = new Solution(0);
        answ.agentList = s.agentList;
        answ.hasNoSoluiton = s.hasNoSoluiton;
        answ.solutionFileName = s.solutionFileName;
        answ.md = s.md;
        answ.solutionData = new ArrayList<List<AgentAction>>();

        for (List<AgentAction> laa : s.solutionData) {
            List<AgentAction> answlst = new ArrayList<>();
            for (AgentAction aa : laa ){
                AgentAction ac = aa.clone();
                if (mapActionDuration.containsKey(ac.action)) {
                    ac.duration = mapActionDuration.get(ac.action);
                }
                answlst.add(ac);
            }
            answ.solutionData.add(answlst);
        }

        return answ;
    }



}
