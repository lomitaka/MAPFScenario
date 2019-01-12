package fileHandling;

import graphics.MVPoint;
import javafx.stage.FileChooser;
import mapfScenario.Consts;
import mapfScenario.Data.MapData;
import mapfScenario.DataStore;
import mapfScenario.mapView.MapView;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapViewFileWorker {
    private final Logger logger = Logger.getLogger(MapViewFileWorker.class);


    public void SaveToFile(MapView mv) {
        logger.debug("saving map to file");
        //trying to edit file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose map file");
        //fileChooser.showOpenDialog(null);
        //fileChooser.setSelectedExtensionFilter();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MAP", "*.map")
        );
        File file = fileChooser.showSaveDialog(DataStore.mainWindow);

        if (file == null) {
            logger.debug("cant write to file");
            return;
        }

        if (!file.getName().toLowerCase().endsWith(".map")){
            file = new File(file.getAbsolutePath()+".map");
        }
        logger.debug(String.format("file chosen: %s ", file.getAbsolutePath()));

        //ALWAYS WRITING REFINED. (unless someone asks for for not refined
        WriteRefinedMv(mv, file);
       /* if (mvNeedsRefine(mv)) {
            logger.info("Saving refined map");
            WriteRefinedMv(mv, file);
        } else {
            logger.info("Saving rough map");
            WriteRoughMv(mv,file);
        }*/

    }

    private boolean mvNeedsRefine(MapView mv){

        for (MVPoint obst: mv.getObstacles())
        {
         if (!MapView.isInGridAndOnGridCross(obst,mv.getSizeXRefined(),mv.getSizeYRefined())){
             return true;
         }
        }
        return false;

    }

    /** returns true if file looks like it is refined. ie has odd width and height and every odd inxes is obstacle */
    private boolean fileIsRefined(MapInnerData mid){
        //check size
        if ((mid.sizeX % 2 != 1) && (mid.sizeY % 2 != 1)){return false;}

        for (int y = 1; y < mid.rows.size();y+=2) {
            String row = mid.rows.get(y);
            for (int x = 1; x < row.length();x+=2){

                if (!(row.charAt(x)+"").equals(Consts.mapSaveFullSpace)){
                    return false;
                }
            }
        }

        return true;
    }

    private void WriteRefinedMv(MapView mv, File file){
        Set<MVPoint> obstacles = mv.getObstacles();
        String nl = System.getProperty("line.separator");

        try {
            FileWriter fw = new FileWriter(file);
            fw.write("type tile");
            fw.write(nl);
            fw.write(String.format("%s %s",heightText, mv.getSizeYRefined()));
            fw.write(nl);
            fw.write(String.format("%s %s",widthText, mv.getSizeXRefined()));
            fw.write(nl);
            fw.write("map");
            fw.write(nl);

            int fileI = 0;
            //Iterate over file and write obstacles.
            for (int y = 0; y < mv.getSizeYRefined(); y++) {
                for (int x = 0; x < mv.getSizeXRefined() ; x++) {
                    MVPoint p = new MVPoint(x, y);
                    if (obstacles.contains(p)) {
                        fw.write(Consts.mapSaveFullSpace);
                        // distance within white cells is not walkable
                    } else if (x % 2 == 1 && y % 2 == 1) {
                        fw.write(Consts.mapSaveFullSpace);
                    } else {
                        fw.write(Consts.mapSaveVoidSpace);
                    }
                }
                fw.write(nl);
            }

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteRoughMv(MapView mv, File file){
        Set<MVPoint> obstacles = mv.getObstacles();
        String nl = System.getProperty("line.separator");

        try {
            FileWriter fw = new FileWriter(file.getName());
            fw.write("type tile");
            fw.write(nl);
            fw.write(String.format("%s %s",heightText, mv.getSizeY()));
            fw.write(nl);
            fw.write(String.format("%s %s",widthText, mv.getSizeX()));
            fw.write(nl);
            fw.write("map");
            fw.write(nl);

            int fileI = 0;
            //Iterate just only over vertexes.
            for (int y = 0; y < mv.getSizeYRefined(); y+=2) {
                for (int x = 0; x < mv.getSizeXRefined(); x+=2) {
                    MVPoint p = new MVPoint(x, y);
                    if (obstacles.contains(p)) {
                        fw.write(Consts.mapSaveFullSpace);
                        // distance within white cells is not walkable
                    } else {
                        fw.write(Consts.mapSaveVoidSpace);
                    }
                }
                fw.write(nl);
            }

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    static final String heightText = "height";
    static final String widthText = "width";





    public MapData LoadFromFile() {
        logger.debug("loading map to file");
        //trying to edit file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Map File");
        //fileChooser.showOpenDialog(null);
        //fileChooser.setSelectedExtensionFilter();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MAP", "*.map")
        );
        //File file = fileChooser.showSaveDialog(DataStore.mainWindow);
        File file  = fileChooser.showOpenDialog(DataStore.mainWindow);

        if (file == null ) {
            logger.debug("cant read file.");
            return null;
        }
        logger.debug(String.format("file chosen: %s ", file.getName()));

        //always laod refined map.
        MapInnerData fileData = this.LoadMapDataInner(file);
        return parseRefinedDataMap(fileData);
        /*if (fileIsRefined(fileData)){
            logger.info("loading refined file");
            return parseRefinedDataMap(fileData);
        } else {
            logger.info("loading rough file");
            return parseRuoghDataMap(fileData);
        }*/

    }

    private class MapInnerData {
        public List<String> rows;
        public int sizeX;
        public int sizeY;

    }

    /** Reads refined grid and returns map data */
    public MapData LoadMapData(File file){
        MapInnerData mid = LoadMapDataInner(file);
        if (mid == null){return null;  }
        return parseRefinedDataMap(mid);
    }

    /* reads data from file into preparsed format */
    private MapInnerData LoadMapDataInner(File file){

        int height=-1;
        int width=-1;

        String readLine = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            readLine = br.readLine();
            if (readLine == null || !readLine.trim().equals("type tile") ){
                logger.warn(String.format("Expected 'type tile', got '%s'",readLine));
                return null;
            }

            readLine = br.readLine();

            height =getLineValue(readLine, heightText);
            if (height < 0) { return null;}

            readLine = br.readLine();
            width =getLineValue(readLine, widthText);
            if (width < 0) { return null;}

            readLine = br.readLine();
            if (!readLine.trim().equals("map")){
                logger.warn(String.format("Expected 'map', got '%s'",readLine));
                return null;
            }

            List<String> result = new ArrayList<>();
            for (int i = 0; i <height;i++){
                readLine = br.readLine();
                if (readLine == null || readLine.trim().length() != width) {
                    logger.warn("unexpected file end");
                    return null;
                }
                result.add(readLine);
            }

            br.close();

            MapInnerData mid = new MapInnerData();
            mid.sizeX = width;
            mid.sizeY = height;
            mid.rows = result;
            return mid;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private MapData parseRefinedDataMap(MapInnerData mid){


        Set<MVPoint> obstacles = new HashSet<>();
        String nl = System.getProperty("line.separator");


            //Iterate over file and write obstacles.
            for (int y = 0; y < mid.sizeY; y++) {
                String line = mid.rows.get(y);


                for (int x = 0; x < mid.sizeX; x++) {
                    String c = line.charAt(x)+"";


                    if (c.equals(Consts.mapSaveFullSpace)) {
                        MVPoint p = new MVPoint(x, y);
                        obstacles.add(p);
                        // distance within white cells is not walkable
                    }
                }

            }

        return new MapData(obstacles,mid.sizeX/2+1,mid.sizeY/2+1);
    }


    private MapData parseRuoghDataMap(MapInnerData mid){

        Set<MVPoint> obstacles = new HashSet<>();
        String nl = System.getProperty("line.separator");


        //Iterate over file and write obstacles.
        for (int y = 0; y < mid.sizeY; y++) {
            String line = mid.rows.get(y);


            for (int x = 0; x < mid.sizeX; x++) {
                String c = line.charAt(x)+"";


                if (c.equals(Consts.mapSaveFullSpace)) {
                    MVPoint p = new MVPoint(x*2, y*2);
                    obstacles.add(p);

                }
            }

        }

        return new MapData(obstacles,mid.sizeX,mid.sizeY);
    }


    private int getLineValue(String readLine, String param){
        if (readLine == null || readLine.length() < param.length() + 2){
            logger.warn(String.format("Expected 'height num', got '%s'",readLine));
            return -1;
        }

        String par1 = readLine.substring(0,param.length()).trim();
        String par2 = readLine.substring(param.length()).trim();
        int height;

        if (!par1.equals(param)){
            logger.warn(String.format("Expected 'num', got '%s'",readLine));
            return -1;
        }

        try {
            return Integer.parseInt(par2);
        } catch (Exception e){
            return -1;
        }

    }


}



