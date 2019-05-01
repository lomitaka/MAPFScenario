package mapfScenario;

import javafx.scene.control.Slider;
import mapfScenario.agents.AgentManager;
import mapfScenario.mapView.MapView;
import mapfScenario.mapView.MapViewSettings;
import org.apache.log4j.Logger;

public class ZoomSlider {

    private final Logger logger = Logger.getLogger(MapView.class);


    private Slider zoomSlider;
    private MapViewSettings mvs;
    private MapView mv;
    private AgentManager am;


    public ZoomSlider(Slider zoomSlider, MapViewSettings mvs, MapView mv, AgentManager am){
        this.zoomSlider= zoomSlider;
        this.mvs = mvs;
        this.am = am;
        this.mv = mv;

        //adds listener
        this.zoomSlider.valueProperty().addListener(a -> {
            logger.debug("zoom slider event happened");
            zoomSliderChange();
        });
    }

    //0      1     2     3     4    5  6  7  8  9  10
    float[] zoomArr = new float[]{  0.25f, 0.33f, 0.40f, 0.50f, 0.66f,  1f, 1.5f, 2f, 2.5f, 3f, 4f};
    public void zoomSliderChange(){
        int value = (int)zoomSlider.getValue();
        double scaledValue = zoomArr[value];
        logger.debug("ScaledValue: " + scaledValue );
        logger.debug("NewEdgeLength: " + (int)(Consts.defaultEdgeLength* scaledValue) );
        mvs.edgeLength = (int)(Consts.defaultEdgeLength* scaledValue);
        int edgeWidth =(int)(Consts.defaultEdgeWidth* scaledValue);
        if (edgeWidth <= 0 ) {edgeWidth = 1;}
        mvs.edgeWidth = edgeWidth ;


        //
        mv.setMapSize(mvs.sizeX,mvs.sizeY);
        mv.reloadObstaclesRequest();

        am.refreshAgentFlags();
    }




}
