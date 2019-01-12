package helpout;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import mapfScenario.Consts;
import mapfScenario.agents.Agent;

import java.util.ArrayList;
import java.util.List;

/**
 * Get Question in form of string, and returns answer also in form of string.
 * */
public class QuestionAgentCount implements IQuestion<List<Agent>,Integer> {

    ChoiceBox cb;
    VBox question;
    boolean isConfirmed = false;
    @Override
    public void setQuestionData(List<Agent> agList,Integer advice ) {
        question = new VBox();

        cb = new ChoiceBox();

        List<String> choices = new ArrayList<>();
        for (int i = 0; i < agList.size();i++ ){
            choices.add(agList.get(i).name);
        }
        choices.add("all");

        ObservableList ol = FXCollections.observableArrayList(choices);
        cb.setItems(ol);
        question.getChildren().add(new Label(Consts.pickAgentNumber));
        question.getChildren().add(cb);
    }



    @Override
    public void setIsConfirmed(boolean isConf) {
        isConfirmed = isConf;
    }

    @Override
    public Node getQuestionData() {
        return question;
    }

    @Override
    public boolean getConfirmed() {
        return isConfirmed;
    }

    @Override
    public Integer getAnswer() {
        return cb.getSelectionModel().getSelectedIndex();
    }
}
