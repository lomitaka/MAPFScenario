package mapfScenario.mapView;

import graphics.GraphicManager;
import graphics.MVPoint;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import mapfScenario.Consts;
import mapfScenario.Data.MapData;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** Takes care about grid, holes and agents point displayed on map.
 * MapView is composed of three overlayed panes.
 * bottom one contains drawed lines to show where agent can move.
 * middle layer contains set of white obstacles, that covers parts of the line, and make it looks like line is divided
 * into two.
 * top layer is empty, and into it is added mouse click listener, which reads position of current click.
 *
 * */
public class MapView {

    private final Logger logger = Logger.getLogger(MapView.class);

    /** settings for current map*/
    public MapViewSettings mvs;



    /** serves to show grid*/
    private Pane mapViewPaneGrid;
    /** serves to show obstacles (mask some grid parts */
    private Pane mapViewPaneObstacles;

    /** list of mapLines used to realMapDisplay grid */
    private HashSet<Line> mapLines;

    /* consist of objects that creates holes in grid (obstacle) */
    private HashMap<MVPoint, Rectangle> obstacles;

    /** help class to manage line transformations */
    private GraphicManager gm;

    /** allows to display some information */
    private Label infoLabel;



    /** represents current meaning of clicking on map */
    public MapViewState.AbstractMapHandler mapActionState = new MapViewState.NullMapHandler();

    public MapView(Pane mapViewPane, Pane mapViewPaneObstacles, int sizeX, int sizeY){

        mvs = new MapViewSettings();
        mvs.sizeX = sizeX;
        mvs.sizeY = sizeY;
        mvs.edgeLength = Consts.defaultEdgeLength;
        mvs.edgeWidth = Consts.defaultEdgeWidth;
        mvs.mapBackGroundColor = Color.web(Consts.mapBackGroundColor );
        this.mapViewPaneGrid = mapViewPane;
        //this.mapViewsizeX = sizeX;
        //this.mapViewsizeY = sizeY;
        this.mapViewPaneObstacles = mapViewPaneObstacles;

        mapLines = new HashSet<>();
        obstacles = new HashMap<MVPoint, Rectangle>();
        //edgeLength = Consts.defaultEdgeLength;
        gm = new GraphicManager(mvs);
    }


    /** assigns infoLabel  */
    public void setSourceinfoLabel(Label l){
        infoLabel = l;
        infoLabel.setText("");
        //infoLabel.setStyle("-fx-fill: red;");
    }

    /** display information to infolabel*/
    public void setInfoLabel(String text){
        infoLabel.setText(text);
    }

    public void reloadMapsize(){
        setMapSize(mvs.sizeX,mvs.sizeY);
    }

    /**
     * clears old grid and makes new one.
     * */
    public void setMapSize(int sizeX,int sizeY){

        logger.debug("set MapSize:" + sizeX + " " + sizeY);


        mapViewPaneGrid.getChildren().removeAll(mapLines);
        mapLines.clear();



        // Generate vertical lines
        for (int i = 0; i <sizeX ; i++){
            Line l = gm.makeLine(new MVPoint(i,0),new MVPoint(i,sizeY-1));
            gm.stretchLineByHeight(l,Consts.mapLineStretch);
            logger.debug(String.format(" Line S start x: %f y: %f, end: x %f, y:%f " ,l.getStartX(),l.getStartY(),l.getEndX(),l.getEndY()));
            gm.moveLineByCoords(l, Consts.mapLineMove,Consts.mapLineMove );
            logger.debug(String.format(" Line M start x: %f y: %f, end: x %f, y:%f " ,l.getStartX(),l.getStartY(),l.getEndX(),l.getEndY()));
            mapViewPaneGrid.getChildren().add(l);
            mapLines.add(l);
        }

        // Generate horizontal lines
        for (int i = 0; i <sizeY ; i++){
            Line l = gm.makeLine(new MVPoint(0,i),new MVPoint(sizeX-1,i));
            gm.stretchLineByWidth(l,Consts.mapLineStretch);
            logger.debug(String.format(" Line S start x: %f y: %f, end: x %f, y:%f " ,l.getStartX(),l.getStartY(),l.getEndX(),l.getEndY()));
            gm.moveLineByCoords(l, Consts.mapLineMove,Consts.mapLineMove );
            logger.debug(String.format(" Line M start x: %f y: %f, end: x %f, y:%f " ,l.getStartX(),l.getStartY(),l.getEndX(),l.getEndY()));
            mapViewPaneGrid.getChildren().add(l);

            mapLines.add(l);
        }

        mvs.sizeX = sizeX;
        mvs.sizeY = sizeY;


    }


    /** evaluates click on mapView plane. Action is handled by state design pattern */
    public void clickAction(MouseEvent event)
    {
        mapActionState.performAction(event);
    }

    public void removeEdgeButtonClick() {
        mapActionState.cancelAction();
        mapActionState = new MapViewState.ObstacleAddMapHandler(this);
    }


    public void addEdgeButtonClick() {
        mapActionState.cancelAction();
        mapActionState = new MapViewState.ObstacleRemoveMapHandler(this);
    }

    public void noneEdgeButtonClick() {
        mapActionState.cancelAction();
        mapActionState = new MapViewState.NullMapHandler();
    }


    public void addObstacleRequest(float x, float y){

        /*
        * variants agent point x+y is even then do add cross
        * */

        MVPoint point = translateScrCoordsToMVPoint(x,y);

        if (point == null){
            logger.debug("invalid point doing nothing ");
            //invalid click do nothing
            return;
        }

        //if clicked inside white square will ignore
        if ((point.x % 2 == 1) && (point.y % 2 == 1)){
            logger.debug("invalid point not on grid, doing nothing ");
            return;
        }

        if (obstacles.containsKey(point)){
            logger.debug("obstacle already exists");
            return;
        }
        logger.debug("creating new rectangle");
        Rectangle rec = gm.makeRectangle(point);
        gm.moveRectangleByCoords(rec, Consts.mapLineMove,Consts.mapLineMove );
        obstacles.put(point,rec);
        //mapViewPaneGrid.getChildren().add(c);
        mapViewPaneObstacles.getChildren().add(rec);

    }

    public void removeObstacleRequest(float x, float y){

        MVPoint point = translateScrCoordsToMVPoint(x,y);

        if (point == null){
            logger.debug("invalid point doing nothing ");
            //invalid click do nothing
            return;
        }

        if (!obstacles.containsKey(point)){
            logger.debug("obstacle not found");
            return;
        }
        Rectangle rec = obstacles.get(point);
        logger.debug("removing square");
        //Circle c = gm.makeCircle(point);
        //gm.moveCircleByCoords(c, Consts.mapLineMove,Consts.mapLineMove );
        obstacles.remove(point);
        //mapViewPaneGrid.getChildren().add(c);
        mapViewPaneObstacles.getChildren().remove(rec);

    }

    /** gets screen coordinates and translates them to map cordinates. returns null if point is invalid*/
    public MVPoint translateScrCoordsToMVPoint( float x, float y ){
        // compute original value without line move (should match cross locations)

        float origX = x -mvs.edgeLength *Consts.mapLineMove;
        float origY = y -mvs.edgeLength *Consts.mapLineMove;
        logger.debug(String.format("InteractionLayerOnclickMouse: M X: %f, Y: %f", origX, origY ));
        //determine x
        int MVx =    ((Double)(Math.floor((origX + (float)mvs.edgeLength /4 ) / (mvs.edgeLength/2)))).intValue();
        int MVy =    ((Double)(Math.floor((origY + (float)mvs.edgeLength /4 ) / (mvs.edgeLength/2)))).intValue();

        if (MVx >= mvs.sizeX*2-1 || MVx < 0){
            logger.warn(String.format("InteractionLayerOnclickMouse OUT OF MAP: MV X: %d, Y: %d", MVx, MVy ));
            return  null;
        }
        if (MVy >= mvs.sizeY*2-1 || MVy < 0){
            logger.warn(String.format("InteractionLayerOnclickMouse OUT OF MAP: MV X: %d, Y: %d", MVx, MVy ));
            return  null;
        }
        logger.debug(String.format("InteractionLayerOnclickMouse: MV X: %d, Y: %d", MVx, MVy ));



        return new MVPoint(MVx,MVy);
    }

    /** clears obstacles on pane, and recreates new obstacles from obstacle definitions */
    public void reloadObstaclesRequest(){

        logger.debug("reloading obstacles");
        Set<MVPoint> obst = getObstacles();
        setObstacles(obst);

    }

    /** removes every obstacle */
    public void clearObstacles(){
        logger.debug("Clearing obstacles");
        Set<MVPoint> empty = new HashSet<MVPoint>();
        setObstacles(empty);
    }


    public void setEdgeLength(int edgeLength){
        mvs.edgeLength = edgeLength;
        setMapSize(mvs.sizeX,mvs.sizeY);
        reloadObstaclesRequest();
    }
    public void setEdgeWidth(int edgeWidth){
        mvs.edgeWidth = edgeWidth;
        setMapSize(mvs.sizeX,mvs.sizeY);
        reloadObstaclesRequest();
    }

    /** return number of vertexes in x direction */
    public int getSizeX(){return mvs.sizeX;}
    /** return number of vertexes in y direction */
    public int getSizeY(){return mvs.sizeY;}
    /** return number of vertexes and removable edges in y direction */
    public int getSizeYRefined(){return mvs.getSizeYRefined();}
    /** return number of vertexes and removable edges in x direction */
    public int getSizeXRefined(){return mvs.getSizeXRefined();}

    /** loads map by mapData */
    public void loadByData(MapData mapData){
        setMapSize(mapData.sizeX,mapData.sizeY);
        setObstacles(mapData.obstacles);
    }

    /** returns map data of current map */
    public MapData getMapData(){
        return new MapData(obstacles.keySet(),mvs.sizeX,mvs.sizeY);
    }

    /** returns obstacle as points */
    public Set<MVPoint> getObstacles(){
        return obstacles.keySet();
    }

    /** clears obstacles from pane, creates new obstacles and updates obstacles variable */
    public void setObstacles(Set<MVPoint> mppoints){
        logger.debug("setting Obstacles");

        logger.debug("clearing current nodes");
        mapViewPaneObstacles.getChildren().clear();
        HashMap<MVPoint, Rectangle> obstaclesNew = new HashMap<>();

        for (MVPoint point : mppoints) {
            //MVPoint point = o.getKey();
            Rectangle rec = gm.makeRectangle(point);
            gm.moveRectangleByCoords(rec, Consts.mapLineMove,Consts.mapLineMove );
            obstaclesNew.put(point,rec);
            mapViewPaneObstacles.getChildren().add(rec);
        }
        obstacles = obstaclesNew;

    }


    /**
     * returns true if clicked point is on visible grid (not in space between)
     * */
    public static boolean isOnGridCross(MVPoint point){
        return (point.x % 2 == 0 && point.y % 2 == 0);
    }


    /** returns true if current point is in the grid, and is inside bounds of grid */
    public boolean isInGridAndOnGridCross(MVPoint pt){
        return MapView.isInGridAndOnGridCross(pt,mvs.getSizeXRefined(),mvs.getSizeYRefined());
    }

    /**
     *  returns true, if point point is in grid cross and in grid boundary
     * */
    public static boolean isInGridAndOnGridCross(MVPoint pt, int mapWidthRefined, int mapHeightRefined){
        Logger logger = Logger.getLogger(MapView.class);
        logger.debug(String.format("PT: %s, maprefW %s, maprefH %s ",pt.toString(),mapWidthRefined,mapHeightRefined));
        if (pt.x >= 0 && pt.x < mapWidthRefined && pt.y >= 0 && pt.y < mapHeightRefined ){
            boolean retval = isOnGridCross(pt);
            if (retval){
                logger.debug("is IN GRID AND IS ON LINE ");
                return true; }
            else{
                logger.debug("is ON GRID BUT NOT ON LINE ");
                return false;
            }
        }
        logger.debug("is NOT IN GRID ");
        return false;
    }

    /** tries to check if there is not too much agents. and there is space to add agents.  */
    public boolean agentIsAddable(int agentCount){
        logger.debug(String.format("agent addable? for %d ",agentCount));
        int crossPoints = (int)obstacles.keySet().stream().filter(mvpoint -> MapView.isOnGridCross(mvpoint)).count();

        if ((mvs.sizeX * mvs.sizeY - crossPoints - agentCount) > 0){
            logger.debug("is addable");
            return true;
        }
        logger.debug("is not addable");
        return false;
    }


}
