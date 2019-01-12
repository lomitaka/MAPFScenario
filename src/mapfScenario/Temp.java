package mapfScenario;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Temp {

    public static VBox getRange(){
        VBox vb = new VBox();

        List<List<Color>> clist = getColorsFromNums(20);

        for (List<Color> cl : clist){
            HBox hb = new HBox();
            for (Color c :cl){
                Rectangle r = new Rectangle();
                r.setWidth(30);
                r.setHeight(30);
                r.setFill(c);
                hb.getChildren().add(r);
            }
            vb.getChildren().add(hb);
        }
        return vb;
    }


   static public List<List<Color>> getColorsFromNums(int agentCount){

       // The smallest .. to the biggest
       List<List<Color>> resultReverse = new ArrayList<>();

       for (int i = 0 ; i < agentCount;i++){
           List<Color> current = new ArrayList<>();
           int num = i;
           if (num ==0) { current.add(numToColor(0)); }
           while (num >0) {
               current.add(numToColor(num % 3));
               num = num /3;
           }
           resultReverse.add(current);
       }
       // get max length
       int maxLen =0;
       for (List<Color> c: resultReverse){ if (c.size() > maxLen){maxLen = c.size();}}


       List<List<Color>> result = new ArrayList<>();
       for (int i = 0 ; i < agentCount;i++){
           List<Color> currRev = resultReverse.get(i);
           List<Color> current = new ArrayList<>();
           while (currRev.size() <= maxLen){currRev.add(Color.color(1,1,1));}
           Color last = Color.color(1,1,1);
           for(int j = currRev.size()-1; j >= 0;j--){
               //if next color is same as the one before, make next white
               if (last.equals(currRev.get(j))) {
                   current.add(Color.color(1,1,1));
                   last = Color.color(1,1,1);
               }else {

                   current.add(currRev.get(j));
                   last = currRev.get(j);
               }

           }
           //Adds black color.
           current.add(Color.color(0,0,0));
           result.add(current);
       }

       return result;
    }


    static private Color numToColor(int num){
        switch (num){
            case 0:
                return Color.color(1,0,0);
            case 1:
                return Color.color(0,1,0);
            case 2:
                return Color.color(0,0,1);
            default:
                return Color.color(1,1,0);
        }
    }

}
