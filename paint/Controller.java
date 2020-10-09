package paint;

import shapes.StraightLine;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import shapes.*;
import Dialogues.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;

import javafx.util.Duration;

public class Controller implements Initializable {

    //variables from FXML
    @FXML
    private ScrollPane sp1, sp2, sp3, sp4, sp5, sp6, sp7;

    private ResizableCanvas selectedCanvas, can1, can2, can3, can4, can5, can6, can7;
    @FXML
    private TextField brushSize;
    @FXML
    public ColorPicker colorPicker;
    @FXML
    private Slider slider;
    @FXML
    private MenuItem save, saveAs, quit, undo, resize, fDraw,
            lDraw, sDraw, rDraw, cDraw, eDraw, filled, transparent,
            about, tDraw, noTool, triDraw, redo, polygon, open, toolEraser;
    @FXML
    private TabPane tabPane;
    @FXML
    Tab tab1, tab2, tab3, tab4, tab5, tab6, tab7;
    @FXML
    private Menu fileFxml, help;
    @FXML
    private Label currentTool;

    public ColorPicker colorPickerFill; //to determine color of fill, not actually on program
    private String mode = "No Tool"; //to determine what tool is currently being used
    private File currentFile, file1, file2, file3, file4, file5, file6, file7; //current file being edited
    private GraphicsContext currentGraphicsContext, gc1, gc2, gc3, gc4, gc5, gc6, gc7;
    public boolean saved = false; //to check if a file has been saved
    private boolean hasBeenOpened = false; //to check if this is a file that has been opened / saved
    private boolean checkAutosave = true; //check if autosave is on or off
    private boolean enableAutosave = false; //autosave is turned on once this is enabled
    @FXML
    MenuButton menuButton = new MenuButton();
    //declaring all the modes
    private StraightLine straightLine;
    private MyRectangle myRectangle;
    private MySquare mySquare;
    private MyEllipse myEllipse;
    private MyCircle myCircle;
    private FreeDraw freeDraw;
    private MyTextBox textBox;
    private MyTriangle triangle;
    private MyPolygon myPolygon;
    private MyEraser myEraser;
    private MySelection mySelection;

    int currentTab = 1;

    //stacks for undo and redo
    private Stack<MyShapes> undoStack = new Stack<>();
    private Stack<MyShapes> redoStack = new Stack<>();

    private ArrayList<Tab> tabList = new ArrayList<Tab>();
    private ArrayList<File> fileList = new ArrayList<File>(7);
    private ArrayList<ResizableCanvas> canList = new ArrayList<ResizableCanvas>();

    private SnapshotParameters backgroundSnap = new SnapshotParameters(); //to set background of snapshot to default
    private Image onDragScreenshot; //screenshot of canvas for seeing shapes as they are drawn

    private final static Logger logger = Logger.getLogger(Controller.class.getName());

    public Controller() {
    }

    public String getMode() {
        return mode;

    }

    /**
     * Saves the image to the computer, popping up Save As Dialogue
     */
    //method for Save As button
    public void onSaveAs() {
        SaveAsDialogue saveAs = new SaveAsDialogue();
        if (hasBeenOpened) {
            currentFile = saveAs.hasBeenOpened(currentFile, selectedCanvas);
            fileList.add(currentTab, currentFile);
            saved = saveAs.getSaved();
        } else {
            currentFile = saveAs.hasNotBeenOpened(currentFile, selectedCanvas);
            fileList.add(currentTab, currentFile);
            saved = saveAs.getSaved();
            hasBeenOpened = saveAs.getHasBeenOpened();
            enableAutosave = saveAs.getEnableAutosave();
        }
    }

    /**
     * Saves the image if it has been saved before or has been opened from file
     * browser
     */
    //method for Save button
    public void onSave() {
        SaveDialogue saveDialogue = new SaveDialogue();
        //if has not been saved before or opened from file, act like save as button
        if (!saved) {
            currentFile = saveDialogue.hasNotBeenSaved(currentFile, selectedCanvas);
            fileList.add(currentTab, currentFile);
            saved = saveDialogue.getSaved();
            hasBeenOpened = saveDialogue.getHasBeenOpened();
            enableAutosave = saveDialogue.getEnableAutosave();
            selectedCanvas.setCanvasSaved(saveDialogue.getSetCanvasSaved());
        } else {
            currentFile = saveDialogue.hasBeenSaved(currentFile, selectedCanvas);
            saved = saveDialogue.getSaved();
            hasBeenOpened = saveDialogue.getHasBeenOpened();
            selectedCanvas.setCanvasSaved(saveDialogue.getSetCanvasSaved());
        }
    }

    /**
     * Used to exit the program. Checks to see if the canvas has been
     * edited/saved. If it has, closes program, otherwise pops up save dialog
     */
    //method for Exit button
    public void onExit() {
        //if this a fresh project and the canvas has been edited
        ExitDialogue exitDialogue = new ExitDialogue();
        if (!selectedCanvas.getCanvasSaved() && !saved) {
            currentFile = exitDialogue.isFreshProject(currentFile, selectedCanvas);
            selectedCanvas.setCanvasSaved(exitDialogue.getSetCanvasSaved());

            //if canvas has been edited but the work was already saved at lease
            //once or has been opened from a file, do the following
        } else if (!selectedCanvas.getCanvasSaved()) {
            currentFile = exitDialogue.isEditedOrOpened(currentFile, selectedCanvas);
            selectedCanvas.setCanvasSaved(exitDialogue.getSetCanvasSaved());
            saved = exitDialogue.getSaved();
            //if the canvas not been edited, or the work was already saved and the
            //canvas has not been edited since last save, exit the program
        } else {
            Platform.exit();
            System.exit(0);
        }
    }

    /**
     * Used to open a file into the canvas using an Open Dialogue
     */
    //method for Open button
    public void onOpen() {

        OpenDialogue openDialogue = new OpenDialogue();
        currentFile = openDialogue.openFile(currentFile, selectedCanvas, currentGraphicsContext);
        fileList.add(currentTab, currentFile);
        saved = openDialogue.getSaved();
        hasBeenOpened = openDialogue.getHasBeenOpened();
        selectedCanvas.setCanvasSaved(openDialogue.getCanvasSaved());
        enableAutosave = openDialogue.getEnableAutosave();
    }

    /**
     * Upon clicking about button, opens up an alert about the program
     */
    //method for About button
    public void onAbout() {
        AboutDialogue aboutDialogue = new AboutDialogue();
        aboutDialogue.openAbout();
    }

    public void logInformation() {
        if (currentFile != null) {
            logger.log(Level.INFO, "\n" + "Current tool: " + mode + "\n"
                    + "File: " + currentFile.getName() + "\n"
                    + "Saved: " + selectedCanvas.getCanvasSaved());
        } else {
            logger.log(Level.INFO, "\n" + "Current tool: " + mode + "\n"
                    + "Saved: " + selectedCanvas.getCanvasSaved());
        }
        //System.out.println("This is a test");
    }

    public void doAutosave() {
        if (checkAutosave) {
            onSave();
            logger.log(Level.INFO, "\n" + "Saved");
        } else {
            logger.log(Level.INFO, "\n" + "Autosave is off");
        }
    }

    /**
     * Initializes the canvas and graphics context, also has mouse events
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the
     * root object was not localized.
     */
    //to initialize canvas upon program start up and what to do when mouse is used in canvas
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabCanDisaster();
        selectedCanvas = can1;

        //initializing canvas so it can be written on
        currentGraphicsContext = gc1;


        //adjusting look of colorpicker and default color
        colorPicker.getStyleClass().add("button");
        colorPicker.setValue(javafx.scene.paint.Color.BLACK);

        //setting default of mode and colorPickerFill so they do not throw Null
        mode = "No Tool";
        currentTool.setText(mode);
        colorPickerFill = colorPicker;
        backgroundSnap.setFill(Color.TRANSPARENT);

        setKeyCombinations();

        //setting start for slider and listener to adjust zoom
        slider.setValue(1);
        slider.valueProperty().addListener((observable, oldvalue, newvalue) -> {
            selectedCanvas.setScaleX(newvalue.doubleValue());
            selectedCanvas.setScaleY(newvalue.doubleValue());
        });

        //setting level of log
        logger.setLevel(Level.INFO);

        //thread for getting current tool, file, and whether the file has been saved, every 60 seconds
        Timeline logUpdate = new Timeline(new KeyFrame(
                Duration.millis(60000),
                ae -> logInformation()));

        //setting the cycle as indefinite and playing it
        logUpdate.setCycleCount(Animation.INDEFINITE);
        logUpdate.play();

        //thread for autosave every 10 seconds
        Timeline autosave = new Timeline(new KeyFrame(
                Duration.millis(10000),
                ae -> doAutosave()));

        //what to do when mouse is clicked on canvas
        selectedCanvas.setOnMousePressed(e -> {
            //setting cycle as indefinite and playing it
            if (enableAutosave) {
                autosave.setCycleCount(Animation.INDEFINITE);
                autosave.play();
            }
            //when the mode is line
            if (mode.equals("Line")) {
                currentGraphicsContext.setStroke(colorPicker.getValue()); //set color of line
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));
                straightLine = new StraightLine(); //instantiating object

                //giving the line object data about color, graphicsContext, and start point
                straightLine.setGraphicsContext(currentGraphicsContext);
                straightLine.setColor(colorPicker);
                straightLine.setLineWidth(Double.parseDouble(brushSize.getText()));
                onDragScreenshot = selectedCanvas.snapshot(backgroundSnap, null);
                straightLine.setStartPoint(e.getX(), e.getY());
                selectedCanvas.setCanvasSaved(false);
                //when the mode is free draw
            } else if (mode.equals("Free Draw")) {
                currentGraphicsContext.setStroke(colorPicker.getValue());
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));
                currentGraphicsContext.beginPath();
                currentGraphicsContext.lineTo(e.getX(), e.getY());

                freeDraw = new FreeDraw();

                freeDraw.setGraphicsContext(currentGraphicsContext);
                freeDraw.setColor(colorPicker);
                freeDraw.setLineWidth(Double.parseDouble(brushSize.getText()));
                freeDraw.setStartPoint(e.getX(), e.getY());
                selectedCanvas.setCanvasSaved(false);

                //when mode is rectangle
            } else if (mode.equals("Rectangle")) {
                currentGraphicsContext.setStroke(colorPicker.getValue()); //set color of rectangle
                currentGraphicsContext.setFill(colorPicker.getValue()); //set fill of rectangle
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));

                myRectangle = new MyRectangle(); //instantiating object

                //give the rectangle object data about color, graphicsContext, and start point
                myRectangle.setGraphicsContext(currentGraphicsContext);
                myRectangle.setColor(colorPicker);
                myRectangle.setFill(colorPickerFill);
                myRectangle.setLineWidth(Double.parseDouble(brushSize.getText()));
                onDragScreenshot = selectedCanvas.snapshot(backgroundSnap, null);
                myRectangle.setStartPoint(e.getX(), e.getY());
                selectedCanvas.setCanvasSaved(false);

                //when mode is square
            } else if (mode.equals("Square")) {
                currentGraphicsContext.setStroke(colorPicker.getValue()); //set color of square
                currentGraphicsContext.setFill(colorPickerFill.getValue()); //set fill of square
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));

                mySquare = new MySquare(); //instantiating object

                //give the square object data about color, graphicsContext, and start point
                mySquare.setGraphicsContext(currentGraphicsContext);
                mySquare.setColor(colorPicker);
                mySquare.setFill(colorPickerFill);
                mySquare.setLineWidth(Double.parseDouble(brushSize.getText()));
                onDragScreenshot = selectedCanvas.snapshot(backgroundSnap, null);
                mySquare.setStartPoint(e.getX(), e.getY());
                selectedCanvas.setCanvasSaved(false);

                //when mode is ellipse
            } else if (mode.equals("Ellipse")) {
                currentGraphicsContext.setStroke(colorPicker.getValue()); //set color of ellipse
                currentGraphicsContext.setFill(colorPickerFill.getValue()); //set fill of ellipse
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));

                myEllipse = new MyEllipse(); //instantiating object

                //give the ellipse object data about color, graphicsContext, and start point
                myEllipse.setGraphicsContext(currentGraphicsContext);
                myEllipse.setColor(colorPicker);
                myEllipse.setFill(colorPickerFill);
                myEllipse.setLineWidth(Double.parseDouble(brushSize.getText()));
                onDragScreenshot = selectedCanvas.snapshot(backgroundSnap, null);
                myEllipse.setCenterPoint(e.getX(), e.getY());
                selectedCanvas.setCanvasSaved(false);

                //when mode is circle
            } else if (mode.equals("Circle")) {
                currentGraphicsContext.setStroke(colorPicker.getValue()); //set color of circle
                currentGraphicsContext.setFill(colorPickerFill.getValue()); //set fill of circle
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));

                myCircle = new MyCircle(); //instantiating object

                //give the circle object data about color, graphicsContext, and start point
                myCircle.setGraphicsContext(currentGraphicsContext);
                myCircle.setColor(colorPicker);
                myCircle.setFill(colorPickerFill);
                myCircle.setLineWidth(Double.parseDouble(brushSize.getText()));
                onDragScreenshot = selectedCanvas.snapshot(backgroundSnap, null);
                myCircle.setCenterPoint(e.getX(), e.getY());
                selectedCanvas.setCanvasSaved(false);

                //when mode is text box
            } else if (mode.equals("Text Box")) {
                textBox = new MyTextBox(); //instantiating object

                //give the text box object data about graphicsContext, positions of click
                textBox.setGraphicsContext(currentGraphicsContext);
                textBox.setPositionX(e.getX());
                textBox.setPositionY(e.getY());

                //draw text box, set save of canvas, and put it in stack
                textBox.draw();
                selectedCanvas.setCanvasSaved(false);
                undoStack.push(textBox);
            } else if (mode.equals("Eraser")) {
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));

                myEraser = new MyEraser();
                myEraser.setGraphicsContext(currentGraphicsContext);
                Image image = selectedCanvas.snapshot(backgroundSnap, null);
                myEraser.whenEraserStart(image);
                myEraser.setLineWidth(Double.parseDouble(brushSize.getText()));
                currentGraphicsContext.clearRect(e.getX(), e.getY(), Double.parseDouble(brushSize.getText()), Double.parseDouble(brushSize.getText()));
            } else if (mode.equals("Triangle")) {
                currentGraphicsContext.setStroke(colorPicker.getValue());
                currentGraphicsContext.setFill(colorPickerFill.getValue());
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));

                triangle = new MyTriangle();

                triangle.setGraphicsContext(currentGraphicsContext);
                triangle.setColorPicker(colorPicker);
                triangle.setColorPickerFill(colorPickerFill);
                triangle.setLineWidth(Double.parseDouble(brushSize.getText()));
                onDragScreenshot = selectedCanvas.snapshot(backgroundSnap, null);
                triangle.setStartX(e.getX());
                triangle.setStartY(e.getY());

                selectedCanvas.setCanvasSaved(false);
            } else if (mode.equals("Selection")) {
                mySelection = new MySelection();
                mySelection.setGraphicsContext(currentGraphicsContext);
                mySelection.setCanvas(selectedCanvas);
                onDragScreenshot = selectedCanvas.snapshot(backgroundSnap, null);
                mySelection.setStartImage(onDragScreenshot);
                mySelection.setStartPoint(e.getX(), e.getY());
                selectedCanvas.setCanvasSaved(false);

            } else if (mode.equals("Paste")) {
                mySelection.setPastePoints(e.getX(), e.getY());
                mySelection.paste();
                Image image = selectedCanvas.snapshot(backgroundSnap, null);
                mySelection.setEndImage(image);
                undoStack.push(mySelection);
                mode = "No Tool";
                currentTool.setText(mode);

            } else if (mode.equals("Polygon")) {
                currentGraphicsContext.setStroke(colorPicker.getValue());
                currentGraphicsContext.setFill(colorPicker.getValue());
                currentGraphicsContext.setLineWidth(Double.parseDouble(brushSize.getText()));

                myPolygon = new MyPolygon();

                myPolygon.setGraphicsContext(currentGraphicsContext);
                myPolygon.setColorPicker(colorPicker);
                myPolygon.setColorPickerFill(colorPickerFill);
                myPolygon.setLineWidth(Double.parseDouble(brushSize.getText()));
                myPolygon.addPoint(e.getX(), e.getY());

                selectedCanvas.setCanvasSaved(false);
            }
        });

        //what to do when mouse is pressed and dragged
        selectedCanvas.setOnMouseDragged(e -> {

            //when mode is free draw
            if (mode.equals("Free Draw")) {
                currentGraphicsContext.lineTo(e.getX(), e.getY());
                currentGraphicsContext.stroke();

                freeDraw.addPoint(e.getX(), e.getY());
                selectedCanvas.setCanvasSaved(false);

                //clear a rectangle at the given coordinate, size = brush size
            } else if (mode.equals("Eraser")) {
                currentGraphicsContext.clearRect(e.getX(), e.getY(), Double.parseDouble(brushSize.getText()), Double.parseDouble(brushSize.getText()));

                //rest of onMouseDragged is same as onMouseReleased, so see comments on that below
            } else if (mode.equals("Triangle")) {
                triangle.setEndX(e.getX());
                triangle.setEndY(e.getY());

                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight()); //clear canvas, then redraw it each time
                currentGraphicsContext.drawImage(onDragScreenshot, 0, 0);
                triangle.draw(selectedCanvas.getHeight(), selectedCanvas.getWidth());

            } else if (mode.equals("Line")) {
                straightLine.setEndPoint(e.getX(), e.getY());

                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                currentGraphicsContext.drawImage(onDragScreenshot, 0, 0);
                straightLine.draw();

            } else if (mode.equals("Rectangle")) {
                myRectangle.setEndPoint(e.getX(), e.getY());

                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                currentGraphicsContext.drawImage(onDragScreenshot, 0, 0);
                myRectangle.setWidth();
                myRectangle.setHeight();
                myRectangle.check();

                myRectangle.draw();

            } else if (mode.equals("Square")) {
                mySquare.setEndPoint(e.getX(), e.getY());

                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                currentGraphicsContext.drawImage(onDragScreenshot, 0, 0);
                mySquare.setWidth();
                mySquare.setHeight();
                mySquare.check();

                mySquare.draw();

            } else if (mode.equals("Ellipse")) {
                myEllipse.setEndPoint(e.getX(), e.getY());

                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                currentGraphicsContext.drawImage(onDragScreenshot, 0, 0);
                myEllipse.setRadius();
                myEllipse.check();

                myEllipse.draw();
            } else if (mode.equals("Circle")) {
                myCircle.setEndPoint(e.getX(), e.getY());

                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                currentGraphicsContext.drawImage(onDragScreenshot, 0, 0);
                myCircle.setRadius();
                myCircle.check();

                myCircle.draw();
            } else if (mode.equals("Polygon")) {
                myPolygon.addPoint(e.getX(), e.getY());
            } else if (mode.equals("Selection")) {
                /*mySelection.setEndPoint(e.getX(), e.getY());
                graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                graphicsContext.drawImage(onDragScreenshot, 0, 0);
                mySelection.setWidth();
                mySelection.setHeight();
                mySelection.check();
                mySelection.clearSection();*/

            }

        });

        //what to do when mouse is pressed and then released
        selectedCanvas.setOnMouseReleased(e -> {
            //when mode is free draw
            if (mode.equals("Free Draw")) {
                currentGraphicsContext.lineTo(e.getX(), e.getY());
                currentGraphicsContext.stroke();

                freeDraw.setEndPoint(e.getX(), e.getY());
                undoStack.push(freeDraw);
                selectedCanvas.setCanvasSaved(false);

                //when mode is line
            } else if (mode.equals("Line")) {
                straightLine.setEndPoint(e.getX(), e.getY()); //set where line ends
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                redrawCanvas();
                straightLine.draw(); //put line on canvas
                undoStack.push(straightLine);
                selectedCanvas.setCanvasSaved(false);

                //when mode is rectangle
            } else if (mode.equals("Rectangle")) {
                myRectangle.setEndPoint(e.getX(), e.getY()); //set where mouse ends
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                redrawCanvas();
                //set width and height
                myRectangle.setWidth();
                myRectangle.setHeight();
                myRectangle.check();

                myRectangle.draw(); //put rectangle on canvas
                undoStack.push(myRectangle);
                selectedCanvas.setCanvasSaved(false);

                //when mode is square
            } else if (mode.equals("Square")) {
                mySquare.setEndPoint(e.getX(), e.getY()); //set where mouse ends
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                redrawCanvas();
                //set width and height
                mySquare.setWidth();
                mySquare.setHeight();
                mySquare.check();

                mySquare.draw(); //put square on canvas
                undoStack.push(mySquare);
                selectedCanvas.setCanvasSaved(false);

                //when mode is ellipse
            } else if (mode.equals("Ellipse")) {
                myEllipse.setEndPoint(e.getX(), e.getY()); //set where mouse ends
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                redrawCanvas();
                //set radius for ellipse
                myEllipse.setRadius();
                myEllipse.check();

                myEllipse.draw(); //put ellipse on canvas
                undoStack.push(myEllipse);
                selectedCanvas.setCanvasSaved(false);

                //when mode is circle
            } else if (mode.equals("Circle")) {
                myCircle.setEndPoint(e.getX(), e.getY()); //set where mouse ends
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                redrawCanvas();
                //set radius for circle
                myCircle.setRadius();
                myCircle.check();

                myCircle.draw(); //put on canvas
                undoStack.push(myCircle);
                selectedCanvas.setCanvasSaved(false);
            } else if (mode.equals("Triangle")) {
                triangle.setEndX(e.getX());
                triangle.setEndY(e.getY());
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                redrawCanvas();
                triangle.draw(selectedCanvas.getWidth(), selectedCanvas.getHeight());
                undoStack.push(triangle);
                selectedCanvas.setCanvasSaved(false);
            } else if (mode.equals("Polygon")) {
                myPolygon.addPoint(e.getX(), e.getY());
                myPolygon.draw();

                undoStack.push(myPolygon);
                selectedCanvas.setCanvasSaved(false);
            } else if (mode.equals("Eraser")) {
                Image image = selectedCanvas.snapshot(backgroundSnap, null);
                myEraser.whenEraserEnd(image);
                undoStack.push(myEraser);
                selectedCanvas.setCanvasSaved(false);
            } else if (mode.equals("Selection")) {
                mySelection.setEndPoint(e.getX(), e.getY());
                mySelection.setWidth();
                mySelection.setHeight();
                mySelection.check();
                mySelection.clearSection();

                mode = "Paste";
                currentTool.setText(mode);

            } else if (mode.equals("Paste")) {

            }

        });

    }

    //following 9 methods are for changing the mode when selected tools
    /**
     * Sets the current mode to Rectangle
     */
    public void onRectangle() {
        mode = "Rectangle";
        currentTool.setText(mode);
    }

    /**
     * Sets the current mode to Free Draw
     */
    public void onFreeDraw() {
        mode = "Free Draw";
        currentTool.setText(mode);
    }

    /**
     * Sets the current mode to Square
     */
    public void onSquare() {
        mode = "Square";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to Ellipse
     */
    public void onEllipse() {
        mode = "Ellipse";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to Circle
     */
    public void onCircle() {
        mode = "Circle";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to Line
     */
    public void onLine() {
        mode = "Line";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to No Tool
     */
    public void onNoTool() {
        mode = "No Tool";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to Text Box
     */
    public void onTextBox() {
        mode = "Text Box";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to Eraser
     */
    public void onEraser() {
        mode = "Eraser";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to Triangle
     */
    public void onTriangle() {
        mode = "Triangle";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to Select Tool
     */
    public void onSelection() {
        mode = "Selection";
        currentTool.setText(mode);
    }

    /**
     * Sets the mode to Polygon
     */
    public void onPolygon() {
        mode = "Polygon";
        currentTool.setText(mode);
    }

    /**
     * Sets the fill to the currently chosen color
     */
    //when filled button is selected under fill
    public void onFill() {
        colorPickerFill = colorPicker; //set fill to whatever current color is
    }

    public void onNew() {
        currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
        onDragScreenshot = null;
        undoStack.clear();
        redoStack.clear();
    }

    public void onTab1Close() {
        tabList.remove(tab1);
    }

    public void onTab2Close() {
        tabList.remove(tab2);
    }

    public void onTab3Close() {
        tabList.remove(tab3);
    }

    public void onTab4Close() {
        tabList.remove(tab4);
    }

    public void onTab5Close() {
        tabList.remove(tab5);
    }

    public void onTab6Close() {
        tabList.remove(tab6);
    }

    public void onTab7Close() {
        tabList.remove(tab7);
    }

    public void onTab1() {
        
        selectedCanvas = can1;
        currentGraphicsContext = gc1;
    }

    public void onTab2() {
        
        selectedCanvas = can2;
        currentGraphicsContext = gc2;
    }

    public void onTab3() {
        
        selectedCanvas = can3;
        currentGraphicsContext = gc3;
    }

    public void onTab4() {
        
        selectedCanvas = can4;
        currentGraphicsContext = gc4;
    }

    public void onTab5() {
       
        selectedCanvas = can5;
        currentGraphicsContext = gc5;
    }

    public void onTab6() {
        
        selectedCanvas = can6;
        currentGraphicsContext = gc6;
    }

    public void onTab7() {
        
        selectedCanvas = can7;
        currentGraphicsContext = gc7;
    }

    /**
     * Sets the fill to transparent, making the shape unfilled
     */
    //when transparent button is selected under fill
    public void onTransparent() {
        colorPickerFill = new ColorPicker(Color.TRANSPARENT); //set fill to transparent
    }

    public void onAutosaveOff() {
        checkAutosave = false;
    }

    public void onAutosaveOn() {
        checkAutosave = true;
    }

    /**
     * Iterates through undo stack, putting in temporary stack minus the first
     * element Reverses temporary stack, goes through it and puts on canvas
     */
    //what to do when clicking undo
    public void onUndo() {

        if (!undoStack.empty()) {
            //remove first element
            MyShapes pulledShape = undoStack.pop();
            redoStack.push(pulledShape);
            currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
            if (pulledShape.getClass() == MyEraser.class) {
                MyEraser tempMyEraser = (MyEraser) pulledShape;
                tempMyEraser.drawUndo();
            } else if (pulledShape.getClass() == MySelection.class) {
                MySelection tempMySelection = (MySelection) pulledShape;
                tempMySelection.onUndo();
            } else {
                //to hold what is in undoStack so that we do not remove anything else from it
                //this is explained a bit more below
                Stack<MyShapes> tempStack = new Stack<>();

                //used to loop through undoStack
                Iterator iterator = undoStack.iterator();

                //putting what is in undoStack into tempStack starting at 1st element
                while (iterator.hasNext()) {
                    tempStack.push((MyShapes) iterator.next());
                }

                //reverses stack to put in order of when shapes were put on canvas
                Collections.reverse(tempStack);
                while (!tempStack.isEmpty()) {
                    //grabs the first element from the temporary stack and removes it for next loop
                    //used so that we do not remove anything from undoStack and have to put it back in
                    MyShapes tempShape = tempStack.pop();

                    if (tempShape.getClass() == MyEraser.class) {
                        MyEraser tempMyEraser = (MyEraser) tempShape;
                        tempMyEraser.drawRedo();
                        redrawCanvas();
                        tempStack.clear();
                    } else if (tempShape.getClass() == MySelection.class) {
                        MySelection tempMySelection = (MySelection) tempShape;
                        tempMySelection.onUndo();
                        redrawCanvas();
                        tempStack.clear();
                    } else {
                        //use the current shape and draw it back on the canvas
                        if (tempShape.getClass() == FreeDraw.class) {
                            FreeDraw tempFreeDraw = (FreeDraw) tempShape;
                            tempFreeDraw.draw();
                        } else if (tempShape.getClass() == StraightLine.class) {
                            StraightLine tempStraightLine = (StraightLine) tempShape;
                            tempStraightLine.draw();
                        } else if (tempShape.getClass() == MyRectangle.class) {
                            MyRectangle tempRectangle = (MyRectangle) tempShape;
                            tempRectangle.draw();
                        } else if (tempShape.getClass() == MyEllipse.class) {
                            MyEllipse tempEllipse = (MyEllipse) tempShape;
                            tempEllipse.draw();
                        } else if (tempShape.getClass() == MySquare.class) {
                            MySquare tempSquare = (MySquare) tempShape;
                            tempSquare.draw();
                        } else if (tempShape.getClass() == MyCircle.class) {
                            MyCircle tempCircle = (MyCircle) tempShape;
                            tempCircle.draw();
                        } else if (tempShape.getClass() == MyTextBox.class) {
                            MyTextBox tempTextBox = (MyTextBox) tempShape;
                            tempTextBox.draw();
                        } else if (tempShape.getClass() == MyTriangle.class) {
                            MyTriangle tempMyTriangle = (MyTriangle) tempShape;
                            tempMyTriangle.draw(selectedCanvas.getWidth(), selectedCanvas.getHeight());
                        } else if (tempShape.getClass() == MyPolygon.class) {
                            MyPolygon tempMyPolygon = (MyPolygon) tempShape;
                            tempMyPolygon.draw();
                        }
                    }
                }
            }
        }
    }

    /**
     * Iterates through redo stack, putting it into a temporary stack. Goes
     * through temporary stack and places it on canvas
     */
    public void onRedo() {

        if (!redoStack.empty()) {
            //graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            MyShapes removedShape = redoStack.pop();
            undoStack.push(removedShape);
            if (removedShape.getClass() == MyEraser.class) {
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                MyEraser tempMyEraser = (MyEraser) removedShape;
                tempMyEraser.drawRedo();
            } else if (removedShape.getClass() == MySelection.class) {
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                MySelection tempMySelection = (MySelection) removedShape;
                tempMySelection.onRedo();
            } else {
                Iterator iterator = undoStack.iterator();
                Stack<MyShapes> tempStack = new Stack<>();

                while (iterator.hasNext()) {
                    tempStack.push((MyShapes) iterator.next());
                }

                Collections.reverse(tempStack);
                int x = 1;
                while (!tempStack.isEmpty() && x == 1) {
                    //grabs the first element from the temporary stack and removes it for next loop
                    //used so that we do not remove anything from undoStack and have to put it back in
                    MyShapes tempShape = tempStack.pop();
                    if (tempShape.getClass() == MyEraser.class) {
                        currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                        MyEraser tempMyEraser = (MyEraser) tempShape;
                        tempMyEraser.drawRedo();

                    } else if (tempShape.getClass() == MySelection.class) {
                        currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                        MySelection tempMySelection = (MySelection) tempShape;
                        tempMySelection.onRedo();

                    } else {
                        //use the current shape and draw it back on the canvas
                        if (tempShape.getClass() == FreeDraw.class) {
                            FreeDraw tempFreeDraw = (FreeDraw) tempShape;
                            tempFreeDraw.draw();
                        } else if (tempShape.getClass() == StraightLine.class) {
                            StraightLine tempStraightLine = (StraightLine) tempShape;
                            tempStraightLine.draw();
                        } else if (tempShape.getClass() == MyRectangle.class) {
                            MyRectangle tempRectangle = (MyRectangle) tempShape;
                            tempRectangle.draw();
                        } else if (tempShape.getClass() == MyEllipse.class) {
                            MyEllipse tempEllipse = (MyEllipse) tempShape;
                            tempEllipse.draw();
                        } else if (tempShape.getClass() == MySquare.class) {
                            MySquare tempSquare = (MySquare) tempShape;
                            tempSquare.draw();
                        } else if (tempShape.getClass() == MyCircle.class) {
                            MyCircle tempCircle = (MyCircle) tempShape;
                            tempCircle.draw();
                        } else if (tempShape.getClass() == MyTextBox.class) {
                            MyTextBox tempTextBox = (MyTextBox) tempShape;
                            tempTextBox.draw();
                        } else if (tempShape.getClass() == MyTriangle.class) {
                            MyTriangle tempMyTriangle = (MyTriangle) tempShape;
                            tempMyTriangle.draw(selectedCanvas.getWidth(), selectedCanvas.getHeight());
                        }
                    }
                }
            }
        }
    }

    /**
     * Redraws the canvas as a line is being dragged
     */
    //same as undo, except it does not remove the first element from undoStack
    public void redrawCanvas() {
        Iterator iterator = undoStack.iterator();
        Stack<MyShapes> tempStack = new Stack<>();

        while (iterator.hasNext()) {
            tempStack.push((MyShapes) iterator.next());
        }

        Collections.reverse(tempStack);

        while (!tempStack.isEmpty()) {
            //grabs the first element from the temporary stack and removes it for next loop
            //used so that we do not remove anything from undoStack and have to put it back in
            MyShapes tempShape = tempStack.pop();
            if (tempShape.getClass() == MyEraser.class) {
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                MyEraser tempMyEraser = (MyEraser) tempShape;
                tempMyEraser.drawRedo();

            } else if (tempShape.getClass() == MySelection.class) {
                currentGraphicsContext.clearRect(0, 0, selectedCanvas.getWidth(), selectedCanvas.getHeight());
                MySelection tempMySelection = (MySelection) tempShape;
                tempMySelection.onRedo();

            } else {
                //use the current shape and draw it back on the canvas
                if (tempShape.getClass() == FreeDraw.class) {
                    FreeDraw tempFreeDraw = (FreeDraw) tempShape;
                    tempFreeDraw.draw();
                } else if (tempShape.getClass() == StraightLine.class) {
                    StraightLine tempStraightLine = (StraightLine) tempShape;
                    tempStraightLine.draw();
                } else if (tempShape.getClass() == MyRectangle.class) {
                    MyRectangle tempRectangle = (MyRectangle) tempShape;
                    tempRectangle.draw();
                } else if (tempShape.getClass() == MyEllipse.class) {
                    MyEllipse tempEllipse = (MyEllipse) tempShape;
                    tempEllipse.draw();
                } else if (tempShape.getClass() == MySquare.class) {
                    MySquare tempSquare = (MySquare) tempShape;
                    tempSquare.draw();
                } else if (tempShape.getClass() == MyCircle.class) {
                    MyCircle tempCircle = (MyCircle) tempShape;
                    tempCircle.draw();
                } else if (tempShape.getClass() == MyTextBox.class) {
                    MyTextBox tempTextBox = (MyTextBox) tempShape;
                    tempTextBox.draw();
                } else if (tempShape.getClass() == MyTriangle.class) {
                    MyTriangle tempMyTriangle = (MyTriangle) tempShape;
                    tempMyTriangle.draw(selectedCanvas.getWidth(), selectedCanvas.getHeight());
                }
            }
        }

    }

    /**
     * Provides a popup for resizing the canvas
     */
    //pop up to resize the canvas
    public void onResize() {
        ResizeDialogue resizeDialogue = new ResizeDialogue();
        selectedCanvas = resizeDialogue.doResize(selectedCanvas);
    }

    /**
     * set of key combinations for all buttons in Controller
     */
    //setting key combinations for all the buttons
    public void setKeyCombinations() {
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
        fileFxml.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.ALT_DOWN));
        undo.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN));
        quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        resize.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        fDraw.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.ALT_DOWN));
        lDraw.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.ALT_DOWN));
        sDraw.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN));
        rDraw.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN));
        cDraw.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN));
        eDraw.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.ALT_DOWN));
        filled.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
        transparent.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        about.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN));
        help.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.ALT_DOWN));
        tDraw.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.ALT_DOWN));
        noTool.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.ALT_DOWN));
        toolEraser.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.ALT_DOWN));
        triDraw.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.ALT_DOWN));
        redo.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        polygon.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.ALT_DOWN));
        colorPicker.setTooltip(new Tooltip("Used to select the color of the drawing. Select this button to open up more options."));
        menuButton.setTooltip(new Tooltip("This is a list of all the tools you can use."));
    }

    public void tabCanDisaster() { //this is a big yikes. tabs are a mess right now. needs more work
        fileList.add(file1);
        fileList.add(file2);
        fileList.add(file3);
        fileList.add(file4);
        fileList.add(file5);
        fileList.add(file6);
        fileList.add(file7);
        tabList.add(tab1);
        tabList.add(tab2);
        tabList.add(tab3);
        tabList.add(tab4);
        tabList.add(tab5);
        tabList.add(tab6);
        tabList.add(tab7);
        canList.add(can1);
        canList.add(can2);
        canList.add(can3);
        canList.add(can4);
        canList.add(can5);
        canList.add(can6);
        canList.add(can7);
        can1 = new ResizableCanvas();
        can2 = new ResizableCanvas();
        can3 = new ResizableCanvas();
        can4 = new ResizableCanvas();
        can5 = new ResizableCanvas();
        can6 = new ResizableCanvas();
        can7 = new ResizableCanvas();
        can1.setWidth(1750);
        can1.setHeight(1000);
        can1.setLayoutX(0);
        can2.setWidth(1750);
        can2.setHeight(1000);
        can2.setLayoutX(0);
        can3.setWidth(1750);
        can3.setHeight(1000);
        can3.setLayoutX(0);
        can4.setWidth(1750);
        can4.setHeight(1000);
        can4.setLayoutX(0);
        can5.setWidth(1750);
        can5.setHeight(1000);
        can5.setLayoutX(0);
        can6.setWidth(1750);
        can6.setHeight(1000);
        can6.setLayoutX(0);
        can7.setWidth(1750);
        can7.setHeight(1000);
        can7.setLayoutX(0);

        gc1 = can1.getGraphicsContext2D();
        gc2 = can2.getGraphicsContext2D();
        gc3 = can3.getGraphicsContext2D();
        gc4 = can4.getGraphicsContext2D();
        gc5 = can5.getGraphicsContext2D();
        gc6 = can6.getGraphicsContext2D();
        gc7 = can7.getGraphicsContext2D();
        
        sp1.setContent(can1);
        sp2.setContent(can2);
        sp3.setContent(can3);
        sp4.setContent(can4);
        sp5.setContent(can5);
        sp6.setContent(can6);
        sp7.setContent(can7);
    }
}
