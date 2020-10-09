/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapes;

import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import paint.ResizableCanvas;

/**
 *
 * @author Logan
 */
public class MySelection extends MyShapes{
    
    //data for rectangle
    private GraphicsContext graphicsContext;
    public double startX, startY, endX, endY, width, height, pasteX, pasteY;
    private WritableImage temp, croppedSnap;
    private ResizableCanvas canvas;
    private Image image, startImage, endImage;
    private Rectangle rectangle = new Rectangle();
    private SnapshotParameters backgroundSnap = new SnapshotParameters();

    public MySelection() {
        backgroundSnap.setFill(Color.TRANSPARENT);
    }

    //following 3 methods to get info from canvas
    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }
    
    public void setPastePoints(double pasteX, double pasteY) {
        this.pasteX = pasteX;
        this.pasteY = pasteY;
    }

    //following 2 methods setting points of rectangle
    public void setStartPoint(double startX, double startY) {
        this.startX = startX;
        this.startY = startY;
        System.out.println(startX + " " + startY);
        rectangle.setX(startX);
        rectangle.setY(startY);
    }

    public void setEndPoint(double endX, double endY) {
        this.endX = endX;
        this.endY = endY;
        System.out.println(endX + " " + endY);
    }

    //setting width and height of rectangle
    public void setWidth() {
        this.width = Math.abs((endX - startX));

        rectangle.setWidth(Math.abs((endX - startX)));
    }

    public void setHeight() {
        this.height = Math.abs((endY - startY));

        rectangle.setHeight(Math.abs((endY - startY)));
    }
    
    public void setCanvas(ResizableCanvas canvas) {
        this.canvas = canvas;
    }
    
    public void setStartImage(Image startImage) {
        this.startImage = startImage;
    }
    
    public void setEndImage(Image endImage) {
        this.endImage = endImage;
    }

    public void check() {
        if (getX() > endX) {
            rectangle.setX(endX);
        }
        if (getY() > endY) {
            rectangle.setY(endY);
        }
    }

    //following 6 methods to get any necessary attributes of rectangle
    public double getX() {
        return rectangle.getX();
    }

    public double getY() {
        return rectangle.getY();
    }

    public double getWidth() {
        return rectangle.getWidth();
    }

    public double getHeight() {
        return rectangle.getHeight();
    }
    
    public ResizableCanvas getCanvas() {
        return canvas;
    }
    
    public Image getStartImage() {
        return startImage;
    }

    public Image getEndImage() {
        return endImage;
    }

    public void clearSection() {
        temp = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(backgroundSnap, temp);
        graphicsContext.clearRect(getX(), getY(), getWidth(), getHeight());
        croppedSnap = new WritableImage(temp.getPixelReader(), (int) startX, (int) startY, (int) getWidth(), (int) getHeight());
        image = croppedSnap;
    }
    //put rectangle on canvas
    public void paste() {
        System.out.println(pasteX + " " + pasteY);
        graphicsContext.drawImage(image, pasteX, pasteY);
        
        //graphicsContext.stroke();
        //graphicsContext.clearRect(getX(), getY(), getWidth(), getHeight());
    }

    public void onUndo() {
         graphicsContext.drawImage(startImage, 0, 0);
    }
    
    public void onRedo() {
        graphicsContext.drawImage(endImage, 0, 0);
    }
    
    @Override
    public boolean containsPoint(Point2D point) {
        return rectangle.contains(point);
    }
}
