package com.lonedev.spacehoops.sandbox;

import com.jme.input.KeyInput;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import java.util.logging.Logger;

/**
 * This class is the implementation of a Controller class that will handle the
 * movement of the space ship.
 *
 * @author Richard Hawkes
 */
public class SpaceShipController extends Controller {
    public enum SpaceShipAction { UP, DOWN, TURN_LEFT, TURN_RIGHT, ROLL_LEFT, ROLL_RIGHT, ACCELERATE, DECELERATE, SNAPSHOT, EXIT };

    private Logger logger = Logger.getLogger(SpaceShipController.class.getName());

    private Spatial spaceShip;
    private GameControlManager gameControlManager;
    private final static float TURN_SPEED = 0.75f;
    private final static float FORWARD_SPEED = -15f;

    private float hAngle, vAngle, zAngle;

    public SpaceShipController(Spatial spaceShip) {
        this.spaceShip = spaceShip;
        gameControlManager = new GameControlManager();
        bindActionKeys();
    }

    private void bindActionKeys() {
        //create all actions
        for (SpaceShipAction action : SpaceShipAction.values()) {
            gameControlManager.addControl(action.name());
        }

        bindKey(SpaceShipAction.EXIT, KeyInput.KEY_X, KeyInput.KEY_ESCAPE);
        bindKey(SpaceShipAction.SNAPSHOT, KeyInput.KEY_F12);
        bindKey(SpaceShipAction.UP, KeyInput.KEY_UP, KeyInput.KEY_W);
        bindKey(SpaceShipAction.DOWN, KeyInput.KEY_DOWN, KeyInput.KEY_S);
        bindKey(SpaceShipAction.TURN_LEFT, KeyInput.KEY_LEFT, KeyInput.KEY_A);
        bindKey(SpaceShipAction.TURN_RIGHT, KeyInput.KEY_RIGHT, KeyInput.KEY_D);
        bindKey(SpaceShipAction.ROLL_LEFT, KeyInput.KEY_Q);
        bindKey(SpaceShipAction.ROLL_RIGHT, KeyInput.KEY_E);
        bindKey(SpaceShipAction.ACCELERATE, KeyInput.KEY_W);
        bindKey(SpaceShipAction.DECELERATE, KeyInput.KEY_S);
    }

    /**
     * For a given action passed in, this will return a value between zero
     * (not-pressed) and one (pressed). For keys presses, this would be 0 or 1,
     * but the GameControlManager allows for joystick controls where there may
     * be an analogue value. Quite neat, and I'm including this functionality
     * just in case for future implementations.
     *
     * @param action The action to be queried.
     * @return A value between zero and one depending on whether the key is
     * pressed, or the joystick is pointing in that direction.
     */
    private float value(SpaceShipAction action) {
        return gameControlManager.getControl(action.name()).getValue();
    }

    /**
     * Binds the key(s) to the SpaceShipAction.
     *
     * @param action The SpaceShipAction enum to bind.
     * @param keys The key(s) to bind it to.
     */
    private void bindKey(SpaceShipAction action, int... keys) {
        final GameControl control = gameControlManager.getControl(action.name());

        for (int key : keys) {
            control.addBinding(new KeyboardBinding(key));
        }
    }

    @Override
    public void update(float tpf) {
        if (value(SpaceShipAction.EXIT) > 0) {
            System.exit(0); //OK, this is just a demo...
        } else if (value(SpaceShipAction.SNAPSHOT) > 0) {
            DisplaySystem.getDisplaySystem().getRenderer().takeScreenShot("img-" + System.currentTimeMillis());
        }

        // In case we need to move back to our old location/rotation (for collision
        // purposes), we store the original location before we moved there.
        Vector3f origTranslation = spaceShip.getLocalTranslation().clone();
        Quaternion origRotation = spaceShip.getLocalRotation().clone();

        // Prepare our new location/rotation.
        Vector3f newLocation = spaceShip.getLocalTranslation().clone();
        Quaternion newRotation = spaceShip.getLocalRotation().clone();

        if (value(SpaceShipAction.TURN_LEFT) > 0 || value(SpaceShipAction.TURN_RIGHT) > 0 
                || value(SpaceShipAction.UP) > 0 || value(SpaceShipAction.DOWN) > 0
                || value(SpaceShipAction.ROLL_LEFT) > 0 || value (SpaceShipAction.ROLL_RIGHT) > 0) {

            float horizontalTurnAmount = TURN_SPEED * tpf * (value(SpaceShipAction.TURN_LEFT) - value(SpaceShipAction.TURN_RIGHT));
            hAngle += horizontalTurnAmount;

            float vertcialTurnAmount = TURN_SPEED * tpf * (value(SpaceShipAction.DOWN) - value(SpaceShipAction.UP));
            vAngle += vertcialTurnAmount;

            float rollTurnAmount = TURN_SPEED * tpf * (value(SpaceShipAction.ROLL_LEFT) - value(SpaceShipAction.ROLL_RIGHT));
            zAngle += rollTurnAmount;

            newRotation.fromAngles(vAngle, hAngle, zAngle);

            spaceShip.getLocalRotation().set(newRotation);
//            spaceShip.updateGeometricState(0, true);
        }
        
        // I don't get this next line. What is getRotationColumn? value must be
        // between 0 and 2. I think 0=x, 1=y and 2=z... But I is baffled!
        Vector3f direction = new Vector3f(spaceShip.getLocalRotation().getRotationColumn(2));

        float speed = FORWARD_SPEED * tpf;
        newLocation.addLocal(direction.mult(speed));

        spaceShip.getLocalTranslation().set(newLocation);
        
        spaceShip.updateRenderState();
        spaceShip.updateModelBound();
//        spaceShip.updateWorldBound();
    }
}
