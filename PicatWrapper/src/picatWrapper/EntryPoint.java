package picatWrapper;

import com.sun.javafx.util.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class EntryPoint {


    public static void main(String[] args){
        if (args.length > 2){

            System.out.println("PicatWrapper2 invoked");
            try {
                ProcessBuilder pb = null;
                Utils.isUnix();
                if (Utils.isWindows()){
                    pb = new ProcessBuilder("cmd", "/C","picatWindows.bat" , "\""+args[1].replace("\"","'") + "\"",args[2]);
                } else if (Utils.isMac()){
                    pb = new ProcessBuilder("bash", "picatMac.sh" , args[1],args[2]);
                } else if (Utils.isUnix()) {
                    pb = new ProcessBuilder("bash", "picatLinux.sh" , args[1],args[2]);
                } else {
                    return;
                }


                String x = projectPath();
                if (x == null){
                    System.out.println("ERROR, location of the PicatWrapper was not found");
                    return;
                }
                File f = new File(x).getParentFile();
                pb.directory(f);
                Process p = pb.start();

                p.waitFor();

                System.out.println(String.format("PicatWrapper ends"));
            } catch (IOException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }


        }else {
            System.out.println("Error: Expecting three arguments: \n" +
                    "workdir - folder where library is. \n" +
                    "calledPredicate - predicate with argumetns that will be run \n" +
                    "picatModule that will be run. \n" +
                    "Example: picatWrapperLibFolder mapf_predicat(\"Problem\",\"Solution\") picat_module.pi");
        }
    }


    static private String projectPath(){
        //return "/home/ivan/IdeaProjects/PicatWrapper2/test/";
        try {
            return new File(EntryPoint.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}

