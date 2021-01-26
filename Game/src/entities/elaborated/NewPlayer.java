package entities.elaborated;

import animation.AnimatedModel;
import collisions.Collision;
import entities.MovableEntity;
import input.Inputs;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class NewPlayer extends MovableEntity {

    private static final float RUN_SPEED = 80;
    private static final float TURN_SPEED = 200;
    private static final float JUMP_POWER = 30;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = true;

    public NewPlayer(AnimatedModel animatedModel, Vector3f position, float rotX, float rotY, float rotZ, float scale, Vector3f... collisionBoxes) {
        super(animatedModel, position, rotX, rotY, rotZ, scale, collisionBoxes);
    }

    @Override
    protected void update(Terrain terrain) {
        checkInputs();
    }

    @Override
    protected void onCollided(Collision collision) {

    }

    @Override
    protected void resetVerticalSpeed() {

    }

    private void checkInputs() {
        float finalSpeed = 0;
        float finalTurnSpeed = 0;

        if (Keyboard.isKeyDown(Inputs.FORWARD)) {
            finalSpeed += RUN_SPEED;
        }

        increasePosition(finalSpeed * DisplayManager.getDelta(), 0, 0);
    }
}
