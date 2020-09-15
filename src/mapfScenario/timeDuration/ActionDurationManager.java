package mapfScenario.timeDuration;


import fileHandling.ActionDurationFileWorker;
import helpout.QuestionName;
import helpout.methods;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Pair;
import mapfScenario.Consts;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Manages action durations. when check box in Action is checked, each click of show solution and start
 * in real map controll modifies solution to have times selected in current category box.  */
public class ActionDurationManager {

    public interface Command{
        void execute();
    }

    Command action_category_edited_true;
    Command action_item_edited_true;
    Command action_category_edited_false;
    Command action_item_edited_false;
    Command renew_simulator_times;

    /** list with action durations  */
    ListView timeListView;

    TextField actionNameTextField;
    TextField actionDurationTextField;

    Button buttonDelete;
    Button buttonEdit;
    Button buttonAdd;

    Button buttonSave;
    Button buttonReset;
    /** list of action durations used in timeListView */
    ObservableList<ActionDuration> actDurObsList;

    private ObservableList<ActionCategory> categoryItems;
    private ChoiceBox categoryBox;

    private ActionCategory activeCategory=null;

    private final Logger logger = Logger.getLogger(ActionDurationManager.class);

    boolean categoryChanged = false;boolean updatefalse;


 /** crates manager, and assigns code to controlls */
    public ActionDurationManager(ListView timeListView, TextField name, TextField duration, Button del, Button edit, Button add,
                                 Button save, Button reset,ChoiceBox categoryBox,Button addCategory,Button removeCategory
    ){
        this.actionNameTextField = name;
        this.actionDurationTextField = duration;
        this.buttonEdit = edit;
        this.buttonDelete = del;
        this.buttonAdd = add;
        this.buttonReset = reset;
        this.buttonSave = save;

        this.timeListView = timeListView;

        this.categoryBox = categoryBox;


        /* Propojuji datalistView s displayedIR, agent tridou IRememberableCell */
        // int agentObsList;
        actDurObsList  =  FXCollections.<ActionDuration>observableArrayList();
        timeListView.setItems(actDurObsList);
        timeListView.setCellFactory(new Callback<ListView<ActionDuration>,
                                             ListCell<ActionDuration>>() {
                                         @Override
                                         public ListCell<ActionDuration> call(ListView<ActionDuration> list) {
                                             return new ActionDurationCellDisplay();
                                         }
                                     }

        );

        categoryItems = FXCollections.<ActionCategory>observableArrayList();

        categoryBox.setItems(categoryItems);
        List<ActionCategory> loaded = ActionDurationFileWorker.loadActionCategories();

        categoryItems.addAll(loaded);

        //solverOptionBox.getItems()

        //hook to select files
        categoryBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            if (categoryChanged && !updatefalse) {
                if(!methods.showDialogYesNo(Consts.categoryNotSavedQuestion)){
                    updatefalse = true;
                    categoryBox.getSelectionModel().select(oldSelection);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updatefalse = false;
                        }
                    });
                    return;
                } else {
                    categoryChanged= false;
                }
            }

            logger.debug("selection changed");
            if (newSelection != null  ) {
                ActionCategory ac= (ActionCategory) newSelection;


                    actDurObsList.clear();
                    actDurObsList.addAll(ac.getActionDurationListClone());
                    activeCategory = ac;
                    renew_simulator_times.execute();
            }
        });


        //update displayed values
        timeListView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<ActionDuration>() {
                    public void changed(ObservableValue<? extends ActionDuration> observable,
                                        ActionDuration oldValue, ActionDuration newValue) {
                        if (newValue != null){
                            actionNameTextField.setText(newValue.actionName);
                            actionDurationTextField.setText(newValue.actionDurations+"");
                        }
                    }
                });




        //initialize button behaviour

        //delete
        buttonDelete.setOnAction(a-> {
            ActionDuration selectedAction = getSelectedAction();
            if (selectedAction != null) {
                actDurObsList.remove(selectedAction);
            }
            action_category_edited_true.execute();
            renew_simulator_times.execute();
            categoryChanged = true;
            // if after deletion there is some item selects it to not confuse button access manager)
           if (!timeListView.getSelectionModel().isEmpty()){
               timeListView.getSelectionModel().clearSelection();
               timeListView.getSelectionModel().selectFirst();
           }


        });

        //edit
        buttonEdit.setOnAction(a-> {

            ActionDuration selectedAction = getSelectedAction();
            if (selectedAction != null) {
                String oldName = selectedAction.actionName;

                //parsed is null if not have right.
                Pair<String, Integer> parsed = getCurrentValues();
                if (parsed == null) { return;}
                selectedAction.actionName = "";


                if (pairIsUnique(parsed.getKey())){
                    selectedAction.actionName = parsed.getKey();
                    selectedAction.actionDurations = parsed.getValue();
                    timeListView.refresh();
                    action_category_edited_true.execute();
                    action_item_edited_false.execute();
                    renew_simulator_times.execute();
                    categoryChanged = true;
                } else {
                    selectedAction.actionName = oldName;
                    methods.showDialog(Consts.errorInvalidActionNimeOccupied);
                    return;
                }


            }

        });

        //add
        buttonAdd.setOnAction(a-> {
            Pair<String, Integer> parsed = getCurrentValues();
            if (parsed == null) { return; }

            if (!pairIsUnique(parsed.getKey())) {
                methods.showDialog(Consts.errorInvalidActionNimeOccupied);
                return;
            }

            ActionDuration ad = new ActionDuration(parsed.getKey(), parsed.getValue());
            if (ad != null) {


                actDurObsList.add(ad);
            }
            if (activeCategory != null) {
                //activeCategory.updateList(actDurObsList);
                action_category_edited_true.execute();
                renew_simulator_times.execute();
                categoryChanged = true;
            }


        });


        buttonReset.setOnAction(a->{
            if (activeCategory == null) { logger.debug("no active category"); return; }

            actDurObsList.clear();
            actDurObsList.addAll(activeCategory.getActionDurationList());
            action_category_edited_false.execute();
            renew_simulator_times.execute();
            categoryChanged = false;
            ///List<ActionDuration> ad = ActionDurationFileWorker.loadActionCategories();
            //actDurObsList.clear();
            //actDurObsList.addAll(ad);

        });

        //save
        buttonSave.setOnAction(a-> {

            List<ActionDuration> adlist = new ArrayList<>();
            for (ActionDuration ad : actDurObsList) {adlist.add(ad);}
            if (activeCategory != null)
            activeCategory.setActionDurationList(adlist);
            action_category_edited_false.execute();
            globalSave();
            categoryChanged = false;
        });

        //add category button
        addCategory.setOnAction(a-> {
            QuestionName qn = new QuestionName();qn.setQuestionData("New category name:","");
            methods.showDialogQuestion(qn);
            String Name = qn.getAnswer();
            if (!qn.getConfirmed() || Name == null || Name.length() == 0) { return; }

            ActionCategory ac = new ActionCategory(Name);
            categoryItems.add(ac);

        });


        //rempve category button
        removeCategory.setOnAction(a-> {

            if (activeCategory == null) { logger.debug("no active category"); return; }

            if (!methods.showDialogYesNo(Consts.removeCategoryQuestion)){ return;}

            actDurObsList.clear();
            categoryItems.remove(activeCategory);
            activeCategory = null;
            globalSave();
        });


        ChangeListener<String> settingsChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (actionNameTextField.isFocused() || actionDurationTextField.isFocused()) {
                    action_item_edited_true.execute();
                }
            }
        };
        actionNameTextField.textProperty().addListener(settingsChangeListener);
        actionDurationTextField.textProperty().addListener(settingsChangeListener);

    }

    public void setButtonControl(Command action_category_edited_false,Command action_category_edited_true,
                                 Command action_item_edited_false, Command action_item_edited_true){
        this.action_category_edited_false = action_category_edited_false;
        this.action_category_edited_true = action_category_edited_true;
        this.action_item_edited_false = action_item_edited_false;
        this.action_item_edited_true = action_item_edited_true;

    }

    public void renewSimulatorTimes(Command renew){
        renew_simulator_times = renew;
    }


    public ActionDuration getSelectedAction(){
        ObservableList<ActionDuration> selectedAction =  timeListView.getSelectionModel().getSelectedItems();
        if (selectedAction.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return null;
        }

        return selectedAction.get(0);
    }

    /** read values from name, and duration text boxes */
    public Pair<String,Integer> getCurrentValues(){
        String action = actionNameTextField.getText();
        String duration = actionDurationTextField.getText();

        if (action.length() == 0){
            methods.showDialog(Consts.errorInvalidActionName);
            return null;
        }

        int intDuration = -1;
        try {
            intDuration = Integer.parseInt(duration);
        } catch (Exception e){

        }

        if (intDuration <=0 ) {
            methods.showDialog(Consts.errorInvalidActionDuration);
            return null;
        }



        return new Pair<>(action,intDuration);
    }

    /** checks if pair already exists */
     boolean pairIsUnique(String actionName){
        for (ActionDuration ad : actDurObsList) {
            if (ad.actionName.equals(actionName)){
                return false;
            }
        }
        return true;
    }


    /** return action duration of active rules */
    public HashMap<String,Integer> getActDurMap(){
        /** returns action duration map of selected category */
        /*if (activeCategory != null){
            return activeCategory.getActionDurationMap();}
            else {
            return new HashMap<>();
        }*/
        HashMap<String,Integer> result = new HashMap<>();
        for(ActionDuration ad : actDurObsList){
            result.put(ad.actionName,ad.actionDurations);
        }
        return result;
    }


    /** save every action duration into file */
    public void globalSave(){
        List<ActionDuration> resultList = new ArrayList<>();
        for (ActionCategory ac: categoryItems) {
            for (ActionDuration ad: ac.getActionDurationList()) {

                String inString = String.format("%s.%s",ac.displayName,ad.actionName);
                resultList.add(new ActionDuration(inString,ad.actionDurations));
            }
        }



        ActionDurationFileWorker.saveSettings(resultList);
    }




}
