import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CollisionBox extends Rectangle{
    
    //Enum to store the state of the collision box
    public static enum State{
        STOP, GO, LEFT, RIGHT
    }

    private Object parent = null;
    private static Color color = Color.RED;
    private boolean isOff = false;
    private TrafficLight.type lightType;

    /**
     * To Create a Basic Rectangular Collision Box
     * @param x The X Coordinate
     * @param y The Y Coordinate
     * @param width The Width
     * @param height The Height
     */
    public CollisionBox(double x, double y, int width, int height) {
        super(x, y, width, height);
        this.setFill(Color.TRANSPARENT);
        this.setStroke(color);
        this.setStrokeWidth(2);
    }

    /**
     * To Create a Basic Rectangular Collision Box
     * @param x The X Coordinate
     * @param y The Y Coordinate
     * @param width The Width
     * @param height The Height
     * @param parent The object Parent type that the Collision Box will be linked to
     */
    public CollisionBox(double x, double y, int width, int height, Object parent) {
        super(x, y, width, height);
        this.setFill(Color.TRANSPARENT);
        this.setStroke(color);
        this.setStrokeWidth(2);
        this.parent = parent;
    }

    /**
     * This will set the State of the Collision Box
     * @param state The state to switch the Collision Box to
     */
    public void setState(State state){
        this.setUserData(state);
    }

    /**
     * This is to get the Collision Box
     * @return Return the Collision Box State
     */
    public State getState(){
        return this.getUserData() == null ? State.STOP : (State)this.getUserData();
    }

    /**
     * This is to get the Parent Class of the Collision Box
     * @return The Parent Class
     */
    public Object getParentClass(){
        return this.parent;
    }

    /**
     * This is to set the type of the Traffic Light
     * @param type The Traffic Light Type to switch to
     */
    public void setType(TrafficLight.type type){
        this.lightType = type;
    }

    /**
     * This is to get the Type of the Trafflic Light
     * @return The trafflic Light type
     */
    public TrafficLight.type getType(){
        return this.lightType;
    }

    /**
     * Check if a collision box is colliding with another object
     * this is acomplished by using the built-in intersects function to return
     * true if a collision box is overlapping with another
     * @param box This is the bounds of the shape to check
     * @return If it is colliding or not.
     */
    public boolean isColliding(Bounds box){
        return this.getBoundsInParent().intersects(box);
    }

}
