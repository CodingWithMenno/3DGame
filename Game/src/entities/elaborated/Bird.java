package entities.elaborated;

import collisions.Collision;
import collisions.OBB;
import entities.MovableEntity;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.World;
import toolbox.Maths;

import java.util.List;
import java.util.Random;

public class Bird extends MovableEntity {

    private static final float SPEED = 30;
    private static final float MAX_SPEED = 1f;
    private static final float MAX_HEIGHT = 150;
    private static final float MIN_HEIGHT = 15;

    private static final float MAX_HEIGHT_DIFFERENCE = 15;
    private static final float SEPARATION_DISTANCE = 13;
    private static final int DESTINATION_DISTANCE = (int) Terrain.getSIZE();

    private static final float SEPARATION_SCALE = 10;
    private static final float COHESION_SCALE = 20;
    private static final float ALIGNMENT_SCALE = 8;
    private static final float DESTINATION_SCALE = 90;

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

        this.velocity = Maths.add(this.getVelocity(), separationVelocity, alignmentVelocity, cohesionVelocity, destinationVelocity);
        this.velocity.scale(SPEED * DisplayManager.getDelta());
        Vector3f newPosition = Vector3f.add(this.getPosition(), this.getVelocity(), null);

        newPosition.y = Maths.clamp(newPosition.y,
                Math.max(terrain.getHeightOfTerrain(newPosition.x, newPosition.z) + MIN_HEIGHT, World.getWaterHeight() + MIN_HEIGHT), MAX_HEIGHT);

        newPosition = Maths.clamp(newPosition, Vector3f.sub(this.position, new Vector3f(MAX_SPEED, MAX_SPEED, MAX_SPEED), null),
                Vector3f.add(this.position, new Vector3f(MAX_SPEED, MAX_SPEED, MAX_SPEED), null));

        lookTo(newPosition);

        this.position = Maths.lerp(this.getPosition(), newPosition, SMOOTH_FACTOR);
    }

    @Override
    protected void onCollided(Collision collision) {

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

            Vector3f birdPosition = new Vector3f(bird.getPosition());
            if (Maths.difference(birdPosition.y, super.getPosition().y) > MAX_HEIGHT_DIFFERENCE) {
                if (birdPosition.y > super.getPosition().y) {
                    birdPosition.y *= 10;
                } else {
                    birdPosition.y *= -10;
                }
            }

            velocity = Vector3f.add(velocity, birdPosition, null);
        }

        velocity.x /= birds.size() - 1;
        velocity.y /= birds.size() - 1;
        velocity.z /= birds.size() - 1;

        velocity = Vector3f.sub(velocity, this.getPosition(), null);
        velocity.x /= COHESION_SCALE;
        velocity.y /= COHESION_SCALE;
        velocity.z /= COHESION_SCALE;

        return velocity;
    }

    private void lookTo(Vector3f lookTo) {

    }

//    private Vector3f setNewDestination(Terrain terrain) {
//        float x = (float) ((this.random.nextInt(DESTINATION_DISTANCE) - DESTINATION_DISTANCE / 2.0) + this.getPosition().x);
//        float z = (float) ((this.random.nextInt(DESTINATION_DISTANCE) - DESTINATION_DISTANCE / 2.0) + this.getPosition().z);
//        float y = (float) ((this.random.nextInt(DESTINATION_DISTANCE) - DESTINATION_DISTANCE / 2.0) + this.getPosition().y);
//
//        x = Maths.clamp(x, 0, Terrain.getSIZE() - 1);
//        z = Maths.clamp(z, 0, Terrain.getSIZE() - 1);
//        y = Maths.clamp(y, Math.max(terrain.getHeightOfTerrain(x, z) + MIN_HEIGHT, World.getWaterHeight()) + MIN_HEIGHT, MAX_HEIGHT);
//
//        return new Vector3f(x, y, z);
//    }
}
