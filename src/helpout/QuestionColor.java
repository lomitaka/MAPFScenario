package helpout;

import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Get Question in form of string, and returns answer also in form of string.
 * */
public class QuestionColor implements IQuestion<Color,Color> {

    ColorPicker cp;
    //Color answ;
    VBox question;
    boolean isConfirmed = false;
    @Override
    public void setQuestionData(Color input,Color advice ) {
        question = new VBox();

        cp = new ColorPicker(advice);
        //answ.setText(advice);
        question.getChildren().add(new Label("Select color:"));
        question.getChildren().add(cp);
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
    public Color getAnswer() {
        return cp.getValue();
    }
}
