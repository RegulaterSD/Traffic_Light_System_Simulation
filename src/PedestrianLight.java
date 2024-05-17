import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
 * object class for pedestrians
 */
public class PedestrianLight {

    public enum LightColor {
        RED, WALKING
    }

    private final Pane pane;
    private final Rectangle rectangle;
    private final ImageView redHandImageView;
    private final ImageView walkingImageView;
    private LightColor lightColor;
    //Random ID for the pedestrian light
    private final int id = (int) (Math.random() * 1000);

    /*
     * This function creates the visual for each of the pedestestrian lights,
     * using the imageHelper class to pull assets for the stop and go states
     * of the light
     */
    public PedestrianLight() {
        pane = new Pane();

        rectangle = new Rectangle(300, 50, 100, 100);
        rectangle.setStroke(Color.WHITE);
        rectangle.setFill(Color.BLACK);

        ImageHelper imageHelper = new ImageHelper();
        redHandImageView = new ImageView(imageHelper.getImage("./images/redHand.jpg"));
        redHandImageView.setFitWidth(100);
        redHandImageView.setFitHeight(100);
        redHandImageView.setX(350);
        redHandImageView.setY(50);

        walkingImageView = new ImageView(imageHelper.getImage("./images/walking.jpg"));
        walkingImageView.setFitWidth(100);
        walkingImageView.setFitHeight(100);
        walkingImageView.setX(300);
        walkingImageView.setY(50);

        lightColor = null;

        pane.getChildren().addAll(redHandImageView, walkingImageView);
        pane.setUserData(this);
    }

    /*
     * The following are setters used to set the state of a chosen light between "RED" and "WALKING"
     * the following getPedestrianLight function returns the parent pane of the light setup
     * 
     * Each of these functions are called in LightController.java
     */
    public Pane getPedestrianLight() {
        return pane;
    }
    public void setRedHand() {
        redHandImageView.setVisible(true);
        walkingImageView.setVisible(false);
        lightColor = LightColor.RED;
    }
    public void setWalking() {
        redHandImageView.setVisible(false);
        walkingImageView.setVisible(true);
        lightColor = LightColor.WALKING;
    }

    /*
     * the following are unused getters that return the lightColor and id
     */
    public LightColor getLightColor() {
        return lightColor;
    }
    public int getId() {
        return id;
    }
}
