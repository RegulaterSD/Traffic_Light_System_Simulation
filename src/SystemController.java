import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.scene.layout.Pane;

/**
 * Controller to communicate to individual light controllers
 */
public class SystemController {
    /**
     * Store the coordinates of the lights for each intersection
     * Pass to each light controller the coordinate of the lights
     * Object[] = {String direction, int x, int y}
     */
    private static final HashMap<Integer, ArrayList<Object[]>> lightCoords = new HashMap<>(){{
        put(1, new ArrayList<Object[]>(){{
            add(new Object[]{"W", TrafficLight.type.SINGLE, 256, 108});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 322, 93});
            add(new Object[]{"N", TrafficLight.type.LEFT, 302, 93});
            add(new Object[]{"E", TrafficLight.type.SINGLE, 337, 126});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 264, 140});
            add(new Object[]{"S", TrafficLight.type.LEFT, 284, 140});
        }});
        put(2, new ArrayList<Object[]>(){{
            add(new Object[]{"W", TrafficLight.type.SINGLE, 594, 108});
            add(new Object[]{"N", TrafficLight.type.LEFT ,636, 92});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 656, 92});
            add(new Object[]{"E", TrafficLight.type.SINGLE, 675, 127});
            add(new Object[]{"S", TrafficLight.type.LEFT, 622, 141});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 602, 141});
        }});
        put(3, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.RIGHT, 319, 342});
            add(new Object[]{"N", TrafficLight.type.LEFT, 299, 342});
            add(new Object[]{"E", TrafficLight.type.RIGHT, 340, 448});
            add(new Object[]{"E", TrafficLight.type.LEFT, 340, 408});
            add(new Object[]{"E", TrafficLight.type.STRAIGHT, 340, 428});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 268, 459});
            add(new Object[]{"S", TrafficLight.type.LEFT, 288, 459});
            add(new Object[]{"W", TrafficLight.type.RIGHT, 255, 355});
            add(new Object[]{"W", TrafficLight.type.LEFT, 255, 395});
            add(new Object[]{"W", TrafficLight.type.STRAIGHT, 255, 375});
        }});
        put(4, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.RIGHT, 661, 342});
            add(new Object[]{"N", TrafficLight.type.LEFT, 641, 342});
            add(new Object[]{"E", TrafficLight.type.STRAIGHT, 675, 425});
            add(new Object[]{"E", TrafficLight.type.RIGHT, 675, 445});
            add(new Object[]{"E", TrafficLight.type.LEFT, 675, 405});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 602, 460});
            add(new Object[]{"S", TrafficLight.type.LEFT, 622, 460});
            add(new Object[]{"W", TrafficLight.type.LEFT, 592, 395});
            add(new Object[]{"W", TrafficLight.type.RIGHT, 592, 355});
            add(new Object[]{"W", TrafficLight.type.STRAIGHT, 592, 375});
        }});
        put(5, new ArrayList<Object[]>(){{
            add(new Object[]{"S", TrafficLight.type.LEFT, 284, 692});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 264, 692});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 320, 643});
            add(new Object[]{"N", TrafficLight.type.LEFT, 300, 643});
            add(new Object[]{"W", TrafficLight.type.SINGLE, 257, 638});
            add(new Object[]{"E", TrafficLight.type.SINGLE, 337, 658});
        }});
        put(6, new ArrayList<Object[]>(){{
            add(new Object[]{"S", TrafficLight.type.LEFT, 620, 691});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 600, 691});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 658, 643});
            add(new Object[]{"N", TrafficLight.type.LEFT, 638, 643});
            add(new Object[]{"W", TrafficLight.type.SINGLE, 595, 638});
            add(new Object[]{"E", TrafficLight.type.SINGLE, 674, 658});
        }});
        //Coordinates for lights at bus intersections
        put(7, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.BUS, 322, 528});
            add(new Object[]{"N", TrafficLight.type.BUS_ALT, 302, 528});
            add(new Object[]{"S", TrafficLight.type.BUS, 284, 589});
            add(new Object[]{"S", TrafficLight.type.BUS_ALT, 264, 589});
        }});
        put(8, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.BUS, 636, 528});
            add(new Object[]{"N", TrafficLight.type.BUS_ALT, 656, 528});
            add(new Object[]{"S", TrafficLight.type.BUS, 602, 589});
            add(new Object[]{"S", TrafficLight.type.BUS_ALT, 622, 589});
        }});
    }};

    //HashMap to store the collision box coordinates for each intersection
    //Object[] = {String associated light, double xCenter, double yCenter}
    private final HashMap<Integer, ArrayList<Object[]>> lightCollisionCoords = new HashMap<>(){{
        put(1, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.RIGHT, 322, 155});
            add(new Object[]{"N", TrafficLight.type.LEFT, 302, 156});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 264, 86});
            add(new Object[]{"S", TrafficLight.type.LEFT, 284, 86});
            add(new Object[]{"E", TrafficLight.type.SINGLE, 242, 130});
            add(new Object[]{"W", TrafficLight.type.SINGLE, 342, 111});
           
        }});
        put(2, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.LEFT, 636, 156});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 656, 156});
            add(new Object[]{"S", TrafficLight.type.LEFT, 622, 86});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 602, 86});
            add(new Object[]{"E", TrafficLight.type.SINGLE, 576, 130});
            add(new Object[]{"W", TrafficLight.type.SINGLE, 686, 111});
        }});
        put(3, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.LEFT, 299, 467});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 319, 467});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 268, 328});
            add(new Object[]{"S", TrafficLight.type.LEFT, 284, 328});
            add(new Object[]{"E", TrafficLight.type.RIGHT, 234, 444});
            add(new Object[]{"E", TrafficLight.type.STRAIGHT, 234, 428});
            add(new Object[]{"E", TrafficLight.type.LEFT, 234, 408});
            add(new Object[]{"W", TrafficLight.type.RIGHT, 345, 355});
            add(new Object[]{"W", TrafficLight.type.STRAIGHT, 345, 375});
            add(new Object[]{"W", TrafficLight.type.LEFT, 345, 395});

        }});
        put(4, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.LEFT, 641, 469});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 661, 469});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 602, 330});
            add(new Object[]{"S", TrafficLight.type.LEFT, 622, 330});
            add(new Object[]{"E", TrafficLight.type.LEFT, 570, 405});
            add(new Object[]{"E", TrafficLight.type.STRAIGHT, 570, 425});
            add(new Object[]{"E", TrafficLight.type.RIGHT, 570, 445});
            add(new Object[]{"W", TrafficLight.type.RIGHT, 682, 355});
            add(new Object[]{"W", TrafficLight.type.STRAIGHT, 682, 375});
            add(new Object[]{"W", TrafficLight.type.LEFT, 682, 395});

        }});
        put(5, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.LEFT, 300, 699});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 320, 699});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 264, 630});
            add(new Object[]{"S", TrafficLight.type.LEFT, 284, 630});
            add(new Object[]{"E", TrafficLight.type.SINGLE, 237, 674});
            add(new Object[]{"W", TrafficLight.type.SINGLE, 346, 653});
        }});
        put(6, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.LEFT, 638, 699});
            add(new Object[]{"N", TrafficLight.type.RIGHT, 658, 699});
            add(new Object[]{"S", TrafficLight.type.RIGHT, 600, 630});
            add(new Object[]{"S", TrafficLight.type.LEFT, 620, 630});
            add(new Object[]{"E", TrafficLight.type.SINGLE, 575, 674});
            add(new Object[]{"W", TrafficLight.type.SINGLE, 685, 653});
        }});
        //Busses
        put(7, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.BUS, 322, 589});
            add(new Object[]{"N", TrafficLight.type.BUS_ALT, 302, 589});
            add(new Object[]{"S", TrafficLight.type.BUS, 284, 528});
            add(new Object[]{"S", TrafficLight.type.BUS_ALT, 264, 528});
            add(new Object[]{"B", TrafficLight.type.STRAIGHT, 250, 548});
            add(new Object[]{"B", TrafficLight.type.STRAIGHT, 30, 568});
        }});
        put(8, new ArrayList<Object[]>(){{
            add(new Object[]{"N", TrafficLight.type.BUS, 636, 589});
            add(new Object[]{"N", TrafficLight.type.BUS_ALT, 656, 589});
            add(new Object[]{"S", TrafficLight.type.BUS, 602, 528});
            add(new Object[]{"S", TrafficLight.type.BUS_ALT, 622, 528});
            add(new Object[]{"B", TrafficLight.type.STRAIGHT, 602, 548});
            add(new Object[]{"B", TrafficLight.type.STRAIGHT, 372, 568});
        }});
    }};
    //HashMap to store the pedestrian light coordinates for each intersection
    //Object[] = {String associated light, double xCenter, double yCenter}
    private final HashMap<Integer, ArrayList<Object[]>> pedestrianLights = new HashMap<>(){{
        put(1, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 322, 155});
            add(new Object[]{"S", 264, 86});
            add(new Object[]{"E", 242, 130});
            add(new Object[]{"W", 342, 111});
        }});
        put(2, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 636, 156});
            add(new Object[]{"S", 622, 86});
            add(new Object[]{"E", 576, 130});
            add(new Object[]{"W", 686, 111});
        }});
        put(3, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 299, 467});
            add(new Object[]{"S", 268, 328});
            add(new Object[]{"E", 234, 408});
            add(new Object[]{"W", 345, 355});
        }});
        put(4, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 641, 469});
            add(new Object[]{"S", 602, 330});
            add(new Object[]{"E", 570, 410});
            add(new Object[]{"W", 682, 355});
        }});
        put(5, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 300, 699});
            add(new Object[]{"S", 264, 630});
            add(new Object[]{"E", 237, 674});
            add(new Object[]{"W", 346, 653});
        }});
        put(6, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 638, 699});
            add(new Object[]{"S", 600, 630});
            add(new Object[]{"E", 575, 674});
            add(new Object[]{"W", 685, 653});
        }});
        //Pedestrian lights for busses
        put(7, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 322, 589});
            add(new Object[]{"S", 284, 528});
        }});
        put(8, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 636, 589});
            add(new Object[]{"S", 602, 528});
        }});
    }};
    //HashMap to store the pedestrian light collision box coordinates for each intersection
    //Object[] = {String associated light, double xCenter, double yCenter, double width, double height}
    private final HashMap<Integer, ArrayList<Object[]>> pedCollisionCoords = new HashMap<>(){{
        put(1, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 254, 97, 76, 1});
            //add(new Object[]{"N", 330, 97});
            add(new Object[]{"S", 257, 150, 76, 1});
            //add(new Object[]{"S", 328, 150});
            add(new Object[]{"E", 340, 106, 1, 30});
            //add(new Object[]{"E", 340, 136});
            add(new Object[]{"W", 241, 106, 1, 30});
            //add(new Object[]{"W", 241, 139});
        }});
        put(2, new ArrayList<Object[]>(){{
            //add(new Object[]{"N", 667, 92});
            add(new Object[]{"N", 595, 92, 76, 1});
            //add(new Object[]{"S", 667, 150});
            add(new Object[]{"S", 595, 150, 76, 1});
            add(new Object[]{"E", 678, 106, 1, 30});
            //add(new Object[]{"E", 678, 136});
            add(new Object[]{"W", 586, 106, 1, 30});
            //add(new Object[]{"W", 586, 139});
        }});
        put(3, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 255, 340, 70, 1});
           // add(new Object[]{"N", 325, 340});
            add(new Object[]{"S", 254, 465, 70, 1});
            //add(new Object[]{"S", 330, 465});
            add(new Object[]{"E", 337, 353, 1, 103});
            //add(new Object[]{"E", 337, 459});
            add(new Object[]{"W", 241, 353, 1, 103});
            //add(new Object[]{"W", 241, 454});
        }});
        put(4, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 595, 340, 76, 1});
            //add(new Object[]{"N", 666, 340});
            //add(new Object[]{"S", 666, 465});
            add(new Object[]{"S", 595, 465, 76, 1});
            add(new Object[]{"E", 679, 350, 1, 109});
            //add(new Object[]{"E", 679, 459});
            add(new Object[]{"W", 583, 350, 1, 109});
            //add(new Object[]{"W", 583, 459});
        }});
        put(5, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 254, 638, 76, 1});
            //add(new Object[]{"N", 330, 638});
            add(new Object[]{"S", 257, 690, 76, 1});
            //add(new Object[]{"S", 328, 690});
            add(new Object[]{"E", 340, 645, 1, 37});
            //add(new Object[]{"E", 340, 682});
            add(new Object[]{"W", 241, 645, 1, 37});
            //add(new Object[]{"W", 241, 682});
        }});
        put(6, new ArrayList<Object[]>(){{
            //add(new Object[]{"N", 667, 638});
            add(new Object[]{"N", 595, 638, 76, 1});
            //add(new Object[]{"S", 667, 690});
            add(new Object[]{"S", 595, 690, 76, 1});
            add(new Object[]{"E", 678, 645, 1, 37});
            //add(new Object[]{"E", 678, 682});
            add(new Object[]{"W", 586, 645, 1, 37});
            //add(new Object[]{"W", 586, 682});
        }});
        //Pedestrian lights for busses
        put(7, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 254, 530, 76, 1});
            //add(new Object[]{"N", 330, 530});
            add(new Object[]{"S", 257, 590, 76, 1});
            //add(new Object[]{"S", 328, 590});
            add(new Object[]{"E", 340, 539, 1, 39});
            //add(new Object[]{"E", 340, 578});
            add(new Object[]{"W", 241, 539, 1, 39});
            //add(new Object[]{"W", 241, 578});
        }});
        put(8, new ArrayList<Object[]>(){{
            //add(new Object[]{"N", 667, 530});
            add(new Object[]{"N", 595, 530, 76, 1});
            //add(new Object[]{"S", 667, 590});
            add(new Object[]{"S", 595, 590, 76, 1});
            add(new Object[]{"E", 678, 539, 1, 39});
            //add(new Object[]{"E", 678, 578});
            add(new Object[]{"W", 586, 539, 1, 39});
            //add(new Object[]{"W", 586, 578});
        }});
    }};
    //HashMap to store the coordinates for the lane collision boxes
    private final HashMap<Integer, ArrayList<Object[]>> laneCollisionCoords = new HashMap<>(){{
        put(1, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 258, 21, 30, 70});
            add(new Object[]{"S", 296, 150, 30, 150});
            add(new Object[]{"E", 343, 106, 80, 15});
            add(new Object[]{"W", 41, 122, 215, 15});
        }});
        put(2, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 597, 21, 30, 70});
            add(new Object[]{"S", 632, 150, 30, 150});
            add(new Object[]{"E", 675, 106, 215, 15});
            add(new Object[]{"W", 484, 124, 100, 15});
        }});
        put(3, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 260, 203, 30, 130});
            add(new Object[]{"S", 295, 465, 30, 60});
            add(new Object[]{"E", 340, 350, 215, 40});
            add(new Object[]{"W", 41, 408, 215, 40});
        }});
        put(4, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 596, 203, 30, 130});
            add(new Object[]{"S", 632, 470, 30, 60});
            add(new Object[]{"E", 682, 351, 215, 40});
            add(new Object[]{"W", 380, 404, 215, 40});
        }});
        put(5, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 259, 584, 30, 60});
            add(new Object[]{"S", 295, 692, 30, 60});
            add(new Object[]{"E", 338, 649, 80, 15});
            add(new Object[]{"W", 41, 648, 215, 15});
        }});
        put(6, new ArrayList<Object[]>(){{
            add(new Object[]{"N", 596, 584, 30, 60});
            add(new Object[]{"S", 632, 692, 30, 60});
            add(new Object[]{"E", 677, 645, 216, 15});
            add(new Object[]{"W", 475, 666, 110, 15});
        }});
    }};

    //HashMap to store the light controllers
    private static final HashMap<Integer, LightController> lightControllers = new HashMap<>();
    //Set final value for the number of intersections
    private final int INTERSECTIONS = 6;
    //Minimum time for a light to be green
    private final static int MIN_GREEN = 15;

    /**
     * Constructor
     */
    public SystemController() {
        spawnLights();
        intersectCollisionBoxes();
    }

    /**
     * Create the lights for each intersection
     */
    private void spawnLights() {
        for (int i = 1; i <= INTERSECTIONS; i++) {
            lightControllers.put(i, new LightController(i, LightController.lightType.STANDARD, lightCoords.get(i), lightCollisionCoords.get(i), pedestrianLights.get(i), pedCollisionCoords.get(i), laneCollisionCoords.get(i)));
        }
        //Create the bus lights, last two elements in the HashMap
        lightControllers.put(7, new LightController(7, LightController.lightType.BUS, lightCoords.get(7), lightCollisionCoords.get(7), pedestrianLights.get(7), pedCollisionCoords.get(7)));
        lightControllers.put(8, new LightController(8, LightController.lightType.BUS, lightCoords.get(8), lightCollisionCoords.get(8), pedestrianLights.get(8), pedCollisionCoords.get(8)));
    }

    /**
     * Add lights to the scene
     * @param root
     */
    public void addLights(Pane root) {
        if (root != null) {
            for (LightController lightController : lightControllers.values()) {
                lightController.addLights(root);
                lightController.startCycle();
            }
        }
    }

    private void intersectCollisionBoxes() {
        //Create a collision box based on the key of the light controller
        lightControllers.get(1).createIntersectionBox(265, 107, 60, 30);
        lightControllers.get(2).createIntersectionBox(603, 107, 60, 30);
        lightControllers.get(3).createIntersectionBox(263, 358, 60, 90);
        lightControllers.get(4).createIntersectionBox(603, 358, 60, 90);
        lightControllers.get(5).createIntersectionBox(262, 652, 60, 30);
        lightControllers.get(6).createIntersectionBox(601, 652, 60, 30);
    }

    //Check for collisions at intersections
    public void checkVehicleCrossing(List<Vehicle3D> vehicles) {
        for (LightController lightController : lightControllers.values()) {
            lightController.incrementVehicleCount(vehicles);
            TrafficScene.vehicleCounters(lightController.getCarCounter(), lightController.getId());
        }
    }

    //Check for collisions with pedestrians
    public void checkPedestrianCrossing(List<Person3D> pedestrians) {
        for (LightController lightController : lightControllers.values()) {
            lightController.checkPedestrianCollision(pedestrians);
        }
    }

    //Check for collisions with busses
    public void checkBusCrossing(List<Bus3D> busses) {
        for (LightController lightController : lightControllers.values()) {
            lightController.checkBusCollision(busses);
            if(lightController.getBusApproaching() == true) {
                TrafficScene.setMessage("Bus approaching intersection!", lightController.getId());
            }
        }
    }

    protected void makeCollisionBoxesInvisible(int opacity){
        for (LightController lightController : lightControllers.values()){
            for (CollisionBox collisionBox : lightController.getBusCollisionBoxes()){
                collisionBox.setOpacity(opacity);
            }
            for (LightController lightController1 : lightControllers.values()){
                if (lightController1.returnIntersectionBox() != null){
                    lightController1.returnIntersectionBox().setOpacity(opacity);
                }
            }
            for (CollisionBox collisionBox1 : lightController.getLaneCollisionBoxes()){
                collisionBox1.setOpacity(opacity);
            }
        }
    }
    //Get the light collision boxes
    public ArrayList<CollisionBox> getLightCollisionBoxes() {
        ArrayList<CollisionBox> collisionBoxes = new ArrayList<>();
        for (LightController lightController : lightControllers.values()) {
            collisionBoxes.addAll(lightController.getLightCollisionBoxes());
        }
        return collisionBoxes;
    }

    //Get the pedestrian collision boxes
    public ArrayList<CollisionBox> getPedestrianCollisionBoxes() {
        ArrayList<CollisionBox> collisionBoxes = new ArrayList<>();
        for (LightController lightController : lightControllers.values()) {
            collisionBoxes.addAll(lightController.getPedCollisionBoxes());
        }
        return collisionBoxes;
    }

    //Receives the number of vehicles from a light controller as input and sends an updated maximum green time as output
    public static int updateMaxGreenTime(int numVehicles, int id) {
        //Calculate the maximum green with an upper bound of 80 seconds
        int time = (int)Math.min(80, Math.floor(MIN_GREEN + (numVehicles / 3.75)));
        systemMessages(numVehicles,id, time);
        return time;
    }
    public static void systemMessages(int numVehicles, int id, int time){
        int x = 0;
        int y = 0;
        switch (id){
            case 1: x = 265;  y = 107; break;
            case 2: x = 603; y = 107; break;
            case 3: x = 263; y = 358; break;
            case 4: x = 603; y = 358; break;
            case 5: x = 262; y = 652; break;
            case 6: x = 601; y = 652;  break;
        }
        String lightMessage = "Traffic Light: " + id + "\nMessage Received \nAdjusting green time for: "
                + numVehicles + " cars. \nNew Time: " + time + " seconds";
        TrafficScene.setSystemData(lightMessage, x, y, id);
    }
}
