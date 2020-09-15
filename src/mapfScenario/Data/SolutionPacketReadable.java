package mapfScenario.Data;

import graphics.MVPoint;
import mapfScenario.agents.Agent;
import mapfScenario.picat.SolverOption;
import mapfScenario.simulation.Solution;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/** solution packet readable by human */
public class SolutionPacketReadable {

    /** solver options that were used to generate solution */
    public SolverOption so;
    /** time stamp when solution was created. Used to recognize answer file. */
    public String timeStamp;
    /** map between number and points. points are point of mapview, and number is numer as point is represented
     * in answer file. */
    public HashMap<Integer, MVPoint> idToPointMap;
    /* list of lines containing entire answer file */
    public List<String> answFile;
    /* solution name */
    public String solutionName;

    /* if problem have some solution */
    public boolean hasNoSoluiton = false;

    public List<Agent> agentList = null;

    public MapData md = null;

    public String solutionFileName;



}
