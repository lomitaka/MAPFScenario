package helpout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Get Question in form of string, and returns answer also in form of string.
 * */
public class QuestionName implements IQuestion<String,String> {

    TextField answ;
    VBox question;
    boolean isConfirmed = false;
    @Override
    public void setQuestionData(String input,String advice ) {
        question = new VBox();

        answ = new TextField();
        answ.setText(advice);
        question.getChildren().add(new Label(input));
        question.getChildren().add(answ);
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
    public String getAnswer() {
        return answ.getText();
    }
}
