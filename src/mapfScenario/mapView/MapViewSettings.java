package mapfScenario.mapView;

import javafx.scene.paint.Color;

public class MapViewSettings {

    /** determines count of vertical lines in map */
    public int sizeX;
    /** determines count of horizontal lines in map */
    public int sizeY;

    /** defines count of vertiacl lines in map and count of spaces between them */
    public int getSizeXRefined(){
        return sizeX*2-1;
    }
    /** defines count of horizontal lines in map and count of spaces between them */
    public int getSizeYRefined(){
        return sizeY*2-1;
    }

    /** length beetween Points (crosspoints)  */
    public int edgeLength;
    /** width of displayed line */
    public int edgeWidth;

    public Color mapBackGroundColor;

    public MapViewSettings(){}

    public MapViewSettings(MapViewSettings mvs){
        sizeX = mvs.sizeX;
        sizeY = mvs.sizeY;
        edgeLength = mvs.edgeLength;
        edgeWidth = mvs.edgeWidth;
        mapBackGroundColor = mvs.mapBackGroundColor;
    }



}
