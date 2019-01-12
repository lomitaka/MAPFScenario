package mapfScenario.solutionPack;

import mapfScenario.simulation.Solution;

import java.io.File;

/** Packet class representing item of solver task loaded from batch file.  */
public class SolutionAutogenItem {
    String itemName;
    File mapFilePath;
    File agsFilePath;
    String solverName;
    String actionCategory;

    public int firstAgentNum;

    public SolutionAutogenItem(String itemName, File mapFilePath, File agsFilePath, String solverName,
                               String actionCategory){
        this.itemName = itemName;
        this.actionCategory = actionCategory;
        this.mapFilePath = mapFilePath;
        this.agsFilePath = agsFilePath;
        this.solverName = solverName;
    }

}
