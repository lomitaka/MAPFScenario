package mapfScenario;

import mapfScenario.simulation.Solution;
import mapfScenario.simulation.SolutionDurationModifier;

/** used to pass some logic from main form controller to another classes. */
public class MainFormDelegate {
    MainFormController mfc ;

    public MainFormDelegate(MainFormController mfc){
        this.mfc = mfc;
    }

    public void DisableButtons(){
        mfc.buttonsDisable();
    }

    public void UnlockButtons(){
        mfc.buttonsEnable();
    }

    public void escapeAction() {mfc.escapeButtonPressed();}

    public void debugLoad() { mfc.debugLoad(); }

    /** if in Actions is checked check box, performs update of default action times. */
    public Solution transofrmSolutionTimeIfChecked(Solution inSolution) {
        if (mfc.useCustomActions()) {
            return SolutionDurationModifier.cloneSolution(inSolution, mfc.tdm.getActDurMap());
        }
        return inSolution;
    }

    /** invokes read from DataStore.autoloadedsols, and add them to solution listview */
    public void loadAutoloadedSols() {
        mfc.pm.loadAutoloadedSols();
    }

}
