
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** when traversing xml file, checks for function existing functions. */
public class MissingFceChecker {

    /** function list that were not found in document yet. */
    Set<String> commandsRep;
    /** initializes missing functions */
    public void MissingFceCheckerInitListList(List<List<String>> commands) {
        commandsRep = new HashSet<String>();
        for (List<String> sl : commands) {
            for (String s : sl) {
                if (!commandsRep.contains(s)) {
                    commandsRep.add(s);
                }
            }
        }
    }

    /** initializes missing functions */
    public void MissingFceCheckerInitList(List<String> commands){
        commandsRep = new HashSet<String>();

        for (String s: commands) {
            if (!commandsRep.contains(s)){
                commandsRep.add(s);
            }
        }


    }


    /** checks function occurence, and if function is in not found list, then removes it. */
    public void noteFceFound(String fceName){
        if (commandsRep.contains(fceName)){
            commandsRep.remove(fceName);
        }

    }

    /* check functions  */
       /*   <block type="procedures_defnoreturn" id="TSWMls)wPZ{Ffdr[)?:k" x="416" y="-384">
            <field name="NAME">INJECTED</field>
            <comment pinned="false" h="80" w="160">Describe this function...</comment>
            </block>**/

     /** generates element for missing functions. */
    public List<GeneralElement> generateMissingFces(){

        List<GeneralElement> genArrList = new ArrayList<GeneralElement>();

        int yval = -384;
        for (String fce : commandsRep) {

            GeneralElement block = new GeneralElement("block");
            block.addParam("type", "procedures_defnoreturn");
            block.addParam("id", CodePatch.generateID());
            block.addParam("x", "416");
            block.addParam("y", "" + yval);

            GeneralElementLeaf field = new GeneralElementLeaf("field");

            field.addParam("name", "NAME");
            field.SetLeafData(fce);
            block.getChilderns().add(field);

            GeneralElementLeaf comment = new GeneralElementLeaf("comment");
            comment.addParam("pinned","false");
            comment.addParam("h","80");
            comment.addParam("w","160");
            comment.SetLeafData("Describe this function...");
            yval -= 80;
            block.getChilderns().add(comment);
            genArrList.add(block);

        }

        return genArrList;
    }
}
