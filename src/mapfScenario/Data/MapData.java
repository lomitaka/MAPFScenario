package mapfScenario.Data;

import graphics.MVPoint;
import javafx.scene.paint.Color;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** class for bare minimum od data to fully represent Map. Contains size of map and list of obstacles.
 * This class is used as model to use in anoter classes where map information are needed.  */
public class MapData implements Serializable {
    public transient Set<MVPoint> obstacles;
    /* rought grid size, ie, count of nodes */
    public int sizeX;
    /* rought grid size, ie, count of nodes */
    public int sizeY;



    public MapData (Set<MVPoint> obstacles,int sizeX,int sizeY){
        this.obstacles = obstacles;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    /** refined grid size, where are edges included */
    public int getSizeXRefined(){
        return sizeX*2-1;
    }
    /** refined grid size, where are edges included */
    public int getSizeYRefined(){
        return sizeY*2-1;
    }


    /** serialization helpout used to serialize obstacles as array */
    private ArrayList<MVPoint> serObstacle;

    private Object readResolve() throws ObjectStreamException {
        obstacles = new HashSet<>(serObstacle);
        return this;
    }
    private Object writeReplace() throws ObjectStreamException{
        serObstacle = new ArrayList<>(obstacles);
        return  this;
    }
}
