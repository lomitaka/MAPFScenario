package graphics;

import helpout.methods;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import mapfScenario.Consts;
import mapfScenario.mapView.MapView;
import mapfScenario.mapView.MapViewSettings;
import mapfScenario.simulation.AgentActionPair;
import org.apache.log4j.Logger;

public class GraphicManager {

    private final Logger logger = Logger.getLogger(MapView.class);
    //public int edgeLength;
    private MapViewSettings mvs;
    /** adds mapline
     * line is numbered from 1 to n */
    public GraphicManager(MapViewSettings mvs){
        this.mvs = mvs;
    }

    private final double angleToRad = Math.PI /180;

    public Line makeLine(MVPoint a, MVPoint b){

        Line l = new Line();
        l.setStrokeWidth(mvs.edgeWidth);
        l.setStartX((a.x )* mvs.edgeLength);
        l.setStartY((a.y )* mvs.edgeLength);
        l.setEndX((b.x )* mvs.edgeLength);
        l.setEndY((b.y )* mvs.edgeLength);

        logger.debug(String.format(" Line start x: %f y: %f, end: x %f, y:%f " ,l.getStartX(),l.getStartY(),l.getEndX(),l.getEndY()));

        return l;


    }

    /** gets line and stretches it by some amount in x cords */
    public void stretchLineByWidth(Line l, float stepLengthRatio ){
        int sign = 0;
        if (l.getStartX() < l.getEndX()){sign = 1;} else {sign = -1;}

        l.setStartX(l.getStartX()-sign*mvs.edgeLength *stepLengthRatio/2);
        l.setEndX(l.getEndX()+sign*mvs.edgeLength *stepLengthRatio/2);

    }

    /** gets line and stretches it by some amount in y cords */
    public void stretchLineByHeight(Line l, float stepLengthRatio ){
        int sign = 0;
        if (l.getStartY() < l.getEndY()){sign = 1;} else {sign = -1;}

        l.setStartY(l.getStartY()-sign*mvs.edgeLength *stepLengthRatio/2);
        l.setEndY(l.getEndY()+sign*mvs.edgeLength *stepLengthRatio/2);

    }

    /** moves current line by portion of edgeLength  1 = edgeLength 2 = edgeLength*2 and so on. */
    public void moveLineByCoords(Line l, float stepLengthRatioX,float stepLengthRatioY ){
        l.setStartX(l.getStartX()+mvs.edgeLength *stepLengthRatioX);
        l.setEndX(l.getEndX()+mvs.edgeLength *stepLengthRatioX);

        l.setStartY(l.getStartY()+mvs.edgeLength *stepLengthRatioY);
        l.setEndY(l.getEndY()+mvs.edgeLength *stepLengthRatioY);
    }

    /** moves current circle by portion of edgeLength  1 = edgeLength 2 = edgeLength*2 and so on. */
    @Deprecated
    public void moveCircleByCoords(Circle c, float stepLengthRatioX, float stepLengthRatioY ){
        c.setCenterX(c.getCenterX()+mvs.edgeLength *stepLengthRatioX);
        c.setCenterY(c.getCenterY()+mvs.edgeLength *stepLengthRatioY);

    }

    public void moveCircleByPixels(Circle c, int pixelsX, int pixelsY ){
        c.setCenterX(c.getCenterX()+pixelsX);
        c.setCenterY(c.getCenterY()+pixelsY);

    }

    public void moveRectangleByCoords(Rectangle c, float stepLengthRatioX,float stepLengthRatioY ){
        c.setX(c.getX()+mvs.edgeLength *stepLengthRatioX);
        c.setY(c.getY()+mvs.edgeLength *stepLengthRatioY);

    }
    /** moves current circle by portion of edgeLength  1 = edgeLength 2 = edgeLength*2 and so on. */
    public void moveImgViewByCoords(Node c, float stepLengthRatioX,float stepLengthRatioY ){
        c.setLayoutX(c.getLayoutX()+mvs.edgeLength *stepLengthRatioX);
        c.setLayoutY(c.getLayoutY()+mvs.edgeLength *stepLengthRatioY);
    }

    public Rectangle makeRectangle(MVPoint a) {
        Rectangle rec = new Rectangle();
        //logger.debug(String.format("MakeCrectangle Color -fx-fill: %s;", methods.colorToWebString(mvs.mapBackGroundColor)));
        //rec.setStyle(String.format("-fx-fill: %s;", methods.colorToWebString(mvs.mapBackGroundColor)));
        rec.setFill(mvs.mapBackGroundColor);
        //rec.setStyle(String.format("-fx-fill: #00FFFF;", methods.colorToWebString(mvs.mapBackGroundColor)));
        rec.setWidth((float)mvs.edgeLength *Consts.mapHoleSize);
        rec.setHeight((float)mvs.edgeLength *Consts.mapHoleSize);

        rec.setX(a.x* mvs.edgeLength/2-mvs.edgeLength/4);
        rec.setY(a.y* mvs.edgeLength/2-mvs.edgeLength/4);
        return  rec;
    }

    /**
     * use rectangle instead
     * */

    public Node makeCircle(MVPoint a, Color color, float radiusRatio){


        Pane result = new Pane();

        Circle c = new Circle();
        float circleRadius =(float)mvs.edgeLength * radiusRatio;
        c.setRadius(circleRadius);
        c.setFill(color);
        c.setStyle(String.format("-fx-border-color: #000000; "/*, methods.colorToWebString(c) */)
                + String.format("-fx-border-width: %s; ","1")
        );

        result.setLayoutX((a.x )* (mvs.edgeLength/2) );
        result.setLayoutY((a.y )* (mvs.edgeLength/2) );

        result.getChildren().add(c);

        return result;

    }

    public void moveImgViewByPixels(Node c, float pixelsX, float pixelsY) {
        c.setLayoutX(c.getLayoutX()+pixelsX);
        c.setLayoutY(c.getLayoutY()+pixelsY);

    }

    public enum FlagType  { Start, End };

    public Node makeFlag(MVPoint a, Color c, FlagType flType  ){



        ImageView img = new ImageView();
       // img.setFitHeight(26.0f);
       // img.setFitWidth(23.0f);
        img.setFitHeight(Consts.mapFlagFlagHeight);
        img.setFitWidth(Consts.mapFlagFlagWidth);
        String borderPart = "";

        double circleRadius = mvs.edgeLength*Consts.mapFlagCircleRadius;
        //constatn to move flag to outside of circle
        double oneOverSqrtof2 = 0.707106; // 1/sqrt(2)
         //img.setX((agent.x )* mvs.edgeLength);
        //img.setY((agent.y )* mvs.edgeLength);

        if (flType == FlagType.Start){
            img.setImage(new Image(Consts.greenFlagImgLoc));

            //top left top corner to circle center
            moveImgViewByPixels(img,(int)((0.9*circleRadius)),(int)(1*circleRadius));
            //bottom right corner to circle center
            moveImgViewByPixels(img,-Consts.mapFlagFlagWidth,-Consts.mapFlagFlagHeight);
            //moves bottom right corerner out of circle (in -45 degree)
            moveImgViewByPixels(img,(int)(-circleRadius*oneOverSqrtof2),(int)(-circleRadius*oneOverSqrtof2));
        } else if (flType == FlagType.End){
            img.setImage(new Image(Consts.blwhFlagImgLoc));
            borderPart = "0 0 3 3";
            //top left top corner to circle center
            moveImgViewByPixels(img,(int)((1.1*circleRadius)),(int)(1*circleRadius));
            //bottom left corner to circle center
            moveImgViewByPixels(img,0,-Consts.mapFlagFlagHeight);
            //moves bottom left corerner corerner out of circle (in 45 degree)
            moveImgViewByPixels(img,(int)(circleRadius*oneOverSqrtof2),(int)(-circleRadius*oneOverSqrtof2));
        } else {
            logger.error("unrecognized flagType error");
        }



        Circle circle = new Circle();
        circle.setFill(c);
        circle.setRadius(circleRadius);
        //moves cirlce to the right left of the border

        //circle.setLayoutY(mvs.edgeLength/2);

        if (flType == FlagType.Start){
            //circle.setLayoutX(mvs.edgeLength/2);
                moveCircleByPixels(circle,(int)(0.9*circleRadius),(int)(1*circleRadius));
        } else if (flType == FlagType.End){
                moveCircleByPixels(circle,(int)(1.1*circleRadius),(int)(1*circleRadius));
        } else {
            logger.error("unrecognized flagType error");
        }

        Pane border = new Pane();

        //border.setStyle( String.format("-fx-border-color: #000000; "/*, methods.colorToWebString(c) */)
         //      + String.format("-fx-border-width: %s; ","1"));

        border.setLayoutX((a.x/2 )* mvs.edgeLength);
        border.setLayoutY((a.y/2 )* mvs.edgeLength);

        border.getChildren().add(circle);
        border.getChildren().add(img);



        return border;
    }

    /* transition is from [0 to 1) where 0 is act, and 1 is next. */
    public Node getInterleavePoint(AgentActionPair aap, double transition){

        Pane result = new Pane();

        Circle c = new Circle();
        float circleRadius =(float)mvs.edgeLength *Consts.mapAgentRepCircleSize;
        c.setRadius(circleRadius);
        c.setStyle(String.format("-fx-fill: %s;", methods.colorToWebString(aap.agent.color))
                + String.format("-fx-border-color: #000000; "/*, methods.colorToWebString(c) */)
                + String.format("-fx-border-width: %s; ","1")
        );

        double transX = (aap.current.position.x)*(1-transition) + (aap.next.position.x)*transition;
        double transY = (aap.current.position.y)*(1-transition) + (aap.next.position.y)*transition;
        result.setLayoutX((transX )* (mvs.edgeLength/2) );
        result.setLayoutY((transY )* (mvs.edgeLength/2) );

        result.getChildren().add(c);

        Line l = getHeadDirection(aap,transition);
        result.getChildren().add(l);

        return result;
    }



    /* transition is from [0 to 1) where 0 is act, and 1 is next. */
    public Node getInterleavePointSoft(AgentActionPair aap, double transition){

        Pane result = new Pane();
        /* inner is color of node outer is black.  */
      /*  Circle c = new Circle();
        float circleRadius =(float)mvs.edgeLength *Consts.mapAgentRepCircleSize;
        c.setRadius(circleRadius*1.48);
        c.setFill(Color.TRANSPARENT);
        c.setStroke(aap.agent.color);
        c.setStrokeWidth(2);

        Circle c2 = new Circle();

        c2.setRadius(circleRadius*1.5);
        c2.setFill(Color.TRANSPARENT);
        c2.setStroke(Color.color(0,0,0));
        c2.setStrokeWidth(1);*/
        float circleRadius =(float)mvs.edgeLength *Consts.mapAgentRepCircleSize;
        for (int i =0 ; i < 4;i++) {
            Arc a = new Arc();
            a.setLength(40);
            a.setType(ArcType.OPEN);
            a.setFill(Color.TRANSPARENT);
            a.setRadiusX(circleRadius*1.48);
            a.setRadiusY(circleRadius*1.48);
            a.setStroke(aap.agent.color);
            a.setStartAngle(25+90*i);
            result.getChildren().add(a);

            Arc a2 = new Arc();
            a2.setLength(40);
            a2.setType(ArcType.OPEN);
            a2.setFill(Color.TRANSPARENT);
            a2.setRadiusX(circleRadius*1.5);
            a2.setRadiusY(circleRadius*1.5);
            a2.setStroke(Color.color(0,0,0));
            a2.setStartAngle(25+90*i);
            result.getChildren().add(a2);

        }



        double transX = (aap.current.position.x)*(1-transition) + (aap.next.position.x)*transition;
        double transY = (aap.current.position.y)*(1-transition) + (aap.next.position.y)*transition;
        result.setLayoutX((transX )* (mvs.edgeLength/2) );
        result.setLayoutY((transY )* (mvs.edgeLength/2) );

       // result.getChildren().add(c);
        //result.getChildren().add(c2);

       // Line l = getHeadDirection(aap,transition);
       // result.getChildren().add(l);

        return result;
    }


    public void moveNodeByCoords(Node n, float stepLengthRatioX,float stepLengthRatioY ){
        n.setLayoutX(n.getLayoutX()+mvs.edgeLength *stepLengthRatioX);
        n.setLayoutY(n.getLayoutY()+mvs.edgeLength *stepLengthRatioY);

    }


    /* emphasize* userData contains color of the node (this apply only for lines*/
    public Node emphasizeAgentActionPairNode(AgentActionPair aap){

        MVPoint start = aap.current.position;
        MVPoint end = aap.next.position;

        if (start == end){
            // it is circle
            return emphasizeAgentActionPairNodeCircle(aap);

        } else {
            //it is line
            return emphasizeAgentActionPairNodeLine(aap);
        }
    }

   private Node emphasizeAgentActionPairNodeLine(AgentActionPair aap){

        Pane sp = new Pane();

        MVPoint start = aap.current.position;
        MVPoint end = aap.next.position;
        Line l = new Line();
        l.setStrokeWidth(3);
        l.setStroke(aap.agent.color);

       // adjust arrow head into some direction
        float headOffsetX =0;
        float headOffsetY=0;

        // moves entire arrow in some direction
        float XarrRepair = 0;
        float YarrRepair = 0;
        Direction d = getDirection(start,end);
        //LEFT
        switch (d){
            case LEFT:
                l.setEndX(mvs.edgeLength);
                l.setEndY(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                l.setStartY(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                headOffsetX = -(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                YarrRepair = -(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                break;
            case RIGHT:
                l.setEndX(mvs.edgeLength);
                l.setEndY(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                l.setStartY(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                YarrRepair = -(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                headOffsetX = mvs.edgeLength-(Consts.arrowEmphSizeRatio*mvs.edgeLength/2);
                break;
            case UP:
                l.setEndY(mvs.edgeLength);
                l.setEndX(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                l.setStartX(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                XarrRepair = -(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                break;
            case DOWN:
                l.setEndY(mvs.edgeLength);
                l.setEndX(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                l.setStartX(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);
                XarrRepair = -(Consts.arrowEmphSizeRatio*mvs.edgeLength/4);;
                headOffsetY = mvs.edgeLength-(Consts.arrowEmphSizeRatio*mvs.edgeLength/2);
                break;

        }


        sp.getChildren().add(l);

        int minX = start.x; if (end.x < minX){ minX = end.x; }
        int minY = start.y; if (end.y < minY){ minY = end.y; }

        sp.setLayoutX(minX*mvs.edgeLength/2+XarrRepair);
        sp.setLayoutY(minY*mvs.edgeLength/2+YarrRepair);

        Polygon arrHead = drawArrowHead(d);
        arrHead.setStyle(String.format("-fx-fill: %s;", methods.colorToWebString(aap.agent.color))
                //  + String.format("-fx-border-color: #000000; "/*, methods.colorToWebString(c) */)
        );

        arrHead.setLayoutX(headOffsetX);
        arrHead.setLayoutY(headOffsetY);
        sp.getChildren().add(arrHead);

        sp.setUserData(aap.agent.color);

        return sp;
    }

    private Direction getDirection(MVPoint start, MVPoint end){
        Direction d = Direction.NONE;

        if (start.x > end.x) {
            //LEFT
            d = Direction.LEFT;
        } else if (start.x < end.x){
            //RIGHT
            d = Direction.RIGHT;
        } else if (start.y > end.y){
            //UP
            d = Direction.UP;
        } else if (start.y < end.y) {
            //DOWN
            d = Direction.DOWN;
        }  else {
            d = Direction.NONE;
        }
        return d;

    }

    private Node emphasizeAgentActionPairNodeCircle(AgentActionPair aap){

        Circle c = new Circle();
        c.setRadius(Consts.circleEmphSizeRatio*mvs.edgeLength/2);
        c.setFill(aap.agent.color);
        c.setLayoutX(aap.current.position.x*mvs.edgeLength/2 );
        c.setLayoutY(aap.current.position.y*mvs.edgeLength/2 );
        return c;
    }


    enum Direction {UP,DOWN, LEFT,RIGHT,NONE};

    public Polygon drawArrowHead(Direction dir){
        Polygon polygon = new Polygon();
         double arrSize = Consts.arrowEmphSizeRatio*mvs.edgeLength/2;
         double arrSizeHLF = arrSize /2;

        //Adding coordinates to the polygon
        switch (dir){
            case UP:
                polygon.getPoints().addAll(arrSizeHLF, 0d,
                        0d, arrSize,
                        arrSize,arrSize);
                break;
            case LEFT:
                polygon.getPoints().addAll(0d, arrSizeHLF,
                        arrSize,0d,
                        arrSize,arrSize);
                break;
            case RIGHT:
                polygon.getPoints().addAll(0d, 0d,
                        arrSize,arrSizeHLF,
                        0d,arrSize);
                break;
            case DOWN:
                polygon.getPoints().addAll(0d, 0d,
                        arrSize,0d,
                        arrSizeHLF,arrSize);
                break;
        }


        return polygon;
    }

    /** assuming direction may differ by maximum 90 angle, and is alligned in 0 90,180 and 270*/
    public Line getHeadDirection(AgentActionPair aap,double interleave){

        Direction d = getDirection(aap.current.position,aap.next.position);

        Line l = new Line();
        l.setStrokeWidth(mvs.edgeLength*Consts.mapAgentRepCircleSize*0.25);
        l.setFill(aap.agent.color);
        float lineLen = mvs.edgeLength*Consts.mapAgentRepCircleSize*1.4f;
        switch (d){
            case UP:
                l.setEndY(-lineLen);
                break;
            case LEFT:
                l.setEndX(-lineLen);
                break;
            case RIGHT:
                l.setEndX(lineLen);
                break;
            case DOWN:
                l.setEndY(lineLen);
                break;
            case NONE:
                int curRot =0;
                int nextRot =0;
                if (aap.current.rotation != null && aap.next.rotation != null){
                    curRot = aap.current.rotation;
                    nextRot = aap.next.rotation;
                    /* adjust, so rotation goes over acute angle **/
                    if (aap.current.rotation == 0  || aap.current.rotation == 360){
                        if (aap.next.rotation < 180){ curRot = 0;} else {curRot = 360;}
                    }

                    if (aap.next.rotation == 0  || aap.next.rotation == 360){
                        if (aap.current.rotation < 180){ nextRot = 0;} else {nextRot = 360;}
                    }


                    /* 0 - up, 90 right. set offset.  **/
                    double rot = curRot*(1f-interleave) + nextRot*interleave;
                    double y = -Math.cos(rot*angleToRad)*lineLen;
                    double x = Math.sin(rot*angleToRad)*lineLen;
                    l.setEndX(x);
                    l.setEndY(y);
                }
        }
        return l;

    }


}
