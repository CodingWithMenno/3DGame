package entities.elaborated;

import animation.AnimatedModel;
import collisions.Collision;
import entities.Camera;
import entities.MovableEntity;
import input.Inputs;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import toolbox.Maths;

public class Player extends MovableEntity {

    private static final float RUN_SPEED = 40;
    private static final float JUMP_POWER = 30;
    private static final float ACCELERATION = 5;
    private static final float MODEL_ROTATION_SPEED = 20;

    private float currentVerticalSpeed = 0;
    private float currentHorizontalSpeed;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private float modelRotation = 0;

    private boolean isInAir = true;

    private Camera camera;

    public Player(AnimatedModel animatedModel, Vector3f position, float rotX, float rotY, float rotZ, float scale, Vector3f collisionBox) {
        super(animatedModel, position, rotX, rotY, rotZ, scale, collisionBox);
    }

    private void doAnimations() {
        if (Maths.difference(this.currentVerticalSpeed, 0) > 20 || Maths.difference(this.currentHorizontalSpeed, 0) > 20) {
            super.setAnimation(1);
        } else {
            super.setAnimation(0);
        }
    }

    private void jump() {
        if (!this.isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            this.isInAir = true;
        }
    }

    @Override
    protected void update(Terrain terrain) {
        super.increaseRotation(0, -this.modelRotation, 0);
        checkInputs();

        super.increaseRotation(0, this.currentTurnSpeed, 0);

        float verticalDistance = this.currentVerticalSpeed * DisplayManager.getDelta();
        float dxv = (float) (verticalDistance * Math.sin(Math.toRadians(super.getRotY())));
        float dzv = (float) (verticalDistance * Math.cos(Math.toRadians(super.getRotY())));

        float horizontalDistance = this.currentHorizontalSpeed * DisplayManager.getDelta();
        float dxh = (float) (horizontalDistance * Math.cos(Math.toRadians(super.getRotY())));
        float dzh = (float) (horizontalDistance * Math.sin(Math.toRadians(super.getRotY())));
        super.increasePosition(dxv + dxh, 0, dzv - dzh);

        this.upwardsSpeed += GRAVITY * DisplayManager.getDelta();
        super.increasePosition(0, this.upwardsSpeed * DisplayManager.getDelta(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.position.x, super.position.z);
        if (super.position.y < terrainHeight) {
            this.upwardsSpeed = 0;
            this.isInAir = false;
            super.position.y = Maths.lerp(super.position.y, terrainHeight, 0.5f);
        } else {
            this.isInAir = true;
        }

        if (this.camera != null) {
            this.camera.setSubtractRotation(this.modelRotation);
        }
        super.increaseRotation(0, this.modelRotation, 0);

        doAnimations();
    }

    @Override
    protected void onCollided(Collision collision) {

    }

    @Override
    protected void resetVerticalSpeed() {
        this.upwardsSpeed = 0;
        this.isInAir = false;
    }

    private void checkInputs() {
        float finalVerticalSpeed = 0;
        float finalHorizontalSpeed = 0;
        float finalTurnSpeed = 0;
        float finalModelRotation = 0;

        boolean forwardPressed = false;
        if (Keyboard.isKeyDown(Inputs.FORWARD)) {
            finalVerticalSpeed += RUN_SPEED;
            forwardPressed = true;
        }

        boolean backwardPressed = false;
        if (Keyboard.isKeyDown(Inputs.BACKWARDS)) {
            finalVerticalSpeed -= RUN_SPEED;
            finalModelRotation += 180;
            backwardPressed = true;
        }

        if (Keyboard.isKeyDown(Inputs.LEFT)) {
            finalHorizontalSpeed += RUN_SPEED;

            if (forwardPressed) {
                finalModelRotation += 45;
            } else if (backwardPressed) {
                finalModelRotation -= 45;
            } else {
                finalModelRotation += 90;
            }
        }

        if (Keyboard.isKeyDown(Inputs.RIGHT)) {
            finalHorizontalSpeed -= RUN_SPEED;

            if (forwardPressed) {
                finalModelRotation -= 45;
            } else if (backwardPressed) {
                finalModelRotation += 45;
            } else {
                finalModelRotation -= 90;
            }
        }

        if (!Keyboard.isKeyDown(Inputs.FREE_CAMERA_ANGLE)) {
            finalTurnSpeed -= Mouse.getDX() * Inputs.SENSITIVITY;
        }

        this.currentVerticalSpeed = Maths.lerp(this.currentVerticalSpeed, finalVerticalSpeed, ACCELERATION * DisplayManager.getDelta());
        this.currentHorizontalSpeed = Maths.lerp(this.currentHorizontalSpeed, finalHorizontalSpeed, ACCELERATION * DisplayManager.getDelta());
        this.currentTurnSpeed = finalTurnSpeed;

        if (Maths.difference(this.modelRotation, finalModelRotation) < 180) {
            this.modelRotation = Maths.lerp(this.modelRotation, finalModelRotation, MODEL_ROTATION_SPEED * DisplayManager.getDelta());
        } else {
            this.modelRotation = finalModelRotation;
        }

        if (Keyboard.isKeyDown(Inputs.JUMP)) {
            jump();
        }
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
