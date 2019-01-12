package mapfScenario.agents;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.apache.log4j.Logger;

/** class used to support View list and uses more advanced view of agent in list. */
public class AgentListCellDisplay extends ListCell<Agent> {

    private final Logger logger = Logger.getLogger(AgentListCellDisplay.class);

    /**
     *  prepisuje zpsob jak se vykresluji polozky v ListView
     */
    //static class IRememberableCell extends ListCell<IAtom> {
        @Override
        public void updateItem(Agent item, boolean empty) {
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
                double colPerc = 100/((double)colCount);

                for (int i = 0; i < colCount;i++){
                    ColumnConstraints c = new ColumnConstraints();
                    c.setPercentWidth(colPerc);
                    colConstr.add(c);
                    logger.debug(String.format("Percent i: %d p: %f" , i,colPerc));
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
                Label tf = new Label(item.name);

               // tf.setPrefWidth(50);
                gp.add(tf,0,0);

                //Label l = new Label("CLR");
                Rectangle rec = new Rectangle(10,10);

                rec.setFill(item.color);
                gp.add(rec,1,0);

                /*
                *                   <ImageView fitHeight="25.0" fitWidth="25.0" pickOnBounds="true" preserveRatio="true">
                     <image>
                        <Image url="@../img/greenflag25x25.png" />
                     </image>
                  </ImageView>
                * */

               /* ImageView startImg = new ImageView();
                startImg.setFitHeight(25.0f);
                startImg.setFitWidth(25.0f);

                startImg.setImage(new Image(Consts.greenFlagImgLoc));
                gp.add(startImg,2,0);
                //Button bs = new Button("ST");
                //gp.add(bs,2,0);

                ImageView endImg = new ImageView();
                endImg.setFitHeight(25.0f);
                endImg.setFitWidth(25.0f);
                endImg.setImage(new Image(Consts.blwhFlagImgLoc));
                gp.add(endImg,3,0);


                ImageView removeImg = new ImageView();
                removeImg.setFitHeight(25.0f);
                removeImg.setFitWidth(25.0f);
                removeImg.setImage(new Image(Consts.removeImgLoc));
                gp.add(removeImg,4,0);
                // vykresluji obrazek:
                /*if (its.showGraphic){
                    gp.add(item.getNodeClone(), colIndex, 0);
                    colIndex++;
                }

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

