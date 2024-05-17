import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This class is old and currently unused...
 */
public class TrafficCarCreation {

    private final ImageView imageView;

    /**
     * Main function to get file
     */
    public TrafficCarCreation() {

        //new Image(new FileInputStream("/car.png"));
        Image image = null;
        try {
            image = new Image(new FileInputStream("./images/car.png"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        imageView = new ImageView(image);

    }

    /**
     * return the fetched image object
     * @return
     */
    public ImageView getTrafficCar() {
        return imageView;
    }
}
