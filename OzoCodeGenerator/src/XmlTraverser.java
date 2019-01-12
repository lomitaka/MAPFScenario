
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.Arrays;
import java.util.List;

/** This file gets elements, and writes passes them to super (which writes them to output)
 * when element ENTRYPOINT is encoutered class will start writing nodes that represent wanted behaviour
 * */
public class XmlTraverser extends XMLFilterImpl {

    boolean startExtension = false;
    MissingFceChecker mfc;
    private CodePatchIface codeExtender;
    public XmlTraverser(CodePatchIface cp,MissingFceChecker mfc) {
        codeExtender = cp;
        this.mfc = mfc;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        super.characters(ch, start, length);
        char[] result = Arrays.copyOfRange(ch,start,start+ length);
        String word =  new String(result);
        if (procedureDefinitionCounter ==2) {
            mfc.noteFceFound(word);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        startExtension = false;

        System.out.println( "START DOCUMENT" );

    }

    int procedureDefinitionCounter = 0;

    public void startElement(String namespaceURI, String localName, String  qName,   Attributes attributes) throws SAXException{
        //System.out.println(qName + " start");

        boolean isEntryPoint = false;
        if (localName.toUpperCase().equals("MUTATION") && attributes != null){
            String value = attributes.getValue("name");
            if (value != null && value.toUpperCase().equals("ENTRYPOINT")){
                isEntryPoint = true;
            }
        }
        if (isEntryPoint){

            // Add Different element
            AttributesImpl atts = new AttributesImpl();
            atts.addAttribute("", "", "name", "", "INJECTED");
            super.startElement(namespaceURI, localName, qName, atts);

            // Extension will start after the end of element
            startExtension = true;


        } else {
            super.startElement(namespaceURI, localName, qName, attributes);
        }

        /* check functions  */
       /*   <block type="procedures_defnoreturn" id="TSWMls)wPZ{Ffdr[)?:k" x="416" y="-384">
            <field name="NAME">INJECTED</field>
            <comment pinned="false" h="80" w="160">Describe this function...</comment>
            </block>*/
       /* increase from 1 to 2 */
        if((procedureDefinitionCounter == 1)&& localName.toUpperCase().equals("FIELD") &&
                attrTest(attributes, "name","NAME")){
            //System.out.println("INCREASE I FIELD");
            procedureDefinitionCounter++;
        }

       /** increase from 0 to 1 */
        if((procedureDefinitionCounter ==  0) && localName.toUpperCase().equals("BLOCK") &&
                attrTest(attributes, "type","procedures_defnoreturn")){
            procedureDefinitionCounter++;
           // System.out.println("INCREASE I BLOCK");
        }


    }


    private boolean attrTest(Attributes atr, String attrKey, String attrValue){

        if (atr != null){
            String attr = atr.getValue(attrKey);
            if(attrValue.toUpperCase().equals(attr.toUpperCase())){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }




   /* public void startElementSuper(String namespaceURI, String localName, String  qName,   Attributes attributes) throws SAXException {
        super.startElement(namespaceURI, localName, qName, attributes);
    }*/

    /*public void characters(char[] ch, int start, int length) throws SAXException {

    }*/

    public void endElement(String uri, String localName, String name)throws SAXException {

        /* from 2 to 1 **/
        if((procedureDefinitionCounter == 2)&& localName.toUpperCase().equals("FIELD")){
            //System.out.println("Decrease FIELD");
            procedureDefinitionCounter--;
        }

        /** increase from 1 to 0 */
        if((procedureDefinitionCounter ==  1) && localName.toUpperCase().equals("BLOCK")) {
            procedureDefinitionCounter--;
            //System.out.println("Decrease BLOCK");
        }

        /** generate missing functions before the end*/
        if (localName.toUpperCase().equals("XML")){

            List<GeneralElement> fces = mfc.generateMissingFces();
            for (GeneralElement ge: fces) {
                ge.construct(this);
            }

        }

        if (startExtension && localName.toUpperCase().equals("MUTATION")) {
            // CLOSE MUTATION TAG
            super.endElement(uri, localName, name);
            //Call Code Extender
            startExtension = false;

            //codeExtender.extendTest(this);
            codeExtender.write(this);
            return;
        }




        /*if (localName.equals("ENTRYPOINT")){

        } else {*/
            super.endElement(uri, localName, name);
       // }


        //System.out.println(name + " end");

    }

   /* public void endElementSuper(String uri, String localName, String name)throws SAXException {
        super.endElement(uri, localName, name);
    }*/

    public void endDocument() throws SAXException {
       /* super.endElement("","AHOJ","BLA");
        super.characters( ("pokus").toCharArray(),0,5);
        super.endElement("","aHOJ","BLA");*/
        super.endDocument();

    }
}