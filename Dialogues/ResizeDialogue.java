/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogues;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import paint.ResizableCanvas;

/**
 *
 * @author Logan
 */
public class ResizeDialogue {
    
    public ResizeDialogue() {
        
    }
    
    public ResizableCanvas doResize(ResizableCanvas canvas) {
        //layout of popup
        GridPane gridPane = new GridPane();
        Label heightLabel = new Label("Height Value: ");
        Label widthLabel = new Label("Width Value: ");

        //getting height/width of current canvas
        TextField heightInput = new TextField(Double.toString(canvas.getHeight()));
        TextField widthInput = new TextField(Double.toString(canvas.getWidth()));
        Button update = new Button("Update"); //to update height/width of canvas

        //putting gridpane together
        gridPane.add(heightLabel, 0, 0);
        gridPane.add(heightInput, 1, 0);
        gridPane.add(widthLabel, 0, 1);
        gridPane.add(widthInput, 1, 1);
        gridPane.add(update, 1, 2);

        //pop up for resizing canvas
        Alert alert = new Alert(Alert.AlertType.INFORMATION); //setting type of alert it is
        alert.setTitle("Resize Canvas");
        alert.getDialogPane().setContent(gridPane); //putting gridPane into alert

        //what to do when clicking update button
        update.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                canvas.resizeCanvas(Double.parseDouble(widthInput.getText()), Double.parseDouble(heightInput.getText()));
                alert.close();
            }
        });
        alert.showAndWait();
        return canvas;
    }
    
}
