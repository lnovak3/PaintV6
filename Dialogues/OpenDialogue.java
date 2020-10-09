/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogues;

import java.io.File;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import paint.Paint;
import paint.ResizableCanvas;

/**
 *
 * @author Logan
 */
public class OpenDialogue extends Dialogue {
    boolean saved;
    boolean hasBeenOpened;
    boolean canvasSaved;
    boolean enableAutosave;
    
    public OpenDialogue() {
        
    }
    
    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    
    public void setCanvasSaved(boolean canvasSaved) {
        this.canvasSaved = canvasSaved;
    }
    
    public void setHasBeenOpened(boolean hasBeenOpened) {
        this.hasBeenOpened = hasBeenOpened;
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
    
    public boolean getCanvasSaved() {
        return canvasSaved;
    }
    
    public boolean getEnableAutosave() {
        return enableAutosave;
    }
    
    public File openFile(File file, ResizableCanvas canvas, GraphicsContext graphicsContext) {
        //opening file chooser
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(Paint.getPrimaryStage());

        //put image on the canvas
        if (file != null) {
            Image image = new Image(file.toURI().toString());

            //setting the canvas to the size of the image
            canvas.setHeight(image.getHeight());
            canvas.setWidth(image.getWidth());
            graphicsContext.drawImage(image, 0, 0); //0 corresponds to x and y position, putting in center
            setSaved(true);
            setCanvasSaved(true);
            setHasBeenOpened(true);
            setEnableAutosave(true);
            
        }
        
        return file;
    }
    
    /**
     * used to set attributes of file chooser
     *
     * @param fileChooser used to open File Chooser Dialogue to open files into
     * canvas
     */
    //configuring the file chooser
    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures"); //title of file chooser
        fileChooser.setInitialDirectory( //which directory the dialogue will start in
                new File(System.getProperty("user.home"))
        );

        //the extentions the user will be open from the file chooser
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.png", "*.jpeg"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg")
        );
    }
    
}
