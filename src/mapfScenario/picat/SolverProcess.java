package mapfScenario.picat;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import fileHandling.PicatFileWorker;
import fileHandling.SolutionFileWorker;
import graphics.MVPoint;
import helpout.methods;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import mapfScenario.Consts;
import mapfScenario.Data.MapData;
import mapfScenario.Data.SolutionPacket;
import mapfScenario.DataStore;
import mapfScenario.agents.Agent;
import org.apache.log4j.Logger;
import mapfScenario.simulation.Solution;
import picatWrapper.OutputContainer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static mapfScenario.picat.SolverProcess.SolverState.*;

/** SolverProcess represents process of finding solution. SolverState indicates in which state is current process.
 * After run, it creates new process which calls PicatWrapper, waits for picat to proces items and on end loads result file
 * and updates infomration in list box. */
public class SolverProcess {

    enum SolverState  { Initializing, Solving,NoSolution, Done,Error,Dummy }

    private SolverState sls = Initializing;
    private MapData mapData;
    private List<Agent> agentList;
    private SolverOption so;
    private String solverFile;

    /** solution of the problem, will be filled when process chanes to state Done */
    private Solution solution;
    /** represents output of picat solver. User can observe picat output and search for errors */
    private String picatErrorOutput;

    /** map of numbers to mappoints. In file generated picat nodes are encoded as numbers. */
    private HashMap<Integer,MVPoint> idToPointMap;
    /** represents file that will contain picat answer */
    private File fileOut;
    /** contains its own wrapper, so it can update it when needed */
    private SolverListItem sli;
    /** represents thread for running picat solver */
    private Thread process;

    private String solutionName;



    /** When SolverProcess is used in real map, this number indicates which RobotID will be assigned to the first agent.
     */
    public int agentStartBlinkNumber = 0;



    private final Logger logger = Logger.getLogger(SolverProcess.class);

    private SolverProcess(){};

    public SolverProcess(MapData mapData, List<Agent> aglist, SolverOption so,String solutionName){
        this.mapData = mapData;


        this.solutionName = solutionName;
        // create new agent list
        this.agentList = new ArrayList<Agent>();
        for(Agent a : aglist) {this.agentList.add(a.clone());}


        this.so = so;
        this.sli = new SolverListItem(this);
        redraw();
    }

    /** recreated process from saved package  */
    public static SolverProcess recreateFromPackage(SolutionPacket packet){
        SolverProcess sp = new SolverProcess();
        sp.solutionName = packet.solutionName;
        sp.mapData = packet.s.getMapData();
        sp.agentList = new ArrayList<Agent>();
        for(Agent a : packet.s.getAglist()) {sp.agentList.add(a.clone());}

        sp.so = packet.so;
        sp.solution = packet.s;
        sp.sli = new SolverListItem(sp);

        sp.solverFile = packet.timeStamp;

        String workDirStr = DataStore.settings.workDirectoryPath;
        File workdir = methods.fileToAbsolute(workDirStr);
        sp.fileOut = new File(workdir.getAbsolutePath()+ File.separator  + "out" + sp.solverFile);
        sp.so.targetFilePath = sp.fileOut.getAbsolutePath();
        sp.solution.setSolutionFileName(sp.fileOut.getAbsolutePath());
        if (!sp.fileOut.exists()){
            SolutionFileWorker sfw = new SolutionFileWorker();
            sfw.writeListToFile(packet.answFile,sp.fileOut);
        }
        sp.sls = SolverState.Done;
        sp.idToPointMap = packet.idToPointMap;
        sp.redraw();
        return  sp;
    }

    /** pack SolverProcess to solution packet */
    public SolutionPacket getSolutionPacket(){

        if (sls == SolverState.Done) {
            SolutionFileWorker sfw = new SolutionFileWorker();

            SolutionPacket sp = new SolutionPacket();
            sp.s = solution;
            sp.so = so;
            sp.timeStamp = solverFile;
            sp.idToPointMap = idToPointMap;
            sp.answFile = sfw.readToList(fileOut);
            sp.solutionName = solutionName;
            return sp;
        }
        else {
            methods.showDialog(Consts.infoStillComputing);
            return null;
        }
    }

    public SolverListItem getSolverListItem(){
        return sli;
    }

    public void killProcess(){
        if (sls == SolverState.Solving){
            process.interrupt();
        }
    }

    public void join(){
        try {
            process.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isInProcess(){return sls == SolverState.Solving;}
    public  boolean isDummyProcess(){
        return sls == SolverState.Dummy;
    }
    public  boolean hasNoSolution(){
        return sls == SolverState.NoSolution;
    }

    /** creates dummy process, which is used as null item in RealMap controll  */
    public static SolverProcess getDummyProcess(){
        List<Agent> a = new ArrayList<Agent>();
        SolverProcess sp = new SolverProcess(null,a,null,"");
        sp.sls = SolverState.Dummy;
        return sp;
    }


    public boolean isInError(){
        return sls == SolverState.Error;
    }

    public Solution getSolution(){
        return solution;
    }

    /** assigns solution to packet */
    public void setSolution(Solution s){
        solution = s;
    }

    public String getErrorString(){
        return picatErrorOutput;
    }

    /** picatsolver call consts of predicat picat solver file, picat predicat, input and output file.
     * and these values are generated as command line arguments to be passed to picatWrapper */
    private String[] constructCallPred(){
        logger.debug("Solver Start ");

        String workDirStr = DataStore.settings.workDirectoryPath;
        File workdir = methods.fileToAbsolute(workDirStr);


        if (!workdir.exists()){
            if (!workdir.mkdirs()){
                logger.warn("cannot create work directory");
                helpout.methods.showDialog(Consts.errorCreateWorkDir);
                return null;
            } else {
                logger.info("created work directory: " + workdir.getAbsolutePath());
            }
        }

        String libDirStr =  Consts.libDir;
        File libdir = methods.fileToAbsolute(libDirStr);
        //File libdir = new File(libDirStr);

        String timeStamp =new SimpleDateFormat(Consts.fileOutputFormatString).format(new Date());;

        File problFle = new File(workdir.getAbsolutePath() + File.separator + "pr" + timeStamp);
        // tests if file already exists.. if so.. sleeps agent little and creates new file.
        for (int i = 0; i < 30;i++) {

            if (problFle.exists()){
                try {
                    logger.info("already exists, waiting");
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                    timeStamp = new SimpleDateFormat(Consts.fileOutputFormatString).format(new Date());
                    problFle = new File(workdir.getAbsolutePath() + File.separator + "pr" + timeStamp );
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
            } else {
                break;
            }
        }
        solverFile = timeStamp;

        fileOut = new File(workdir.getAbsolutePath()+ File.separator  + "out" + timeStamp );

        HashMap<String,String> options = new HashMap<>();
        if (useCustomBounds) {
            options.put("lowerBound", lowerBound+"");
            options.put("upperBound", upperBound+"");
        }

        PicatFileWorker pfw = new PicatFileWorker(mapData, agentList,problFle,options);
        pfw.writeFile();
        idToPointMap = pfw.getIDToPointMap();
        //second call picat

        //PicatWrapper pw = new PicatWrapper();

        //String workdir =  Consts.workDir;
        //String calledPredicate = "mapf_simple(\"workdir/mapf_problem003.pi\",\"workdir/problem003.solved\")";
        // String calledFile = "/afs/ms.mff.cuni.cz/u/k/krasicei/IdeaProjects/OzoblockCodeGenerator/workdir/picat_iface.pi";
        // String calledFile = "A";
        // picat example argument: -g function\("ahoj","bla"\) test1.pi
        //pw.callPicatMain(workdir,calledPredicate,calledFile);
        //String calledPredicate =
        //        String.format("%s(\"%s\",\"%s\")",so.predicatName,problFle.getPath(), outFle.getPath() );

       /* String calledProg =
                String.format("java -jar %s%sPicatWrapper.jar %s %s(\"%s\",\"%s\") %s",
                        libdir.getAbsolutePath(),
                        File.separator,
                        libdir.getAbsolutePath(),

                        so.predicatName,
                        problFle.getPath(),
                        fileOut.getPath(),
                        so.targetFilePath );*/
        String[] calledProg = {
           //     "java",  //run java
         //       "-jar",  // jar
                // absolute path to called library
        //        String.format("%s%sPicatWrapper.jar",libdir.getAbsolutePath(), File.separator),
                //absolute path to library (probably not needed, but what if.
                libdir.getAbsolutePath(),
                //predicat that should be called
                String.format("%s(\"%s\",\"%s\")", so.predicatName,problFle.getName(),fileOut.getName()),
                //file in which precita will be called
                so.targetFilePath
        };
        StringBuilder logTmp = new StringBuilder();
        for (String s : calledProg){ logTmp.append(s);logTmp.append(" "); }
        logger.debug("picat argument:" + logTmp.toString());
        return calledProg;
    }

    /** constructs input for solver, calls solver, adds hooks on process end. */
    public void Start(){


        String[] calledProg = constructCallPred();


        SolverProcessRun spr = new SolverProcessRun(this,calledProg);
        Thread tak = new Thread(spr);
        process = tak;
        tak.start();
        sls = SolverState.Solving;
        redraw();
    }

    /** in case of batch running calls process from active thread */
    public void startFromActiveThread(){


        String[] calledProg = constructCallPred();
        sls = SolverState.Solving;
        runInBackground(calledProg);
        runProcessFinishing();
        redraw();
    }


    boolean useCustomBounds = false;
    int lowerBound;
    int upperBound;
    /** custom bounds sets the lower and upper bound of solution length to search */
    public void setandUseCustomBounds(int lower,int upper){
        lowerBound = lower;
        upperBound = upper;
        useCustomBounds = true;
    }


    public void disableCustomBounds(){
        useCustomBounds = false;
    }


    /** class that represents new running thread. */
    private class SolverProcessRun implements  Runnable{
        SolverProcess sp;
        String calledProg[];
        public SolverProcessRun(SolverProcess sp,String[] calledProg){
            this.sp = sp;
            this.calledProg = calledProg;
        }

        @Override
        public void run() {
            runInBackground(calledProg);
            Platform.runLater(() -> sp.runProcessFinishing());
        }
    }


    private void runInActiveThread(){

    }

    /** creates new process, and calls solver and waits untill it ends. */
    private  void runInBackground(String[] calledProg){
        //try {
        logger.debug("solving");
        StringBuilder picatOutput = new StringBuilder();

        //Process proc = Runtime.getRuntime().exec(calledProg);

        OutputContainer outputContainer = new OutputContainer();
        picatWrapper.EntryPoint.callPicat(calledProg,outputContainer);

        // Then retreive the process output
       // InputStream in = proc.getInputStream();
        // InputStream err = proc.getErrorStream();
        //OutputStream ostr = proc.getOutputStream();
        //BufferedInputStream bis = new BufferedInputStream(in);
        //this waits anyway.
        /*int newLine = (int)System.getProperty("line.separator").charAt(0);
        int znak = 0;
        while ((znak = bis.read())!= -1) {
            System.out.print((char) znak);
            picatOutput.append((char) znak);
            if (znak == newLine){
                Platform.runLater(()->{picatErrorOutput= picatOutput.toString();});
            }
        }*/

        picatErrorOutput = outputContainer.output;



        //proc.waitFor();
        /*} catch (InterruptedException e) {
            logger.error("exectuion interrupted");
            logger.error(e.toString());
        } catch (IOException e){
            logger.error("read write exception");
            logger.error(e.toString());
        }*/

    }


    /** code runned after solving process ended */
    private void runProcessFinishing(){
        logger.debug("finishing");
        sls = SolverState.Done;
        redraw();
        // check for error
        if(!fileOut.exists()){
            sls = SolverState.Error;
            solution = null;redraw();

            logger.error("no result file written" + picatErrorOutput );
            return;
        }
        // pw.callPicatMain(workdir.getAbsolutePath(), calledPredicate, so.targetFilePath);


        SolutionFileWorker sfw = new SolutionFileWorker();


        //File outFle = getProblemOutputFile(solverFile);

        solution = sfw.loadSolutionFile(fileOut,idToPointMap,agentList.size());
        solution.setSolutionName(solutionName);
        if (solution == null){
            sls = SolverState.Error;redraw();
            return;
        }

        if (solution.hasNoSoluiton){
            sls = SolverState.NoSolution;redraw();
            return;
        }

        /** adds to solution reference to data */
        solution.addStageData(agentList,mapData);
        // thirld.. wait untill it ends. HOW DO I Catch MISTAKES?


        // read output.
    }

    /*private File getProblemOutputFile (String solverFile){
        String workDirStr = DataStore.settings.workDirectoryPath;
        File workdir = new File(workDirStr);
        return  new File(workdir.getAbsolutePath() + File.separator + "pr" + solverFile + ".pi");

    }*/

    /** update display name of solver process */
    public void redraw(){
        sli.displayName.set( this.toString());
    }

    public String toString(){
        String state = "";
        switch (sls){

            case Initializing:
                state = "Initializing..";
                break;
            case Solving:
                state =  "Solving " + solutionName ;
                break;
            case Done:
                //state ="Solution" + solverFile;
                if (solutionName.length() ==0) {state = "solution " +  solverFile;} else {
                    state = solutionName;
                }
                break;
            case Error:
                state = "Error";
                break;
            case Dummy:
                state ="None";
                break;
            case NoSolution:
                state ="No solution Found";
                break;
            default:
                    logger.error("Unhandled option of displaying SolverProcess");
        }
        logger.debug("update state");
        return state;
    }

  /*  public Solution loadSolution(){
        SolutionFileWorker sfw = new SolutionFileWorker();

        File outFle = getProblemOutputFile(solverFile);

        return sfw.loadSolutionFile(outFle,idToPointMap,agentList.size());

    }*/

}
