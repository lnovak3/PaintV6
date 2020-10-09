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
public class SaveAsDialogue extends Dialogue{

    private boolean saved;
    private boolean hasBeenOpened;
    private boolean enableAutosave;
    private SnapshotParameters backgroundSnap = new SnapshotParameters();

    public SaveAsDialogue() {
    }

    public void setHasBeenOpened(boolean hasBeenOpened) {
        this.hasBeenOpened = hasBeenOpened;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
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
    
    public boolean getEnableAutosave() {
        return enableAutosave;
    }

    public File hasBeenOpened(File file, ResizableCanvas canvas) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("Saving in an alternate file format may cause data loss. Continue?");
        Optional<ButtonType> rslt = alert.showAndWait();
        if ((rslt.isPresent()) && rslt.get() == ButtonType.OK) {
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
                setSaved(true);
            } catch (Exception e) {
                System.out.println("Failed to save image.");
            }
        }
        return file;
    }

    public File hasNotBeenOpened(File file, ResizableCanvas canvas) {
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
            setSaved(true);
            setHasBeenOpened(true);
            setEnableAutosave(true);
            
        } catch (Exception e) {
            System.out.println("Failed to save image.");
        }
        
        return file;
    }

}






