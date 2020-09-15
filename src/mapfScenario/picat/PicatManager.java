package mapfScenario.picat;

import fileHandling.PicatInterfaceParser;
import fileHandling.SolutionFileWorker;
import helpout.methods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import mapfScenario.Consts;
import mapfScenario.Data.MapData;
import mapfScenario.Data.SolutionPacket;
import mapfScenario.Data.SolutionPacketReadable;
import mapfScenario.DataStore;
import mapfScenario.agents.Agent;
import mapfScenario.simulation.Solution;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
*  handles logic about serving picat. Handles choice box with solver list, manages list with solutions.
*  Manages SolverProcesses.
* */
public class PicatManager {

    private final Logger logger = Logger.getLogger(PicatManager.class);

    private String picatInterfaceFile;


    /**
     * options that are readed from interface file
     */
    private List<SolverOption> predefinedOptions;
    /** options that are showed in list */
    private ObservableList<SolverOption> picatInterfaceOptions;

    ChoiceBox solverOptionBox;
    ListView solverResults;

    /** items that are showed in list with solutions */
    private ObservableList<SolverListItem> listViewProcesses;

    public PicatManager(ChoiceBox solverOpt, ListView solverResults) {
        solverOptionBox = solverOpt;
        this.solverResults = solverResults;
        listViewProcesses = FXCollections.observableArrayList(SolverListItem.extractor());
        solverResults.setItems(listViewProcesses);
        //solverOptionBox.getItems()

        //hook to select files
        solverOptionBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                SolverOption so = (SolverOption) newSelection;
                if (so.targetFilePath == null) {
                    loadSolverFromFile();
                }
            }
        });

        //hook to show from additional loadded files.
        loadAutoloadedSols();

    }

    boolean useCustomBounds = false;
    int lowerBound;
    int upperBound;

    public void setandUseCustomBounds(int lower, int upper) {
        lowerBound = lower;
        upperBound = upper;
        useCustomBounds = true;
    }

    /** when called default solver bound will be used  */
    public void disableCustomBounds() {
        useCustomBounds = false;
    }

    /**
     * loads data from predefinedoptions. adds to them option from file, if there is some.
     * displays option in list
     */
    private void updateSolverOptions(SolverOption fromFileOne) {

        picatInterfaceOptions.clear();
        List<SolverOption> sdata = new ArrayList<SolverOption>();
        sdata.addAll(predefinedOptions);
        if (fromFileOne != null) {
            sdata.add(fromFileOne);
        }

        SolverOption so = new SolverOption();
        so.displayName = "From File...";
        //picatInterfaceOptions.add(so);s
        sdata.add(so);
        //add file one
        //return sdata;
        picatInterfaceOptions.addAll(sdata);


    }

    /** loads list of solutions. Writen for batch purposes */
    public void loadAutoloadedSols() {
        //hook
        if (DataStore.audoloadedSolverProcess != null) {
            for (SolverProcess sp : DataStore.audoloadedSolverProcess) {
                listViewProcesses.add(sp.getSolverListItem());
            }
        }
    }


    /** when option load from file is choosen.  **/
    private void loadSolverFromFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chose Picat file");
        //fileChooser.showOpenDialog(null);
        //fileChooser.setSelectedExtensionFilter();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Picat", "*.pi")
        );
        //File file = fileChooser.showSaveDialog(DataStore.mainWindow);
        File file  = fileChooser.showOpenDialog(DataStore.mainWindow);

        if (file == null ) {
            logger.debug("cant read file.");
            solverOptionBox.getSelectionModel().clearSelection();
            return ;
        }
        SolverOption so = new SolverOption();
        so.targetFilePath = file.getAbsolutePath();
        so.displayName = file.getName();
        so.predicatName = DataStore.settings.customFilePredicat;
        updateSolverOptions(so);

        solverOptionBox.getSelectionModel().select(so);

    }


    /** load available solvers from file and populates choice box with them */
    public void preprocessPicatInterface(){

        //%[SCENARIO_EXPORT] Simple
        //mapf_simple(FileIn,FileOut)
       PicatInterfaceParser pip = new PicatInterfaceParser();


       List<SolverOption> data = pip.loadPiatInterfaceOptions(DataStore.settings.defaultPicatInterfaceFileName);
       predefinedOptions = data;

        ObservableList<SolverOption> dataobservable = FXCollections.observableArrayList();
        picatInterfaceOptions =  dataobservable;
        solverOptionBox.setItems(picatInterfaceOptions);

        updateSolverOptions(null);


    }

    /** inits new search.  */
    public void findAction(MapData mapData, List<Agent> agents, SolverOption so, String solutionName){

        SolverProcess sp = new SolverProcess(mapData, agents,so,solutionName);

        if (useCustomBounds){
            sp.setandUseCustomBounds(lowerBound,upperBound);
        } else {
            sp.disableCustomBounds();
        }


        listViewProcesses.add(sp.getSolverListItem());

         sp.Start();

    }

    /* return selected solution or null if solution is not ready or is error */
    public Solution getSelectedSolution(){


        ObservableList<Agent> selectedSolutions = solverResults.getSelectionModel().getSelectedItems();
        if (selectedSolutions.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return null;
        }
        SolverListItem sp = (SolverListItem)solverResults.getSelectionModel().getSelectedItems().get(0);
        if (sp.solverProcess.isInError()){
            methods.showDialog(Consts.errorNoPicatFile + sp.solverProcess.getErrorString());
            return null;
        } else if (sp.solverProcess.isInProcess()) {
            methods.showDialog(Consts.infoStillComputing + sp.solverProcess.getErrorString());
            return  null;
        } else if (sp.solverProcess.hasNoSolution()){
            methods.showDialog(Consts.infoNoSolutionFound);
            return null;
        }
        else{
            return sp.solverProcess.getSolution();
        }


    }


    /** removes solution from solution list, and if solution still runs, kill it. */
    public void removeSelectedSolution(){


        ObservableList<Agent> selectedSolutions = solverResults.getSelectionModel().getSelectedItems();
        if (selectedSolutions.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return;
        }
        SolverListItem si = (SolverListItem)solverResults.getSelectionModel().getSelectedItems().get(0);
        SolverProcess sp = si.solverProcess;
        //still computing.. kill it
        if (sp.isInProcess()){
            sp.killProcess();
        }

        solverResults.getItems().remove(si);
        //TODO Remove solution files?
    }


    /** returns processes were were no errors. */
    public List<SolverProcess> getSolverProcessNotErrorList(){
        List<SolverProcess> result = new ArrayList<SolverProcess>() ;
        for (SolverListItem sli: listViewProcesses ) {
          if (!sli.solverProcess.isInError()){
              result.add(sli.solverProcess);
        }
        }
        return result;
    }

    /** allows to load solution from file. uses serialization. */
    public void loadSelectedSolution() {

        SolutionFileWorker sfw = new SolutionFileWorker();

        SolutionPacket packet = sfw.loadSolutionPacket();
        if (packet == null) { return;}


        SolverProcess sp = SolverProcess.recreateFromPackage(packet);
        listViewProcesses.add(sp.getSolverListItem());
    }

    /** saves solution to file. Uses serialization */
    public void saveSelectedSolution() {
        ObservableList<Agent> selectedSolutions = solverResults.getSelectionModel().getSelectedItems();
        if (selectedSolutions.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return;
        }
        SolverListItem sp = (SolverListItem)solverResults.getSelectionModel().getSelectedItems().get(0);
        SolutionPacketReadable packet = sp.solverProcess.getSolutionPacketReadable();
        if (packet != null){
            SolutionFileWorker sfw = new SolutionFileWorker();
            sfw.saveSolutionPacketReadable(packet);
        }
    }

    /*public void CallPicatSolver(MapView mv, List<Agent> agentList){

        //String timeStamp = new SimpleDateFormat("yy-MM-dd-HH-mm-ss").format(new Date());
        //File f = new File(Consts.workDir  + "problem" + timeStamp + ".pi" );


        //PicatFileWorker pfw = new PicatFileWorker(mv, agentList,f);
        //pfw.writeFile();

        PicatWrapper pw = new PicatWrapper();
        String workdir =  Consts.workDir;
        String calledPredicate = "mapf_simple(\"workdir/mapf_problem003.pi\",\"workdir/problem003.solved\")";
        String calledFile = "/afs/ms.mff.cuni.cz/u/k/krasicei/IdeaProjects/OzoblockCodeGenerator/workdir/picat_iface.pi";
       // String calledFile = "A";
        pw.callPicatMain(workdir,calledPredicate,calledFile);



    }*/





}