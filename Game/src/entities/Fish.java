package entities;

import engineTester.MainGameLoop;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import toolbox.Maths;

import java.util.List;
import java.util.Random;

public class Fish extends MovableEntity {

    private static final float SPEED = 0.07f;
    private static final float MAX_SPEED = 1f;

    private static final float VIEW_DISTANCE = 10;

    private static final float MAX_HEIGHT = MainGameLoop.WATER_HEIGHT - 10f;

    private static final float separationFactor = 2f;
    private static final float alignmentFactor = 1f;
    private static final float cohesionFactor = 1f;
    private static final float destinationFactor = 15f;

    private Vector3f destination;
    private List<Fish> allFish;

    public Fish(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
                float scale, Vector3f... collisionBoxes) {
        super(model, position, rotX, rotY, rotZ, scale, collisionBoxes);

        this.destination = new Vector3f(position);
    }

    public Fish(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
                float scale, Vector3f... collisionBoxes) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale, collisionBoxes);
        
        this.destination = new Vector3f(position);
    }

    @Override
    protected void update(Terrain terrain) {
        doFishBehaviour();
        stayInWater(terrain);
    }

    private void doFishBehaviour() {
        lookForward();

        Vector3f separation = separation();
        separation.scale(separationFactor);

        Vector3f alignment = alignment();
        alignment.scale(alignmentFactor);

        Vector3f cohesion = cohesion();
        cohesion.scale(cohesionFactor);

        Vector3f destination = goToDestination();
        destination.scale(destinationFactor);

        Vector3f finalDirection = Maths.add(separation, alignment, cohesion, destination);
        finalDirection.scale(DisplayManager.getDelta());
        finalDirection.x = Math.min(MAX_SPEED, finalDirection.x);
        finalDirection.y = Math.min(MAX_SPEED, finalDirection.y);
        finalDirection.z = Math.min(MAX_SPEED, finalDirection.z);

        Vector3f goToVector = Vector3f.add(super.position, finalDirection, null);
        super.position = Maths.lerp(super.position, goToVector, SPEED);
    }

    private Vector3f goToDestination() {
        return Vector3f.sub(this.destination, super.getPosition(), null);
    }

    private Vector3f cohesion() {
        Vector3f steering = new Vector3f(0, 0, 0);

        for (Fish fish : this.allFish) {
            if (fish == this) {
                continue;
            }

            if (Maths.getDistanceBetween(fish.position, super.position) < VIEW_DISTANCE) {
                steering = Vector3f.add(steering, fish.position, null);
            }
        }

        return steering;
    }

    private Vector3f separation() {
        Vector3f steering = new Vector3f(0, 0, 0);

        for (Fish fish : this.allFish) {
            if (fish == this) {
                continue;
            }

            float distance = Maths.getDistanceBetween(fish.position, super.position);
            if (distance < VIEW_DISTANCE) {
                Vector3f sub = Vector3f.sub(super.position, fish.position, null);
                sub.x /= (distance * distance);
                sub.y /= (distance * distance);
                sub.z /= (distance * distance);
                steering = Vector3f.sub(steering, sub, null);
            }
        }

        return steering;
    }

    private Vector3f alignment() {
        Vector3f steering = new Vector3f(0, 0, 0);

        for (Fish fish : this.allFish) {
            if (fish == this) {
                continue;
            }

            if (Maths.getDistanceBetween(fish.position, super.position) < VIEW_DISTANCE) {
                steering = Vector3f.add(steering, fish.velocity, null);
            }
        }

        return steering;
    }

    private void lookForward() {
        Vector3f vector = Vector3f.sub(super.getPosition(), this.destination, null);

        float rotX = (float) Math.toDegrees(-Math.atan2(vector.y, vector.z));
        float rotY = (float) Math.toDegrees(Math.atan2(vector.x, Math.sqrt(vector.y * vector.y + vector.z * vector.z))) + 180;

        if (rotX < 90) {
            super.rotZ = Maths.lerp(super.rotZ, 0, 0.05f);
        } else {
            super.rotZ = Maths.lerp(super.rotZ, -180, 0.05f);;
        }

        super.rotX = Maths.lerp(super.rotX, rotX, 0.01f);
        super.rotY = Maths.lerp(super.rotY, rotY, 0.01f);
    }

    private void stayInWater(Terrain terrain) {
        int terrainSize = (int) Terrain.getSIZE() / 2;
        float terrainHeight = terrain.getHeightOfTerrain(super.position.x, super.position.z) + 2;

        if (super.position.y > MAX_HEIGHT) {
            super.position.y = MAX_HEIGHT;
        } else if (super.position.y < terrainHeight) {
            super.position.y = terrainHeight;
        }

        if (super.position.x < 2) {
            super.position.x  = 2;
        } else if (super.position.x > terrainSize) {
            super.position.x = terrainSize;
        }

        if (super.position.z > -5) {
            super.position.z  = -5;
        } else if (super.position.z < -terrainSize) {
            super.position.z = -terrainSize;
        }
    }

    public void setAllFish(List<Fish> allFish) {
        this.allFish = allFish;
    }

    public void setDestination(Vector3f newDestination) {
        this.destination = newDestination;
    }

    @Override
    protected void resetGravity() {}
}
