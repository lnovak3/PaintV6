package paint;

import java.awt.Dimension;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Paint extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

 
        setPrimaryStage(stage);

        //setting the scene based off of the FXML file paint.fmxl and showing
        //it when the file is opened
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("paint.fxml")));
        stage.setScene(scene);
        stage.setTitle("Paint");
        //set Stage boundaries to visible bounds of the main screen
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        stage.show();
    }

    //following two methods used in order to grant access to the current stage in the Controller
    private void setPrimaryStage(Stage stage) {
        Paint.primaryStage = stage;
        primaryStage.setTitle("Paint");
    }

    public static Stage getPrimaryStage() {
        return Paint.primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
