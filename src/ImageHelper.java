/*
 * As described below, the ImageHelper class is used to manage javafx Image objects.
 * Currently, the only function used in this project is getImage as a way to import
 * png and jpg assets as 2d Image objects.
 */

/**
 * E. Puzak
 * CS 251 Project 5
 * Key Shooter
 *
 * This program is a simple game that allows the user to interact and shoot the targets by typing
 * the word on screen correctly.
 * I went a bit above the spec of the program and made about twice the amount of classes given as starters
 * and I also made 2 new game modes.
 * There is the speed shooter scene and the 3D key shooter scene.
 * I made a scene controller class that controls which scenes are displayed.
 */

/**
 * The purpose of this class was to reduce code. I call this method a few times that build an image
 * used to display the different sprites.
 */

import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * class loads images
 */
public class ImageHelper {

    private static boolean imagesLoaded;

    /**
     * This function is currently unused...
     * 
     * Main get image function
     */
    public ImageHelper(){
        try{
            getImage("./images/menuBackground.png");
            imagesLoaded = true;
        }
        catch (Exception e){
            imagesLoaded = false;
        }
    }

    /**
     *This function is currently unused...
     * 
     * @return returns loaded images
     */
    public boolean getImagesLoaded(){
        return imagesLoaded;
    }

    /**
     *This function is currently unused...

     * @param imageLocation
     * @return background image and sets it
     */
    public Background getBackgroundImage(String imageLocation){
        Image buttonBack = null;
        try {
            buttonBack = new Image(new FileInputStream(imageLocation));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BackgroundImage buttonimage = new BackgroundImage(buttonBack, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background buttonbackground = new Background(buttonimage);
        return  buttonbackground;
    }

    /**
     * This function is used throughout Bus3D.java, PedestrianLight.java
     * Traffic2D.java, TrafficLight.java, TrafficScene.java, and Vehicle3D.java
     * it is used to load 2d textures or assets as a javafx image.
     * @param imageLocation
     * @return image from string
     */
    public Image getImage(String imageLocation){
        Image image = null;
        try {
            image = new Image(new FileInputStream(imageLocation));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return  image;
    }
}
