/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogues;

import java.io.File;
import java.util.Optional;
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
public class SaveDialogue extends Dialogue{

    private boolean saved;
    private boolean hasBeenOpened;
    private boolean setCanvasSaved;
    private boolean enableAutosave;
    private SnapshotParameters backgroundSnap = new SnapshotParameters();

    public SaveDialogue() {
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void setHasBeenOpened(boolean hasBeenOpened) {
        this.hasBeenOpened = hasBeenOpened;
    }

    public void setSetCanvasSaved(boolean setCanvasSaved) {
        this.setCanvasSaved = setCanvasSaved;
    }
    
    public void setEnableAutosave(boolean enableAutosave) {
        this.enableAutosave = enableAutosave;
    }

    public boolean getSaved() {
        return saved;
    }

    public boolean getHasBeenOpened() {
        return hasBeenOpened;
    }

    public boolean getSetCanvasSaved() {
        return setCanvasSaved;
    }
    
    public boolean getEnableAutosave() {
        return enableAutosave;
    }

    public File hasNotBeenSaved(File file, ResizableCanvas canvas) {
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
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", file);
            setSaved(true);
            setSetCanvasSaved(true);
            setHasBeenOpened(true);
            setEnableAutosave(true);
        } catch (Exception e) {
            System.out.println("Failed to save image.");
        }
        return file;
    }

    public File hasBeenSaved(File file, ResizableCanvas canvas) {
        //alert to confirm save
        /*Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Work?");
        alert.setContentText("Overwrite File?");
        Optional<ButtonType> rslt = alert.showAndWait();
        //if user selectes ok, save the file
        if ((rslt.isPresent()) && rslt.get() == ButtonType.OK) {
            //get current screen and save it to the selected file*/
            try {
                backgroundSnap.setFill(Color.TRANSPARENT);
                Image image = canvas.snapshot(backgroundSnap, null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", file);
                setSaved(true);
                setSetCanvasSaved(true);
                setHasBeenOpened(true);
            } catch (Exception e) {
                System.out.println("Failed to save image.");
            }
        //}
        return file;
    } 
}