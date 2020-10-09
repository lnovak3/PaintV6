/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogues;

import java.awt.Desktop;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Logan
 */
public class AboutDialogue extends Dialogue{
    
    public AboutDialogue() {   
    }
    
    public void openAbout() {
        //creating a TextArea where the following will be stored to describe the progam
        TextArea textArea = new TextArea("Paint V6\n"
                + "The following program is a paint application. "
                + "You can open a picture by clicking: \n\n"
                + "File -> Open -> Select an image \n\n"
                + "Once a file is opened, you can draw on it. "
                + "Once you are done, save the file:\n\n"
                + "File -> Save As -> Save in desired directory");
        textArea.setEditable(false);
        textArea.setWrapText(true);

        Button patchNotes = new Button("Patch Notes");
        Button tools = new Button("Tools Tutorial");

        patchNotes.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                File filePatch = new File("src/paint/paint_release.txt");
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(filePatch);
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
        });

        tools.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                File filePatch = new File("src/paint/paint_tutorial.txt");
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(filePatch);
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
        });
        //creating GridPane to hold TextArea
        GridPane gridPane = new GridPane();
        gridPane.add(textArea, 0, 0); //0 corresponds to x and y position, putting in center
        gridPane.add(patchNotes, 0, 1);
        gridPane.add(tools, 0, 2);

        //creating the popup for the help menu
        Alert alert = new Alert(Alert.AlertType.INFORMATION); //setting type of alert it is
        alert.setTitle("About Paint");
        alert.getDialogPane().setContent(gridPane); //putting gridPane into alert
        alert.showAndWait(); //wait for user to do something with alert
    }
}
