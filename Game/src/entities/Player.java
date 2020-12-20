package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Player extends Entity {

    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;

    private static final float TERRAIN_HEIGHT = 0;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(){
        checkInputs();
        super.increaseRotation(0, this.currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = this.currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);

        this.upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, this.upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        if (super.getPosition().y < TERRAIN_HEIGHT) {
            this.upwardsSpeed = 0;
            this.isInAir = false;
            super.getPosition().y = TERRAIN_HEIGHT;
        }
    }

    private void jump() {
        if (!this.isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            this.isInAir = true;
        }
    }

    private void checkInputs() {
        float finalSpeed = 0;
        float finalTurnSpeed = 0;

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            finalSpeed += RUN_SPEED;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            finalSpeed -= RUN_SPEED;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            finalTurnSpeed -= TURN_SPEED;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            finalTurnSpeed += TURN_SPEED;
        }

        this.currentSpeed = finalSpeed;
        this.currentTurnSpeed = finalTurnSpeed;

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            jump();
        }
    }
}
