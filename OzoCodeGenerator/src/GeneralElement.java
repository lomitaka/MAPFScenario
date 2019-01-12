import javafx.util.Pair;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** represenation of html element. Used to easily represent actions that i want to include. */
public class GeneralElement {

    /** name of element */
    protected String name;
    /** list of elements */
    protected List<Pair<String,String>> args = new ArrayList<>();
    /** list of childrens. */
    protected List<GeneralElement> childerns = new ArrayList<GeneralElement>();


    public List<GeneralElement> getChilderns() {
        return childerns;
    }


    public GeneralElement(String name){
        this.name = name;
    }

    public void addParam(String name, String value){
        args.add(new Pair<>(name,value));
    }

    /** performs element write into content handler. */
    public void construct(ContentHandler target){
        /* ELEMENT  BLOCK */
         //elementName = "block";
        AttributesImpl atts = new AttributesImpl();
        //Attributes attr = new Attributes();
        for (Pair<String,String> pair: args) {
            atts.addAttribute("", "", pair.getKey(), "", pair.getValue());
        }

        try {
            target.startElement("","",  name, atts);
            for (GeneralElement child: childerns) {
                child.construct(target);
            }
            target.endElement("",  "",name);
        } catch (SAXException e) {
            e.printStackTrace();
            System.out.print("error"+ e.getMessage());
        }


    }



}
