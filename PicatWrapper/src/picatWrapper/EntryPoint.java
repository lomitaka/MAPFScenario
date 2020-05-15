package picatWrapper;

import com.sun.javafx.util.Utils;
import javafx.application.Platform;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;


public class EntryPoint {





    public static void main(String[] args){
        OutputContainer oc = new OutputContainer();
        callPicat(args,oc);
        System.out.println(oc.output);
    }


    /** calls picat with output function */
    public static void callPicat(String[] args, OutputContainer output ){
        if (args.length > 2){

            System.out.println("PicatWrapper2 invoked");
            try {
                ProcessBuilder pb = null;

                Utils.isUnix();
                if (Utils.isWindows()){
                    File cygwinCheck = new File(args[0] + File.separator +  "useCygwin");
                    if (cygwinCheck.exists()){
                        System.out.println( String.format("executing: %s %s %s %s  ", "bash", "picatCygwin.sh",  args[1], args[2] ));
                        pb = new ProcessBuilder("bash", "picatCygwin.sh", "\"" + args[1], args[2]);
                    } else {
                        System.out.println( String.format("executing: %s %s %s %s %s  ", "cmd", "/C", "picatWindows.bat", "\"" + args[1].replace("\"", "'") + "\"", args[2]));
                        pb = new ProcessBuilder("cmd", "/C", "picatWindows.bat", "\"" + args[1].replace("\"", "'") + "\"", args[2]);
                    }
                } else if (Utils.isMac()){
                    System.out.println( String.format("executing: %s %s %s %s ", "bash", "picatMac.sh" , args[1],args[2]));
                    pb = new ProcessBuilder("bash", "picatMac.sh" , args[1],args[2]);
                } else if (Utils.isUnix()) {
                    System.out.println( String.format("executing: %s %s %s %s ", "bash", "picatLinux.sh" , args[1],args[2]));
                    pb = new ProcessBuilder("bash", "picatLinux.sh" , args[1],args[2]);
                } else {
                    return;
                }




                File f = projectPath();
                if (f == null){
                    System.out.println("ERROR, location of the PicatWrapper was not found");
                    return;
                }

                pb.directory(f);
                Process p = pb.start();

                StringBuilder picatOutput = new StringBuilder();

                InputStream in = p.getInputStream();
                // InputStream err = proc.getErrorStream();
                //OutputStream ostr = proc.getOutputStream();
                BufferedInputStream bis = new BufferedInputStream(in);

                int newLine = (int)System.getProperty("line.separator").charAt(0);
                int znak = 0;
                while ((znak = bis.read())!= -1) {
                    System.out.print((char) znak);
                    picatOutput.append((char) znak);
                    if (znak == newLine){
                        Platform.runLater(()->{output.output= picatOutput.toString();});
                    }
                }

                InputStream ein = p.getErrorStream();
                // InputStream err = proc.getErrorStream();
                //OutputStream ostr = proc.getOutputStream();
                BufferedInputStream ebis = new BufferedInputStream(ein);

                znak = 0;
                while ((znak = ebis.read())!= -1) {
                    System.out.print((char) znak);
                    picatOutput.append((char) znak);
                    if (znak == newLine){
                        Platform.runLater(()->{output.output= picatOutput.toString();});
                    }
                }

                output.output = picatOutput.toString();

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


    /*static private String projectPath(){
        //return "/home/ivan/IdeaProjects/PicatWrapper2/test/";
        try {
            return new File(EntryPoint.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    /**returns c:/blabla/folder */
    public static File projectPath(){

        try {


            File s = new File(EntryPoint.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();

            //DEBUG Purposses
            if (s.getAbsolutePath().contains("/out/production")){
                File f = new File (EntryPoint.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                for (int i = 0; i < 3;i++) f = f.getParentFile();

                File[] flist = f.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().contains("lib");
                    }
                });

                if (flist.length == 1){
                    return flist[0];
                }
                return null;
            }

            //methods.showDialog(s);
            return s;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

}

