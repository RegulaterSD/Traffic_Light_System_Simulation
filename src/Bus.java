import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Object class defining buses
 */
public class Bus {

    private static final double[][] PATHS = {
            {13,570,1185,570},{1185,552,13,552}
    };

    private List<double[]> startingPaths = new ArrayList<>();
    private List<double[]> pathArr = new ArrayList<>();
    private Path path;
    private double distance;
    private double seconds;
    private Boolean collided;
    private PathTransition pathTransition;
    private Shape busShape;

    /**
     * Constructor for bus
     * @param tempPane This is the Main Panel that the bus will be spawned on
     * @param collidableBus This is the list of Bus's
     */
    public Bus(Pane tempPane, List<Bus> collidableBus){
        initializeArrays();
        createPath();
        initializeBusShape();
        initializePathTransition(tempPane, collidableBus);
        this.collided = false;
    }

    /**
     * Creates bus shape
     * Each bus is given a set width and height here,
     * this function also references each path segment to determine
     * the orientation of each bus as it travels.
     */
    private void initializeBusShape() {
        busShape = new Rectangle(8, 50);
        //Set initial angle based on the first segment
        if (!pathArr.isEmpty()) {
            double[] firstSegment = pathArr.get(0);
            busShape.setRotate(calculateAngle(firstSegment[0], firstSegment[1], firstSegment[2], firstSegment[3]));
        }
    }

    /**
     * Runs buses on paths
     * This function utilizes tempPane and collidableBus, both obtained when the constructor
     * is first called for the bus
     * It then adds the path and carShape values to the proveded tempPane, and provided that both values exist
     * it will initialize the pathTransition with an event to remove the bus once it has completed the path once.
     * @param tempPane This is the main panel the item will be spawned upon
     * @param collidableBus This is the list of Buses on the panel
     */
    private void initializePathTransition(Pane tempPane, List<Bus> collidableBus) {
        tempPane.getChildren().addAll(path, busShape);
        if (path != null && busShape != null) {
            pathTransition = new PathTransition(Duration.millis(seconds*1000), path, busShape);
            pathTransition.setInterpolator(Interpolator.LINEAR);
            pathTransition.setCycleCount(1);

            pathTransition.setOnFinished(event -> {
                tempPane.getChildren().removeAll(path, busShape);
                collidableBus.remove(this);
            });
        }
    }

    /**
     * A protected method that runs the path transition for a selected bus
     */
    protected void startAnimation() {
        if (pathTransition != null) {
            pathTransition.play();
        }
    }

    /**
     * A protected method that pauses the path transition for a selected bus
     */
    protected void stopBus() {
        if (pathTransition != null) {
            pathTransition.pause();
        }
    }

    /**
     * A protected method that restarts the path transition for a selected bus
     */
    protected void restartBus() {
        if (pathTransition != null) {
            pathTransition.play();
        }
    }

    /**
     * initializeArrays adds each viable path from PATHS into startingPaths 
     */
    private void initializeArrays(){
        startingPaths.addAll(Arrays.asList(PATHS));
    }

    /**
     * Creates paths for buses to follow
     * This function starts by randomly selecting a vaible path in the startingPaths array
     * it then initializes a path and sets the correct starting location and MoveTo element 
     * from startingPaths 0 and 1.
     * Looping through each element in the selected startingPath we create distance vectors
     * to represent the distance and direction of each step in the bus' overall path.
     * The bus' speed is also set here as a ratio of the distance traveled / 100
     */
    protected void createPath(){
        Random random = new Random();
        int tempInt = random.nextInt(2);
        pathArr.add(startingPaths.get(tempInt));
        path = new Path();
        path.getElements().add(new MoveTo(pathArr.get(0)[0], pathArr.get(0)[1]));
        double startX = pathArr.get(0)[0];
        double startY = pathArr.get(0)[1];
        for (int i = 0; i < pathArr.size(); i++) {
            double[] point = pathArr.get(i);
            path.getElements().add(new LineTo(point[2], point[3]));
            double endX = point[2];
            double endY = point[3];
            distance += abs((endX - startX)) + abs((endY - startY));
            startX = point[2];
            startY = point[3];
        }
        //This is where you edit the Speed
        seconds = distance / 100;
        path.setOpacity(0);
    }

    /**
     * calculateAngle is a helper function designed to calculate the angle of a bus
     * based on which path segment it is currently on.
     * @param startX This is the Starting X position
     * @param startY This is the Starting Y position
     * @param endX This is the Ending X Position
     * @param endY This is the Ending Y Position
     * @return The angle at which the bus should be facing along this segment.
     */

    protected double calculateAngle(double startX, double startY, double endX, double endY) {
        double angle = Math.toDegrees(Math.atan2(endX - startX, endY - startY));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    /**
     * setCollided is a helper function used to set the collision value of the bus to the provided value.
     * @param bool The bool the collided should be set to
     */
    protected void setCollided(boolean bool){
        collided = bool;
    }

    /**
     * The following are temporary helper functions used while debugging.
     * They are simple return functions that provide a bus' path, seconds, pathArr, carShape, and collided
     * values.
     * @return Returns the path of the Bus
     */
    protected Path returnPath(){
        return path;
    }

    /***
     * This is to return the total amount of Seconds the bus will run
     * @return The total amount of seconds in double form
     */
    protected double returnSeconds(){
        return seconds;
    }

    /***
     * This is to return the Path Array that the bus will be taking.
     * @return A double[] List of the Bus's path.
     */
    protected List<double[]> returnPathArray(){
        return pathArr;
    }

    /***
     * This is to return the Bus Shape on the pane
     * @return The bus shape on the Pane and all information of it.
     */
    protected Shape returnBusShape() {
        return busShape;
    }

    /**
     * This is to return if the Bus has collided into anything
     * @return True/False if the bus has collided with anything
     */
    protected boolean returnCollided(){
        return collided;
    }

}
