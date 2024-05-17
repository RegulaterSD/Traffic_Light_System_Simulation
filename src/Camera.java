/**
 * Using this class that I found on GitHub. I was trying to get my own class made but the functionality was off
 * and this code works so well that I felt I should use it.
 * I changed a few things in this class like the attachment of the UI elements that now will move with the camera.
 */

/**
 * Using this class that I found on GitHub. I was trying to get my own class made but the functionality was off
 * and this code works so well that I felt I should use it.
 * I changed a few things in this class like the attachment of the UI elements that now will move with the camera.
 */

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Callback;
//import org.fxyz3d.geometry.MathUtils;

/**
 * A self initializing First Person Shooter camera
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class Camera extends Parent {

    public Camera() {
        initialize();
    }

    private void update() {
        updateControls();
    }

    private void updateControls() {
        if (fwd && !back) {
            moveForward();
        }
        if (strafeL) {
            strafeLeft();
        }
        if (strafeR) {
            strafeRight();
        }
        if (back && !fwd) {
            moveBack();
        }
        if (up && !down) {
            moveUp();
        }
        if (down && !up) {
            moveDown();
        }
    }
    /*==========================================================================
     Initialization
     */
    private final Group root = new Group();
    private final Affine affine = new Affine();
    private final Translate t = new Translate(0, 0, 0);
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS),
            rotateY = new Rotate(0, Rotate.Y_AXIS),
            rotateZ = new Rotate(0, Rotate.Z_AXIS);

    private boolean fwd, strafeL, strafeR, back, up, down, shift;

    private double mouseSpeed = 1.0, mouseModifier = 0.1;
    private double moveSpeed = 15.0;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    /**
     * initializes camera
     */
    private void initialize() {
        root.getTransforms().add(affine);
        getChildren().add(root);
        getTransforms().add(affine);
        initializeCamera();
        startUpdateThread();
    }

    /**
     * handles camera booleans for movement
     * @param scene
     */
    public void loadControlsForSubScene(SubScene scene) {
        sceneProperty().addListener(l -> {
            if (getScene() != null) {
                getScene().addEventHandler(KeyEvent.ANY, ke -> {
                    if (ke.getEventType() == KeyEvent.KEY_PRESSED) {
                        switch (ke.getCode()) {
                            case Q:
                                up = true;
                                break;
                            case E:
                                down = true;
                                break;
                            case W:
                                fwd = true;
                                break;
                            case S:
                                back = true;
                                break;
                            case A:
                                strafeL = true;
                                break;
                            case D:
                                strafeR = true;
                                break;
                            case SHIFT:
                                shift = true;
                                moveSpeed = 50;
                                break;
                        }
                    } else if (ke.getEventType() == KeyEvent.KEY_RELEASED) {
                        switch (ke.getCode()) {
                            case Q:
                                up = false;
                                break;
                            case E:
                                down = false;
                                break;
                            case W:
                                fwd = false;
                                break;
                            case S:
                                back = false;
                                break;
                            case A:
                                strafeL = false;
                                break;
                            case D:
                                strafeR = false;
                                break;
                            case SHIFT:
                                moveSpeed = 50;
                                shift = false;
                                break;
                        }
                    }
                    ke.consume();
                });
            }
        });
        scene.addEventHandler(MouseEvent.ANY, me -> {
            if (me.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();

            } else if (me.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                mouseSpeed = 1.0;
                mouseModifier = 0.1;

                if (me.isPrimaryButtonDown()) {
                    if (me.isControlDown()) {
                        mouseSpeed = 0.1;
                    }
                    if (me.isShiftDown()) {
                        mouseSpeed = 2.0;
                    }
                    t.setX(getPosition().getX());
                    t.setY(getPosition().getY());
                    t.setZ(getPosition().getZ());

                    affine.setToIdentity();

                    rotateY.setAngle(
                            MathUtils.clamp(-360, ((rotateY.getAngle() + mouseDeltaX * (mouseSpeed * mouseModifier)) % 360 + 540) % 360 - 180, 360)
                    ); // horizontal
                    rotateX.setAngle(
                            MathUtils.clamp(-45, ((rotateX.getAngle() - mouseDeltaY * (mouseSpeed * mouseModifier)) % 360 + 540) % 360 - 180, 35)
                    ); // vertical
                    affine.prepend(t.createConcatenation(rotateY.createConcatenation(rotateX)));

                } else if (me.isSecondaryButtonDown()) {
                    /*
                     init zoom?
                     */
                } else if (me.isMiddleButtonDown()) {
                    /*
                     init panning?
                     */
                }
            }
        });

        scene.addEventHandler(ScrollEvent.ANY, se -> {

            if (se.getEventType().equals(ScrollEvent.SCROLL_STARTED)) {

            } else if (se.getEventType().equals(ScrollEvent.SCROLL)) {

            } else if (se.getEventType().equals(ScrollEvent.SCROLL_FINISHED)) {

            }
        });
    }

    /**
     * This is where the magic happens. It took me a while to figure out what he was doing here but once I did
     * I was able to alter it.
     * The main section that provides the correct movement for the camera is this
     *  MathUtils.clamp(-360, ((rotateY.getAngle() + mouseDeltaX * (mouseSpeed * mouseModifier)) % 360 + 540) % 360 - 180, 360)
     *  there is a secondary class that does the math to figure where the camera should be pointing towards
     * @param scene
     * @param subScene
     * @param ui
     */
    public void loadControlsForScene(Scene scene, SubScene subScene , Group ui) {
        scene.addEventHandler(KeyEvent.ANY, ke -> {
            if (ke.getEventType() == KeyEvent.KEY_PRESSED) {
                switch (ke.getCode()) {
                /*    case Q:
                        up = true;
                        break;
                   case E:
                        down = true;
                        break;*/
                    case W:
                        fwd = true;
                        break;
                    case S:
                        back = true;
                        break;
                    case A:
                        strafeL = true;
                        break;
                    case D:
                        strafeR = true;
                        break;
                    case SHIFT:
                        shift = true;
                        moveSpeed = 50;
                        break;
                }
            } else if (ke.getEventType() == KeyEvent.KEY_RELEASED) {
                switch (ke.getCode()) {
                 /*   case Q:
                        up = false;
                        break;
                    case E:
                        down = false;
                        break;*/
                    case W:
                        fwd = false;
                        break;
                    case S:
                        back = false;
                        break;
                    case A:
                        strafeL = false;
                        break;
                    case D:
                        strafeR = false;
                        break;
                    case SHIFT:
                        moveSpeed = 50;
                        shift = false;
                        break;
                }
            }
            ke.consume();
        });
        subScene.addEventHandler(MouseEvent.ANY, me -> {
            if (me.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();

                fwd = false;
                back = false;

                strafeL = false;
                strafeR = false;

            } else if (me.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                mouseSpeed = 1.0;
                mouseModifier = 0.1;

                if (me.isPrimaryButtonDown()) {
                    if (me.isControlDown()) {
                        mouseSpeed = 0.1;
                    }
                    if (me.isShiftDown()) {
                        mouseSpeed = 1.0;
                    }
                    t.setX(getPosition().getX());
                    t.setY(getPosition().getY());
                    t.setZ(getPosition().getZ());

                    affine.setToIdentity();

                    rotateY.setAngle(
                            MathUtils.clamp(-360, ((rotateY.getAngle() + mouseDeltaX * (mouseSpeed * mouseModifier)) % 360 + 540) % 360 - 180, 360)
                    ); // horizontal
                    rotateX.setAngle(
                            MathUtils.clamp(-90, ((rotateX.getAngle() - mouseDeltaY * (mouseSpeed * mouseModifier)) % 540 + 540) % 360 - 180, -70)
                    ); // vertical

                    affine.prepend(t.createConcatenation(rotateY.createConcatenation(rotateX)));
                    subScene.getCamera().getTransforms().setAll(affine);
                    //scene.getCamera().getTransforms().setAll(affine);
                    //ui.getTransforms().setAll(affine);
                   /* subScene.setTranslateX(affine.getTx());
                    subScene.setTranslateY(affine.getTy());
                    subScene.setTranslateZ(affine.getTz());*/

                    //,affine.getTz());

                } else if (me.isSecondaryButtonDown()) {
                    /*
                     init zoom?
                     */
                    System.out.println(me.getScreenX());
                    if(me.getScreenX() > 800)
                    {
                        strafeR = true;
                        strafeL = false;
                    }
                    else if (me.getSceneX() < 800){
                        strafeR = false;
                        strafeL = true;
                    }

                } else if (me.isMiddleButtonDown()) {
                    /*
                     init panning?
                     */

                }
            }
        });

       subScene.addEventHandler(ScrollEvent.SCROLL, se -> {

            if (se.getEventType().equals(ScrollEvent.SCROLL_STARTED)) {
                double deltaY = se.getDeltaY();

                //Scroll In
                if(deltaY > 0){
                    fwd = true;
                    back = false;
                    System.out.println("Scroll in");
                }

                //Scroll Out
                if(deltaY < 0){
                    back = true;
                    fwd = false;
                    System.out.println("Scroll back");
                }
                else {
                    fwd = false;
                    back = false;
                    System.out.println("Is stopped");
                }
                //scroll down
            } else if (se.getEventType().equals(ScrollEvent.SCROLL)) {
                double deltaY = se.getDeltaY();

                //Scroll In
                if(deltaY > 0){
                    fwd = true;
                    back = false;
                    System.out.println("Scroll in");
                }

                //Scroll Out
                if(deltaY < 0){
                    back = true;
                    fwd = false;
                    System.out.println("Scroll back");
                }


            } else if (se.getEventType().equals(ScrollEvent.SCROLL_FINISHED)) {
                fwd = false;
                back = false;
            }
        });
    }

    /**
     * Sets initial camera values
     */
    private void initializeCamera() {
        getCamera().setNearClip(0.1);
        getCamera().setFarClip(100000);
        getCamera().setFieldOfView(90);
        getCamera().setVerticalFieldOfView(true);
        //root.getChildren().add(getCamera());
    }

    /**
     * Updates camera on thread
     */
    private void startUpdateThread() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        }.start();
    }
    /*==========================================================================
     Movement
     */

    /**
     * movement handling
     */
    private void moveForward() {
        affine.setTx(getPosition().getX() + moveSpeed * getN().getX());
        affine.setTy(getPosition().getY() + moveSpeed * getN().getY());
        affine.setTz(getPosition().getZ() + moveSpeed * getN().getZ());
    }

    /**
     * left strafe movement handling
     */
    private void strafeLeft() {
        affine.setTx(getPosition().getX() + moveSpeed * -getU().getX());
        affine.setTy(getPosition().getY() + moveSpeed * -getU().getY());
        affine.setTz(getPosition().getZ() + moveSpeed * -getU().getZ());
    }

    /**
     * right strafe movement handling
     */
    private void strafeRight() {
        affine.setTx(getPosition().getX() + moveSpeed * getU().getX());
        affine.setTy(getPosition().getY() + moveSpeed * getU().getY());
        affine.setTz(getPosition().getZ() + moveSpeed * getU().getZ());
    }

    /**
     * dorsal movement, moving backwards handling
     */
    private void moveBack() {
        affine.setTx(getPosition().getX() + moveSpeed * -getN().getX());
        affine.setTy(getPosition().getY() + moveSpeed * -getN().getY());
        affine.setTz(getPosition().getZ() + moveSpeed * -getN().getZ());
    }

    /**
     * up height movement handling
     */
    private void moveUp() {
        affine.setTx(getPosition().getX() + moveSpeed * -getV().getX());
        affine.setTy(getPosition().getY() + moveSpeed * -getV().getY());
        affine.setTz(getPosition().getZ() + moveSpeed * -getV().getZ());
    }

    /**
     * down height movement handling
     */
    private void moveDown() {
        affine.setTx(getPosition().getX() + moveSpeed * getV().getX());
        affine.setTy(getPosition().getY() + moveSpeed * getV().getY());
        affine.setTz(getPosition().getZ() + moveSpeed * getV().getZ());
    }

    /*==========================================================================
     Properties
     */
    private final ReadOnlyObjectWrapper<PerspectiveCamera> camera = new ReadOnlyObjectWrapper<>(this, "camera", new PerspectiveCamera(true));

    /**
     * Gets camera objects
     * @return
     */
    public final PerspectiveCamera getCamera() {
        return camera.get();
    }

    /**
     * get camera objects
     * @return
     */
    public ReadOnlyObjectProperty cameraProperty() {
        return camera.getReadOnlyProperty();
    }

    /*==========================================================================
     Callbacks
     | R | Up| F |  | P|
     U |mxx|mxy|mxz|  |tx|
     V |myx|myy|myz|  |ty|
     N |mzx|mzy|mzz|  |tz|

     */
    //Forward / look direction
    private final Callback<Transform, Point3D> F = (a) -> {
        return new Point3D(a.getMzx(), a.getMzy(), a.getMzz());
    };
    private final Callback<Transform, Point3D> N = (a) -> {
        return new Point3D(a.getMxz(), a.getMyz(), a.getMzz());
    };
    // up direction
    private final Callback<Transform, Point3D> UP = (a) -> {
        return new Point3D(a.getMyx(), a.getMyy(), a.getMyz());
    };
    private final Callback<Transform, Point3D> V = (a) -> {
        return new Point3D(a.getMxy(), a.getMyy(), a.getMzy());
    };
    // right direction
    private final Callback<Transform, Point3D> R = (a) -> {
        return new Point3D(a.getMxx(), a.getMxy(), a.getMxz());
    };
    private final Callback<Transform, Point3D> U = (a) -> {
        return new Point3D(a.getMxx(), a.getMyx(), a.getMzx());
    };
    //position
    private final Callback<Transform, Point3D> P = (a) -> {
        return new Point3D(a.getTx(), a.getTy(), a.getTz());
    };

    private Point3D getF() {
        return F.call(getLocalToSceneTransform());
    }

    public Point3D getLookDirection() {
        return getF();
    }

    private Point3D getN() {
        return N.call(getLocalToSceneTransform());
    }

    public Point3D getLookNormal() {
        return getN();
    }

    private Point3D getR() {
        return R.call(getLocalToSceneTransform());
    }

    private Point3D getU() {
        return U.call(getLocalToSceneTransform());
    }

    private Point3D getUp() {
        return UP.call(getLocalToSceneTransform());
    }

    private Point3D getV() {
        return V.call(getLocalToSceneTransform());
    }

    public final Point3D getPosition() {
        return P.call(getLocalToSceneTransform());
    }

}

