import javafx.util.Pair;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.ArrayList;
import java.util.List;
/**
 * element list, which can contain value.
 * */
public class GeneralElementLeaf extends GeneralElement {


    public List<GeneralElement> getChilderns() {
        return null;
    }

    public GeneralElementLeaf(String name){
        super(name);
    }
    private String leafData;

    public void SetLeafData(String leafData){
        this.leafData = leafData;
    }

    /** writes content of element into contentHandler */
    public void construct(ContentHandler target){

        //


         //elementName = "block";
        AttributesImpl atts = new AttributesImpl();
        //Attributes attr = new Attributes();
        for (Pair<String,String> pair: args) {
            atts.addAttribute("", "", pair.getKey(), "", pair.getValue());
        }

        try {
            target.startElement("","",  name, atts);

            target.characters(leafData.toCharArray(),0,leafData.length());
            /*for (GeneralElement child: childerns) {
                child.construct(target);
            }*/
            target.endElement("",  "",name);
        } catch (SAXException e) {
            e.printStackTrace();
            System.out.print("error"+ e.getMessage());
        }


    }



}
