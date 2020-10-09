/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogues;

import java.io.File;
import java.util.Optional;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import paint.Paint;
import paint.ResizableCanvas;

/**
 *
 * @author Logan
 */
public class ExitDialogue extends Dialogue {

    boolean setCanvasSaved;
    boolean saved;

    private SnapshotParameters backgroundSnap = new SnapshotParameters();

    public ExitDialogue() {

    }

    public boolean getSaved() {
        return saved;
    }

    public boolean getSetCanvasSaved() {
        return setCanvasSaved;
    }

    public void setSetCanvasSaved(boolean setCanvasSaved) {
        this.setCanvasSaved = setCanvasSaved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public File isFreshProject(File file, ResizableCanvas canvas) {
        //popup reminding the user work is not saved
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Work?");
        alert.setContentText("Exiting will lose all progress. Save work?");
        Optional<ButtonType> rslt = alert.showAndWait();
        //if user selects ok to save, act like saveAs button
        if ((rslt.isPresent()) && rslt.get() == ButtonType.OK) {
            //opening file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg")
        );
            file = fileChooser.showSaveDialog(Paint.getPrimaryStage());

            //get current screen and make a file
            backgroundSnap.setFill(Color.TRANSPARENT);
            Image image = canvas.snapshot(backgroundSnap, null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (Exception e) {
                System.out.println("Failed to save image.");
            }
            setSetCanvasSaved(true);
        } //if user chooses cancel button, the program will close and not save
        else if ((rslt.isPresent()) && rslt.get() == ButtonType.CANCEL) {
            Platform.exit();
            System.exit(0);
        }
        return file;
    }

    public File isEditedOrOpened(File file, ResizableCanvas canvas) {
        //pop up reminding them work has not been saved
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Work?");
        alert.setContentText("Exiting will lose all progress. Save work?");
        Optional<ButtonType> rslt = alert.showAndWait();
        //if they choose to save work, acts like save button when ok is pressed
        if ((rslt.isPresent()) && rslt.get() == ButtonType.OK) {
            //get current screen and save it to the selected file
            try {
                backgroundSnap.setFill(Color.TRANSPARENT);
                Image image = canvas.snapshot(backgroundSnap, null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                setSaved(true);
                setSetCanvasSaved(true);
            } catch (Exception e) {
                System.out.println("Failed to save image.");
            }
        }
        //after saving, exit the program
        Platform.exit();
        System.exit(0);

        return file;
    }

}