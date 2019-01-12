package mapfScenario;

import fileHandling.AgentFileWorker;
import fileHandling.MapViewFileWorker;
import fileHandling.SettingsFileWorker;
import helpout.QuestionName;
import helpout.methods;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import mapfScenario.Data.MapData;
import mapfScenario.mapView.MapView;
import mapfScenario.mapView.MapViewState;
import mapfScenario.picat.SolverListItem;
import mapfScenario.realMap.RealMapControll;
import mapfScenario.realMap.RealMapManager;
import mapfScenario.agents.Agent;
import mapfScenario.agents.AgentManager;
import mapfScenario.picat.PicatManager;
import mapfScenario.picat.SolverOption;
import mapfScenario.simulation.Simulator;
import mapfScenario.simulation.Solution;
import org.apache.log4j.Logger;
import mapfScenario.timeDuration.ActionDurationManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Controller class for Main form. Connects together every other class, and every window input is managed from there.
 *
 * Controller is logically divided into theese parts: <br/>
 * MapView - takes care of displaying map on form<br/>
 * AgentManager - takes Takes care about Managing Agents, managing their start and end positions, names, and displayiny them.<br/>
 * RealMapManager - class used for testing, is able to show map to test on screen, and to save map to file or print it. <br/>
 * PicatManager -  handles creating tasks, calling solver, and display solutions <br/>
 * MainFormDelegate - used to pass some functionality of MainFormController to another classes.<br/>
 * Simulator - handles Simulation. Have its part in bottom window.<br/>
 * ActionDurationManager - allows to modify duration of actions, to allow more variability.
 *
 * */
public class MainFormController implements Initializable {

    /* ***************************FXML Elements  ************************************/

    @FXML /** root anchor of every element*/
    AnchorPane mainAnchor;

    @FXML /** pane for base grid*/
    Pane FXMLmapGrid;

    @FXML /** pane for obstacles (circles) */
    Pane FXMLmapObstacles;

    @FXML /** used to realMapDisplay flags (agents start and ends) */
    Pane FXMLmapFlags;

    /* elements that defines size of Map */
    @FXML
    TextField FXMLmapSizeX;
    @FXML
    TextField FXMLmapSizeY;

    /** element used to show agents.*/
    @FXML
    ListView<Agent> agentListView;

    /** used to realMapDisplay some important info */
    @FXML
    Label infoLabel;

    /* *********************REAL MAP CONTROLLS***************************/
    /** Map edge length textfield for real map   */
    @FXML
    TextField FXRMedgeLength;
    /** Map edge width textfield for real map   */
    @FXML
    TextField FXRMedgeWidth;
    /** Used to show total size of the map */
    @FXML
    Label FXRMTotal;
    /** Map edge length textfield for real map used for print  */
    @FXML
    TextField FXRMedgeLengthMm;
    /*Map edge width textfield for real map used for print  */
    @FXML
    TextField FXRMedgeWidthMm;

    /* ******************* Solution Controlls*******************************************/
    /** choice box with solver options */
    @FXML
    ChoiceBox FXMLsolverOption;
    /** list with solver processes */
    @FXML
    ListView<SolverListItem> FXMLsolverProcess;

    @FXML TextField FXMLSoltionName;

    @FXML
    CheckBox FXMLSolverCustomBounds;

    /* ******************** settings ************************************************ */
    @FXML
    TextField FXRSettingsWorkDir;
    @FXML
    TextField FXRSettingsIfaceFile;
    @FXML
    TextField FXRSettingsPicatAnotString;
    @FXML
    TextField FXRSettingsPredicateFromCustomFile;

    @FXML
    TextField FXRSettingsOzobotTemplateFile;

    /* **************simulator *******************************************************/
    @FXML
    Slider FXSimulationSlider;
    @FXML
    Slider FXSimulationScaleSlider;
    @FXML
    Pane FXSimulationControlPane;
    @FXML
    Pane FXSimulationMapPane;
    @FXML
    Pane FXSimulationInfoMapPane;

    @FXML /* NOTE THIS ELEMENT IS MOUSE TRANSPARENT */
    Pane FXSimulationTimeMarkPane;
    @FXML
    ScrollPane FXSimulationScrollPane;
    //@FXML
    //Button FXSimulationBtnStop;

   // @FXML
    //Button FXSimulationBtnPlay;
    @FXML
    Slider FXSimulationPathDisplaySlider;

    @FXML /* bar to show agent names */
    Pane FXMLSimulationAgentBar;

    /** elements to handle Time duration actions **/
    @FXML
    TextField FXRTimeName;
    @FXML
    TextField FXRTimeDuration;
    @FXML
    Button FXRActDurDelete;
    @FXML
    Button FXRActDurEdit;
    @FXML
    Button FXRActDurAdd;

    @FXML
    Button FXRActDurSave;
    @FXML
    Button FXRActDurReset;

    @FXML
    ListView FXRActionDurationList;

    @FXML
    CheckBox CustomActionCheckbox;

    @FXML
    Button FXMLactionCategoryRemove;
    @FXML
    Button FXMLactionCategoryAdd;
    @FXML
    ChoiceBox FXMLactionCategory;


    /* *************************variables *****************************************/

    MainFormControllerLogic mfcl = new MainFormControllerLogic();

    private final Logger logger = Logger.getLogger(MainFormController.class);

    /** takes care about map */
    MapView mv;
    /* takes care about agents adding, removing, displaying. */
    AgentManager am;
   /** takes care about running solver, exporting to files. */
    PicatManager pm;

    /** some kind of controll that allows others to controll main form */
    MainFormDelegate mfd;
    /** takes care about displaying simulation.  */
    Simulator sim;

    /** contains every button on scene */
    ArrayList<Button> buttons;
    /** manages action durations */
    ActionDurationManager tdm;

    UserButtonAccessManager ubam;

    /** solution that was shown last time */
    Solution activeSolution = null;



    /**************************************************************************/

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mv = new MapView(FXMLmapGrid,FXMLmapObstacles,0,0);
        mv.setSourceinfoLabel(infoLabel);
        // Set background color so it will match void space
        FXMLmapGrid.setStyle(String.format("-fx-background-color: %s;", Consts.mapBackGroundColor));

        // manager for agents
        am = new AgentManager(agentListView,mv.mvs,FXMLmapFlags);
        //afm = new AgentFlagManager(FXMLmapFlags,mv.mvs);

        mfd = new MainFormDelegate(this);
        // Initialize picat manager (alias solver Manager)
        pm = new PicatManager(FXMLsolverOption,FXMLsolverProcess);
        pm.preprocessPicatInterface();
        // fills button (to be able to disable and enable them
        buttons = MainFormControllerHelpout.getAllButtons(mainAnchor);
        for (Button b: buttons) {
            logger.debug(String.format("Found Button: %s",b.getText()));
        }

        //set default values into nodes.
        FXRMedgeLength.setText(Consts.defaultEdgeLength+"");
        FXRMedgeWidth.setText(Consts.defaultEdgeWidth+"");

        FXRMedgeLengthMm.setText(Consts.defaultEdgeLengthForPrintMm+"");
        FXRMedgeWidthMm.setText(Consts.defaultEdgeWidthForPrintMm+"");

        //Initialize escape action
        mfcl.initKeyPressHandler(mfd);




        sim = new Simulator(FXSimulationSlider, FXSimulationMapPane,FXSimulationControlPane,
                FXSimulationScaleSlider,FXSimulationTimeMarkPane,FXSimulationScrollPane,mv.mvs,
                FXMLSimulationAgentBar,FXSimulationPathDisplaySlider,FXSimulationInfoMapPane);

        /** manages settings for update and save actions */
        tdm = new ActionDurationManager(FXRActionDurationList,FXRTimeName,FXRTimeDuration,FXRActDurDelete,FXRActDurEdit,
                FXRActDurAdd,FXRActDurSave,FXRActDurReset,FXMLactionCategory,FXMLactionCategoryAdd,FXMLactionCategoryRemove);


        /* manages button enable/disable logic  */
        ubam = new UserButtonAccessManager(buttons);
        ubam.refresh();

        buttonEnableDisableManagementInit();

        //sim
        tdm.renewSimulatorTimes(() -> {
            if (CustomActionCheckbox.isSelected()) {
                solverShowSolutionRenewLast();
            }
        });

        /** invokes graph reloading on duration changes */
        CustomActionCheckbox.selectedProperty().addListener((observable, oldValue, newValue) ->  {
            if (activeSolution != null){
                this.solverShowSolutionRenewLast();
            }

        });

        //initialize settings variables (puts default values into text fields)
        solverSettingsCancel();

      /*  ccc.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyDataModel>() {

            @Override
            public void changed(ObservableValue<? extends MyDataModel> observable, MyDataModel oldValue, MyDataModel newValue) {
                // Your action here
                System.out.println("Selected item: " + newValue);
            }
        });*/

    }

    /************************* Mouse Click Events *************************************************/
    @FXML
    private void handleIteractionLayerOnMouseClicked(MouseEvent event)
    {
        mv.clickAction(event);
    }

    public void addObstacleAction(ActionEvent actionEvent) {
        mv.addEdgeButtonClick();
    }


    public void removeObstacleAction(ActionEvent actionEvent) {
        mv.removeEdgeButtonClick();
    }

    public void noneObstacleAction(ActionEvent actionEvent) {
        mv.noneEdgeButtonClick();
    }

    /** saves map to file */
    public void mapSaveAction(){
        logger.debug("Map Save Clicked");

        MapViewFileWorker mvfw = new MapViewFileWorker();
        mvfw.SaveToFile(mv);
    }

    /** load map from file*/
    public void mapLoadAction(){
        logger.debug("Map Load Clicked");
        MapViewFileWorker mvfw = new MapViewFileWorker();
        MapData md = mvfw.LoadFromFile();
        if (md != null) {
            mv.loadByData(md);
            ubam.setTrue(UserButtonAccessManager.PS.MAP_VALID);
        } else{
            logger.debug("failed to read data");
        }
    }


    public void CreateNewMap(ActionEvent actionEvent) {
        String strX = FXMLmapSizeX.getText();
        String strY = FXMLmapSizeY.getText();

        try {
            int x = Integer.parseInt(strX);
            int y = Integer.parseInt(strY);
            if ( x < Consts.mapSizeMinX || x > Consts.mapSizeMaxX) throw new NumberFormatException();
            if ( y < Consts.mapSizeMinY || y > Consts.mapSizeMaxY) throw new NumberFormatException();
            mv.clearObstacles();
            mv.setMapSize(x,y);
            ubam.setTrue(UserButtonAccessManager.PS.MAP_VALID);

        } catch (NumberFormatException ex){
            methods.showDialog("Error, invalid input");
        }

    }

    /*******************************AGENT ACTIONS*****************************************/

    public void loadAgentsAction(){
        AgentFileWorker afw = new AgentFileWorker();
         List<Agent> aglist = afw.LoadFromFile();
         if (aglist != null) {
             am.setAgents(aglist);
         }
        agentButtonsCheck();
    }

    public void saveAgentsAction(){
        AgentFileWorker afw = new AgentFileWorker();
        afw.SaveToFile(am.getAgents());

    }

    public void agentAddAction(ActionEvent actionEvent) {
        logger.debug("Agent Add button Pressed");

        if (!mv.agentIsAddable(am.getAgents().size()+1)){
            helpout.methods.showDialog(Consts.errorNoPlaceForAgent);
            return;
        }

        buttonsDisable();
        Agent a = am.CreateAgent(am.getAgents());

        if (a == null) {
            logger.info("Agent Creation Canceled");
            buttonsEnable();
            mv.mapActionState = new MapViewState.NullMapHandler();
            return ;
        }

        am.addAgent(a);
        ubam.setTruePasive(UserButtonAccessManager.PS.AGENT_1ATLEAST);
        mv.mapActionState = new MapViewState.AgentSetStartMapHandler(a,mv,am,true,mfd);


    }

    public void agentRemoveAction(ActionEvent actionEvent) {
        logger.debug("Agent Remove button Pressed");

        ObservableList<Agent> selectedAgent = agentListView.getSelectionModel().getSelectedItems();
        if (selectedAgent.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return;
        }

        Agent a = selectedAgent.get(0);
        am.removeAgent(a);
        if (!agentListView.getItems().isEmpty()) {
            agentListView.getSelectionModel().clearSelection();
            agentListView.getSelectionModel().select(0);
        }
        agentButtonsCheck();

    }

    public void setAgentStartAction(ActionEvent actionEvent){
        logger.debug("Agent Set Start Pressed");
        logger.debug("Agent Set Start Pressed");

        ObservableList<Agent> selectedAgent = agentListView.getSelectionModel().getSelectedItems();
        if (selectedAgent.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return;
        }

        Agent a = selectedAgent.get(0);

        logger.debug(String.format("Currently SelectedAgent: %s", a.name));
        mv.mapActionState = new MapViewState.AgentSetStartMapHandler(a,mv,am,false,mfd);

    }

    public void setAgentEndAction(ActionEvent actionEvent){
        logger.debug("Agent Set End Pressed");


        ObservableList<Agent> selectedAgent = agentListView.getSelectionModel().getSelectedItems();
        if (selectedAgent.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return;
        }

        Agent a = selectedAgent.get(0);
        mv.mapActionState = new MapViewState.AgentSetEndMapHandler(a,mv,am,mfd);

    }

    public void setAgentNameAction(){
        am.setAgentName();
    }

    public void setAgentColorAction(){
       am.setAgentColor();
    }

    public void agentButtonsCheck(){
        if (am.getAgents().size() > 0){
            ubam.setTrue(UserButtonAccessManager.PS.AGENT_1ATLEAST);
        } else {
            ubam.setFalse(UserButtonAccessManager.PS.AGENT_1ATLEAST);
        }
    }


    /************************************ REAL MAP CONTROL  ***********************************************************/
    /** creates new realMapView, scales values, and saves them to file */
    public void realMapExportMap(){
        int edgeLength = mv.mvs.edgeLength;
        int edgeWidth = mv.mvs.edgeWidth;

        try {
            edgeLength = Integer.parseInt(FXRMedgeLength.getText());
            edgeWidth = Integer.parseInt(FXRMedgeWidth.getText());
        } catch (Exception e){
            helpout.methods.showDialog(Consts.realMapCanNotParse);
            logger.info("unable to parse edge length or width");
        }

        RealMapManager rmm = new RealMapManager(mv);
        rmm.setEdgeLength( edgeLength);
        rmm.setEdgeWidth(edgeWidth);
        rmm.setForPrint();
        rmm.reinit();
        rmm.SaveMap();

    }

    /** RealMap  displays current setting for screen.  */
    public void realMapDisplay(){
        int edgeLength = mv.mvs.edgeLength;
        int edgeWidth = mv.mvs.edgeWidth;

        try {
            edgeLength = Integer.parseInt(FXRMedgeLength.getText());
            edgeWidth = Integer.parseInt(FXRMedgeWidth.getText());
        } catch (Exception e){
            helpout.methods.showDialog(Consts.realMapCanNotParse);
            logger.info("unable to parse edge length or width");
        }

        RealMapManager rmm = DataStore.rmm ;
        if (rmm != null){
            rmm.closeWindow();
        }

        rmm = new RealMapManager(mv);
        //sends command, what to do on close
        rmm.addExecuteOnExit(() -> ubam.setFalse(UserButtonAccessManager.PS.REALMAP_DISPLAY_PRESENT));

        DataStore.rmm = rmm;

        rmm.setEdgeLength(edgeLength);
        rmm.setEdgeWidth(edgeWidth);

        if (DataStore.rmc != null){
            DataStore.rmc.reinit(rmm.getControllPane(),rmm.getMVS(),am.getAgents());
        }

        rmm.showWindow();
        rmm.reinit();
        ubam.setTrue(UserButtonAccessManager.PS.REALMAP_DISPLAY_PRESENT);

    }

    /** RealMap displays controll window for screen  */
    public void realMapControll(){
        RealMapManager rmm = DataStore.rmm;

        if (DataStore.rmm != null && DataStore.rmc == null){

            Pane controll = rmm.getControllPane();

            DataStore.rmc = new RealMapControll(controll,DataStore.rmm.getMVS(),am.getAgents(),pm,mfd);
            DataStore.rmc.show();
        }else if (DataStore.rmm != null &&  DataStore.rmc != null){
            // reiniting controll
            DataStore.rmc.reinit(rmm.getControllPane(),rmm.getMVS(),am.getAgents());
        }
        else{
            //rmm = null
            methods.showDialog(Consts.noRealDisplayWindowOpened);
        }




    }

    public void realMapPrint(){
        int edgeLength = Consts.defaultEdgeLengthForPrintMm;
        int edgeWidth = Consts.defaultEdgeWidthForPrintMm;

        try {
            edgeLength = Integer.parseInt(FXRMedgeLengthMm.getText());
            edgeWidth = Integer.parseInt(FXRMedgeWidthMm.getText());
        } catch (Exception e){
            helpout.methods.showDialog(Consts.realMapCanNotParse);
            logger.info("unable to parse edge length or width");
        }
        mfcl.realMapPrintLogic(mv,edgeLength,edgeWidth);
    }

    public void realMapUpdateSupposedSize(){
        logger.debug("updating supposed size");
        int edgeLength = Consts.defaultEdgeLengthForPrintMm;
        int edgeWidth = Consts.defaultEdgeWidthForPrintMm;
        FXRMTotal.setText("");
        try {
            edgeLength = Integer.parseInt(FXRMedgeLengthMm.getText());
            edgeWidth = Integer.parseInt(FXRMedgeWidthMm.getText());
        } catch (Exception e){

            logger.info("unable to parse edge length or width");
            return;
        }

        //actually there is map defined
        if (mv != null && mv.mvs != null ) {

            int xmm = Math.round(mv.mvs.sizeX * edgeLength + edgeLength * Consts.mapLineStretch);
            int ymm = Math.round(mv.mvs.sizeY * edgeLength + edgeLength * Consts.mapLineStretch);
            FXRMTotal.setText(String.format("%dx%d mm", xmm, ymm));
        }

    }

    public void escapeButtonPressed(){
        mv.noneEdgeButtonClick();
    }

    /*******************SOLVER CONTROLL *****************************************************/

    public void solverFindAction(){


        SolverOption so = (SolverOption) FXMLsolverOption.getSelectionModel().getSelectedItem();

        //adding custom bounds.
        if (FXMLSolverCustomBounds.isSelected()){

            QuestionName qn = new QuestionName();qn.setQuestionData("Choose lower and upper bound. format \"LOWER UPPER\"","");
            methods.showDialogQuestion(qn);

            if (!qn.getConfirmed()) { return; }
            String[] answ = qn.getAnswer().split(" ");
            if (answ.length != 2) { methods.showDialog(Consts.errorInvalidFormat); return; }
            int low = -1; int upper = -1;
            try {
                low = Integer.parseInt(answ[0]);
                upper = Integer.parseInt(answ[1]);

            } catch (Exception e) {
                methods.showDialog(Consts.errorInvalidFormat);
                return;
            }
            if (low <= 0 || upper <= 0 || low > upper ) {  methods.showDialog(Consts.errorInvalidValue);
                return;
            }
            pm.setandUseCustomBounds(low,upper);
        } else {
            pm.disableCustomBounds();
        }

        if (so == null){ helpout.methods.showDialog("No Solver is chosen"); return; }
        if( !mfcl.problemIsValidCheck(mv.getMapData(),am.getAgents())) { return; }
        pm.findAction(mv.getMapData(),am.getAgents(),so,FXMLSoltionName.getText());

    }

    public void solverShowSolutionRenewLast(){
        if (activeSolution != null){

            Solution sol = mfd.transofrmSolutionTimeIfChecked(activeSolution);
            sim.clearSolution();
            sim.loadSolution(sol,mv.mvs);
        }
    }

    public void solverShowSolution(){
        /* hides prevous solution */
        solverHideSolution();

        Solution sol = pm.getSelectedSolution();
        if (sol != null) {
            activeSolution = sol;
            sol = mfd.transofrmSolutionTimeIfChecked(sol);

            //if (!sol.isEnriched()) { sol.addStageData(am.getAgents(),mv.getMapData());}
            if (!mfcl.currentMapAndSolutionMatch(sol,mv.getMapData(),am.getAgents())){
                //boolean isYes = methods.showDialogYesNo(Consts.solutionDoesNotMatchCurrentMap);
                //if (isYes){
                    am.createBackup();
                    mv.loadByData(sol.getMapData());
                    am.setAgents(sol.getAglist());
                    sim.loadSolution(sol,mv.mvs);
                    int makespan = sim.getMakeSpan();
                    infoLabel.setText(String.format("Makespan %d",makespan));
                    ubam.setTrue(UserButtonAccessManager.PS.MAP_VALID);
                    ubam.setTrue(UserButtonAccessManager.PS.AGENT_1ATLEAST);

                //}
            } else {
                am.createBackup();
                am.setAgents(sol.getAglist());
                sim.loadSolution(sol,mv.mvs);
            }
            ubam.setTrue(UserButtonAccessManager.PS.SOLUTION_SHOWED);
        }

    }

    public void solverHideSolution(){
        ubam.setFalse(UserButtonAccessManager.PS.SOLUTION_SHOWED);
        logger.debug("solverHide called");
        am.restoreFromBackup();
        sim.clearSolution();
        activeSolution = null;

       // methods.showDialog("TO BE DONE");
    }

    public void solverRemoveSolution(){
        logger.debug("solverRemove called");
        pm.removeSelectedSolution();
        if (!FXMLsolverProcess.getSelectionModel().isEmpty()){
            FXMLsolverProcess.getSelectionModel().clearSelection();
            FXMLsolverProcess.getSelectionModel().selectFirst();
        }

    }

    public void solverSolutionSave(){
         pm.saveSelectedSolution();
    }

    public void solverSolutionLoad(){

        pm.loadSelectedSolution();

    }


    public void solverSettingsCancel(){

        Settings s = DataStore.settings;
            FXRSettingsPredicateFromCustomFile.setText(s.customFilePredicat);
            FXRSettingsPicatAnotString.setText(s.picatAnotationString) ;
            FXRSettingsWorkDir.setText(s.workDirectoryPath);
            FXRSettingsIfaceFile.setText(s.defaultPicatInterfaceFileName) ;
            FXRSettingsOzobotTemplateFile.setText(s.ozobotTemplateFile);
            ubam.setFalse(UserButtonAccessManager.PS.SETTINGS_EDITED);

    }

    public void solverSettignsSave(){
        Settings s = new Settings();
        s.customFilePredicat =FXRSettingsPredicateFromCustomFile.getText();
        //s.libDirectoryPath = DataStore.settings.libDirectoryPath;
        s.picatAnotationString = FXRSettingsPicatAnotString.getText();;
        s.workDirectoryPath =FXRSettingsWorkDir.getText();
        s.defaultPicatInterfaceFileName =  FXRSettingsIfaceFile.getText();
        s.ozobotTemplateFile =  FXRSettingsOzobotTemplateFile.getText();

        if (!s.settingsAreValid()){return;}
        DataStore.settings = s;
        SettingsFileWorker.saveSettings(s);
        pm.preprocessPicatInterface();
        ubam.setFalse(UserButtonAccessManager.PS.SETTINGS_EDITED);

    }

    public void ozobotCodeExport(){
        Solution sol = pm.getSelectedSolution();
        if (sol != null) {
            OzoCodeExport oce = new OzoCodeExport();
            oce.doExport(sol);
        } else {
            methods.showDialog(Consts.infoNothingSelected);
            return;
        }


    }



    /********************************** Simulator controll*********************************************/

    //public void simulatorPlusAction(){}

    //public void simulatroMinusAction(){}

    public void simulatorPlayAction(){
        sim.buttonPlayAction();
    }

    public void simulatorStopAction(){
        sim.buttonStopAction();
    }

    /*public void simulatorShowAction(){

    }*/


    /********************************************************************************************/

    public void buttonsEnable(){
        /*for (Button b: buttons) {
            logger.debug(String.format("Enable Button: %s",b.getText()));
            b.setDisable(false);
        }*/

        ubam.refresh();

        agentListView.setDisable(false);
        FXMLsolverProcess.setDisable(false);
        FXRActionDurationList.setDisable(false);
    }
    public void buttonsDisable(){
        for (Button b: buttons) {
            logger.debug(String.format("Disable Button: %s",b.getText()));
            b.setDisable(true);
        }
        agentListView.setDisable(true);
        FXMLsolverProcess.setDisable(true);
        FXRActionDurationList.setDisable(true);

    }

    public boolean useCustomActions(){
        return CustomActionCheckbox.isSelected();
    }


    private void buttonEnableDisableManagementInit(){
        /* manages selection change of agents list view */
        agentListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Agent>() {
            @Override
            public void changed(ObservableValue<? extends Agent> observable, Agent oldValue, Agent newValue) {
                logger.debug("agent selection changed");
                ObservableList<Agent> selectedAgent = agentListView.getSelectionModel().getSelectedItems();
                if (selectedAgent.size()  == 0) {
                    ubam.setFalse(UserButtonAccessManager.PS.AGENT_SELECTED);
                } else {
                    ubam.setTrue(UserButtonAccessManager.PS.AGENT_SELECTED);
                }
            }
        });

        /* manages selection change of solver choice box (solver type) list view */
        FXMLsolverOption.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SolverOption>() {
            @Override
            public void changed(ObservableValue<? extends SolverOption> observable, SolverOption oldValue, SolverOption newValue) {
                logger.debug("solver type selection changed");
                SolverOption so = (SolverOption)FXMLsolverOption.getSelectionModel().getSelectedItem();
                if (so == null) {
                    logger.debug("no solver selected");
                    ubam.setFalse(UserButtonAccessManager.PS.SOLVER_TYPE_CHOSEN);
                } else {
                    ubam.setTrue(UserButtonAccessManager.PS.SOLVER_TYPE_CHOSEN);
                }
            }
        });

        FXMLsolverProcess.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    logger.debug("solution selection changed");
                    if (FXMLsolverProcess.getSelectionModel().getSelectedItems().isEmpty()) {
                        logger.debug("no solverProcess selected");
                        ubam.setFalse(UserButtonAccessManager.PS.SOLVER_RESULT_SELECTED);
                    } else {
                        ubam.setTrue(UserButtonAccessManager.PS.SOLVER_RESULT_SELECTED);
                    }
                }
        );



        ChangeListener<String> settingsChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ubam.setTrue(UserButtonAccessManager.PS.SETTINGS_EDITED);
            }
        };
        FXRSettingsWorkDir.textProperty().addListener(settingsChangeListener);
        FXRSettingsIfaceFile.textProperty().addListener(settingsChangeListener);
        FXRSettingsPicatAnotString.textProperty().addListener(settingsChangeListener);
        FXRSettingsPredicateFromCustomFile.textProperty().addListener(settingsChangeListener);
        FXRSettingsOzobotTemplateFile.textProperty().addListener(settingsChangeListener);


        FXMLactionCategory.getSelectionModel().selectedItemProperty().addListener(observable -> {
            Object o = FXMLactionCategory.getSelectionModel().getSelectedItem();
            if (o == null){
                ubam.setFalse(UserButtonAccessManager.PS.ACTION_VALID_CATEGORY);
            } else {
                ubam.setTrue(UserButtonAccessManager.PS.ACTION_VALID_CATEGORY);
            }
        });


        FXRActionDurationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    logger.debug("active action duration item");
                    if (FXRActionDurationList.getSelectionModel().getSelectedItems().isEmpty()) {
                        logger.debug("no action_duration selected");
                        ubam.setFalse(UserButtonAccessManager.PS.ACTION_ITEM_SELECTED);
                    } else {
                        ubam.setTrue(UserButtonAccessManager.PS.ACTION_ITEM_SELECTED);
                    }
                }
        );

        tdm.setButtonControl(()->{ ubam.setFalse(UserButtonAccessManager.PS.ACTION_CATEGORY_EDITED);},
                ()->{ ubam.setTrue(UserButtonAccessManager.PS.ACTION_CATEGORY_EDITED);},
                ()->{ ubam.setFalse(UserButtonAccessManager.PS.ACTION_ITEM_EDITED);},
                ()->{ ubam.setTrue(UserButtonAccessManager.PS.ACTION_ITEM_EDITED);});

    }


    /*********************************************************************************************/

    /** invoked by printing F2. if firs argumetn was mapfile, and second agent file, reads both of them. */
    public void debugLoad(){
        if (DataStore.args.length >=2) {
            String fleMap = DataStore.args[0];
            String fleAgs = DataStore.args[1];

            //loads map
            File fr = new File(fleMap);
            MapViewFileWorker mvfw = new MapViewFileWorker();
            mv.loadByData(mvfw.LoadMapData(fr));

            //load agents
            fr = new File(fleAgs);
            AgentFileWorker afw = new AgentFileWorker();
            am.setAgents(afw.LoadAgents(fr));

            ubam.setTrue(UserButtonAccessManager.PS.MAP_VALID);
            ubam.setTrue(UserButtonAccessManager.PS.AGENT_1ATLEAST);

        } else {
            methods.showDialog("No args specified");
        }

    }
}
