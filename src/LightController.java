import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

/*
 * -------------------------------------
 * Class to control the traffic lights at an intersection
 * ToDo: Collision boxes to stop cars, pedestrian lights, arrows
 * -------------------------------------
 */
public class LightController {

    //HashMap to store the traffic lights
    //Key stores the location of the light(N, S, E, W)
    private final HashMap<String, List<Pane>> trafficLights = new HashMap<>(){{
        put("N", new ArrayList<>());
        put("S", new ArrayList<>());
        put("E", new ArrayList<>());
        put("W", new ArrayList<>());
    }};

    //HashMap to store the pedestrian lights
    //Not implemented yet
    private final HashMap<String, Pane> pedestrianLights = new HashMap<>();
    //HashMap to store the lane collision boxes
    //Key stores the location of the light(N, S, E, W)
    private final HashMap<String, CollisionBox> laneCollisionBoxes = new HashMap<>();
    //HashMap to store the collision boxes
    //Key stores the location of the light(N, S, E, W)
    private final HashMap<String, List<CollisionBox>> lightCollisionBoxes = new HashMap<>(){{
        put("N", new ArrayList<>());
        put("S", new ArrayList<>());
        put("E", new ArrayList<>());
        put("W", new ArrayList<>());
        put("B", new ArrayList<>());
    }};

    //HashMap to store the collision boxes
    //Key stores the location of the light(N, S, E, W)
    private final HashMap<String, List<CollisionBox>> pedCollisionBoxes = new HashMap<>(){{
        put("N", new ArrayList<>());
        put("S", new ArrayList<>());
        put("E", new ArrayList<>());
        put("W", new ArrayList<>());
    }};

    private CollisionBox intersectionBox = null;

    //Store a list of vehicles in the intersection
    private CopyOnWriteArrayList<Vehicle3D> vehicles = new CopyOnWriteArrayList<>();

    //Vars to store the time in seconds for the lights
    private final int cycleTime = 120;
    private final int yellow = 6;
    //ID for the controller
    private final int id;
    private int minGreen = 15;
    private int greenTime = minGreen;
    private int maxGreen = 30;
    private int vehicleCount = 0;//currently unused
    private Vehicle currentVehicle = null;//currently unused
    private Boolean busApproaching = false;

    //Enum to control the cycle changes
    private enum direction {NS, EW};

    //Enum to mark the type of light
    protected enum lightType {STANDARD, BUS};
    private lightType type;

    //List of scheduled pedestrian light changes
    private List<String> pedLightChanges = new ArrayList<>();

    //Queue to store the pedestrians waiting to cross
    private List<Person3D> pedQueue = new ArrayList<>();

    //Lists to check if pedestrians are crossing
    private CopyOnWriteArrayList<Person3D> pedCrossingNorth = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Person3D> pedCrossingSouth = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Person3D> pedCrossingEast = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Person3D> pedCrossingWest = new CopyOnWriteArrayList<>();
    //Lists to check if left turn vehicles are in lanes
    private CopyOnWriteArrayList<Vehicle3D> trafficLaneNorth = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Vehicle3D> trafficLaneSouth = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Vehicle3D> trafficLaneEast = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Vehicle3D> trafficLaneWest = new CopyOnWriteArrayList<>();

    //Constructor
    //Takes a list of coordinates for the lights
    //lightCoord[0]: location of the light(N, S, E, W)
    //lightCoord[1]: x coordinate
    //lightCoord[2]: y coordinate
    //See SystemController for the list of coordinates
    public LightController(int id, lightType type, List<Object[]> lightCoords, List<Object[]> lightCollisionCoords, List<Object[]> pedestrianLightCoords, List<Object[]> pedestrianCollisionCoords, List<Object[]> laneCollisionCoords) {
        //Create the traffic lights at the intersection
        for(Object[] coord : lightCoords){
            createTrafficLight((String)coord[0], (TrafficLight.type)coord[1], (Integer)coord[2], (Integer)coord[3]);
        }
        //Create the collision boxes at the intersection
        for(Object[] coord : lightCollisionCoords){
            createLightCollisionBox((String)coord[0], (TrafficLight.type)coord[1], (Integer)coord[2], (Integer)coord[3]);
        }
        //Create the pedestrian lights at the intersection
        for(Object[] coord : pedestrianLightCoords){
            createPedestrianLight((String)coord[0], (Integer)coord[1], (Integer)coord[2]);
        }
        //Create the collision boxes for the pedestrian lights
        for(Object[] coord : pedestrianCollisionCoords){
            createPedCollisionBox((String)coord[0], (Integer)coord[1], (Integer)coord[2], (Integer)coord[3], (Integer)coord[4]);
        }
        for(Object[] coord : laneCollisionCoords){
            createLaneCollisionBoxes((String)coord[0], (Integer)coord[1], (Integer)coord[2], (Integer)coord[3], (Integer)coord[4]);
        }
        this.type = type;
        this.id = id;
    }

    protected CollisionBox returnIntersectionBox(){
        return intersectionBox;
    }

    //Constructor for busses
    public LightController(int id, lightType type, List<Object[]> lightCoords, List<Object[]> lightCollisionCoords, List<Object[]> pedestrianLightCoords, List<Object[]> pedestrianCollisionCoords) {
        //Create the traffic lights at the intersection
        for(Object[] coord : lightCoords){
            createTrafficLight((String)coord[0], (TrafficLight.type)coord[1], (Integer)coord[2], (Integer)coord[3]);
        }
        //Create the collision boxes at the intersection
        for(Object[] coord : lightCollisionCoords){
            createLightCollisionBox((String)coord[0], (TrafficLight.type)coord[1], (Integer)coord[2], (Integer)coord[3]);
        }
        //Create the pedestrian lights at the intersection
        for(Object[] coord : pedestrianLightCoords){
            //createPedestrianLight((String)coord[0], (Integer)coord[1], (Integer)coord[2]);
        }
        //Create the collision boxes for the pedestrian lights
        for(Object[] coord : pedestrianCollisionCoords){
            createPedCollisionBox((String)coord[0], (Integer)coord[1], (Integer)coord[2], (Integer)coord[3], (Integer)coord[4]);
        }
        this.type = type;
        this.id = id;
    }

    /**
     * creates a traffic light at a given location with a layout determined by x and y
     * the light is initially constructed on its own pane, and is added to its given location
     * before being returned.
     *
     * @param location
     * @param type of light: RIGHT, LEFT, STRAIGHT
     * @param x position
     * @param y position
     * @return pane of trafic light
     */
    private Pane createTrafficLight(String location, TrafficLight.type type, Integer x, Integer y){
        //Create a pane to hold the traffic light
        Pane trafficLight = new Pane();

        //Create the traffic light object
        TrafficLight trafficLightData = new TrafficLight();
        trafficLight = trafficLightData.getTrafficLight();

        //Set the location of the light
        trafficLight.setLayoutY(y);
        trafficLight.setLayoutX(x);
        trafficLight.setTranslateZ(0);
        //Set the type of light
        trafficLightData.setType(type);
        //Add the light to the hashmap
        trafficLights.get(location).add(trafficLight);
        return trafficLight;
    }


    /* Creates a collision box in the road for buses and cars.
     *
     * This function is one of two that manages the collision boxes at each intersection.
     * These collision boxes will manage the flow of traffic by phisically stopping any
     * car/bus/pedestrian with a collision box.
     *
     * The alternating state of these collision boxes, "GO" and "STOP" determine if they are active,
     * this is necessary to prevent cars/buses/pedestrians from getting stuck inside the intersection
     * when the light changes.
     */
    protected void createLightCollisionBox(String location, TrafficLight.type type, double x, double y){
        CollisionBox collisionBox;
        if(location == "B") {
            collisionBox = new CollisionBox(x, y, 300, 6, this);
            collisionBox.setState(CollisionBox.State.GO);

        }
        else {
            collisionBox = new CollisionBox(x, y, 6, 6, this);
            collisionBox.setState(CollisionBox.State.STOP);

            //Connect the collision box to the associated traffic light
            for(Pane trafficLight : trafficLights.get(location)){
                TrafficLight trafficLightData = (TrafficLight) trafficLight.getUserData();
                if(trafficLightData.getType() == type && trafficLightData.getCollisionBox() == null){
                    trafficLightData.setCollisionBox(collisionBox);
                }
            }
        }
        collisionBox.setType(type);

        //Add collision box to the list of collision boxes for the location
        lightCollisionBoxes.get(location).add(collisionBox);
    }

    /* Creates a collision box on the sidewalk for pedestrians.
     *
     * As with the above "createLightCollisionBox" function this function is used to manage the flow of traffic
     * by phisically blocking one direction with a collision box.
     */
    protected void createPedCollisionBox(String location, double x, double y, int width, int height){
        CollisionBox collisionBox = new CollisionBox(x, y, width, height, this);
        collisionBox.setState(CollisionBox.State.STOP);
        //Add collision box to the list of collision boxes for the location
        pedCollisionBoxes.get(location).add(collisionBox);
    }


    //This method is currently unused...
    protected void createIntersectionBox(double x, double y, int width, int height){
        intersectionBox = new CollisionBox(x, y, width, height, this);
    }
    //Create a set of lane collision boxes
    protected void createLaneCollisionBoxes(String location, double x, double y, int width, int height){
        CollisionBox collisionBox = new CollisionBox(x, y, width, height, this);
        laneCollisionBoxes.put(location, collisionBox);
    }

    /**This method is currently unused...
     *
     * Creates a pedestrian light, Not implemented
     * @param x position
     * @param y position
     * @return pane of pedestrian light
     */
    private Pane createPedestrianLight(String location, double x, double y){
        Pane pedestrianLight = new Pane();
        PedestrianLight pedestrianLightCreation = new PedestrianLight();
        pedestrianLight.getChildren().add(pedestrianLightCreation.getPedestrianLight());
        pedestrianLight.setLayoutY(y);
        pedestrianLight.setLayoutX(x);
        pedestrianLight.setTranslateZ(0);
        pedestrianLights.put(location, pedestrianLight);

        return pedestrianLight;
    }

    /**
     * addLights adds each traffic/pedestrian light to the main scene
     * it does this by looping through the list of lights and adding them to the root scene.
     * each traffic light required a bit of tweaking to get its scale and position correct.
     *
     * The function also adds each correspponding traffic & pedestrian collision box
     *
     * @param root
     */
    public void addLights(Pane root) {
        int counter = 0;
        for(List<Pane> trafficLightList : trafficLights.values()){
            int innerCount = 0;
            for(Pane trafficLight : trafficLightList){
                root.getChildren().add(trafficLight);
                //trafficLight.setTranslateZ(-150);
                trafficLight.setScaleY(.5);
                trafficLight.setScaleX(.5);
                if(counter == 0) {
                    if(id == 1){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-14);
                                trafficLight.setTranslateX(20);
                                trafficLight.setTranslateZ(-140);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));

                                trafficLight.setTranslateY(-14.5);
                                trafficLight.setTranslateX(-40);
                                trafficLight.setTranslateZ(-80);
                                break;
                        }
                    }
                    if(id == 2) {
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-15);
                                trafficLight.setTranslateX(-28);
                                trafficLight.setTranslateZ(-80);
                                //trafficLight.setTranslateY(-50);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-14.5);
                                trafficLight.setTranslateX(30);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 3){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-13.5);
                                trafficLight.setTranslateX(20);
                                trafficLight.setTranslateZ(-140);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-14.5);
                                trafficLight.setTranslateX(-38);
                                trafficLight.setTranslateZ(-80);
                                break;
                        }
                    }
                    if(id == 4){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-14.5);
                                trafficLight.setTranslateX(26);
                                trafficLight.setTranslateZ(-140);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));

                                trafficLight.setTranslateY(-15);
                                trafficLight.setTranslateX(-34);
                                trafficLight.setTranslateZ(-80);
                                break;
                        }
                    }
                    if(id == 5){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-16.5);
                                trafficLight.setTranslateX(-38);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-16);
                                trafficLight.setTranslateX(20);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 6){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-15.5);
                                trafficLight.setTranslateX(-32);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-15);
                                trafficLight.setTranslateX(26);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 7){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-18);
                                trafficLight.setTranslateX(-38);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-18);
                                trafficLight.setTranslateX(22);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 8){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-18);
                                trafficLight.setTranslateX(-14);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-17.5);
                                trafficLight.setTranslateX(5);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }

                    innerCount++;
                }
                else if(counter == 1){
                    if(id == 1){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20);
                                trafficLight.setTranslateX(-5);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 2){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20.5);
                                trafficLight.setTranslateX(6);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 3) {
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-12);
                                trafficLight.setTranslateX(-5);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-22.5);
                                trafficLight.setTranslateX(-5);
                                trafficLight.setTranslateZ(-140);
                                //trafficLight.setTranslateY(-50);
                                break;
                            case 2:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-21.5);
                                trafficLight.setTranslateX(-5);
                                trafficLight.setTranslateZ(-140);
                                //trafficLight.setTranslateY(-50);
                                break;
                        }
                    }
                    if(id == 4){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-8);
                                trafficLight.setTranslateX(2);
                                trafficLight.setTranslateZ(-140);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-9);
                                trafficLight.setTranslateX(1);
                                trafficLight.setTranslateZ(-80);
                                //trafficLight.setTranslateY(-50);
                                break;
                            case 2:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-21.5);
                                trafficLight.setTranslateX(2);
                                trafficLight.setTranslateZ(-140);
                                //trafficLight.setTranslateY(-50);
                                break;
                        }
                    }
                    if(id == 5){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-8);
                                trafficLight.setTranslateX(0);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 6){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(90, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-8);
                                trafficLight.setTranslateX(0);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 7){

                    }
                    if(id == 8){

                    }

                    innerCount++;
                }
                else if(counter == 2){
                    if(id == 1){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20);
                                trafficLight.setTranslateX(-15);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 2){
                    switch (innerCount) {
                        case 0:
                            trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                    new Rotate(0, Rotate.Z_AXIS));
                            trafficLight.setTranslateY(-20);
                            trafficLight.setTranslateX(-12);
                            trafficLight.setTranslateZ(-140);
                            break;
                        }
                    }

                    if(id == 3) {
                        switch (innerCount) {
                            case 0:

                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(18);
                                trafficLight.setTranslateX(-8);
                                trafficLight.setTranslateZ(-140);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-78);
                                trafficLight.setTranslateX(-8);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 2:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-25.5);
                                trafficLight.setTranslateX(-8);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 4){
                        switch (innerCount) {
                            case 0:

                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-75);
                                trafficLight.setTranslateX(-1);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(17);
                                trafficLight.setTranslateX(-1);
                                trafficLight.setTranslateZ(-140);
                                break;
                            case 2:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-25.5);
                                trafficLight.setTranslateX(-1);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 5){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(4);
                                trafficLight.setTranslateX(-10);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 6){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(90, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(4);
                                trafficLight.setTranslateX(-10);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 7){

                    }
                    if(id == 8){

                    }

                    innerCount++;
                }
                else if(counter == 3){
                    if(id == 1){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-19.5);
                                trafficLight.setTranslateX(14);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20.5);
                                trafficLight.setTranslateX(-4);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 2){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20.5);
                                trafficLight.setTranslateX(9);
                                trafficLight.setTranslateZ(-140);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-19.5);
                                trafficLight.setTranslateX(26);
                                trafficLight.setTranslateZ(-80);
                                break;
                        }
                    }
                    if(id == 3) {
                        switch (innerCount) {
                            case 0:

                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20.5);
                                trafficLight.setTranslateX(15);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-21.5);
                                trafficLight.setTranslateX(-5);
                                trafficLight.setTranslateZ(-140);
                                //trafficLight.setTranslateY(-50);
                                break;
                        }
                    }
                    if(id == 4){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20);
                                trafficLight.setTranslateX(15);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-21);
                                trafficLight.setTranslateX(-4);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 5){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-19.5);
                                trafficLight.setTranslateX(14);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20.5);
                                trafficLight.setTranslateX(-4);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 6){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-20);
                                trafficLight.setTranslateX(18);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-21);
                                trafficLight.setTranslateX(-2);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 7){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-10);
                                trafficLight.setTranslateX(15);
                                trafficLight.setTranslateZ(-80);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-10);
                                trafficLight.setTranslateX(-5);
                                trafficLight.setTranslateZ(-140);
                                break;
                        }
                    }
                    if(id == 8){
                        switch (innerCount) {
                            case 0:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-10);
                                trafficLight.setTranslateX(2);
                                trafficLight.setTranslateZ(-140);
                                break;
                            case 1:
                                trafficLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
                                        new Rotate(0, Rotate.Z_AXIS));
                                trafficLight.setTranslateY(-10);
                                trafficLight.setTranslateX(23);
                                trafficLight.setTranslateZ(-80);
                                break;
                        }
                    }
                    innerCount++;
                }
            }
            counter++;
        }

       /*     Pane trafficlightLeftSide = new Pane(trafficLight);

            root.getChildren().add(trafficlightLeftSide);
            //Light scale and position 3D
            trafficlightLeftSide.setTranslateZ(-150);
            trafficlightLeftSide.setScaleY(1.25);
            trafficlightLeftSide.setScaleX(1.25);*/

           
        for(Pane pedestrianLight : pedestrianLights.values()){
            root.getChildren().add(pedestrianLight);
            pedestrianLight.setScaleX(.025);
            pedestrianLight.setScaleY(.025);
            pedestrianLight.setScaleZ(.25);
            pedestrianLight.setTranslateZ(-100);
            pedestrianLight.setTranslateX(-240);
            pedestrianLight.setTranslateY(-80);
            pedestrianLight.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS),new Rotate(0, Rotate.Y_AXIS),
                    new Rotate(0, Rotate.Z_AXIS));
        }
        for(List<CollisionBox> collisionBox : lightCollisionBoxes.values()){
            for(CollisionBox box : collisionBox){
                root.getChildren().add(box);
            }
        }
        for(List<CollisionBox> collisionBox : pedCollisionBoxes.values()){
            for(CollisionBox box : collisionBox){
                root.getChildren().add(box);
            }
        }
        for(CollisionBox box : laneCollisionBoxes.values()){
            root.getChildren().add(box);
        }
        if(intersectionBox != null) {
            root.getChildren().add(intersectionBox);
        }
    }

    //Get the type of light, returns STANDARD or BUS
    public LightController.lightType getType() {
        return type;
    }
    
    //Get the id for the light controller
    public int getId() {
        return id;
    }

    //Get the bus collision boxes
    public List<CollisionBox> getBusCollisionBoxes() {
        return lightCollisionBoxes.get("B");
    }

    protected List<CollisionBox> getLaneCollisionBoxes(){
        List<CollisionBox> temp = new ArrayList<>();
        for (CollisionBox collisionBox : laneCollisionBoxes.values()){
            temp.add(collisionBox);
        }
        return temp;
    }

    //Check collsions with bus collision boxes
    public void checkBusCollision(List<Bus3D> allBuses) {
        //Print the size of the list holding the boxes
      //  System.out.println("Number of collision boxes bus: " + lightCollisionBoxes.get("B").size());
        if(lightCollisionBoxes.get("B").size() == 0){
            return;
        }
        CollisionBox box1 = lightCollisionBoxes.get("B").get(0);
        CollisionBox box2 = lightCollisionBoxes.get("B").get(1);
        for (Bus3D bus : allBuses) {
            for (CollisionBox box : lightCollisionBoxes.get("B")) {
                if (box.isColliding(bus.getBoundsInGrandparent(bus.returnBusShape()))) {
                    this.busApproaching = true;
                }
            }
            //Check if there is no bus in either lanes
            if(!box1.isColliding(bus.getBoundsInGrandparent(bus.returnBusShape())) && !box2.isColliding(bus.getBoundsInGrandparent(bus.returnBusShape()))){
                this.busApproaching = false;
            }
        }
    }

    /*
     * gets the list of light collision boxes
     */
    public List<CollisionBox> getLightCollisionBoxes(){
        List<CollisionBox> boxes = new ArrayList<>();
        for(List<CollisionBox> collisionBox : lightCollisionBoxes.values()){
            boxes.addAll(collisionBox);
        }
        return boxes;
    }

    /*
     * gets the list of pedestrian collision boxes
     */
    public List<CollisionBox> getPedCollisionBoxes(){
        List<CollisionBox> boxes = new ArrayList<>();
        for(List<CollisionBox> collisionBox : pedCollisionBoxes.values()){
            boxes.addAll(collisionBox);
        }
        return boxes;
    }
    
    //Set the maximum green time for the lights
    public void setMaxGreen(int time) {
        this.maxGreen = time;
    }

    //Increment the vehicle count if a vehicle passes through the intersection box
    public void incrementVehicleCount(List<Vehicle3D> allVehicles) {
        if(intersectionBox != null) {
            for (Vehicle3D vehicle : allVehicles) {
                if (intersectionBox.getBoundsInParent().intersects(vehicle.getBoundsInGrandparent(vehicle.returnCarShape()))) {
                    if(!vehicles.contains(vehicle)) {
                        //Increment the green time up to the max green time
                        //Only increment time if there are lights in the green state
                        if(greenTime < maxGreen) {
                            if(getAllLightState("N") == TrafficLight.LightColor.GREEN ||
                             getAllLightState("S") == TrafficLight.LightColor.GREEN ||
                            getAllLightState("E") == TrafficLight.LightColor.GREEN || 
                            getAllLightState("W") == TrafficLight.LightColor.GREEN) {
                                greenTime++;
                            }
                        }
                        vehicle.setTimeInIntersection(System.currentTimeMillis());
                        vehicleCount++;
                        //System.out.println("Vehicle Count: " + vehicleCount);
                        vehicles.add(vehicle);
                    }
                }
            }
        }
    }

    //Method to check pedestrian collision
    public void checkPedestrianCollision(List<Person3D> allPeople) {
        for (Person3D person : allPeople) {
            for (CollisionBox box : pedCollisionBoxes.get("N")) {
                if (box.isColliding(person.getBoundsInGrandparent(person.returnPersonShape()))) {
                    if (box.getState() != CollisionBox.State.GO && !pedLightChanges.contains("N")) {
                        pedLightChanges.add("N");   
                    }
                    if (box.getState() == CollisionBox.State.GO && !pedCrossingNorth.contains(person)) {
                        pedCrossingNorth.add(person);
                    }
                }
                
            }
            for (CollisionBox box : pedCollisionBoxes.get("S")) {
                if (box.isColliding(person.getBoundsInGrandparent(person.returnPersonShape()))) {
                    if (box.getState() != CollisionBox.State.GO && !pedLightChanges.contains("S")) {
                        pedLightChanges.add("S");
                    }
                    if (box.getState() == CollisionBox.State.GO && !pedCrossingSouth.contains(person)) {
                        pedCrossingSouth.add(person);
                    }
                }
            }
            for (CollisionBox box : pedCollisionBoxes.get("E")) {
                if (box.isColliding(person.getBoundsInGrandparent(person.returnPersonShape()))) {
                    if (box.getState() != CollisionBox.State.GO && !pedLightChanges.contains("E")) {
                        pedLightChanges.add("E");
                    }
                    if (box.getState() == CollisionBox.State.GO && !pedCrossingEast.contains(person)) {
                        pedCrossingEast.add(person);
                    }
                }
            }
            for (CollisionBox box : pedCollisionBoxes.get("W")) {
                if (box.isColliding(person.getBoundsInGrandparent(person.returnPersonShape()))) {
                    if (box.getState() != CollisionBox.State.GO && !pedLightChanges.contains("W")) {
                        pedLightChanges.add("W");
                    }
                    if (box.getState() == CollisionBox.State.GO && !pedCrossingWest.contains(person)) {
                        pedCrossingWest.add(person);
                    }   
                }
            }
        }
        //Remove the Person3D from the list if they are no longer colliding
        for (Person3D person : pedCrossingNorth) {
            if (!pedCollisionBoxes.get("N").get(0).isColliding(person.returnPersonShape().getBoundsInParent())) {
                pedCrossingNorth.remove(person);
            }
        }
        for (Person3D person : pedCrossingSouth) {
            if (!pedCollisionBoxes.get("S").get(0).isColliding(person.returnPersonShape().getBoundsInParent())) {
                pedCrossingSouth.remove(person);
            }
        }
        for (Person3D person : pedCrossingEast) {
            if (!pedCollisionBoxes.get("E").get(0).isColliding(person.returnPersonShape().getBoundsInParent())) {
                pedCrossingEast.remove(person);
            }
        }
        for (Person3D person : pedCrossingWest) {
            if (!pedCollisionBoxes.get("W").get(0).isColliding(person.returnPersonShape().getBoundsInParent())) {
                pedCrossingWest.remove(person);
            }
        }

    }

    public void checkPedestrianCollision3D(List<Person3D> allPeople) {
        for (Person3D person : allPeople) {
            for (CollisionBox box : pedCollisionBoxes.get("N")) {
                if (box.isColliding(person.returnPersonShape().getBoundsInParent())) {
                    if (box.getState() != CollisionBox.State.GO && !pedLightChanges.contains("N")) {
                        pedLightChanges.add("N");
                    }
                    if (box.getState() == CollisionBox.State.GO && !pedCrossingNorth.contains(person)) {
                        pedCrossingNorth.add(person);
                    }
                }

            }
            for (CollisionBox box : pedCollisionBoxes.get("S")) {
                if (box.isColliding(person.returnPersonShape().getBoundsInParent())) {
                    if (box.getState() != CollisionBox.State.GO && !pedLightChanges.contains("S")) {
                        pedLightChanges.add("S");
                    }
                    if (box.getState() == CollisionBox.State.GO && !pedCrossingSouth.contains(person)) {
                        pedCrossingSouth.add(person);
                    }
                }
            }
            for (CollisionBox box : pedCollisionBoxes.get("E")) {
                if (box.isColliding(person.returnPersonShape().getBoundsInParent())) {
                    if (box.getState() != CollisionBox.State.GO && !pedLightChanges.contains("E")) {
                        pedLightChanges.add("E");
                    }
                    if (box.getState() == CollisionBox.State.GO && !pedCrossingEast.contains(person)) {
                        pedCrossingEast.add(person);
                    }
                }
            }
            for (CollisionBox box : pedCollisionBoxes.get("W")) {
                if (box.isColliding(person.returnPersonShape().getBoundsInParent())) {
                    if (box.getState() != CollisionBox.State.GO && !pedLightChanges.contains("W")) {
                        pedLightChanges.add("W");
                    }
                    if (box.getState() == CollisionBox.State.GO && !pedCrossingWest.contains(person)) {
                        pedCrossingWest.add(person);
                    }
                }
            }
        }
        //Remove the Person3D from the list if they are no longer colliding
        for (Person3D person : pedCrossingNorth) {
            if (!pedCollisionBoxes.get("N").get(0).isColliding(person.returnPersonShape().getBoundsInParent())) {
                pedCrossingNorth.remove(person);
            }
        }
        for (Person3D person : pedCrossingSouth) {
            if (!pedCollisionBoxes.get("S").get(0).isColliding(person.returnPersonShape().getBoundsInParent())) {
                pedCrossingSouth.remove(person);
            }
        }
        for (Person3D person : pedCrossingEast) {
            if (!pedCollisionBoxes.get("E").get(0).isColliding(person.returnPersonShape().getBoundsInParent())) {
                pedCrossingEast.remove(person);
            }
        }
        for (Person3D person : pedCrossingWest) {
            if (!pedCollisionBoxes.get("W").get(0).isColliding(person.returnPersonShape().getBoundsInParent())) {
                pedCrossingWest.remove(person);
            }
        }

    }

    //Method to check for vehicle collisions with lane boxes
    //Determines right of way for vehicles
    public void checkLaneCollision(List<Vehicle3D> allVehicles) {
        //Add a vehicle to the lane list if it is in the lane
        for (Vehicle3D vehicle : allVehicles) {
            if(laneCollisionBoxes.get("N").isColliding(vehicle.getBoundsInGrandparent(vehicle.returnCarShape()))){
                trafficLaneNorth.add(vehicle);
            }
            if(laneCollisionBoxes.get("S").isColliding(vehicle.getBoundsInGrandparent(vehicle.returnCarShape()))){
                trafficLaneSouth.add(vehicle);
            }
            if(laneCollisionBoxes.get("E").isColliding(vehicle.getBoundsInGrandparent(vehicle.returnCarShape()))){
                trafficLaneEast.add(vehicle);
            }
            if(laneCollisionBoxes.get("W").isColliding(vehicle.getBoundsInGrandparent(vehicle.returnCarShape()))){
                trafficLaneWest.add(vehicle);
            }
        }
        //Check each lane for left turning vehicles
        //If a vehicle is in the lane, set the state of the opposing lane to stop
        /*
        for(Vehicle3D vehicle : trafficLaneNorth){
            if(vehicle.getTurnDirection() == Vehicle3D.TurnDirection.LEFT){
                laneCollisionBoxes.get("S").setState(CollisionBox.State.STOP);
            }
        }
        for(Vehicle3D vehicle : trafficLaneSouth){
            if(vehicle.getTurnDirection() == Vehicle3D.TurnDirection.LEFT){
                laneCollisionBoxes.get("N").setState(CollisionBox.State.STOP);
            }
        }
        for(Vehicle3D vehicle : trafficLaneEast){
            if(vehicle.getTurnDirection() == Vehicle3D.TurnDirection.LEFT){
                laneCollisionBoxes.get("W").setState(CollisionBox.State.STOP);
            }
        }
        for(Vehicle3D vehicle : trafficLaneWest){
            if(vehicle.getTurnDirection() == Vehicle3D.TurnDirection.LEFT){
                laneCollisionBoxes.get("E").setState(CollisionBox.State.STOP);
            }
        }
        */
    }

    //Method to activate the pedestrian lights
    public void changePedestrianLight(String location, PedestrianLight.LightColor state) {
        Pane pedestrianLight = pedestrianLights.get(location);
        //Get the light attached to the pane and change the color
        PedestrianLight pedestrianLightData = (PedestrianLight) pedestrianLight.getUserData();
        switch (state) {
            case RED:
                pedestrianLightData.setRedHand();
                break;
            case WALKING:
                pedestrianLightData.setWalking();
                break;
        }
    }

    
    /**
     * Changes the state for a light at the intersection
     * @param location
     * @param color
     * @param type
     */
    private void changeLightState(String location, TrafficLight.type type, String color) {
        //Get the light pane
        Pane trafficLight = trafficLights.get(location).stream().filter(light -> ((TrafficLight)light.getUserData()).getType() == type).findFirst().orElse(null);
        
        //Get the light attached to the pane and change the color
        if(trafficLight == null){
            return;
        }
        TrafficLight trafficLightData = (TrafficLight) trafficLight.getUserData();
        switch (color) {
            case "red":
                trafficLightData.setRed();
                trafficLightData.getCollisionBox().setState(CollisionBox.State.STOP);
                break;
            case "yellow":
                trafficLightData.setYellow();
                break;
            case "green":
                trafficLightData.setGreen();
                trafficLightData.getCollisionBox().setState(CollisionBox.State.GO);
                break;
            case "green-left":
                trafficLightData.setGreenLeftTurnArrow();
                trafficLightData.getCollisionBox().setState(CollisionBox.State.LEFT);
                break;
            case "green-right":
                trafficLightData.setGreenRightTurnArrow();
                trafficLightData.getCollisionBox().setState(CollisionBox.State.RIGHT);
                break;
            case "yellow-left":
                trafficLightData.setYellowLeftTurnArrow();
                break;
            case "yellow-right":
                trafficLightData.setYellowRightTurnArrow();
                break;
            default:
                break;
        }
    }

    /**
     * Changes the state for a light at the intersection
     * @param location
     * @param color
     */
    private void changeAllLightState(String location, String color) {
        //Get all light panes at the location
        List<Pane> trafficLightList = trafficLights.get(location);
        
        //Get the light attached to the pane and change the color
        for(Pane trafficLight : trafficLightList) {
            TrafficLight trafficLightData = (TrafficLight) trafficLight.getUserData();
            switch (color) {
                case "red":
                    trafficLightData.setRed();
                    trafficLightData.getCollisionBox().setState(CollisionBox.State.STOP);
                    break;
                case "yellow":
                    trafficLightData.setYellow();
                    break;
                case "green":
                    trafficLightData.setGreen();
                    trafficLightData.getCollisionBox().setState(CollisionBox.State.GO);
                    break;
                default:
                    break;
            }
        }
        
    }
    //Get the light state
    private TrafficLight.LightColor getLightState(String location, TrafficLight.type type) {
        //Get the light pane
        Pane trafficLight = trafficLights.get(location).stream().filter(light -> ((TrafficLight)light.getUserData()).getType() == type).findFirst().orElse(null);
        //Get the light attached to the pane and change the color
        if(trafficLight == null){
            return null;
        }
        TrafficLight trafficLightData = (TrafficLight) trafficLight.getUserData();
        return trafficLightData.getLightColor();
    }
     //Get the light state
     private TrafficLight.LightColor getAllLightState(String location) {
        //Get the light pane
        TrafficLight.LightColor lightColor = null;
        for(Pane trafficLight : trafficLights.get(location)) {
            TrafficLight trafficLightData = (TrafficLight) trafficLight.getUserData();
            lightColor =  trafficLightData.getLightColor();
        }
        //Get the light attached to the pane and change the color
        if(lightColor == null){
            return null;
        }
        return lightColor;
    }

    //Send vehicle count to the system controller
    public void sendData() {
        this.maxGreen = SystemController.updateMaxGreenTime(this.vehicleCount, id );
        String message = "Traffic Light: " + getId()+ "\nCar Counter: " + vehicleCount +
                "\n Message Sent to Central Controller";
        TrafficScene.setData(message,id);
        this.vehicleCount = 0;
    }

    //Animation cycle for the lights
    //Uses an animation timer to change light colors at the intersection
    //Counts time in milliseconds
    public void startCycle() {
        AnimationTimer timer = new AnimationTimer() {
            private direction dir = direction.NS;
            private int time = cycleTime;
            private int yellowTime = yellow;
            private int leftTurnTime = 40;
            private Duration lastUpdate = Duration.of(0, ChronoUnit.NANOS);

            /**
             *
             * @param now
             *            The timestamp of the current frame given in nanoseconds. This
             *            value will be the same for all {@code AnimationTimers} called
             *            during one frame.
             */
            @Override
            public void handle(long now) {
                Duration nowDur = Duration.of(now, ChronoUnit.NANOS);
                if (nowDur.minus(lastUpdate).toMillis() >= 300) {
                    //Update last update time
                    lastUpdate = nowDur;

                    for (Vehicle3D vehicle : vehicles) {
                        if (!intersectionBox.getBoundsInParent().intersects(vehicle.getBoundsInGrandparent(vehicle.returnCarShape()))) {
                            vehicles.remove(vehicle);
                        }

                        //Send a message about a current accident
                        if ((System.currentTimeMillis() - vehicle.getTimeInIntersection() >= 2500)) {
                            TrafficScene.setData("Accident at intersection: ", id);

                            //Remove the cars that are in the intersection
                            vehicles.remove(vehicle);
                            TrafficScene.removeFromRoot(vehicle);
                        }

                    }
                    //Update min green time to 30 seconds if the pedestrian light contains an item
                    if(pedLightChanges.size() > 0){
                        minGreen = 30;
                    }
                    else{
                        minGreen = 15;
                    }
                    //If bus light, set green time to 0 if a pedestrian light contains an item
                    
                    if(type == lightType.BUS){
                        leftTurnTime = 0;
                        //System.out.println(busApproaching);
                        if(busApproaching || pedLightChanges.size() > 0 || !pedCrossingNorth.isEmpty() || !pedCrossingSouth.isEmpty() || !pedCrossingEast.isEmpty() || !pedCrossingWest.isEmpty()){
                            greenTime = 0;
                            yellowTime = 0;
                        }
                    }
                    //Change the light color for perpendicular lights
                    //Identified by the location of the light
                    //North and South lights
                    if(time <= 0){
                       
                        //Reset the time
                        time = cycleTime;
                        //Send traffic data to the system controller
                        sendData();
                    }

                    //Change to the left turn arrow
                    if(leftTurnTime > 0){
                        if(dir == direction.NS && pedCrossingNorth.isEmpty() && pedCrossingSouth.isEmpty() && vehicles.isEmpty()){
                            //Set north box to left and east box to right for five seconds
                            if(leftTurnTime >= 30) {
                                changeLightState("N", TrafficLight.type.LEFT, "green-left");
                                changeLightState("E", TrafficLight.type.RIGHT, "green-right");
                                changeLightState("E", TrafficLight.type.SINGLE, "green-right");
                            }
                            //Set to yellow for 5 seconds
                            else if(leftTurnTime < 30 && leftTurnTime > 20){
                                changeLightState("N", TrafficLight.type.LEFT, "yellow-left");
                                changeLightState("E", TrafficLight.type.RIGHT, "yellow-right");
                                changeLightState("E", TrafficLight.type.SINGLE, "yellow-right");
                            }
                            //Allow left turn at south light and right turn at west light
                            else if(leftTurnTime <= 20 && leftTurnTime > 10){
                                changeLightState("N", TrafficLight.type.LEFT, "red");
                                changeLightState("E", TrafficLight.type.RIGHT, "red");
                                changeLightState("E", TrafficLight.type.SINGLE, "red");
                                changeLightState("S", TrafficLight.type.LEFT, "green-left");
                                changeLightState("W", TrafficLight.type.RIGHT, "green-right");
                                changeLightState("W", TrafficLight.type.SINGLE, "green-right");
                            }
                            //Set to yellow for 4 seconds
                            else if(leftTurnTime <= 10 && leftTurnTime > 1){
                                changeLightState("S", TrafficLight.type.LEFT, "yellow-left");
                                changeLightState("W", TrafficLight.type.RIGHT, "yellow-right");
                                changeLightState("W", TrafficLight.type.SINGLE, "yellow-right");
                            }
                            //Set to red for 1 second
                            else if(leftTurnTime == 1){
                                changeLightState("S", TrafficLight.type.LEFT, "red");
                                changeLightState("W", TrafficLight.type.RIGHT, "red");
                                changeLightState("W", TrafficLight.type.SINGLE, "red");
                            }
                            
                        }
                        else if(dir == direction.EW && pedCrossingNorth.isEmpty() && pedCrossingSouth.isEmpty() && vehicles.isEmpty()){
                            //Set east box to left and north box to right for five seconds
                            if(leftTurnTime >= 30) {
                                changeLightState("E", TrafficLight.type.LEFT, "green-left");
                                changeLightState("E", TrafficLight.type.SINGLE, "green-left");
                                changeLightState("N", TrafficLight.type.RIGHT, "green-right");
                            }
                            //Set to yellow for 5 seconds
                            else if(leftTurnTime < 30 && leftTurnTime > 20){
                                changeLightState("E", TrafficLight.type.LEFT, "yellow-left");
                                changeLightState("E", TrafficLight.type.SINGLE, "yellow-left");
                                changeLightState("N", TrafficLight.type.RIGHT, "yellow-right");
                            }
                            //Allow left turn at west light and right turn at south light
                            else if(leftTurnTime <= 20 && leftTurnTime > 10){
                                changeLightState("E", TrafficLight.type.LEFT, "red");
                                changeLightState("E", TrafficLight.type.SINGLE, "red");
                                changeLightState("N", TrafficLight.type.RIGHT, "red");
                                changeLightState("W", TrafficLight.type.LEFT, "green-left");
                                changeLightState("W", TrafficLight.type.SINGLE, "green-left");
                                changeLightState("S", TrafficLight.type.RIGHT, "green-right");
                            }
                            //Set to yellow for 4 seconds
                            else if(leftTurnTime <= 10 && leftTurnTime > 1){
                                changeLightState("W", TrafficLight.type.LEFT, "yellow-left");
                                changeLightState("W", TrafficLight.type.SINGLE, "yellow-left");
                                changeLightState("S", TrafficLight.type.RIGHT, "yellow-right");
                            }
                            //Set to red for 1 second
                            else if(leftTurnTime == 1){
                                changeLightState("W", TrafficLight.type.LEFT, "red");
                                changeLightState("W", TrafficLight.type.SINGLE, "red");
                                changeLightState("S", TrafficLight.type.RIGHT, "red");
                            }
                        }
                        leftTurnTime--;
                    }
                    //Change the light color for the current direction
                    else if (greenTime > 0) {
                        if(dir == direction.NS && pedCrossingNorth.isEmpty() && pedCrossingSouth.isEmpty() && vehicles.isEmpty()){
                            changeAllLightState("N", "green");
                            changeAllLightState("S", "green");
                        }
                        else if(dir == direction.EW && pedCrossingEast.isEmpty() && pedCrossingWest.isEmpty() && vehicles.isEmpty()){
                            changeAllLightState("E", "green");
                            changeAllLightState("W", "green");
                        }
                        //Decrement the green time
                        greenTime--;
                        //Increment green time if bus lane and no pedestrians
                        if(type == lightType.BUS){
                            if(!busApproaching && pedLightChanges.size() == 0){
                                greenTime++;
                            }
                        }
                    } else if (yellowTime > 0) {
                        if(dir == direction.NS){
                            changeAllLightState("N", "yellow");
                            changeAllLightState("S", "yellow");
                            //Set the pedestrian collision boxes to stop
                            for(CollisionBox box : pedCollisionBoxes.get("E")){
                                box.setState(CollisionBox.State.STOP);
                            }
                            for(CollisionBox box : pedCollisionBoxes.get("W")){
                                box.setState(CollisionBox.State.STOP);
                            }
                        }
                        else if(dir == direction.EW){
                            changeAllLightState("E", "yellow");
                            changeAllLightState("W", "yellow");
                            //Set the pedestrian collision boxes to stop
                            for(CollisionBox box : pedCollisionBoxes.get("N")){
                                box.setState(CollisionBox.State.STOP);
                            }
                            for(CollisionBox box : pedCollisionBoxes.get("S")){
                                box.setState(CollisionBox.State.STOP);
                            }
                        }
                        yellowTime--;
                        //Red light logic
                    } else {
                        for(CollisionBox box : pedCollisionBoxes.get("E")){
                            box.setState(CollisionBox.State.STOP);
                        }
                        for(CollisionBox box : pedCollisionBoxes.get("W")){
                            box.setState(CollisionBox.State.STOP);
                        }
                        for(CollisionBox box : pedCollisionBoxes.get("N")){
                            box.setState(CollisionBox.State.STOP);
                        }
                        for(CollisionBox box : pedCollisionBoxes.get("S")){
                            box.setState(CollisionBox.State.STOP);
                        }
                        if(dir == direction.NS){
                            changeAllLightState("N", "red");
                            changeAllLightState("S", "red");
                            
                            //Change the east and west pedestrian lights to walk if the pedestrian light contains an item
                            if(pedLightChanges.contains("S")){
                                //changePedestrianLight("S", PedestrianLight.LightColor.WALKING);
                                //Set the collision boxes to go
                                for(CollisionBox box : pedCollisionBoxes.get("S")){
                                    //System.out.println("Pedestrian Light changing");
                                    box.setState(CollisionBox.State.GO);
                                }
                                //Remove the item from the list
                                pedLightChanges.remove("S");
                            }
                            if(pedLightChanges.contains("N")){
                               // changePedestrianLight("N", PedestrianLight.LightColor.WALKING);
                                //Set the collision boxes to go
                                for(CollisionBox box : pedCollisionBoxes.get("N")){
                                    box.setState(CollisionBox.State.GO);
                                }
                                //Remove the item from the list
                                pedLightChanges.remove("N");
                            }
                            
                            //Set East and West pedestrian lights to stop
                            //changePedestrianLight("E", PedestrianLight.LightColor.RED);
                            //changePedestrianLight("W", PedestrianLight.LightColor.RED);
                           
                            //Transition to the next direction
                            if(type == LightController.lightType.STANDARD){
                                dir = direction.EW;
                            }
                        }
                        else if(dir == direction.EW){
                            changeAllLightState("E", "red");
                            changeAllLightState("W", "red");
                            //Change the east and west pedestrian lights to walk if the pedestrian light contains an item
                            if(pedLightChanges.contains("E")){
                                //changePedestrianLight("E", PedestrianLight.LightColor.WALKING);
                                //Set the collision boxes to go
                                for(CollisionBox box : pedCollisionBoxes.get("E")){
                                    box.setState(CollisionBox.State.GO);
                                }
                                //Remove the item from the list
                                pedLightChanges.remove("E");
                            }
                            if(pedLightChanges.contains("W")){
                               // changePedestrianLight("W", PedestrianLight.LightColor.WALKING);
                                //Set the collision boxes to go
                                for(CollisionBox box : pedCollisionBoxes.get("W")){
                                    box.setState(CollisionBox.State.GO);
                                }
                                //Remove the item from the list
                                pedLightChanges.remove("W");
                            }
                            
                            //Set North and South pedestrian lights to stop
                            //changePedestrianLight("N", PedestrianLight.LightColor.RED);
                            //changePedestrianLight("S", PedestrianLight.LightColor.RED);
                            
                            dir = direction.NS;
                        }
                        //Set the green time for the next direction
                        greenTime = minGreen;
                        yellowTime = yellow;
                        leftTurnTime = 40;
                    }
                    time--;

                }
            }
        };
        timer.start();
    }

    public boolean getBusApproaching(){
        return busApproaching;
    }

    public int getCarCounter(){
        return vehicleCount;
    }
}