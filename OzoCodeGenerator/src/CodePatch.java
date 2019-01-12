import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.List;
import java.util.Random;

/**
 * takes care for right extension of the code.
 * */

public class CodePatch implements CodePatchIface {

    public CodePatch(List<String> commands){
        this.commands = commands;
    }

    @Override
    public void write(ContentHandler target) {
        doExtend(target);
    }

    /* list of instructions ie name of functions that should be called */
    private List<String> commands;

    public void doExtend(ContentHandler target){

        if (commands == null ) { System.out.println("PATH SHOULD NOT BE NULL"); }
        doExtend(commands,0,target);

    }

    public void extendTest(ContentHandler target){

        String elementName = "TEST";

        //Attributes attr = new Attributes();

        try {
            target.startElement("","",  elementName, null);
        } catch (SAXException e) {
            e.printStackTrace();
        }

        try {
            target.endElement("",  "",elementName);
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }



/**
* recursive method, iterates over list of commands, and generates chain of embeded elements.
*EXAMPLE
* 		<block type="procedures_callnoreturn" id="F.)=*Mh7rT[Bn8zW]iX0">
* 			<mutation name="goWorward"></mutation>
* 			<next>
* 				<!-- LAST BLOCK -->
* 				<block type="procedures_callnoreturn" id="ZB9HToEjFAczhHxuzl|0">
* 				<mutation name="Flash End"></mutation>
* 				</block>
* 			</next>
* 		</block>
     *
     * */
    private void doExtend(List<String> path, int step , ContentHandler target){
        try {
            // IT IS NOT LAST ELEMENT
            if (step  < path.size()  ) {
                String elementName = "next";
                target.startElement("",  "",elementName, null);

                {
                    /* ELEMENT BLOCK */
                    elementName = "block";
                    AttributesImpl atts = new AttributesImpl();
                    //Attributes attr = new Attributes();
                    atts.addAttribute("", "", "id", "", generateID());
                    atts.addAttribute("", "", "type", "", "procedures_callnoreturn");
                    target.startElement("", "",elementName,  atts);

                    {
                        /* ELEMENT MUTATUION */
                        elementName = "mutation";
                        atts = new AttributesImpl();
                        //Attributes attr = new Attributes();
                        atts.addAttribute("", "", "name", "", path.get(step).toString());
                        target.startElement("","",  elementName, atts);

                        target.endElement("",  "",elementName);


                        //Next element will be the last


                            step++;
                            doExtend(path, step, target);

                    }

                    elementName = "block";
                    target.endElement("",  "",elementName);
                }
                elementName = "next";
                target.endElement("", "",elementName);

            }
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }


    /* source characters for id - inspired by ozoblockly */
    static String kg="!#$%()*+,-./:;=?@[]^_`{|}~ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String generateID(){/*Fb()*/
        StringBuilder sb = new StringBuilder(20);
        int a = kg.length();
        Random rnd = new Random();
        for (int i = 0 ; i < 20; i++)
        {
            sb.append(kg.charAt(rnd.nextInt(a)));
        }
        return sb.toString();

    }



}
