import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;//this import is unused...


/**
 * This class creates a traffic light with three circles and a rectangle.
 * Added methds to set the color of the circles and get the pane of which the traffic light is created.
 */
public class TrafficLight {

    public enum LightColor {
        RED, YELLOW, GREEN,
        GREENLEFT,YELLOWLEFT,REDLEFT,
        GREENRIGHT,YELLOWRIGHT,REDRIGHT
    }
    public enum type{
        LEFT, RIGHT, STRAIGHT, SINGLE, BUS, BUS_ALT
    }

    private final GridPane pane;
    private final Circle redCircle;
    private final Circle yellowCircle;
    private final Circle greenCircle;
    private final ImageView leftTurnArrowImageView;
    private final ImageView rightTurnArrowImageView;
    private final ImageView leftTurnYellowImageView;
    private final ImageView leftTurnRedImageView;
    private final ImageView rightTurnYellowImageView;
    private final ImageView rightTurnRedImageView;
    private CollisionBox collisionBox = null;
    private type type;

    private LightColor lightColor;
    //Create a pseudo-random ID for the traffic light
    private final int id = (int) (Math.random() * 1000);
    /**
     * Constructor that creates a traffic light with three circles and a rectangle.
     */
    public TrafficLight() {
        pane = new GridPane();
        pane.setStyle("-fx-background-color: black;");
        pane.setMaxSize(20, 50);

        redCircle = new Circle(5);
        redCircle.setFill(null);
        redCircle.setStroke(Color.WHITE);
        GridPane.setConstraints(redCircle, 0, 2);

        yellowCircle = new Circle(5);
        yellowCircle.setFill(null);
        yellowCircle.setStroke(Color.WHITE);
        GridPane.setConstraints(yellowCircle, 0, 1);

        greenCircle = new Circle(5);
        greenCircle.setFill(null);
        greenCircle.setStroke(Color.WHITE);
        GridPane.setConstraints(greenCircle, 0, 0);

        leftTurnArrowImageView = new ImageView();
        rightTurnArrowImageView = new ImageView();

        leftTurnYellowImageView = new ImageView();
        leftTurnRedImageView = new ImageView();
        rightTurnYellowImageView = new ImageView();
        rightTurnRedImageView = new ImageView();

        ImageHelper imageHelper = new ImageHelper();

        Image map = imageHelper.getImage("./images/Steady_Green_Arrow-removebg-preview.png");
        leftTurnArrowImageView.setImage(map);
        leftTurnArrowImageView.setFitWidth(10); // Match the diameter of the greenCircle
        leftTurnArrowImageView.setFitHeight(10);
        leftTurnArrowImageView.setX(325); // Position it over the greenCircle
        leftTurnArrowImageView.setY(205);

        map = imageHelper.getImage("./images/right_Steady_Green_Arrow-removebg-preview.png");
        rightTurnArrowImageView.setImage(map);
        rightTurnArrowImageView.setFitWidth(10); // Match the diameter of the greenCircle
        rightTurnArrowImageView.setFitHeight(10);
        rightTurnArrowImageView.setX(325); // Position it over the greenCircle
        rightTurnArrowImageView.setY(205);

        map = imageHelper.getImage("./images/left_yellow_arrow.png");
        leftTurnYellowImageView.setImage(map);
        leftTurnYellowImageView.setFitWidth(10); // Match the diameter of the greenCircle
        leftTurnYellowImageView.setFitHeight(10);
        leftTurnYellowImageView.setX(325); // Position it over the greenCircle
        leftTurnYellowImageView.setY(205);

        map = imageHelper.getImage("./images/left_red_arrow.png");
        leftTurnRedImageView.setImage(map);
        leftTurnRedImageView.setFitWidth(10); // Match the diameter of the greenCircle
        leftTurnRedImageView.setFitHeight(10);
        leftTurnRedImageView.setX(325); // Position it over the greenCircle
        leftTurnRedImageView.setY(205);

        map = imageHelper.getImage("./images/right_yellow_arrow.png");
        rightTurnYellowImageView.setImage(map);
        rightTurnYellowImageView.setFitWidth(10); // Match the diameter of the greenCircle
        rightTurnYellowImageView.setFitHeight(10);
        rightTurnYellowImageView.setX(325); // Position it over the greenCircle
        rightTurnYellowImageView.setY(205);

        map = imageHelper.getImage("./images/right_red_arrow.png");
        rightTurnRedImageView.setImage(map);
        rightTurnRedImageView.setFitWidth(10); // Match the diameter of the greenCircle
        rightTurnRedImageView.setFitHeight(10);
        rightTurnRedImageView.setX(325); // Position it over the greenCircle
        rightTurnRedImageView.setY(205);

        lightColor = null;


        pane.getChildren().addAll(redCircle, yellowCircle, greenCircle);
        pane.setUserData(this);
        this.setRed();
    }

    /**
     * Method to get the pane of which the traffic light is created.
     * @return pane
     */
    public Pane getTrafficLight() {
        return pane;
    }

    /**
     * Method to set the color of the red circle to red and the other circles to null.
     */
    public void setRed(){
        redCircle.setFill(Color.RED);
        yellowCircle.setFill(null);
        greenCircle.setFill(null);
        lightColor = LightColor.RED;

        if(pane.getChildren().contains(leftTurnArrowImageView)){
            pane.getChildren().remove(leftTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().remove(rightTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().remove(rightTurnYellowImageView);
        }
        if (pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().remove(rightTurnRedImageView);
        }
        if (pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().remove(leftTurnYellowImageView);
        }
        if (pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().remove(leftTurnRedImageView);
        }
    }

    /**
     * Method to set the color of the yellow circle to yellow and the other circles to null.
     */
    public void setYellow(){
        redCircle.setFill(null);
        yellowCircle.setFill(Color.YELLOW);
        greenCircle.setFill(null);
        lightColor = LightColor.YELLOW;

        if(pane.getChildren().contains(leftTurnArrowImageView)){
            pane.getChildren().remove(leftTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().remove(rightTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().remove(rightTurnYellowImageView);
        }
        if (pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().remove(rightTurnRedImageView);
        }
        if (pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().remove(leftTurnYellowImageView);
        }
        if (pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().remove(leftTurnRedImageView);
        }
    }

    /**
     * Method to set the color of the green circle to green and the other circles to null.
     */
    public void setGreen(){
        redCircle.setFill(null);
        yellowCircle.setFill(null);
        greenCircle.setFill(Color.GREEN);
        lightColor = LightColor.GREEN;

        if(pane.getChildren().contains(leftTurnArrowImageView)){
            pane.getChildren().remove(leftTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().remove(rightTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().remove(rightTurnYellowImageView);
        }
        if (pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().remove(rightTurnRedImageView);
        }
        if (pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().remove(leftTurnYellowImageView);
        }
        if (pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().remove(leftTurnRedImageView);
        }
    }
    //============================================================================================
    //SET LEFT TURN SIGNAL COLORS
    /**
     * Updates traffic light image to reflect
     * green left turn signal
     */
    public void setGreenLeftTurnArrow(){
        redCircle.setFill(null);
        yellowCircle.setFill(null);
        greenCircle.setFill(null);

        // Add the ImageView to the pane if it's not already there
        if (!pane.getChildren().contains(leftTurnArrowImageView)) {
            pane.getChildren().add(leftTurnArrowImageView);
        }

        if (pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().remove(rightTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().remove(rightTurnYellowImageView);
        }
        if (pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().remove(rightTurnRedImageView);
        }
        if (pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().remove(leftTurnYellowImageView);
        }
        if (pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().remove(leftTurnRedImageView);
        }
        lightColor = LightColor.GREENLEFT;
    }
    /**
     * Updates traffic light image to reflect
     * yellow left turn signal
     */
    public void setYellowLeftTurnArrow(){
        redCircle.setFill(null);
        yellowCircle.setFill(null);
        greenCircle.setFill(null);

        // Add the ImageView to the pane if it's not already there
        if (!pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().add(leftTurnYellowImageView);
        }

        // Make sure there are no other displaying turn signal images
        if (pane.getChildren().contains(leftTurnArrowImageView)) {
            pane.getChildren().remove(leftTurnArrowImageView);
        }
        if (pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().remove(leftTurnRedImageView);
        }
        if (pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().remove(rightTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().remove(rightTurnYellowImageView);
        }
        if (pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().remove(rightTurnRedImageView);
        }
        lightColor = LightColor.YELLOWLEFT;
    }
    /**
     * Updates traffic light image to reflect
     * red left turn signal
     */
    public void setRedLeftTurnArrow(){
        redCircle.setFill(null);
        yellowCircle.setFill(null);
        greenCircle.setFill(null);

        // Add the ImageView to the pane if it's not already there
        if (!pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().add(leftTurnRedImageView);
        }

        // Make sure there are no other displaying turn signal images
        if (pane.getChildren().contains(leftTurnArrowImageView)) {
            pane.getChildren().remove(leftTurnArrowImageView);
        }
        if (pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().remove(leftTurnYellowImageView);
        }
        if (pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().remove(rightTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().remove(rightTurnYellowImageView);
        }
        if (pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().remove(rightTurnRedImageView);
        }
        lightColor = LightColor.REDLEFT;
    }
    //==================================================================================
    //SET RIGHT TURN SIGNAL COLORS
    /**
     * Updates traffic light image to reflect
     * right green turn signal
     */
    public void setGreenRightTurnArrow(){
        redCircle.setFill(null);
        yellowCircle.setFill(null);
        greenCircle.setFill(null);

        // Add the ImageView to the pane if it's not already there
        if (!pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().add(rightTurnArrowImageView);
        }

        if(pane.getChildren().contains(leftTurnArrowImageView)){
            pane.getChildren().remove(leftTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().remove(rightTurnYellowImageView);
        }
        if (pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().remove(rightTurnRedImageView);
        }
        if (pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().remove(leftTurnYellowImageView);
        }
        if (pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().remove(leftTurnRedImageView);
        }
        lightColor = LightColor.GREENRIGHT;
    }
    /**
     * Updates traffic light image to reflect
     * yellow right turn signal
     */
    public void setYellowRightTurnArrow(){
        redCircle.setFill(null);
        yellowCircle.setFill(null);
        greenCircle.setFill(null);

        // Add the ImageView to the pane if it's not already there
        if (!pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().add(rightTurnYellowImageView);
        }

        // Make sure there are no other displaying turn signal images
        if (pane.getChildren().contains(leftTurnArrowImageView)) {
            pane.getChildren().remove(leftTurnArrowImageView);
        }
        if (pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().remove(leftTurnYellowImageView);
        }
        if (pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().remove(leftTurnRedImageView);
        }
        if (pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().remove(rightTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().remove(rightTurnRedImageView);
        }
        lightColor = LightColor.YELLOWRIGHT;
    }
    /**
     * Updates traffic light image to reflect
     * red right turn signal
     */
    public void setRedRightTurnArrow(){
        redCircle.setFill(null);
        yellowCircle.setFill(null);
        greenCircle.setFill(null);

        // Add the ImageView to the pane if it's not already there
        if (!pane.getChildren().contains(rightTurnRedImageView)) {
            pane.getChildren().add(rightTurnRedImageView);
        }

        // Make sure there are no other displaying turn signal images
        if (pane.getChildren().contains(leftTurnArrowImageView)) {
            pane.getChildren().remove(leftTurnArrowImageView);
        }
        if (pane.getChildren().contains(leftTurnYellowImageView)) {
            pane.getChildren().remove(leftTurnYellowImageView);
        }
        if (pane.getChildren().contains(leftTurnRedImageView)) {
            pane.getChildren().remove(leftTurnRedImageView);
        }
        if (pane.getChildren().contains(rightTurnArrowImageView)) {
            pane.getChildren().remove(rightTurnArrowImageView);
        }
        if (pane.getChildren().contains(rightTurnYellowImageView)) {
            pane.getChildren().remove(rightTurnYellowImageView);
        }
        lightColor = LightColor.REDRIGHT;
    }

    //============================================================================
    //Return constructor values
    /**
     *
     * @return constructor's enum light state
     */
    public LightColor getLightColor() {
        return lightColor;
    }

    /**
     *
     * @return traffic light's object id
     */
    public int getId() {
        return id;
    }

    /**
     * Method to set the type of the traffic light.
     * @param type
     */
    public void setType(type type) {
        this.type = type;
    }

    /**
     * Method to get the type of the traffic light.
     * @return type
     */
    public type getType() {
        return type;
    }

    /**
     * Method to add a collision box to the traffic light.
     * @param collisionBox
     */
    public void setCollisionBox(CollisionBox collisionBox) {
        this.collisionBox = collisionBox;
    }

    /**
     * Method to get the collision box of the traffic light.
     * @return collisionBox
     */
    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

}
