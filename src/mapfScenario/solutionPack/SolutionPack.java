package mapfScenario.solutionPack;

import fileHandling.*;
import helpout.methods;
import mapfScenario.Consts;
import mapfScenario.Data.MapData;
import mapfScenario.DataStore;
import mapfScenario.MainFormControllerLogic;
import mapfScenario.OzoCodeExport;
import mapfScenario.agents.Agent;
import mapfScenario.picat.SolverOption;
import mapfScenario.picat.SolverProcess;
import mapfScenario.simulation.AgentAction;
import mapfScenario.simulation.Solution;
import mapfScenario.simulation.SolutionDurationModifier;
import mapfScenario.timeDuration.ActionCategory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Solution pack is class supporting loading and executing multiple solutions from batch file, or from special command
 *
 *
 * */
public class SolutionPack {

    //input file outputfile.ozocode load
    // if load -> shows program.; if no load quietly exists
   /**
    *  method expects following arguments:
    *  solpack inputfile.solutionpack.sp [ozor|run | ozo ] file.ozocode.template output.ozocode
    *
    *  example: solpack /home/ivan/test1/unif4.sp ozor /home/ivan/test1/template.11.unif.ozocode /home/ivan/test1/unif4.ozocode
    *
    *  Method loads file inputfile.solutionpack.sp and each line translates into solver task that executes.
    *  After taskexecution it each task loads into solution, and generates output.ozocode file, which contains numbered
    *  paths for every solution. Also loaded solutions contains inner number, which will use realmapControll, and
    *  when is that solution chosen to replay action, it blinks Robot id which match current loaded path.
    *
    *  each line of solpack file should have format:
    *  SolutionName;map_file.map;agent_file.map;SolverName;TimeDurationChoice
    *  example:
    *  5.cross;/home/ivan/tests/1.cross.map;/home/ivan/tests/1.cross.ags;EdgeSplit;Real
    *
    * */

    public  static boolean processSolutionPackTrueIfExit(String args[]){
        //args [0] solpack [1] filename [2] ozocode
        if (args.length < 3) {                  //         1                    2                  3                 4
            System.out.println("expected args: solpack source_filename action{RUN|OZO|OZOR} [template_file] [output_ozocode_fle]");
            return true;
        }
        String sfile= args[1];
        File file = helpout.methods.fileToAbsolute(sfile);
        if (!file.exists()){
            System.out.println("Error no file found");
            return true;
        }

        //initializes solution pack
        processPackToOzocode(file);

        if (args[2].trim().toLowerCase().equals("ozo")) {
            if (args.length < 5) { System.out.println("filename expecting"); return false; }
            DataStore.settings.ozobotTemplateFile = args[3];
            exportSolutionsItemToOzocode(DataStore.audoloadedSolverProcess, args[4]);
            return true;
        }

        if (args[2].trim().toLowerCase().equals("ozor")) {
            if (args.length < 5) { System.out.println("filename expecting"); return false; }
            DataStore.settings.ozobotTemplateFile = args[3];
            exportSolutionsItemToOzocode(DataStore.audoloadedSolverProcess, args[4]);

            return false;
        }


        return true;
    }



    /** loads solution pack from file, and stores it to datastore. as audoloadedSolverProcess  */
    public static void processPackToOzocode(File inputFile){

       // List<Solution> solutionPack = new ArrayList<Solution>();
        List<SolverProcess> spExtra = new ArrayList<SolverProcess>();

        List<SolutionAutogenItem> tasks = SolutionPackFileWorker.loadSolutionPackFile(inputFile);
        PicatInterfaceParser pip = new PicatInterfaceParser();
        List<SolverOption> solData = pip.loadPiatInterfaceOptions(DataStore.settings.defaultPicatInterfaceFileName);

        MapViewFileWorker mvfw = new MapViewFileWorker();
        AgentFileWorker afw = new AgentFileWorker();


        List<ActionCategory> aclist = ActionDurationFileWorker.loadActionCategories();

        List<SolutionAutogenItem> sucessfulItems = new ArrayList<>();

        for (SolutionAutogenItem spi: tasks) {

            System.out.println("processing" + spi.itemName);

            MapData md = mvfw.LoadMapData(spi.mapFilePath);

            if (md == null){
                return;
            }

            List<Agent> ags = afw.LoadAgents(spi.agsFilePath);

            if (ags == null){
                return;
            }

            SolverOption chosenSo = null;
            for(SolverOption so : solData){
                if (spi.solverName.trim().toLowerCase().equals(so.displayName.trim().toLowerCase())){
                    chosenSo = so;
                    break;
                }
            }

            if (chosenSo == null){
                System.out.println("ERROR: solverOption not found: "+ spi.solverName + " ignoring " + spi.itemName);
                continue;
            }

            if( !MainFormControllerLogic.problemIsValidCheck(md,ags)) {
                System.out.println("Error: Agent positions does not match map ");
                return;
            }

            SolverProcess sp = new SolverProcess(md, ags,chosenSo,spi.itemName);
            sp.startFromActiveThread();
            //wait untill thread ends.


            while ((sp.isInProcess())) { Thread.yield(); }

            if (sp.hasNoSolution()){
                System.out.println("WARNING: "+ spi.itemName + " has no solution");
                continue;
            }
            if (sp.isInError()){
                System.out.println("WARNING: "+ spi.itemName + " is in error");
                System.out.println(sp.getErrorString());
                continue;
            }

            Solution s=  sp.getSolution();



            ActionCategory ac = null;
            for(ActionCategory aci : aclist){
                if (aci.toString().trim().toLowerCase().equals(spi.actionCategory.toLowerCase())){
                    ac = aci;
                    break;
                }
            }
            if (ac == null){
                System.out.println("Action category not found. " + spi.actionCategory + " for " + spi.itemName);
                continue;
            }


            Solution smodif = SolutionDurationModifier.cloneSolution(s, ac.getActionDurationMap());

            sp.setSolution(smodif);

            spExtra.add(sp);

        }

        DataStore.audoloadedSolverProcess= spExtra;

        //writes



    }


    private static void exportSolutionsItemToOzocode(List<SolverProcess> items, String ozocodeFile){
        List<List<String>> actionList = new ArrayList<>();

        int pathNo = 0;
        for (SolverProcess sp: items) {
            sp.agentStartBlinkNumber = pathNo;
            List<List<AgentAction>> lla = sp.getSolution().getSolutionActions();

            for (List<AgentAction> la : lla) {
                List<String> agentAction = new ArrayList<>();
                for (AgentAction a : la ) {
                    agentAction.add(a.action);
                }
                actionList.add(agentAction);
                pathNo++;
            }
        }

        //writes files for


        File workdir = methods.fileToAbsolute(DataStore.settings.workDirectoryPath);
        if (!workdir.exists()) { workdir.mkdir();}
        String timeStamp =new SimpleDateFormat(Consts.fileOutputFormatString).format(new Date());;
        File fileForOzoGenerator = new File(workdir.getAbsolutePath()+ File.separator  + "out" + timeStamp);

        SolutionFileWorker swf = new SolutionFileWorker();
        swf.createOzoGeneratorInputFile(actionList,fileForOzoGenerator);

        File output = methods.fileToAbsolute(ozocodeFile);

        OzoCodeExport oce = new OzoCodeExport();
        oce.doExport(fileForOzoGenerator.getAbsolutePath(),output,-1);

    }






}
