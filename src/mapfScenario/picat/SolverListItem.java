package mapfScenario.picat;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/** Wrapper aroun SolverProcess which allows renaming solution on run. (Because of some JAVAFX constrains */
public class SolverListItem {

    public SolverProcess solverProcess;
    StringProperty displayName = new SimpleStringProperty();

    public SolverListItem(SolverProcess sp){
        this.solverProcess = sp;
    }



    public static Callback<SolverListItem, Observable[]> extractor() {
        return new Callback<SolverListItem, Observable[]>() {
            @Override
            public Observable[] call(SolverListItem param) {
                return new Observable[]{param.displayName};
            }
        };
    }

    @Override
    public String toString() {
        return displayName.getValue();
    }

}
