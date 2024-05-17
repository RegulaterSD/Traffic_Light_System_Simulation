import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Traffic extends Application {

    /**
     * The Main method of the file
     * @param args Any arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This is the start of the JavaFx
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        TrafficScene trafficScene = new TrafficScene();

        primaryStage.setTitle("Traffic Light");
        primaryStage.setScene(trafficScene.Traffic());

        primaryStage.show();
    }
}
