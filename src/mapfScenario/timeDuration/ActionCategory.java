package mapfScenario.timeDuration;

import javafx.collections.ObservableList;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *  Class representing category for action durations.
 *  User may have many various categories and can switch beetween them.
 *  This class is used from Action tab.
 * */
public class ActionCategory {

    String displayName;

    List<ActionDuration> actionList;

    public ActionCategory(String name){
        displayName = name;
        actionList = new ArrayList<ActionDuration> ();
    }

    public ActionCategory(String name,List<ActionDuration> ads){
        displayName = name;
        actionList = ads;
    }





    public List<ActionDuration> getActionDurationList(){
        return actionList;
    }

    public List<ActionDuration> getActionDurationListClone(){
        List<ActionDuration> adClone = new ArrayList<>();
        for (ActionDuration ad  : actionList){
            adClone.add(new ActionDuration(ad.actionName,ad.actionDurations));
        }
        return adClone;
    }

    public void setActionDurationList(List<ActionDuration> actionList){
        this.actionList = actionList;
    }

    public void updateList(ObservableList<ActionDuration> actDur){
        actionList.clear();
        for (ActionDuration ad: actDur) {
            actionList.add(ad);

        }
    }


    @Override
    public String toString() {
        return displayName;
    }


    public  HashMap<String,Integer> getActionDurationMap() {

        HashMap<String,Integer> answ = new HashMap<>();
        for (ActionDuration ad: actionList) {
            if (!answ.containsKey(ad)){
                answ.put(ad.actionName,ad.actionDurations);
            }
        }
        return answ;

    }
}
