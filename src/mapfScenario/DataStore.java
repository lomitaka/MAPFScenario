package mapfScenario;

import javafx.stage.Window;
import mapfScenario.picat.SolverProcess;
import mapfScenario.realMap.RealMapControll;
import mapfScenario.realMap.RealMapManager;
import mapfScenario.solutionPack.SolutionAutogenItem;

import java.util.List;
/**
 * Used for global variables that needed to be accesssed globally
 * */
public class DataStore {

    /** represents window with realMap */
    public static RealMapManager rmm;
    /** represents window with controll of realMap */
    public static RealMapControll rmc;
    /** main form window*/
    public static Window mainWindow;
    /** global settings readed on start from file */
    public static Settings settings;
    /** command line args if there any */
    public static String[] args;
    /** when working in batch mode this contains batch solutions */
    public static List<SolverProcess> audoloadedSolverProcess;

}
