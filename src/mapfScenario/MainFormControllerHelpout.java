package mapfScenario;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;

/**
 * Help method to get every button on main form
 * */
public class MainFormControllerHelpout {

    /** returns every button of the scene */
    public static ArrayList<Button> getAllButtons(Parent root){
        ArrayList<Button> answ = new ArrayList<>();
        for (Node n: getAllNodes(root)) {
          if (n instanceof Button){
              answ.add((Button)n);
              //System.out.println(((Button)n).getId());
          }
        }
        return answ;
    }

    /** iterates over node, and its descendant and return every of them. Also travers TabPanes */
    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof SplitPane){
                for (Node n:((SplitPane) node).getItems()) {
                    if (n instanceof Parent)
                    addAllDescendents((Parent)n,nodes);
                }

            }
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
            //Iterates also inside tabPanes
            if (node instanceof TabPane)
                for (Tab t : ((TabPane) node).getTabs())
                    addAllDescendents((Parent)t.getContent(), nodes);

        }
    }

}
