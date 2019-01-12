package graphics;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import mapfScenario.agents.Agent;
import mapfScenario.simulation.AgentAction;
import mapfScenario.simulation.SimulatorDisplaySettings;
import org.apache.log4j.Logger;

public class GraphicSimulationManager {

    SimulatorDisplaySettings sds ;
    private final Logger logger = Logger.getLogger(GraphicSimulationManager.class);

    public GraphicSimulationManager(SimulatorDisplaySettings sds){
        this.sds = sds;
    }


    public Node makeControlAgentAction(AgentAction aa){

        Label l = new Label();
        l.setText(aa.action);
        l.setLabelFor(new Label(aa.note));
        l.setPrefWidth(aa.duration*sds.constrollStretch);
        l.setPrefHeight(sds.agentLineHeight);
        logger.debug( String.format("Formatting: Agent %s %f",aa.note, aa.duration*sds.constrollStretch));
        l.setPadding(new Insets(5,5,5,5));
        l.setUserData(aa);
        l.setStyle("-fx-border-color: gray");
        return l;
    }

    /** returns node with text */
    public Node makeInfoText(String text, double prefWidth){

        VBox vb = new VBox();
        vb.setPrefWidth(prefWidth);
        vb.setStyle("-fx-border-color: gray");

        Label l = new Label();
        l.setText(text);
        vb.setPrefHeight(sds.agentLineHeight);
        l.setPadding(new Insets(5,5,5,5));
        logger.debug( String.format("Formatting: Text %s %f",text, prefWidth));
        vb.getChildren().add(l);
        return vb;
    }

    public Node makeInfoAgent(Agent a, double prefWidth){

        HBox hb = new HBox();
        hb.setPrefWidth(prefWidth);
        hb.setAlignment(Pos.CENTER_LEFT);
       // hb.setPadding(new Insets(5,5,5,5));
        hb.setStyle("-fx-border-color: gray");
        hb.setPrefHeight(sds.agentLineHeight);
        Label l = new Label();
        l.setText(a.name);

        l.setPadding(new Insets(5,5,5,5));
        logger.debug( String.format("Formatting: Agent %s %f",a.name, prefWidth));
        hb.getChildren().add(l);

        Rectangle rec = new Rectangle(10,10);
        rec.setFill(a.color);
        hb.getChildren().add(rec);
        return hb;
    }


   public Node drawTimeMark(double time){
       HBox hbox = new HBox();
       Line l = new Line();
       l.setStroke(Color.web("#abacd0"));
       l.setStrokeWidth(1);
       l.setEndY((sds.agentCount+1)*sds.agentLineHeight);

       //second line
       Line l2 = new Line();
       l2.setStrokeWidth(3);
       l2.setEndY((sds.agentCount+1)*sds.agentLineHeight);
       l2.setStroke(Color.web("#d8d6eb"));


       Line l3 = new Line();
       l3.setStroke(Color.web("#ABACD0"));
       l3.setStrokeWidth(1);
       l3.setEndY((sds.agentCount+1)*sds.agentLineHeight);

       hbox.getChildren().add(l);
       hbox.getChildren().add(l2);
       hbox.getChildren().add(l3);
       hbox.setLayoutX(time*sds.constrollStretch);
       hbox.setMouseTransparent(true);

       return hbox;
   }

    public void updateTimeMark(Node nl,double time){

        nl.setLayoutX(time*sds.constrollStretch);
    }


   public Node drawTimeLine(double totalTimeLen){
       Pane p = new Pane();
       Line horline = new Line();
       horline.setMouseTransparent(true);
       double totalLen =totalTimeLen*sds.constrollStretch;
       horline.setEndX(totalLen);

       horline.setStartY(sds.agentLineHeight/2);
       horline.setEndY(sds.agentLineHeight/2);
       p.getChildren().add(horline);
       HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setMouseTransparent(true);

       float textLength = 50;
       int stepSize = 1000;
       /***/
       int stepCount = (int)Math.floor(totalTimeLen /stepSize );

       while (totalLen /(stepCount) < textLength ){
           stepSize *=2;
           stepCount = (int)Math.floor(totalTimeLen /stepSize );
           if (stepCount <= 2) { break;}
       }

       for (int i = 0; i < stepCount;i++){

           Label l = new Label();
           l.setText(i*stepSize+"");
           l.setPrefWidth(stepSize *sds.constrollStretch-1);
           l.setPrefHeight(sds.agentLineHeight);
           l.setPadding(new Insets(0,0,sds.agentLineHeight/2,0));
           hbox.getChildren().add(l);

           Line line = new Line();
           line.setEndY(sds.agentLineHeight*0.5);
           hbox.getChildren().add(line);
       }
       p.getChildren().add(hbox);

        return p;
   }

}
