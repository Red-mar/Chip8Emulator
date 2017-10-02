package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    final VBox vBox = new VBox();
    Chip chip;

    @Override
    public void start(Stage primaryStage) throws Exception{

        chip = new Chip();

        /**
         * Set up render system and register input callbacks
         */


        /**
         * Initialize the chip8 system and load the game into memory
         */
        chip.initialize();
        chip.loadGame("?");

        /**
         * Emulation loop?
         */


        //for (;;){
            /**
             * Emulate 1 cycle
             */

            /**
             * If draw flag is set update the screen.
             */

            /**
             * Store the key press state (Press and release)
             */
        //}


        primaryStage.setTitle("emu");
        final Scene parentScene = new Scene(vBox);

        parentScene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
        primaryStage.setScene(parentScene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
