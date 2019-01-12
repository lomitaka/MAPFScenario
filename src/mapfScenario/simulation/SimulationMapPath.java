package mapfScenario.simulation;

import graphics.GraphicManager;
import graphics.MVPoint;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import mapfScenario.Consts;

import java.util.*;

import static mapfScenario.Consts.parralelLinesDelta;

/** used to draw agents paths.  */
public class SimulationMapPath {


    class Edge {
        MVPoint a;
        MVPoint b;
        double defLayoutX;
        double defLayoutY;
        int lastCount = 0;

        public Edge(AgentActionPair aap, Node n){
            if (aap.current.position.compareTo(aap.next.position) > 0){
                a = aap.current.position;
                b = aap.next.position;
            }else {
                b = aap.current.position;
                a = aap.next.position;
            }

            defLayoutX = n.getLayoutX();
            defLayoutY = n.getLayoutY();
        }

        public boolean isVertical(){
            return a.x == b.x;
        }

        @Override
        public boolean equals(Object obj) {
            //return super.equals(obj);

            if (obj == null) return false;
            if (obj == this) return true;
            if (!obj.getClass().getName().equals(this.getClass().getName())){
                return false;
            }
            Edge mvp = (Edge)obj;

            if (this.a == mvp.a && this.b == mvp.b) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = this.a.hashCode()*1024;
            hash = hash + this.b.hashCode();
            return hash;
        }

    }

    // used to distinguish nodes that are going over the same edge
    HashMap<Edge, HashSet<Node>> edgeMarker = new HashMap<>();

    /** where paths will be drawn  */
    Pane displayInfoPane;
    /** draw settigns */
    SimulatorDisplaySettings sds;
    GraphicManager gm;

    /** map of currently displayed AgentActionPair to drawn Node. It is used to cache Nodes, and avoid redrawing them
     * too often. */
    HashMap<AgentActionPair,Node> displayedAap;

    public SimulationMapPath(Pane displayInfoPane,SimulatorDisplaySettings sds,GraphicManager gm){
        this.displayInfoPane = displayInfoPane;
        this.sds = sds;
        this.gm = gm;
        displayedAap = new HashMap<>();
    }

    /** draws one action like arrow on map */
    private void updateAapShow(AgentActionPair aap){

        if (!displayedAap.containsKey(aap)){
            Node arrow = gm.emphasizeAgentActionPairNode(aap);
            gm.moveNodeByCoords(arrow,Consts.mapLineMove,Consts.mapLineMove);
            displayInfoPane.getChildren().add(arrow);
            displayedAap.put(aap,arrow);

            //if node is edge adds it to marker
            if (aap.current.position != aap.next.position) {
                //Adds nodes to edgemarker
                Edge e = new Edge(aap,arrow);
                if (!edgeMarker.containsKey(e)) {
                    edgeMarker.put(e, new HashSet<>());
                }
                edgeMarker.get(e).add(arrow);
            }

        }

    }

    /** hide drawn action  */
    private void updateAapHide(AgentActionPair aap){
        if (displayedAap.containsKey(aap)){
            Node arr = displayedAap.get(aap);
            displayInfoPane.getChildren().remove(arr);
            displayedAap.remove(aap);
            Edge e = new Edge(aap,arr);
            if (edgeMarker.containsKey(e)){
                edgeMarker.get(e).remove(arr);
            }
        }
    }

    /** for each agent draw his path. displayDiameter shows how big part of path to show around agent.
     * displayTime shows where in which time agents currently are. */
    public void displayPath(List<List<AgentActionPair>> aapList, int displayDiameter, int displayTime){
        //edgeMarker.clear();
        int displayTimeStart = displayTime - displayDiameter;
        int displayTimeEnd = displayTime + displayDiameter;
        if (displayTimeStart < 0) {displayTimeStart =-1;}
        if (displayTimeEnd > sds.totalTimeLength) {displayTimeEnd = sds.totalTimeLength+1;}

        for (List<AgentActionPair> aaList : aapList){
            // for (int i = 0; i < aaList.size()-1;i++){
            for ( AgentActionPair aap :aaList){
                if ((displayDiameter > 0) && aap.startTimeMark+aap.current.duration > displayTimeStart &&
                        (aap.startTimeMark) <= displayTimeEnd ){
                    //display arrow
                    updateAapShow(aap);
                } else {
                    //hide arrow
                    updateAapHide(aap);
                }
            }
        }
        moveMarkedEdges();
    }

    /** goes over each marked node and moves it a bit, so arrows do not overlap */
    private void moveMarkedEdges(){
        float delta = Consts.parralelLinesDelta;
        for (java.util.Map.Entry<Edge,HashSet<Node>> enPair : edgeMarker.entrySet()){
            // recompute only where is more than one edge, and count of edges is changed compared by past
            if (enPair.getValue().size() > 1 && enPair.getKey().lastCount != enPair.getValue().size()){
                float offset = (enPair.getValue().size()-1)*delta/2.0f;
                int i = 0;
                //List nodes = new ArrayList();
                Object[] nodes  =(enPair.getValue().toArray());
                Arrays.sort(nodes, (a, b) -> ((Node)a).getUserData().toString().compareTo(((Node)b).getUserData().toString()));
                //for (Node n : enPair.getValue()){
                for (Object o : nodes){
                    Node n = (Node)o;
                    n.setLayoutX(enPair.getKey().defLayoutX);
                    n.setLayoutY(enPair.getKey().defLayoutY);
                    if (enPair.getKey().isVertical()){
                        // adjust x value
                        gm.moveNodeByCoords(n,-offset+delta*i,0);
                    } else {
                        // adjust y value
                        gm.moveNodeByCoords(n,0,-offset+delta*i);
                    }
                    i++;

                }

            }
        }
    }

    /** clear drawed objects. */
    public void clear(){
        displayedAap.clear();
        displayInfoPane.getChildren().clear();
        edgeMarker.clear();
    }
}
