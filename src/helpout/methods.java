package helpout;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import mapfScenario.DataStore;
import mapfScenario.MainFormController;
import mapfScenario.picat.SolverProcess;

import java.io.File;
import java.lang.management.PlatformManagedObject;
import java.net.URISyntaxException;


public class methods {


        /**
         Ukaze okno s textem  "vstup"
         */

        static public void showDialog(String vstup){

            if (handleCMD(vstup)){return;}

            final Stage dialog = new Stage();
            Text t =  new Text(25, 25, vstup);
            t.setStyle("-fx-font-size:15;fx-padding: 10;");
            t.maxWidth(500);
            GridPane gp =  new GridPane();
            gp.setPadding(new Insets(40, 60, 40, 60));
            gp.add(t, 0, 0);
            gp.setVgap(20);gp.setHgap(20);

            Scene scene = new Scene(gp);

            Button okButton = new Button("OK");
            okButton.setStyle("fx-padding: 30;");
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                Stage s = dialog;
                @Override
                public void handle(ActionEvent t) {;s.close();}});
            gp.add(okButton, 0, 1);
            GridPane.setHalignment(okButton, HPos.CENTER);

            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();


        }

        private static boolean handleCMD(String text){

            if (!Platform.isFxApplicationThread()) {
                System.out.println(text);
                return true;
            }
            return false;
        }




        static public void showDialog(Node vstup){


            final Stage dialog = new Stage();


            //Text t =  new Text(25, 25, vstup);
            //t.setStyle("-fx-font-size:15;fx-padding: 10;");


            GridPane gp =  new GridPane();
            gp.setPadding(new Insets(20, 40, 20, 40));
            gp.add(vstup, 0, 0);
            gp.setVgap(20);gp.setHgap(20);
            GridPane.setVgrow(vstup, Priority.ALWAYS);
            Scene scene = new Scene(gp);

            Button okButton = new Button("Zavřit");
            okButton.setStyle("fx-padding: 30;");
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                Stage s = dialog;
                @Override
                public void handle(ActionEvent t) {;s.close();}});
            gp.add(okButton, 0, 1);
            GridPane.setHalignment(okButton, HPos.CENTER);

            dialog.setScene(scene);

            //dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.show();
            //dialog.showAndWait();


        }

        /**
         Ukaze okno s textem  "vstup" agent dvema tlacitky ano/ne
         */
        static public boolean showDialogYesNo(String vstup) {
            /**
             * vytvori okno s dotazem, dvema tlacitky, tlacitky prida
             * schopnosti nastavit titulek na yes, nebo no agent zavrit okno
             * taky blokuje okna na pozadi.
             */

            final Stage dialog = new Stage();
            GridPane gp = new GridPane();
            Text t = new Text(25, 25, vstup);
            Button yesButton = new Button("Ano");
            Button noButton = new Button("Ne");

            //stylovani gridPane
            gp.setVgap(20);
            gp.setHgap(20);
            //gp.setStyle("fx-padding: 60, 80;");
            gp.setPadding(new Insets(40, 60, 40, 60));
            t.setStyle("-fx-font-size:15;fx-padding: 10;");


            gp.add(t, 0, 0, 2, 1);
            GridPane.setHalignment(t, HPos.CENTER);

            //nastavovani yesButtonu:
            yesButton.setStyle("fx-padding: 30;");
            yesButton.setOnAction(new EventHandler<ActionEvent>() {
                Stage s = dialog;

                @Override
                public void handle(ActionEvent t) {
                    s.setTitle("yes");
                    s.close();
                }
            });

            // Nastavovani NO buttonu
            noButton.setStyle("fx-padding: 30;");
            noButton.setOnAction(new EventHandler<ActionEvent>() {
                Stage s = dialog;

                @Override
                public void handle(ActionEvent t) {
                    s.setTitle("no");
                    s.close();
                }
            });


            gp.add(yesButton, 0, 1);
            gp.add(noButton, 1, 1);
            GridPane.setHalignment(yesButton, HPos.RIGHT);
            GridPane.setHalignment(noButton, HPos.LEFT);

            Scene scene = new Scene(gp);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();


            if (dialog.getTitle() != null && dialog.getTitle().equals("yes")) {
                return true;
            } else {
                return false;
            }
        }



        /**
         Ukaze okno s textem  "vstup" agent dvema tlacitky ano/ne
         */

        public static <InputType,AnswType> AnswType showDialogQuestion(IQuestion<InputType,AnswType> input) {
            /**
             * vytvori okno s dotazem, dvema tlacitky, tlacitky prida
             * schopnosti nastavit titulek na yes, nebo no agent zavrit okno
             * taky blokuje okna na pozadi.
             */

            final Stage dialog = new Stage();
            GridPane gp = new GridPane();
            //Text t = new Text(25, 25, vstup);
            Node t = input.getQuestionData();
            Button yesButton = new Button("Yes");
            Button noButton = new Button("No");

            //stylovani gridPane
            gp.setVgap(20);
            gp.setHgap(20);
            //gp.setStyle("fx-padding: 60, 80;");
            gp.setPadding(new Insets(40, 60, 40, 60));
            t.setStyle("-fx-font-size:15;fx-padding: 10;");


            gp.add(t, 0, 0, 2, 1);
            GridPane.setHalignment(t, HPos.CENTER);

            //nastavovani yesButtonu:
            //yesButton.setStyle("fx-padding: 30;");
            yesButton.setOnAction(new EventHandler<ActionEvent>() {
                Stage s = dialog;

                @Override
                public void handle(ActionEvent t) {
                    input.setIsConfirmed(true);
                    //s.setTitle("yes");
                    s.close();
                }
            });


            // Nastavovani NO buttonu
            noButton.setStyle("fx-padding: 30;");
            noButton.setOnAction(new EventHandler<ActionEvent>() {
                Stage s = dialog;

                @Override
                public void handle(ActionEvent t) {
                    //s.setTitle("no");
                    s.close();
                }
            });


            gp.add(yesButton, 0, 1);
            gp.add(noButton, 1, 1);
            GridPane.setHalignment(yesButton, HPos.RIGHT);
            GridPane.setHalignment(noButton, HPos.LEFT);

            Scene scene = new Scene(gp);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            yesButton.requestFocus();
            setGlobalEventHandler(yesButton,noButton,gp);
            dialog.showAndWait();


          //  if (dialog.getTitle() != null && dialog.getTitle().equals("yes")) {
                return input.getAnswer();
          //  } else {
          //      return null;
          //  }
        }

    private static void setGlobalEventHandler(Button ok, Button cancel,Node root) {
        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                ok.fire();
                ev.consume();
            }
            if (ev.getCode() == KeyCode.ESCAPE) {
                cancel.fire();
                ev.consume();
            }
        });
    }

        public static String colorToWebString (Color color){



            String r = (Integer.toHexString ((int)(color.getRed()*255))+"00").substring(0,2);
            String g = (Integer.toHexString ((int)(color.getGreen()*255))+"00").substring(0,2);
            String b = (Integer.toHexString ((int)(color.getBlue()*255))+"00").substring(0,2);
            return String.format("#%s%s%s",r, g,b );
            // With # prefix.
            //return "#" + Integer.toHexString(color.hashCode());
            //color.
        }

        /**returns c:/blabla/folder */
        public static String getRunningFolder(){

            try {


                String s = new File(SolverProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();

                //DEBUG Purposses
                if (s.contains("/out/production")){
                    File f = new File (SolverProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                    for (int i = 0; i < 3;i++) f = f.getParentFile();
                    return f.getAbsolutePath();
                }

                //methods.showDialog(s);
                return s;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return "";
        }

        /** tests if file is with absolute path, if not, appends apps path.*/
        public static File fileToAbsolute(String fileName){
            File file = new File(fileName);
            if (!file.isAbsolute()){
                String fullPath = getRunningFolder()  +File.separator  + fileName;
                file = new File(fullPath);
            }

            return file;
        }


}
