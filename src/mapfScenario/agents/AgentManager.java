package mapfScenario.agents;

import graphics.MVPoint;
import helpout.QuestionColor;
import helpout.QuestionName;
import helpout.methods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import mapfScenario.Consts;
import mapfScenario.mapView.MapViewSettings;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AgentManager {

    /** manages flags ( start and end positions)  */
     AgentFlagManager afm;

    /**  data list for ListView */
    ObservableList<Agent> agentObsList;
    /** list view for agents */
    ListView<Agent> agentListView;

    private final Logger logger = Logger.getLogger(AgentManager.class);

    public AgentManager(ListView<Agent> agentListView, MapViewSettings mvs,Pane flagPane){

        afm = new AgentFlagManager(flagPane,mvs);

        /* Propojuji datalistView s displayedIR, agent tridou IRememberableCell */
        // int agentObsList;
        agentObsList  =  FXCollections.<Agent>observableArrayList();
        agentListView.setItems(agentObsList);
        agentListView.setCellFactory(new Callback<ListView<Agent>,
                     ListCell<Agent>>() {
                 @Override
                 public ListCell<Agent> call(ListView<Agent> list) {
                     return new AgentListCellDisplay();
                 }
             }

        );
        this.agentListView = agentListView;
    }


    public void addAgent(Agent a){
        logger.debug(String.format("Adding Aggent %s to AgentManager ",a.name));
        agentObsList.add(a);

    }

    public void removeAgent(Agent a){
        logger.debug(String.format("Removing Agent %s from AgentManager ",a.name));
        afm.removeStartFlag(a);
        afm.removeEndFlag(a);
        agentObsList.remove(a);
    }


    /* returns true if was sucessfull */
    public boolean setStartFlag(Agent a,MVPoint newStart){
        if (afm.canSetStartFlagThere(newStart)) {
            a.start = newStart;
            afm.addStartFlag(a);
            return true;
        }
        return false;
    }

    public List<Agent> getAgents(){
        return new ArrayList(agentObsList);
    }

    /** clears agent list, and repopulates it with new agent also sets flags */
    public void setAgents(List<Agent> aglist){

        //TODO
        agentObsList.clear();
        for (Agent a : aglist){
            agentObsList.add(a);
        }
        afm.setFlags(aglist);
    }

    /* returns true if was sucessfull */
    public boolean setEndFlag(Agent a ,MVPoint newEnd){
        if (afm.canSetEndFlagThere(newEnd)) {
            a.end = newEnd;
            afm.addEndFlag(a);
            return true;
        }
        return false;
    }

    public void updateAgentFlags(Agent a){
        afm.removeStartFlag(a);
        afm.removeStartFlag(a);
        afm.addStartFlag(a);
        afm.addEndFlag(a);

    }

    public Agent CreateAgent(List<Agent> agentList){
        //agentObsList.add(new Agent());

        Agent a = new Agent();

        String agentName = getAgentName(getAgentAdviceName(agentList));
        if (agentName == null){ return null; }

        a.name = agentName;
        //ColorPicker colorPicker1 = new ColorPicker();
        //colorPicker1.show();

        Color c = getAgentColor(getAgentAdviceColor(agentList.size()));
        if (c == null) {return null; }

        a.color = c;

        return a;


    }

    private String getAgentAdviceName(List<Agent> aglist){
        String name = String.format("Agent_%d",aglist.size());
        return name;
    }


    /** predefined colors for agents.. each next agents gets color from this color range */
    private String[] colorList = new String[] { "#336666","#4a66cc","#4d3399","#804d80","#80334d","#e6994d",
            "#cc6633","#e6e64d","#cc9833","#4a804d","#999933","#336633" };


    /** chooses which color of agent to advice when addint new agent.
     *  color is added by count of actual agents.. If agent count is more than 12 it computes color. */
    private Color getAgentAdviceColor(int agentCount) {
       // int agentCount = agentList.size();
        if (agentCount < colorList.length) {
            return Color.web(colorList[agentCount]);

        } else {
            agentCount -= colorList.length;
        }

        float step = 210.625f;
        float cycle = 360 / step;
        float hue = (step * agentCount) % 360;
        float saturation = 0.9f;
        if (agentCount % cycle > 1) {
            saturation = 0.6f;
        }
        if (agentCount % cycle > 2) {
            saturation = 0.4f;
        }
        float bright = 0.9f;
        if (agentCount > 36) {
            saturation = 0.6f;
        }
        if (agentCount > 72) {
            saturation = 0.4f;
        }
        //logger.debug(String.format("color advice: %f %f %f",hue,saturation,bright));
        return Color.hsb(hue, saturation, bright);

    }
    /*private Color getAgentAdviceColor(List<Agent> agentList){
        int agentCount = agentList.size();
        float step = 45.625f;
        float hue = (step*agentCount) % 360;
        float saturation = 1f;
        if (agentCount%36 > 12) { saturation = 0.6f;}
        if (agentCount%36 > 24) { saturation = 0.4f;}
        float bright = 0.6f;
        if (agentCount > 36) { saturation = 0.6f;}
        if (agentCount > 72) { saturation = 0.4f;}
        logger.debug(String.format("color advice: %f %f %f",hue,saturation,bright));
        return Color.hsb(hue,saturation,bright);

    }*/


    /** shows dialog that asks for agent new name. */
    public String getAgentName(String advice){
        QuestionName qn = new QuestionName();qn.setQuestionData("Input agent name:",advice);
        methods.showDialogQuestion(qn);
        String agentName = qn.getAnswer();
        agentName = agentName.replace(";","_");
        logger.debug(String.format("Answer to Name question is: %s (%s) ",agentName, qn.getConfirmed() ? "isConfirme" : "notConfirmed"));
        if (!qn.getConfirmed()) {

            return null;
        }
        return agentName;
    }

    /** shows dialog that asks for agent new color. */
    public Color getAgentColor(Color origColor){
        QuestionColor qc = new QuestionColor();qc.setQuestionData(null,origColor);
        methods.showDialogQuestion(qc);
        logger.debug(String.format("Anwser to Color question is: %s (%s) ",qc.getAnswer().toString(),qc.getConfirmed() ? "isConfirme" : "notConfirmed"));
        if (!qc.getConfirmed()) { return null; }
        return qc.getAnswer();

    }


    public void setAgentColor(){
        ObservableList<Agent> selectedAgent = agentListView.getSelectionModel().getSelectedItems();
        if (selectedAgent.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return;
        }
        Agent a = selectedAgent.get(0);
        Color ac = getAgentColor(a.color);
        if (ac == null){return;};

        a.color = ac;
        //reload flags
        agentListView.refresh();
        updateAgentFlags(a);
    }

    public void setAgentName(){
        ObservableList<Agent> selectedAgent = agentListView.getSelectionModel().getSelectedItems();
        if (selectedAgent.size()  == 0) {
            logger.debug("Nothing selected");
            methods.showDialog(Consts.infoNothingSelected);
            return;

        }Agent a = selectedAgent.get(0);
        String name = getAgentName(a.name);
        if (name == null) { return;}

        a.name = name;
        agentListView.refresh();
    }

    /* agent list create backup and restore**/

    List<Agent> agentBackupList;

    public void createBackup(){
        List<Agent> agList = getAgents();
        List<Agent> backup = new ArrayList<>();
        for (Agent a: agList) {
            backup.add(a.clone());
        }
        agentBackupList = backup;
    }

    public void restoreFromBackup(){
        if (agentBackupList != null){
            setAgents(agentBackupList);
            agentBackupList = null;
        }
    }




}
