package entities.elaborated;

import collisions.Collision;
import entities.MovableEntity;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.World;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bird extends MovableEntity {

    private static final int FLY_SPEED = 20;
    private static final float MAX_SPEED = 1f;

    private static final int VISION = 30;
    private static final int DESTINATION_DISTANCE = (int) Terrain.getSIZE();

    private static final float MAX_HEIGHT = 150;
    private static final float MIN_HEIGHT = 30;

    private static final int RANDOM_SCALE = 5;
    private static final int COHESION_SCALE = 10;
    private static final int ALIGNMENT_SCALE = 10;
    private static final int SEPARATION_SCALE = 10;
    private static final float DESTINATION_SCALE = 1;

    private Vector2f currentTurnSpeed;
    private Vector2f randomDirection;

    private BirdGroup birdGroup;


    public Bird(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale, BirdGroup birdGroup) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale);

        this.birdGroup = birdGroup;
        this.currentTurnSpeed = new Vector2f(0, 0);

        Random random = new Random();
        this.randomDirection = new Vector2f(random.nextInt(180) - 90 , random.nextInt(180) - 90);
        this.randomDirection = Maths.divide(this.randomDirection, RANDOM_SCALE);
    }

    @Override
    protected void update(Terrain terrain) {
        List<Bird> birds = getBirdsInVision(this.birdGroup.getBirds());
        calculateTurnSpeed(birds, terrain);

        super.increaseRotation(
                this.currentTurnSpeed.x * DisplayManager.getDelta(),
                this.currentTurnSpeed.y * DisplayManager.getDelta(),
                0);

        float distance = FLY_SPEED * DisplayManager.getDelta();
//        float distance = 0;
        float dx = Maths.clamp((float) (distance * Math.sin(Math.toRadians(super.getRotY()))), -MAX_SPEED, MAX_SPEED);
        float dz = Maths.clamp((float) (distance * Math.cos(Math.toRadians(super.getRotY()))), -MAX_SPEED, MAX_SPEED);
        float dy = Maths.clamp((float) (distance * Math.sin(-Math.toRadians(super.getRotX()))), -MAX_SPEED, MAX_SPEED);
        super.increasePosition(dx, dy, dz);

        super.position.y = Maths.clamp(super.position.y,
                Math.max(terrain.getHeightOfTerrain(super.position.x, super.position.z) + MIN_HEIGHT, World.getWaterHeight() + MIN_HEIGHT), MAX_HEIGHT);
    }

    @Override
    public void onCollided(Collision collision) {

    }

    @Override
    protected void resetVerticalSpeed() {

    }

    private void calculateTurnSpeed(List<Bird> birds, Terrain terrain) {
        Vector2f ownVector = this.randomDirection;
        Vector2f cohesionVector = cohesion(birds);
        Vector2f alignmentVector = alignment(birds);
        Vector2f separationVector = separation(birds);
        Vector2f destinationVector = destination(terrain);
        Vector2f stayInBoundingPosVector = stayInBoundingBox(terrain);

        Vector2f finalDirection = Maths.add(ownVector, cohesionVector, alignmentVector, separationVector, destinationVector, stayInBoundingPosVector);
        finalDirection = Maths.divide(finalDirection, 6);

        this.currentTurnSpeed = new Vector2f(finalDirection.x, finalDirection.y);
    }

    private Vector2f stayInBoundingBox(Terrain terrain) {
        float targetY = Maths.clamp(super.getPosition().y,
                Math.max(terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z) + MIN_HEIGHT, World.getWaterHeight() + MIN_HEIGHT), MAX_HEIGHT);

        float targetX = Maths.clamp(super.getPosition().x, 10, Terrain.getSIZE() - 10);
        float targetZ = Maths.clamp(super.getPosition().z, 10, Terrain.getSIZE() - 10);

        if (Maths.difference(super.getPosition().y, targetY) > 20) {
            targetY = super.getVelocity().y;
        }

        if (Maths.difference(super.getPosition().x, targetX) > 20) {
            targetX = super.getVelocity().x;
        }

        if (Maths.difference(super.getPosition().z, targetZ) > 20) {
            targetZ = super.getVelocity().z;
        }

        Vector2f angle = getAngle(super.getPosition(), new Vector3f(targetX, targetY, targetZ));
        angle.scale(10);
        return angle;
    }

    private Vector2f destination(Terrain terrain) {
        if (this.birdGroup.getDestination() == null) {
            this.birdGroup.setNewDestination(terrain, new Vector3f(this.getPosition()), DESTINATION_DISTANCE, MIN_HEIGHT, MAX_HEIGHT);
        }

        if (Maths.getDistanceBetween(this.getPosition(), this.birdGroup.getDestination()) < DESTINATION_DISTANCE / 10.0) {
            this.birdGroup.setNewDestination(terrain, new Vector3f(this.getPosition()), DESTINATION_DISTANCE, MIN_HEIGHT, MAX_HEIGHT);
        }

        Vector2f angle = getAngle(this.getPosition(), this.birdGroup.getDestination());
        angle = Maths.divide(angle, DESTINATION_SCALE);

        float addX = angle.x - super.getRotX();
        float addY = angle.y - super.getRotY();

        return new Vector2f(addX, addY);
    }

    private Vector2f separation(List<Bird> birds) {
        Vector2f separationVector = new Vector2f(0, 0);

        if (birds.size() < 1) {
            return separationVector;
        }

        for (Bird bird : birds) {
            Vector3f targetPosition = Vector3f.add(super.getPosition(), bird.getPosition(), null);
            targetPosition = Maths.divide(targetPosition, 2);
            targetPosition.negate();
            Vector2f angle = getAngle(super.getPosition(), targetPosition);

            float addX = angle.x - super.getRotX();
            float addY = angle.y - super.getRotY();

            separationVector = Vector2f.add(separationVector, new Vector2f(addX, addY), null);
        }

        separationVector = Maths.divide(separationVector, birds.size());
        separationVector = Maths.divide(separationVector, SEPARATION_SCALE);
        return separationVector;
    }

    private Vector2f alignment(List<Bird> birds) {
        Vector2f alignmentVector = new Vector2f(0, 0);

        if (birds.size() < 1) {
            return alignmentVector;
        }

        for (Bird bird : birds) {
            Vector2f angle = getAngle(super.getVelocity(), bird.getVelocity());

            float addX = angle.x - super.getRotX();
            float addY = angle.y - super.getRotY();

            alignmentVector = Vector2f.add(alignmentVector, new Vector2f(addX, addY), null);
        }

        alignmentVector = Maths.divide(alignmentVector, birds.size());
        alignmentVector = Maths.divide(alignmentVector, ALIGNMENT_SCALE);
        return alignmentVector;
    }

    private Vector2f cohesion(List<Bird> birds) {
        Vector2f cohesionVector = new Vector2f(0, 0);

        if (birds.size() < 1) {
            return cohesionVector;
        }

        for (Bird bird : birds) {
            Vector2f angle = getAngle(super.getPosition(), bird.getPosition());

            float addX = angle.x - super.getRotX();
            float addY = angle.y - super.getRotY();

            cohesionVector = Vector2f.add(cohesionVector, new Vector2f(addX, addY), null);
        }

        cohesionVector = Maths.divide(cohesionVector, birds.size());
        cohesionVector = Maths.divide(cohesionVector, COHESION_SCALE);
        return cohesionVector;
    }

    private Vector2f getAngle(Vector3f from, Vector3f to) {
        float rotY = (float) Math.toDegrees(Math.atan2(to.x - from.x, to.z - from.z));
        float rotX = (float) Math.toDegrees(-Math.atan2(to.y - from.y, to.z - from.z));

        return new Vector2f(rotX, rotY);
    }

    private List<Bird> getBirdsInVision(List<Bird> birds) {
        List<Bird> birdsInVision = new ArrayList<>();

        for (Bird bird : birds) {
            if (this == bird) {
                continue;
            }

            if (Maths.getDistanceBetween(super.getPosition(), bird.getPosition()) < VISION) {
                birdsInVision.add(bird);
            }
        }

        return birdsInVision;
    }
}
