package entities;

import collisions.AABB;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import toolbox.Maths;

public class Player extends MovableEntity {

    private static final float RUN_SPEED = 100;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 20;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, AABB... collisionBoxes) {
        super(model, position, rotX, rotY, rotZ, scale, collisionBoxes);
    }

    @Override
    protected void move(Terrain terrain) {
        checkInputs();
        super.increaseRotation(0, this.currentTurnSpeed * DisplayManager.getDelta(), 0);
        float distance = this.currentSpeed * DisplayManager.getDelta();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);

        this.upwardsSpeed += GRAVITY * DisplayManager.getDelta();
        super.increasePosition(0, this.upwardsSpeed * DisplayManager.getDelta(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            this.upwardsSpeed = 0;
            this.isInAir = false;
            super.getPosition().y = Maths.lerp(super.getPosition().y, terrainHeight, 0.5f);
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

        this.currentSpeed = Maths.lerp(this.currentSpeed, finalSpeed, 0.04f);
        this.currentTurnSpeed = Maths.lerp(this.currentTurnSpeed, finalTurnSpeed, 0.05f);

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            jump();
        }
    }
}
