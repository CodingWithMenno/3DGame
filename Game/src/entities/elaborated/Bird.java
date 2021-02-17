package entities.elaborated;

import collisions.Collision;
import entities.MovableEntity;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.World;
import toolbox.Maths;

import java.util.List;

public class Bird extends MovableEntity {

    private static final float SPEED = 100;
    private static final float MAX_SPEED = 1f;
    private static final float MAX_HEIGHT = 150;
    private static final float MIN_HEIGHT = 15;

    private static final float SEPARATION_DISTANCE = 20;
    private static final int DESTINATION_DISTANCE = (int) Terrain.getSIZE();

    private static final float SEPARATION_SCALE = 1f;
    private static final float COHESION_SCALE = 100;
    private static final float ALIGNMENT_SCALE = 8;
    private static final float DESTINATION_SCALE = 100;

    private static final float SMOOTH_FACTOR = 0.3f;

    private BirdGroup birdGroup;

    public Bird(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale, BirdGroup birdGroup) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale);
        this.birdGroup = birdGroup;
    }

    @Override
    protected void update(Terrain terrain) {
        Vector3f separationVelocity = separation(this.birdGroup.getBirds());
        Vector3f alignmentVelocity = alignment(this.birdGroup.getBirds());
        Vector3f cohesionVelocity = cohesion(this.birdGroup.getBirds());
        Vector3f destinationVelocity = destination(terrain);

        this.velocity = Maths.add(this.getVelocity(), separationVelocity, cohesionVelocity, destinationVelocity);

        this.velocity.scale(SPEED * DisplayManager.getDelta());
        Vector3f newPosition = Vector3f.add(this.getPosition(), this.getVelocity(), null);

        newPosition = Maths.clamp(newPosition, Vector3f.sub(this.position, new Vector3f(MAX_SPEED, MAX_SPEED, MAX_SPEED), null),
                Vector3f.add(this.position, new Vector3f(MAX_SPEED, MAX_SPEED, MAX_SPEED), null));

        newPosition.y = Maths.clamp(newPosition.y,
                Math.max(terrain.getHeightOfTerrain(newPosition.x, newPosition.z) + MIN_HEIGHT, World.getWaterHeight() + MIN_HEIGHT), MAX_HEIGHT);
        newPosition.x = Maths.clamp(newPosition.x, 0, Terrain.getSIZE());
        newPosition.z = Maths.clamp(newPosition.z, 0, Terrain.getSIZE());

        this.position = Maths.lerp(this.position, newPosition, SMOOTH_FACTOR);

        lookTo(newPosition);
    }

    @Override
    public void onCollided(Collision collision) {

    }

    @Override
    protected void resetVerticalSpeed() {

    }

    private Vector3f destination(Terrain terrain) {
        if (this.birdGroup.getDestination() == null) {
            this.birdGroup.setNewDestination(terrain, new Vector3f(this.getPosition()), DESTINATION_DISTANCE, MIN_HEIGHT, MAX_HEIGHT);
        }

        if (Maths.getDistanceBetween(this.getPosition(), this.birdGroup.getDestination()) < DESTINATION_DISTANCE / 2.0) {
            this.birdGroup.setNewDestination(terrain, new Vector3f(this.getPosition()), DESTINATION_DISTANCE, MIN_HEIGHT, MAX_HEIGHT);
        }

        Vector3f velocity = Vector3f.sub(this.birdGroup.getDestination(), this.getPosition(), null);

        velocity.x /= DESTINATION_SCALE;
        velocity.y /= DESTINATION_SCALE;
        velocity.z /= DESTINATION_SCALE;

        return velocity;
    }

    private Vector3f separation(List<Bird> birds) {
        Vector3f velocity = new Vector3f(0, 0, 0);

        for (Bird bird : birds) {
            if (this == bird) {
                continue;
            }

            if (Maths.getDistanceBetween(bird.getPosition(), this.getPosition()) < SEPARATION_DISTANCE) {
                velocity = Vector3f.sub(velocity, Vector3f.sub(bird.getPosition(), this.getPosition(), null), null);
            }
        }

        velocity.x /= SEPARATION_SCALE;
        velocity.y /= SEPARATION_SCALE;
        velocity.z /= SEPARATION_SCALE;

        return velocity;
    }

    private Vector3f alignment(List<Bird> birds) {
        Vector3f velocity = new Vector3f(0, 0, 0);

        for (Bird bird : birds) {
            if (this == bird) {
                continue;
            }

            velocity = Vector3f.add(velocity, bird.getVelocity(), null);
        }

        velocity.x /= birds.size() - 1;
        velocity.y /= birds.size() - 1;
        velocity.z /= birds.size() - 1;

        velocity = Vector3f.sub(velocity, this.getVelocity(), null);
        velocity.x /= ALIGNMENT_SCALE;
        velocity.y /= ALIGNMENT_SCALE;
        velocity.z /= ALIGNMENT_SCALE;

        return velocity;
    }

    private Vector3f cohesion(List<Bird> birds) {
        Vector3f velocity = new Vector3f(0, 0, 0);

        for (Bird bird : birds) {
            if (this == bird) {
                continue;
            }

            velocity = Vector3f.add(velocity, bird.getPosition(), null);
        }

        velocity.x /= (birds.size() - 1);
        velocity.y /= (birds.size() - 1);
        velocity.z /= (birds.size() - 1);

        velocity = Vector3f.sub(velocity, this.getPosition(), null);
        velocity.x /= COHESION_SCALE;
        velocity.y /= COHESION_SCALE;
        velocity.z /= COHESION_SCALE;

        return velocity;
    }

    private void lookTo(Vector3f target) {
        float rotX = (float) Math.toDegrees(-Math.atan2(target.y, target.z));
        float rotY = (float) Math.toDegrees(Math.atan2(target.x, Math.sqrt(target.y * target.y + target.z * target.z))) - 180;

        float addX = rotX - super.getRotX();
        float addY = rotY - super.getRotY();

        increaseRotation(addX, addY, 0);

//        if (rotX < -90) {
//            super.rotZ = Maths.lerp(super.rotZ, 180, 0.05f);
//        } else {
//            super.rotZ = Maths.lerp(super.rotZ, 0, 0.05f);;
//        }
//
//        super.rotX = Maths.lerp(super.rotX, rotX, 0.01f);
//        super.rotY = Maths.lerp(super.rotY, rotY, 0.01f);
    }
}
