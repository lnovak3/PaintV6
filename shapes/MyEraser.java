/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author Logan
 */
public class MyEraser extends MyShapes{
    private GraphicsContext graphicsContext;
    private double lineWidth;
    private Image startImage, endImage;
    
    public MyEraser() {
        
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    

    public Image getStartImage() {
        return startImage;
    }

    public Image getEndImage() {
        return endImage;
    }
    
    public void whenEraserStart(Image image) {
        startImage = image;
    }
    
    public void whenEraserEnd(Image image) {
        endImage = image;
    }
    
    public void drawUndo() {
        graphicsContext.drawImage(startImage, 0, 0);
    }
    
    public void drawRedo() {
        graphicsContext.drawImage(endImage, 0, 0);
    }
}
