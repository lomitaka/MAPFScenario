package mapfScenario.timeDuration;

/** pair, for Action, and its duration. */
public class ActionDuration {
    public String actionName;
    /** action duration in ms. */
    public int actionDurations;

    public ActionDuration(String actionName,int actionDurationms){
        this.actionDurations = actionDurationms;
        this.actionName = actionName;
    }
}
