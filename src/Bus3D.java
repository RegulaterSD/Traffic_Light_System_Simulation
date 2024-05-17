import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import static java.lang.Math.abs;

/**
 * 3D version of 2D bus.java object
 */
public class Bus3D {

    private static final double[][] PATHS = {
            {13,570,1185,570},{1185,552,13,552}
    };

    private List<double[]> startingPaths = new ArrayList<>();
    private List<double[]> temp = new ArrayList<>();
    private Path path;
    private double distance;
    private double seconds;
    private Boolean collided;
    private PathTransition pathTransition;
    private Shape busShape;
    private Group buses = new Group();
    private ImageHelper imageHelper = new ImageHelper();

    /**
     * Constructor
     * @param tempPane pane for placing buses
     * @param collidableBus collidable for buses
     */
    public Bus3D(Pane tempPane, List<Bus3D> collidableBus){
        initializeArrays();
        createPath();
        bus();
        initializePathTransition(tempPane, collidableBus);
        this.collided = false;
    }

    /**
     * Creates 3D model for bus object based on the Schoolbus object found in
     * "/vehicleModles/School Bus.obj"
     * In addition to the default model used, this function creates interactable
     * headlights and tailights, using setTranslate to position the lights on the model
     * and using setMaterial to set the texture.
     * Finally, this function uses the firstSegment values to determine the Bus' initial orientation
     * 
     * @return A group of the Bus and its 3D Model
     */
    private Group bus(){
        ObjModelImporter importes = new ObjModelImporter();
        try {
            importes.read(this.getClass().getResource("/vehicleModels/School Bus.obj"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        MeshView[] meshViewss = importes.getImport();
        Sphere headLightL = new Sphere(.15);
        Sphere headLightR = new Sphere(.15);
        Sphere tailLightL = new Sphere(.15);
        Sphere tailLightR = new Sphere(.15);

        PhongMaterial headLightMat = new PhongMaterial();
        headLightMat.setSelfIlluminationMap(imageHelper.getImage("./images/white.png"));

        PhongMaterial tailLightMat = new PhongMaterial();
        tailLightMat.setSelfIlluminationMap(imageHelper.getImage("./images/red.png"));

        headLightL.setMaterial(headLightMat);
        headLightR.setMaterial(headLightMat);

        tailLightL.setMaterial(tailLightMat);
        tailLightR.setMaterial(tailLightMat);

        headLightL.setTranslateX(-3.65);
        headLightL.setTranslateY(.55);
        headLightL.setTranslateZ(.75);

        headLightR.setTranslateX(-3.65);
        headLightR.setTranslateY(.55);
        headLightR.setTranslateZ(-.75);

        tailLightL.setTranslateX(3.75);
        tailLightL.setTranslateY(.55);
        tailLightL.setTranslateZ(.75);

        tailLightR.setTranslateX(3.75);
        tailLightR.setTranslateY(.55);
        tailLightR.setTranslateZ(-.75);

        Group group3 = new Group();

        PointLight headLights = new PointLight(Color.WHITE);
        headLights.setScaleX(.01);
        headLights.setScaleY(.01);
        headLights.setScaleZ(.01);

        group3.getChildren().addAll(meshViewss);
        group3.getChildren().addAll(headLightL, headLightR, tailLightL, tailLightR );

        group3.setScaleX(8);
        group3.setScaleY(8);
        group3.setScaleZ(35);

        //group.setTranslateY(1000);
        group3.setTranslateZ(-45);
        group3.setTranslateY(0);
        group3.setTranslateX(10);
        group3.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS),new Rotate(-90, Rotate.Y_AXIS),
                new Rotate(0, Rotate.Z_AXIS));
        buses.getChildren().add(group3);
        buses.setTranslateY(-100);

        busShape = new Rectangle(8, 50);
        busShape.setFill(Color.YELLOW);
        busShape.setTranslateX(5.5);
        busShape.setTranslateY(-25);

        //Set initial angle based on the first segment
        if (!temp.isEmpty()) {
            double[] firstSegment = temp.get(0);
            buses.setRotate(-calculateAngle(firstSegment[0], firstSegment[1], firstSegment[2], firstSegment[3]));
        }

        buses.getChildren().add(busShape);
        return group3;
    }

    /**
     * Runs buses on paths
     * This function utilizes tempPane and collidableBus, both obtained when the constructor
     * is first called for the bus.
     * It then adds the path and carShape values to the proveded tempPane, and provided that both values exist
     * it will initialize the pathTransition with an event to remove the bus once it has completed the path once.
     * this is the same as in Bus.java
     * @param tempPane This is the main panel the item will be spawned upon
     * @param collidableBus This is the list of Buses on the panel
     */
    private void initializePathTransition(Pane tempPane, List<Bus3D> collidableBus) {
        tempPane.getChildren().addAll(path,buses);
        if (path != null && buses != null) {
            pathTransition = new PathTransition(Duration.millis(seconds*1000), path, buses);
            pathTransition.setInterpolator(Interpolator.LINEAR);
            pathTransition.setCycleCount(1);

            pathTransition.setOnFinished(event -> {
                tempPane.getChildren().removeAll(path,buses);
                collidableBus.remove(this);
            });
        }
    }

    /**
     * A protected method that runs the path transition for a selected bus
     * same as in Bus.java
     */
    protected void startAnimation() {
        if (pathTransition != null) {
            pathTransition.play();
        }
    }

    /**
     * A protected method that pauses the path transition for a selected bus
     * same as in Bus.java
     */
    protected void stopBus() {
        if (pathTransition != null) {
            pathTransition.pause();
        }
    }

    /**
     * A protected method that restarts the path transition for a selected bus
     * same as in Bus.java
     */
    protected void restartBus() {
        if (pathTransition != null) {
            pathTransition.play();
        }
    }

    /**
     * initializeArrays adds each viable path from PATHS into startingPaths
     * is the same as in Bus.java
     */
    private void initializeArrays(){
        Collections.addAll(startingPaths, PATHS);
    }

    /**
     * Creates paths for buses to follow
     * This function starts by randomly selecting a viable path in the startingPaths array
     * it then initializes a path and sets the correct starting location and MoveTo element 
     * from startingPaths 0 and 1.
     * Looping through each element in the selected startingPath we create distance vectors
     * to represent the distance and direction of each step in the bus' overall path.
     * The bus' speed is also set here as a ratio of the distance traveled / 100
     * is the same as in Bus.java
     */
    protected void createPath(){
        Random random = new Random();
        int tempInt = random.nextInt(2);
        temp.add(startingPaths.get(tempInt));
        path = new Path();
        path.getElements().add(new MoveTo(temp.get(0)[0], temp.get(0)[1]));
        double startX = temp.get(0)[0];
        double startY = temp.get(0)[1];
        for (int i = 0; i < temp.size(); i++) {
            double[] point = temp.get(i);
            path.getElements().add(new LineTo(point[2], point[3]));
            double endX = point[2];
            double endY = point[3];
            distance += abs((endX - startX)) + abs((endY - startY));
            startX = point[2];
            startY = point[3];
        }
        //This is where you edit the Speed
        seconds = distance / 150;
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

    /**
     * This is to get the bounds of the shape in the grandparent node
     * @param node The node you want to grab the grandparent from
     * @return The bounds of the shape
     */
    protected Bounds getBoundsInGrandparent(Node node) {
        Bounds nodeInParent = node.localToParent(node.getBoundsInLocal());
        return node.getParent().localToParent(nodeInParent);
    }

}

