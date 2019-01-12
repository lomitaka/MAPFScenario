package mapfScenario;

import javafx.scene.control.Button;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.*;

public class UserButtonAccessManager {

    private HashSet<PS> currentStatus = new HashSet<>();
    private HashMap<String,Button> programButtons = new HashMap<>();
    private ArrayList<Pair<List<PS>,List<BL>>> actionMap = new ArrayList<>();
    private final Logger logger = Logger.getLogger(UserButtonAccessManager.class);


    public void refresh() {
        buttonsUpdate();
    }

    public enum PS{ /* PROGRAM STATUS */
        ALWAYS_ON,
        MAP_VALID,
        AGENT_1ATLEAST,
        AGENT_SELECTED,
        SETTINGS_EDITED,
        SOLVER_TYPE_CHOSEN,
        SOLVER_RESULT_SELECTED,
        SOLUTION_SHOWED,
        REALMAP_DISPLAY_PRESENT,
        ACTION_VALID_CATEGORY,
        ACTION_ITEM_SELECTED,
        ACTION_ITEM_EDITED,
        ACTION_CATEGORY_EDITED
    }

    /** Button List **/
    public enum BL {
        /** ALWAYS ENABLED */
        mapdefinition_load,
        agents_load,
        solver_load,
        agents_add,
        actions_plus,
        mapdefinition_create,
        /* mapped */
        mapdefinition_save,
        mapdefinition_add,
        mapdefinition_remove,
        map_definition_none,
        agents_save,
        agents_remove,
        agents_setstart,
        agents_setend,
        agents_setname,
        agents_setcolor,
        realmap_display,
        realmapcontrol,
        realmap_export,
        realmap_print,
        solver_solve,
        solver_show,
        solver_hide,
        solver_remove,
        solver_ozoexport,
        solver_save,
        settings_save,
        settings_cancel,
        actions_minus,
        actions_add,
        actions_edit,
        actions_delete,
        actions_save,
        actions_reset,
        simulation_play,
        simulation_stop
    }




    public UserButtonAccessManager(ArrayList<Button> buttons){


        for (Button b: buttons) {
            programButtons.put(b.getId(),b);
        }
        initializeActionMap();

        currentStatus.add(PS.ALWAYS_ON);

    }

    private void initializeActionMap() {

        List<PS> av;
        List<BL> bl;

        // ALWAYS ON
        av = Arrays.asList(PS.ALWAYS_ON);
        bl = Arrays.asList(BL.mapdefinition_load,BL.agents_load,BL.solver_load,BL.agents_add,BL.actions_plus,BL.mapdefinition_create);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.MAP_VALID);
        bl = Arrays.asList(BL.mapdefinition_save,BL.mapdefinition_add,BL.mapdefinition_remove,BL.map_definition_none,
                BL.realmap_display, BL.realmap_export, BL.realmap_print);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.AGENT_1ATLEAST);
        bl = Arrays.asList(BL.agents_save);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.AGENT_SELECTED);
        bl = Arrays.asList(BL.agents_remove, BL.agents_setstart, BL.agents_setend, BL.agents_setname, BL.agents_setcolor);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.SETTINGS_EDITED);
        bl = Arrays.asList(BL.settings_save, BL.settings_cancel);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.SOLVER_RESULT_SELECTED);
        bl = Arrays.asList(BL.solver_remove,BL.solver_show, BL.solver_save, BL.solver_ozoexport);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.SOLUTION_SHOWED);
        bl = Arrays.asList(BL.solver_hide, BL.simulation_play, BL.simulation_stop);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.REALMAP_DISPLAY_PRESENT, PS.AGENT_1ATLEAST);
        bl = Arrays.asList(BL.realmapcontrol );
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.ACTION_VALID_CATEGORY);
        bl = Arrays.asList(BL.actions_add,BL.actions_minus);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.ACTION_VALID_CATEGORY,PS.ACTION_ITEM_SELECTED);
        bl = Arrays.asList(BL.actions_delete);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.ACTION_VALID_CATEGORY,PS.ACTION_ITEM_SELECTED,PS.ACTION_ITEM_EDITED);
        bl = Arrays.asList(BL.actions_edit);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.ACTION_VALID_CATEGORY,PS.ACTION_CATEGORY_EDITED);
        bl = Arrays.asList(BL.actions_reset,BL.actions_save);
        actionMap.add(new Pair<>(av, bl));

        av = Arrays.asList(PS.SOLVER_TYPE_CHOSEN,PS.MAP_VALID,PS.AGENT_1ATLEAST);
        bl = Arrays.asList(BL.solver_solve);
        actionMap.add(new Pair<>(av, bl));


    }


    public void setTrue(PS ps){
        currentStatus.add(ps);
        buttonsUpdate();
    }

    /** sets flag, but do not reenable buttons */
    public void setTruePasive(PS ps) {
        currentStatus.add(ps);
        buttonsUpdate();
    }

    public  void setFalse(PS ps){
        currentStatus.remove(ps);
        buttonsUpdate();
    }

    private void buttonsUpdate(){

        for (Pair<List<PS>,List<BL>> action : actionMap){
            if (conditionIsTrue(action.getKey())){
                buttonsChangeStatus(action.getValue(),false);
            } else {
                buttonsChangeStatus(action.getValue(),true);
            }
        }


    }


    private boolean conditionIsTrue(List<PS> conditions){


        for (PS p : conditions){
            if (!currentStatus.contains(p)){  return false;}
        }
        return true;
    }

    private void buttonsChangeStatus(List<BL> buttons,boolean setDisable){
        for (BL b : buttons){
            if(!programButtons.containsKey(b.name())) { logger.error("button not found:" + b.name()); continue; }
                Button btn = programButtons.get(b.name());
                btn.setDisable(setDisable);
        }

    }




}
