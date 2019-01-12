package mapfScenario;

import helpout.methods;

import java.io.File;

/** this is wrapper for user changeable settings. */
public class Settings {
    public String workDirectoryPath;
    //public String libDirectoryPath;
    /** picat_interface.pi not path */
    public String defaultPicatInterfaceFileName;
    /** [SCENARIO_EXPORT] */
    public String picatAnotationString;

    public String customFilePredicat;

    public String ozobotTemplateFile;

    /** checks if settings are valid */
    public boolean settingsAreValid(){
        File f = methods.fileToAbsolute(workDirectoryPath);
        //File f = new File(workDirectoryPath);
        if (!f.isDirectory()) {
            helpout.methods.showDialog(Consts.errorWrongWorkdDirectory);
            return false;

        }
        /*f = new File(libDirectoryPath);
        if (!f.isDirectory()) {
            helpout.methods.showDialog(Consts.errorWrongLibDirectory);
            return false;

        }*/
        //f = new File(ozobotTemplateFile);
        f = methods.fileToAbsolute(ozobotTemplateFile);
        if (!f.isFile()){
            helpout.methods.showDialog(Consts.errorInvalidTemplateFile);
            return false;
        }
        //f = new File(defaultPicatInterfaceFileName);
        f = methods.fileToAbsolute(defaultPicatInterfaceFileName);
        if (!f.isFile()){
            helpout.methods.showDialog(Consts.errorInvalidInterfaceFile);
            return false;
        }

        if (this.customFilePredicat.length() == 0 || customFilePredicat.contains("(") ||
                customFilePredicat.contains(")") || customFilePredicat.contains(",") ||
                customFilePredicat.contains(".") || customFilePredicat.contains("%")){
            helpout.methods.showDialog(Consts.errorInvalidpredicate);
            return false;
        }

        if (this.customFilePredicat.length() == 0 || customFilePredicat.contains("(") ||
                customFilePredicat.contains(")") || customFilePredicat.contains(",") ||
                customFilePredicat.contains(".") || customFilePredicat.contains("%")){
            helpout.methods.showDialog(Consts.errorInvalidpredicate);
            return false;
        }

        if (picatAnotationString.length() == 0 || customFilePredicat.contains("%")){
            helpout.methods.showDialog(Consts.errorInvalidAnotationString);
            return false;
        }
        return  true;
    }

}
