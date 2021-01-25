package entities;

import animation.AnimatedModel;
import collisions.Collision;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import toolbox.Maths;

public class Player extends MovableEntity {

    private static final float RUN_SPEED = 80;
    private static final float TURN_SPEED = 200;
    private static final float JUMP_POWER = 30;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = true;

    public Player(AnimatedModel animatedModel, Vector3f position, float rotX, float rotY, float rotZ,
                  float scale, Vector3f... collisionBoxes) {
        super(animatedModel, position, rotX, rotY, rotZ, scale, collisionBoxes);
    }

    @Override
    protected void update(Terrain terrain) {
        checkInputs();
        super.increaseRotation(0, this.currentTurnSpeed * DisplayManager.getDelta(), 0);
        float distance = this.currentSpeed * DisplayManager.getDelta();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);

        this.upwardsSpeed += GRAVITY * DisplayManager.getDelta();
        super.increasePosition(0, this.upwardsSpeed * DisplayManager.getDelta(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.position.x, super.position.z);
        if (super.position.y < terrainHeight) {
            this.upwardsSpeed = 0;
            this.isInAir = false;
            super.position.y = Maths.lerp(super.position.y, terrainHeight, 0.5f);
        }

        doAnimations();
    }

    @Override
    protected void onCollided(Collision collision) {
        if (collision.isHitFromSide()) {
            System.out.println("Hit the side");
        } else {
            System.out.println("Hit the top");
        }
    }

    private void doAnimations() {
        if (Maths.difference(this.currentSpeed, 0) > 20) {
            super.setAnimation(1);
        } else {
            super.setAnimation(0);
        }
    }

    @Override
    protected void resetVerticalSpeed() {
        this.upwardsSpeed = 0;
        this.isInAir = false;
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
