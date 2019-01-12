package mapfScenario.picat;

import java.io.Serializable;

/** This is item of choice box with available solvers */
public class SolverOption implements Serializable {

    /** file with full path to picat loadable file */
    public String targetFilePath;
    /** predicat that has two arguments and is in targetFilePath */
    public String predicatName;
    /** optional how will be displayed predicat on screen */
    public String displayName;

    @Override
    public String toString() {
        if (displayName.length() == 0)
        { return predicatName;}
        else {
            return displayName;
        }

    }
}
