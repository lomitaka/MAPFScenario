package mapfScenario.timeDuration;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

/**
 * Support class to display action durations
 * */
    public class ActionDurationCellDisplay extends ListCell<ActionDuration> {

        private final Logger logger = Logger.getLogger(mapfScenario.agents.AgentListCellDisplay.class);

        /**
         * prepisuje zpsob jak se vykresluji polozky v ListView
         */
        //static class IRememberableCell extends ListCell<IAtom> {
        @Override
        public void updateItem(ActionDuration item, boolean empty) {
            super.updateItem(item, empty);

            // Zde hlavne nastavim grafiku, setGraphic(gp) dle its.

            int colCount = 2;
            //if (its.showGraphic) {colCount++;}
            //if (its.showText) {colCount++;}
            //if (its.showAlterText) {colCount++;}

            if (item != null) {

                GridPane gp = new GridPane();
                // gp.setHgap(5);


                ObservableList<ColumnConstraints> colConstr = gp.getColumnConstraints();
                double colPerc = 100 / ((double) colCount);

                for (int i = 0; i < colCount; i++) {
                    ColumnConstraints c = new ColumnConstraints();
                    c.setPercentWidth(colPerc);
                    colConstr.add(c);
                    logger.debug(String.format("Percent i: %d p: %f", i, colPerc));
                }
                int colIndex = 0;


                /** FOR DEBUGGING PURPOSSE**/
                /*
                colPerc = 100/((double)(colCount+2));
                for (int i = 0; i < colCount;i++){
                    ColumnConstraints c = new ColumnConstraints();
                    c.setPercentWidth(colPerc);
                    colConstr.add(c);ChoiceBox
                }


                 gp.add(new Label(String.valueOf(item.getId())), colIndex, 0);
                 colIndex++;

                 gp.add(new Label(String.valueOf(item.getGid())), colIndex, 0);
                 colIndex++;*/
                /** END FOR DEBUGING PURPOSE**/

                //NAME
                Label tf = new Label(item.actionName);

                // tf.setPrefWidth(50);
                gp.add(tf, 0, 0);

                Label dur = new Label(item.actionDurations + "");

                // tf.setPrefWidth(50);
                gp.add(dur, 1, 0);


               /*

                if (its.showText){
                    Text t = new Text(item.getText());
                    gp.add(t, colIndex, 0);
                    colIndex++;
                }

                if (its.showAlterText){
                    Text t = new Text(item.getAlternativeText());
                    gp.add(t, colIndex, 0);
                    colIndex++;
                }*/

                setGraphic(gp);

            } else {
                Pane p = new Pane();
                setGraphic(p);
            }
        }

    }


