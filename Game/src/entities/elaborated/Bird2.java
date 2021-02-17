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

public class Bird2 extends MovableEntity {

    private static final int FLY_SPEED = 10;

    private static final int VISION = 30;

    private static final float MAX_HEIGHT = 150;
    private static final float MIN_HEIGHT = 15;

    private static final int RANDOM_SCALE = 10;
    private static final int COHESION_SCALE = 1;

    private Vector2f currentTurnSpeed;
    private Vector2f randomDirection;

    private BirdGroup2 birdGroup;


    public Bird2(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale, BirdGroup2 birdGroup) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale);

        this.birdGroup = birdGroup;
        this.currentTurnSpeed = new Vector2f(0, 0);

        Random random = new Random();
        this.randomDirection = new Vector2f(random.nextInt(180) - 90 , random.nextInt(180) - 90);
        this.randomDirection = Maths.divide(this.randomDirection, RANDOM_SCALE);
    }

    @Override
    protected void update(Terrain terrain) {
        List<Bird2> birds = getBirdsInVision(this.birdGroup.getBirds());
        calculateTurnSpeed(birds);

        super.increaseRotation(
                this.currentTurnSpeed.x * DisplayManager.getDelta(),
                this.currentTurnSpeed.y * DisplayManager.getDelta(),
                0);

        float distance = FLY_SPEED * DisplayManager.getDelta();
//        float distance = 0;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        float dy = (float) (distance * Math.sin(-Math.toRadians(super.getRotX())));
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

    private void calculateTurnSpeed(List<Bird2> birds) {
        Vector2f ownVector = this.randomDirection;
        Vector2f cohesionVector = cohesion(birds);

        Vector2f finalDirection = Maths.add(ownVector, cohesionVector);
        finalDirection = Maths.divide(finalDirection, 2);

        this.currentTurnSpeed = new Vector2f(finalDirection.x, finalDirection.y);

//        this.currentTurnSpeed = cohesion(birds);
    }

    private Vector2f cohesion(List<Bird2> birds) {
        Vector2f cohesionVector = new Vector2f(0, 0);

        for (Bird2 bird : birds) {
            float rotY = (float) Math.toDegrees(Math.atan2(bird.getPosition().x - super.getPosition().x, bird.getPosition().z - super.getPosition().z));
            float rotX = (float) Math.toDegrees(-Math.atan2(bird.getPosition().y - super.getPosition().y, bird.getPosition().z - super.getPosition().z));

            float addX = rotX - super.getRotX();
            float addY = rotY - super.getRotY();

            cohesionVector = Vector2f.add(cohesionVector, new Vector2f(addX, addY), null);
        }

        cohesionVector = Maths.divide(cohesionVector, birds.size());
        cohesionVector = Maths.divide(cohesionVector, COHESION_SCALE);
        return cohesionVector;
    }

    private List<Bird2> getBirdsInVision(List<Bird2> birds) {
        List<Bird2> birdsInVision = new ArrayList<>();

        for (Bird2 bird : birds) {
            if (this == bird) {
                continue;
            }

            if (Maths.getDistanceBetween(super.getPosition(), bird.getPosition()) < VISION) {
                birdsInVision.add(bird);
            }
        }

        return birdsInVision;
    }

    public Vector2f getCurrentTurnSpeed() {
        return currentTurnSpeed;
    }
}
