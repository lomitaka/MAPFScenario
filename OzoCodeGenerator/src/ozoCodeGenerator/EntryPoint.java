package ozoCodeGenerator;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Main class for SAX parsing
 *
 * This class is used to write Solution to file with instructions of ozobot.
 * It takes as arguments: file that picat created, template file which contains predefined instructions,
 * output file, and number of agent to write actions for. can hanlde -1 or all, then it will write action of every agent
 * in solution and split them in switch which separates them by agent ID.
 * Uses Sax parsing.
 *
 */
public class EntryPoint {

    // Path to input file
    //private static final String INPUT_FILE = "data.in.xml";
    //private static final String OUTPUT_FILE = "data.out.xml";


    /**
     * Main method
     * @param args command line arguments
     */
    public static void main(String[] args) {

        if (args.length < 4){
            System.out.print("Error, expecting tree arguments: " +
                    "<solution_input_file> <template_ozoblockly_xml> <output_ozobolockly_xml> agentNumber");
            return;
        }

        File solIn = new File(args[0]);
        if (solIn == null || !solIn.isFile() || !solIn.exists()){
            System.out.print("Error, input file not found");
            return ;
        }
        File templateFle = new File(args[1]);
        if (templateFle == null || !templateFle.isFile() || !templateFle.exists()){
            System.out.print("Error, template file not found");
            return ;
        }
        File fileOut = new File(args[2]);
        if (fileOut == null ){
            System.out.print("Error, cannot write to file: " + fileOut.getAbsolutePath());
            return ;
        }

        String agentToExportStr = args[3];
        int agentToExport = 0;
        try{
            if (agentToExportStr.toUpperCase().equals("ALL") || agentToExportStr.toUpperCase().equals("-1") ){
                agentToExport = -1;
            }else {
                agentToExport = Integer.parseInt(agentToExportStr);
            }
        } catch (Exception e){
            System.out.print("Cannot parse agent count number");
        }



        ActionFileWorker afw = new ActionFileWorker();
        List<List<String>> cmds= afw.loadSolutionFile(solIn);
        List<String> resultCommands = new ArrayList<>();
        /** export all agnets */
        CodePatchIface cp = null;
        MissingFceChecker mfc = new MissingFceChecker();
        if (agentToExport == -1){

            cp = new CodePatchMutiple(cmds);
            mfc.MissingFceCheckerInitListList(cmds);

        } else {
            /** export only agent number. */
            cp = new CodePatch(cmds.get(agentToExport));
            mfc.MissingFceCheckerInitList(cmds.get(agentToExport));
        }

     /*   ArrayList<String> Commands = new ArrayList<>();
        Commands.add("FlashStart");
        Commands.add("goForward");
        Commands.add("goRight");
        Commands.add("goForward");
        Commands.add("goLeft");
        Commands.add("goLeft");
        Commands.add("goLeft");
        Commands.add("goForward");
        Commands.add("goRight");
        Commands.add("FlashEnd");*/


       // System.out.println(ozoCMDS.goLeft.toString());

        generateXml(cp,templateFle.getAbsolutePath(),fileOut.getAbsolutePath(),mfc);


    }

    public static void directRun(String inputFile,String template,File fileOut,Integer agentToExport) {


        File solIn = new File(inputFile);
        if (solIn == null || !solIn.isFile() || !solIn.exists()){
            System.out.print("Error, input file not found");
            return ;
        }
        File templateFle = new File(template);
        if (templateFle == null || !templateFle.isFile() || !templateFle.exists()){
            System.out.print("Error, template file not found");
            return ;
        }
      /*  File fileOut = new File(args[2]);
        if (fileOut == null ){
            System.out.print("Error, cannot write to file: " + fileOut.getAbsolutePath());
            return ;
        }*/

        /*String agentToExportStr = args[3];
        int agentToExport = 0;
        try{
            if (agentToExportStr.toUpperCase().equals("ALL") || agentToExportStr.toUpperCase().equals("-1") ){
                agentToExport = -1;
            }else {
                agentToExport = Integer.parseInt(agentToExportStr);
            }
        } catch (Exception e){
            System.out.print("Cannot parse agent count number");
        }*/



        ActionFileWorker afw = new ActionFileWorker();
        List<List<String>> cmds= afw.loadSolutionFile(solIn);
        List<String> resultCommands = new ArrayList<>();
        /** export all agnets */
        CodePatchIface cp = null;
        MissingFceChecker mfc = new MissingFceChecker();
        if (agentToExport == -1){

            cp = new CodePatchMutiple(cmds);
            mfc.MissingFceCheckerInitListList(cmds);

        } else {
            /** export only agent number. */
            cp = new CodePatch(cmds.get(agentToExport));
            mfc.MissingFceCheckerInitList(cmds.get(agentToExport));
        }

     /*   ArrayList<String> Commands = new ArrayList<>();
        Commands.add("FlashStart");
        Commands.add("goForward");
        Commands.add("goRight");
        Commands.add("goForward");
        Commands.add("goLeft");
        Commands.add("goLeft");
        Commands.add("goLeft");
        Commands.add("goForward");
        Commands.add("goRight");
        Commands.add("FlashEnd");*/


        // System.out.println(ozoCMDS.goLeft.toString());

        generateXml(cp,templateFle.getAbsolutePath(),fileOut.getAbsolutePath(),mfc);


    }


    public static void generateXml(CodePatchIface cp,String templateFile,String outputFile, MissingFceChecker mfc){

        try {


            // Create parser instance
            XMLReader parser = XMLReaderFactory.createXMLReader();

            // Create input stream from source XML document
            InputSource source = new InputSource(templateFile);

            /* set tranformation handeler with output to file */
            SAXTransformerFactory factory = (SAXTransformerFactory)TransformerFactory.newInstance();

            TransformerHandler serializer = factory.newTransformerHandler();
            //OMIT XML DECLARATION
            serializer.getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            //serializer.getTransformer().set
            Result result = new StreamResult(outputFile);
            serializer.setResult(result);


            XMLFilterImpl filter = new XmlTraverser(cp,mfc);
            //filter .setOmitXMLDeclaration(true);
            filter.setContentHandler(serializer);


            //OLD
            parser.setContentHandler(filter);

            parser.parse(source);


        } catch (Exception e) {

            e.printStackTrace();

        }

    }






}








