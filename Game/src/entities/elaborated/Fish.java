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

public class Fish extends MovableEntity {

    private static final float SPEED = 0.07f;
    private static final float MAX_SPEED = 0.5f;

    private static final float VIEW_DISTANCE = 10;

    private static final float MAX_HEIGHT = World.getWaterHeight() - 10f;

    private static final float separationFactor = 1f;
    private static final float alignmentFactor = 0.5f;
    private static final float cohesionFactor = 2f;
    private static final float destinationFactor = 10f;

    private Vector3f destination;
    private List<Fish> allFish;


    public Fish(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
                float scale, Vector3f collisionBox) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale, collisionBox);

        //this.destination = new Vector3f(position);
        this.destination = new Vector3f(super.getPosition());
    }

    @Override
    protected void update(Terrain terrain) {
        doFishBehaviour();
        stayInWater(terrain);
    }

    @Override
    protected void onCollided(Collision collision) {

    }

    private void doFishBehaviour() {
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

        lookTo(Vector3f.sub(super.position, goToVector, null));

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

    private void lookTo(Vector3f lookTo) {
        float rotX = (float) Math.toDegrees(-Math.atan2(lookTo.y, lookTo.z));
        float rotY = (float) Math.toDegrees(Math.atan2(lookTo.x, Math.sqrt(lookTo.y * lookTo.y + lookTo.z * lookTo.z))) - 180;

        if (rotX < -90) {
            super.rotZ = Maths.lerp(super.rotZ, 180, 0.05f);
        } else {
            super.rotZ = Maths.lerp(super.rotZ, 0, 0.05f);;
        }

        super.rotX = Maths.lerp(super.rotX, rotX, 0.01f);
        super.rotY = Maths.lerp(super.rotY, rotY, 0.01f);
    }

    private void stayInWater(Terrain terrain) {
        int terrainSize = (int) Terrain.getSIZE();
        float terrainHeight = terrain.getHeightOfTerrain(super.position.x, super.position.z) + 2;

        if (super.position.y > MAX_HEIGHT) {
            super.position.y = MAX_HEIGHT;
        } else if (super.position.y < terrainHeight) {
            if (terrainHeight <= MAX_HEIGHT) {
                super.position.y = terrainHeight;
            } else {
                super.position.x -= 1;
                super.position.z -= 1;
            }
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
    protected void resetVerticalSpeed() {}
}
