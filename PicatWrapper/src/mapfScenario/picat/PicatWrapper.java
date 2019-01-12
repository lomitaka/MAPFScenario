package mapfScenario.picat;

/** this class represents native library in our case picat execution environment. */
public class PicatWrapper {


    //TO GET C HEADER RUN javac -h <output_dir> mapfScenario.picat.PicatWrapper.java
    // this result file with header add to picat and compile picat as library.
    // function will be called by following way picat -g function\("ahoj","bla"\) test1.pi

    /***
     * calls native library
     * work dir is directory local directory of picat like environment (zero argument in main args.
     * calledPredicate has to match pattern function("problem.path","solution.path")
     * picatFile is file which will be used as picat source program.
     */


    public native void callPicatMain(String workdir ,String calledPredicate,String picatFile);

    static {
        String model = System.getProperty("sun.arch.data.model");
        if (model.equals("64")) {
            System.loadLibrary("picat64");
        } else
        {
            System.loadLibrary("picat32");
        }
    }

}
