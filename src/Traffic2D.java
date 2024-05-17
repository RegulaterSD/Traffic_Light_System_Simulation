import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class renders 2D space and images for implementation of
 * Traffic Light Controller, Pedestrian Light Controller, Bus Light Controller
 */
public class Traffic2D extends Application {
    private final Boolean getCoordinates = false;
    private List<Vehicle> vehicleCollidables = new ArrayList<>();
    private List<Vehicle3D> vehicleCollidables3D = new ArrayList<>();
    private List<CollisionBox> lightCollisionBoxes = new ArrayList<>();
    private List<CollisionBox> pedCollisionBoxes = new ArrayList<>();
    private List<Bus> busCollidables = new ArrayList<>();
    private List<Bus3D> busCollidables3D = new ArrayList<>();
    //
    private List<Person> personCollidables = new ArrayList<>();
    private List<Person3D> personCollidables3D = new ArrayList<>();
    private StackPane root = new StackPane();
    private Pane tempPane = new Pane();
    private AtomicInteger clickCount = new AtomicInteger(0);
    private boolean stopSpawning = false;
    private boolean currentlySpawning = false;
    private boolean flag3D = false;
    private SystemController systemController;
    public static void main(String[] args) {
        launch(args);
    }

    /**
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        startCollisionTimer();
        createRoot(clickCount);

        Scene scene = new Scene(root, 1200, 800);
        root.setFocusTraversable(true);
        root.requestFocus();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Traffic2D");
        primaryStage.show();
    }

    /**
     * Creates base view of TransitFlow simulation,
     * generating the default images and linking objects to scene
     */
    public void createRoot(AtomicInteger clickCount){
        ImageHelper imageHelper = new ImageHelper();
        Image map;
        if(!flag3D){
            map = imageHelper.getImage("./images/trafficMap2.png");
        }
        else{
            map = imageHelper.getImage("./images/trafficMap3d.png");
        }
        ImageView fullMap = new ImageView(map);
        double imageW = map.getWidth();
        double imageH = map.getHeight();

        Pane mapPane = resizeImage(fullMap, 1200, 800);
        //root.getChildren().add(mapPane);

        if (!flag3D) {
            root.setOnMouseClicked(event -> {
                if (getCoordinates) {
                    double[] temp = {event.getX(), event.getY()};
                    System.out.println("X Position: " + temp[0] + ", Y Position: " + temp[1] + ", Dot Number: " + clickCount.get());
                    Circle circle = new Circle(event.getX(), event.getY(), 2);
                    circle.setFill(Color.RED);
                    Text dotCount = new Text(event.getX() + 3, event.getY(), String.valueOf(clickCount.get()));
                    dotCount.setFill(Color.RED);
                    clickCount.getAndIncrement();
                    tempPane.getChildren().addAll(circle, dotCount);
                } else {
                    if (!currentlySpawning) {
                        stopSpawning = false;
                        currentlySpawning = true;
                        addVehiclesUntilCount(vehicleCollidables.size(), tempPane, vehicleCollidables);
                        addBuses(busCollidables.size(), tempPane, busCollidables);
                        addPeople(personCollidables.size(), tempPane, personCollidables);
                    } else {
                        System.out.println("Currently Spawning Bool: " + currentlySpawning);
                        System.out.println("Already Spawning");
                    }
                }
            });
            root.setOnKeyPressed(event1 -> {
                if (event1.getCode() == KeyCode.SPACE) {
                    if (!stopSpawning) {
                        stopSpawning = true;
                        currentlySpawning = false;
                    } else {
                        currentlySpawning = false;
                    }
                }
            });
        }
        //Add lights to the map
        systemController = new SystemController();
        systemController.addLights(tempPane);
        root.getChildren().add(tempPane);
         //Add collision boxes to list
        lightCollisionBoxes = systemController.getLightCollisionBoxes();
        pedCollisionBoxes = systemController.getPedestrianCollisionBoxes();
    }

    /**
     * This will make the collision boxes invisible or visible
     * @param opacity 0 to 1 for invisible or visible
     */
    protected void makeCollisionBoxInvisible(int opacity){
        for (CollisionBox collisionBox : lightCollisionBoxes){
            collisionBox.setOpacity(opacity);
        }
        for (CollisionBox collisionBox : pedCollisionBoxes){
            collisionBox.setOpacity(opacity);
        }
        systemController.makeCollisionBoxesInvisible(opacity);
    }

    /**
     * Generates and adds cars to main scene in pane using count as an upper bound
     * @param count amount of vehicles in scene
     * @param tempPane create new temporary pane to place vehicles
     * @param vehicleCollidables vehicle object list to act on
     */
    public void addVehiclesUntilCount(int count, Pane tempPane, List<Vehicle> vehicleCollidables) {
        if (count >= 1 || stopSpawning) {
            return;
        }

        Vehicle vehicle = new Vehicle(tempPane, vehicleCollidables, lightCollisionBoxes);
        vehicle.startAnimation();
        vehicleCollidables.add(vehicle);
        //Using a recursive method to guarantee that the pause actually occurs.
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(150));
        pause.setOnFinished(event1 -> {
            addVehiclesUntilCount(vehicleCollidables.size(), tempPane, vehicleCollidables);
        });
        pause.play();
    }

    /**
     * Generates and adds cars to main scene in pane using count as an upper bound
     * @param count amount of vehicles in scene
     * @param tempPane create new temporary pane to place vehicles
     * @param vehicleCollidables3D vehicle object list to act on
     */
    public void addVehicles3D(int count, Pane tempPane, List<Vehicle3D> vehicleCollidables3D) {
        if (count >= 100 || stopSpawning) {
            return;
        }

        Vehicle3D vehicle = new Vehicle3D(tempPane, vehicleCollidables3D, lightCollisionBoxes);
        vehicle.startAnimation();
        vehicleCollidables3D.add(vehicle);
        //Using a recursive method to guarantee that the timeframe actually occurs.
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(1000));
        pause.setOnFinished(event1 -> {
            addVehicles3D(vehicleCollidables3D.size(), tempPane, vehicleCollidables3D);
        });
        pause.play();
    }

    /**
     * This is to stop spawning everything
     */
    protected void stopSpawns(){
        stopSpawning = true;
    }

    /**
     * This is to restart spawning everything
     */
    protected void restartSpawns(){
        stopSpawning = false;
    }

    /**
     * Generates and adds buses to main scene in pane using count as an upper bound
     * @param count upper bound on bus objects
     * @param tempPane pane to place bus objects
     * @param busCollidables bus objects themselves
     */
    public void addBuses(int count, Pane tempPane, List<Bus> busCollidables) {
        if (count >= 10) {
            return;
        }

        Bus bus = new Bus(tempPane, busCollidables);
        bus.startAnimation();
        busCollidables.add(bus);
        //Using a recursive method to guarantee that the pause actually occurs.
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(30000));
        pause.setOnFinished(event1 -> {
            addBuses(busCollidables.size(), tempPane, busCollidables);
        });
        pause.play();
    }

    /**
     * Generates and adds buses to main scene in pane using count as an upper bound
     * @param count upper bound on bus objects
     * @param tempPane pane to place bus objects
     * @param busCollidables bus objects themselves
     */
    public void addBuses3D(int count, Pane tempPane, List<Bus3D> busCollidables) {
        if (count >= 10 || stopSpawning) {
            return;
        }

        Bus3D bus = new Bus3D(tempPane, busCollidables);
        bus.startAnimation();
        busCollidables.add(bus);
        //Using a recursive method to guarantee that the pause actually occurs.
        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(30));
        pause.setOnFinished(event1 -> {
            addBuses3D(busCollidables.size(), tempPane, busCollidables);
        });
        pause.play();
    }

    /**
     * Generates and adds pedestrians to main scene in pane using count as an upper bound
     * @param count upper bound on pedestrian objects
     * @param tempPane pane to place pedestrian objects
     * @param personCollidables pedestrian objects themselves
     */
    public void addPeople(int count, Pane tempPane, List<Person> personCollidables) {
        if (count >= 50) {
            return;
        }

        Person person = new Person(tempPane, personCollidables, pedCollisionBoxes);
        person.startAnimation();
        personCollidables.add(person);
        //Using a recursive method to guarantee that the pause actually occurs.
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(300));
        pause.setOnFinished(event1 -> {
            addPeople(personCollidables.size(), tempPane, personCollidables);
        });
        pause.play();

    }

    /**
     * Generates and adds pedestrians to main scene in pane using count as an upper bound
     * @param count upper bound on pedestrian objects
     * @param tempPane pane to place pedestrian objects
     * @param personCollidable pedestrian objects themselves
     */
    public void addPeople3D(int count, Pane tempPane, List<Person3D> personCollidable) {
        if (count >= 20 || stopSpawning) {
            return;
        }

        Person3D person = new Person3D(tempPane, personCollidable, pedCollisionBoxes);
        person.startAnimation();
        personCollidable.add(person);
        //Using a recursive method to guarantee that the pause actually occurs.
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(1000));
        pause.setOnFinished(event1 -> {
            addPeople3D(personCollidable.size(), tempPane, personCollidable);
        });
        pause.play();
    }

    /**
     * Animation timer to check for collisions
     */
    public void startCollisionTimer() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(flag3D){
                    System.out.println("Vehicle Collidables 3D Size: " + vehicleCollidables3D.size());
                    for(Vehicle3D v1 : vehicleCollidables3D){
                        v1.checkCollision(vehicleCollidables3D);
                        systemController.checkVehicleCrossing(vehicleCollidables3D);
                    }
                    for (Person3D p1 : personCollidables3D) {
                        p1.checkCollision();
                    }
                    systemController.checkVehicleCrossing(vehicleCollidables3D);
                    systemController.checkPedestrianCrossing(personCollidables3D);
                    systemController.checkBusCrossing(busCollidables3D);
                }
                else {
                    for (Vehicle v1 : vehicleCollidables) {
                        v1.checkCollision(vehicleCollidables);
                    }
                    for (Person p1 : personCollidables) {
                        p1.checkCollision();
                    }
                    
                }
            }
        };
        timer.start();
    }

    /**
     * Animation timer to check for collisions
     */
    public void startCollisionTimer3D(List<Vehicle3D> vehicleCollidable3D, List<Bus3D> busCollidable3D, List<Person3D> personCollidable3D) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for(Vehicle3D v1 : vehicleCollidable3D){
                    v1.checkCollision(vehicleCollidable3D);
                    systemController.checkVehicleCrossing(vehicleCollidable3D);
                }
                for (Person3D p1 : personCollidable3D) {
                    p1.checkCollision();
                }
                systemController.checkVehicleCrossing(vehicleCollidable3D);
                systemController.checkPedestrianCrossing(personCollidable3D);
                systemController.checkBusCrossing(busCollidable3D);
            }
        };
        timer.start();
    }

    /**
     * Scales the street image for the pane
     * @param imageView The image being resized
     * @param maxWidth The maximum width of the Pane
     * @param maxHeight The maximum height of the Pane
     * @return A resized image made to fit to the Pane
     */
    public Pane resizeImage(ImageView imageView, double maxWidth, double maxHeight) {
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(maxWidth);
        imageView.setFitHeight(maxHeight);

        Pane pane = new Pane();
        pane.getChildren().add(imageView);

        imageView.layoutXProperty().bind(pane.widthProperty().subtract(imageView.fitWidthProperty()).divide(2));
        imageView.layoutYProperty().bind(pane.heightProperty().subtract(imageView.fitHeightProperty()).divide(2));

        return pane;
    }

    /**
     * To get the root of the file
     * @return The root node
     */
    public Pane getRoot(){
        return root;
    }

    /**
     * To set if this file is being used in the 3D file or on its own
     * @return True/False if the file is being used in 3D or 2D
     */
    public boolean set3DFlag(){
        return flag3D = true;
    }


}
