package mapfScenario;


/**
 * Consts is static class which contains every constant that is in project. It allows simply to modify params sizes
 * and looks of entire application. Also includes warning and text messages.
 * */
public class Consts {

    /** map size constraints. */
    public static final int mapSizeMinX  =1;
    public static final int mapSizeMinY = 1;
    public static final int mapSizeMaxX = 30;
    public static final int mapSizeMaxY = 30;

    /** grid line that was maked can be stretched from original size  value indicates strech ratio.
     * for example: stretched 1 = edgeLength; */
    public static final float mapLineStretch = 1.0f;
    /** grid lines will be moved from upper left corner over ratio of edge length (value in edge ratio) */
    public static final float mapLineMove = 0.53f;

    /** flag offset X from vertex point (value in edge ratio) */
    public static final float mapFlagOffsetStartX = -0.52f;
    /** flag offset Y from point center (value in edge ratio)*/
    public static final float mapFlagOffsetStartY = -0.52f;

    /** flag offset X from vertex point (value in edge ratio)*/
    public static final float mapFlagOffsetFinishX = 0.06f;
    /** flag offset Y form vertex point (value in edge ratio)*/
    public static final float mapFlagOffsetFinishY = -0.52f;

    /** determines size of hole or obstacle (value in edge ratio) */
    public static final float mapHoleSize = 0.51f;


    /** size of the agent figure on simulation map (value in edge ratio) */
    public static final float mapAgentRepCircleSize = 0.20f;

    /** on real map how big is start position of the robot (value in edge ratio) */
    public static final float mapAgentRealMapStartPosCircleSize = 0.30f;
    /** on real map offset X from start point (value in edge ratio). */
    public static final float mapAgentRealMapStartPosCircleXOffset = 0.0f;
    /** on real map offset Y from start point (value in edge ratio). */
    public static final float mapAgentRealMapStartPosCircleYOffset = 0.30f;

    /* absolute distance between vertex defined in pixels, and used as base for other constants  */
    public static final int defaultEdgeLength = 60;

    /** width of the line (default one) */
    public static final int defaultEdgeWidth = 1;


    /** default distance of crosses for print */
    public static final int defaultEdgeLengthForPrintMm = 50;

    /** width of the line (default one) used for print */
    public static final int defaultEdgeWidthForPrintMm = 5;

    /** color which will be used as backroung for map editor */
    public static String mapBackGroundColor = "#f4f4f4";
    /** color which will be used as backroung for RealMap */
    public static String mapBackGroundColorForDisplay = "#ffffff";
    /** color chic will be used as backroung for printing */
    public static String mapBackGroundColorForPrint = "#ffffff";

    /* Printer Settings */
    /** resolutions of image sended to print. 72
     * other resolutions will break, because printer ignores resolution settings. (from java) */
    public static int printResolutionDpi = 72; // I am setting both to same resolution. Thats IT.
    //public static float getPrintResolutionYdpi = 300;

    /** conversion from mm to Inch. (by multiplying that value)  */
    public static final float mmPerInch = 0.0393700787f;

                                          //  @../img/greenflag25x25.png
    public static String greenFlagImgLoc = "img/greenflag23x26.png";
    public static String blwhFlagImgLoc = "img/blwhflag23x26.png";
    public static String removeImgLoc = "img/remove25x25.png";

    public static final String setAgentStartText = "Select agent start position";
    public static final String setAgentEndText = "Select agent end position";
    public static final String infoNothingSelected = "Nothing selected";

    public static final String warningPointForFlagAlredyTaken = "Cannot set multiple flags on one position";

    /* RealMap */
    public static final String realMapCanNotParse = "Error, cannot parse value";

    //public static final String errorRealMapManagerNotSet = "Click realMapDisplay before print";

    //save map to file.
    public static final String mapSaveVoidSpace = ".";
    public static final String mapSaveFullSpace = "@";
    public static final String mapSaveOverrideQuestion = "Map already exists, do you wish to override it?";

    /************ FOLDER SETTINGS**************************/
    //public static final String workDir = "workdir";
    public static final String workDir = "/afs/ms.mff.cuni.cz/u/k/krasicei/IdeaProjects/OzoblockCodeGenerator/workdir/";
    public static final String libDir = "lib";
    public static final String settingsFile = "config.properties";
    public static final String actionDurationFile = "action_durations";


    public static final String errorCreateWorkDir = "Error, cannot create working directory";
    public static final String errorNoPlaceForAgent = "Error, there is no place for agent start and end position";
    public static final String errorCannotCreatePrinterJob = "Error, No printer found";
    public static final String infoAgentStartPositionInvalidStrF = "Agent %s has invalid start position \n";
    public static final String infoAgentEndPositionInvalidStrF = "Agent %s has invalid end position \n";
    public static final String infoNoAgentsDefined = "No agents defined";

    public static final String errorWrongWorkdDirectory = "Invalid work directory";
    public static final String errorInvalidInterfaceFile = "Invalid interface file";
    public static final String errorInvalidTemplateFile = "Invalid template file";
    public static final String errorInvalidAnotationString = "Invalid anotation string";
    public static final String errorInvalidpredicate = "Invalid predicate";
    public static final String errorWrongLibDirectory = "Invalid lib directory";
    public static final String errorNoPicatFile = "Error: No answer generated \n";
    public static final String infoStillComputing = "Info: Still Computing \n";
    public static final String solutionDoesNotMatchCurrentMap = "Solution does not match with loaded Stage,  \n do you want to reload stage?";

    public static final String noRealDisplayWindowOpened = "Display has to be opened before settigns are possible";
    public static final String pickAgentNumber = "Select agent to export ";
    public static final String errorMissingTemplateFile = "Error, Missing template File";

    /* simulation */
    /* when computing available space remove some pixels. (like margin) */
    //public static final int controllPaneWidthAdjust = -80;
    /** determines height space given for one agent in simulation controll  */
    public static final int controllPaneAgentLineHeight = 30;

    public static final String timeLine = "time line";
    /** how big is the arrow on the end of line (in simulation) */
    public static float arrowEmphSizeRatio = 0.35f;
    /** how big is emphasized agent size in simulation (value in ratio)  */
    public static float circleEmphSizeRatio = 0.20f;
    /** where more than one line is present during path display specifies distance between them */
    public static float parralelLinesDelta = 0.12f;

    /** formatting string for timer takes double input */
    public static String timerFormatString = "%.1f";

    /** during simulation play determines time between readraw  */
    public static int realMapPlayerTimeStep = 80;

    public static String solutionNotFoundPicatString = "no_solution_found";
    public static String infoNoSolutionFound = "Info: No Solution Found";

    public static String errorInvalidActionName = "Error: Invalid action Name";
    public static String errorInvalidActionNimeOccupied = "Error : Action name already exists";
    public static String errorInvalidActionDuration = "Error : Invalid action duration";


    public static String textExecOnStart = "Exec on start";
    public static String textExecOnEnd = "Exec on end";
    public static String callStopBeforeStart = "Stop before start";
    public static String actionDurationPrefix = "action_duration.";
    public static String errorInvalidFormat = "Error: invalid format";
    public static String errorInvalidValue = "Error: invalid value";
    public static String removeCategoryQuestion = "Do you want to remove category?";
    public static String categoryNotSavedQuestion = "Not saved changes will be lost, continue?";
}

