import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import static java.lang.Math.abs;

/**
 * 3D implementation of pedestrian object
 */
public class Person3D {
    private static final double[][] PATHS = {
            {333,247,333,500}, {194, 467, 383, 467}, {206, 345, 384, 345},
            {583, 238, 583, 501}, {681, 501, 681, 242}, {532, 153, 751, 153},
            {719, 91, 543, 91}, {108, 100, 360, 100}, {121, 591, 463, 591},
            {868, 529, 516, 529}, {244, 62, 244, 246}
    };

    private List<CollisionBox> collidableBoxes = new ArrayList<>();
    private List<double[]> startingPaths = new ArrayList<>();
    private List<double[]> temp = new ArrayList<>();
    private Path path;
    private double distance;
    private double seconds;
    private Boolean collided;
    private PathTransition pathTransition;
    private Shape personShape;
    private Boolean isCrossing = false;
    private Group people = new Group();

    /**
     * Constructor
     * @param tempPane This is the Main Panel that the person will be spawned on
     * @param collidablePerson This is the list of People
     */

    public Person3D(Pane tempPane, List<Person3D> collidablePerson, List<CollisionBox> collidableBoxes){
        person();
        initializeArrays();
        createPath();
        person();
        initializePathTransition(tempPane, collidablePerson);
        this.collided = false;
        this.collidableBoxes = collidableBoxes;
    }

    /**
     * Creates 3D model for person based on the Male_Casual object found in
     * "/People/Male_Casual.obj"
     * In addition to the default model used, this function correctly scales and shifts
     * each person in order to better fit the visuals.
     * Finally, this function uses the firstSegment values to determine the Person' initial orientation
     * @return A Group that groups the personShape with the 3D File
     */
    private Group person(){
        personShape = new Circle(4);
        personShape.setFill(Color.GREEN);
        personShape.setTranslateX(10);
        personShape.setTranslateY(7.5);
        //Set initial angle based on the first segment
        if (!temp.isEmpty()) {
            double[] firstSegment = temp.get(0);
            people.setRotate(-calculateAngle(firstSegment[0], firstSegment[1], firstSegment[2], firstSegment[3]));
        }

        ObjModelImporter importes = new ObjModelImporter();
        try {
            importes.read(this.getClass().getResource("/People/Male_Casual.obj"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        MeshView[] meshViewss = importes.getImport();

        Group group3 = new Group();


        group3.getChildren().addAll(meshViewss);
        group3.setScaleX(4);
        group3.setScaleY(4);
        group3.setScaleZ(10);

        //group.setTranslateY(1000);
        group3.setTranslateZ(-5);
        group3.setTranslateY(0);
        group3.setTranslateX(10);
        group3.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS),new Rotate(0, Rotate.Y_AXIS),
                new Rotate(0, Rotate.Z_AXIS));
        people.getChildren().addAll(group3, personShape);
        people.setTranslateY(-100);
        return group3;
    }
    /**
     * Runs 3D people on paths
     * This function utilizes tempPane and collidablePerson, both obtained when the constructor
     * is first called for the person.
     * It then adds the path and person object values to the proveded tempPane, and provided that both values exist
     * it will initialize the pathTransition with an event to remove the person once it has completed the path once.
     * @param tempPane This is the Main Panel that the person will be spawned on
     * @param collidablePerson This is the list of People
     */
    private void initializePathTransition(Pane tempPane, List<Person3D> collidablePerson) {
        tempPane.getChildren().addAll(path,people);
        if (path != null && people != null) {
            pathTransition = new PathTransition(Duration.millis(seconds*1000), path, people);
            pathTransition.setInterpolator(Interpolator.LINEAR);
            pathTransition.setCycleCount(1);

            pathTransition.setOnFinished(event -> {
                tempPane.getChildren().removeAll(path,people);
                collidablePerson.remove(this);
            });
        }
    }

    /**
     * A protected method that runs the path transition for a selected person
     */
    protected void startAnimation() {
        if (pathTransition != null) {
            pathTransition.play();
        }
    }
    /**
     * A protected method that pauses the path transition for a selected person
     */
    protected void stopPerson() {
        if (pathTransition != null) {
            pathTransition.pause();
        }
    }
    /**
     * A protected method that restarts the path transition for a selected person
     */
    protected void restartPerson() {
        if (pathTransition != null) {
            pathTransition.play();
        }
    }

    /**
     * initializeArrays adds each viable path from PATHS into startingPaths
     */
    private void initializeArrays(){
        Collections.addAll(startingPaths, PATHS);
    }

    /**
     * Creates paths for people to follow
     * This function starts by randomly selecting a vaible path in the startingPaths array
     * it then initializes a path and sets the correct starting location and MoveTo element 
     * from startingPaths 0 and 1.
     * Looping through each element in the selected startingPath we create distance vectors
     * to represent the distance and direction of each step in the person's overall path.
     * The person's speed is also set here as a ratio of the distance traveled / 50
     */
    protected void createPath(){
        Random random = new Random();
        int tempInt = random.nextInt(PATHS.length);
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
     * calculateAngle is a helper function designed to calculate the angle of a Person
     * based on which path segment it is currently on.
     * @param startX This is the Starting X position
     * @param startY This is the Starting Y position
     * @param endX This is the Ending X Position
     * @param endY This is the Ending Y Position
     * @return The angle at which the Person should be facing along this segment.
     */
    protected double calculateAngle(double startX, double startY, double endX, double endY) {
        double angle = Math.toDegrees(Math.atan2(endX - startX, endY - startY));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    /**
     * setCollided is a helper function used to set the collision value of the person to the provided value.
     * @param bool The bool the collided should be set to
     */
    protected void setCollided(boolean bool){
        collided = bool;
    }

    /**
     * The following are temporary helper functions used while debugging.
     * They are simple return functions that provide various info about a
     * selected pedestrian's values.
     * @return True/False if the person is crossing.
     */
    public boolean isCrossing() {
        return isCrossing;
    }

    /**
     * This is to set the Person as Crossing the Road
     * @param crossing The bool you want to set the Person to
     */
    public void setCrossing(boolean crossing) {
        this.isCrossing = crossing;
    }

    /**
     * The following are temporary helper functions used while debugging.
     * values.
     * @return Returns the path of the Person
     */
    protected Path returnPath(){
        return path;
    }

    /***
     * This is to return the total amount of Seconds the person will run
     * @return The total amount of seconds in double form
     */
    protected double returnSeconds(){
        return seconds;
    }

    /***
     * This is to return the Path Array that the person will be taking.
     * @return A double[] List of the Persons path.
     */
    protected List<double[]> returnPathArray(){
        return temp;
    }

    /***
     * This is to return the Person Shape on the pane
     * @return The person shape on the Pane and all information of it.
     */
    protected Shape returnPersonShape() {
        return personShape;
    }

    /**
     * This is to return if the Person has collided into anything
     * @return True/False if the Person has collided with anything
     */
    protected boolean returnCollided(){
        return collided;
    }

    /**
     * This checks collision of the person with the Pedestrian Lights. If the person collides
     * with a collision box, it is like the person clicking the pedestrian crossing button.
     */
    protected void checkCollision(){
        for (CollisionBox box : collidableBoxes){
            if (box.isColliding(getBoundsInGrandparent(personShape))){
                if (box.getState() == CollisionBox.State.STOP && !this.isCrossing){
                    stopPerson();
                }
                else {
                    restartPerson();
                    this.isCrossing = true;
                }
            }
        }
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
