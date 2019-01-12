package mapfScenario.Data;

import graphics.MVPoint;
import mapfScenario.picat.SolverOption;
import mapfScenario.simulation.Solution;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/** this class serves as pack, to save solution into file */
public class SolutionPacket implements Serializable {
    /** solution to save */
    public Solution s;
    /** solver options that were used to generate solution */
    public SolverOption so;
    /** time stamp when solution was created. Used to recognize answer file. */
    public String timeStamp;
    /** map between number and points. points are point of mapview, and number is numer as point is represented
     * in answer file. */
    public HashMap<Integer,MVPoint> idToPointMap;
    /* list of lines containing entire answer file */
    public List<String> answFile;
    /* solution name */
    public String solutionName;



}
