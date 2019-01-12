package mapfScenario.picat;

import java.io.File;
import java.lang.reflect.Field;

/** This EntryPoint belongs to PicatWrapper.
 * It is one simple procedure containing main, with arguments.
 * It takes arguments and call solver.
 * Picat run environment was modified to be loaded as a library.
 * Main Call expects three arguments: full workdir where native libraries of picat are,
 * called predicate with arguments, that will be passed to picat, and full path to file where predicat is.
 *
 * It is expected that arguments of predicat to call will have input and output files.
 * Input file exists, and contains problem description for picat, and utput file will be created by picat and in
 * will be written solution of problem.
 *
 * */
public class EntryPoint {

    public static void main(String[] args){

        if (args.length > 2){

            System.out.println("calling");

            updateLibraryPath(args[0]);

            PicatWrapper pw = new PicatWrapper();


            pw.callPicatMain(args[0],args[1],args[2]);
            System.out.println(String.format("Done"));


        }else {
            System.out.println("Error: Expecting three arguments: " +
                    "workdir - folder where library is." +
                    "calledPredicate - predicate with argumetns that will be run" +
                    "picatModule that will be run. " +
                    "Example: picatFolder mapf_predicat(\"Problem\",\"Solution\") picat_module.pi");
        }

    }

    private static void updateLibraryPath(String path){

        String prop = System.getProperty("java.library.path");
        System.setProperty( "java.library.path", prop + File.pathSeparator + path  );
        System.out.println("prop: " + prop);

        try {
            Field fieldSysPath = null;
            fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
            fieldSysPath.setAccessible( true );
            fieldSysPath.set( null, null );

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

         prop = System.getProperty("java.library.path");
        System.out.println("prop_changed: " + prop);

    }
}
