package helpout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public interface IQuestion<InputType,AnswType> {

    /** inputData - additional data for question
     * advice - preselected choice*/
    void setQuestionData(InputType inputData,AnswType advice);
    void setIsConfirmed(boolean answ);
    Node getQuestionData();
    boolean getConfirmed();
    AnswType getAnswer();
}
