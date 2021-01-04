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

    private static final float SPEED = 50f;
    private static final float VIEW_DISTANCE = 25;

    private Random random;

    private Vector3f destination;
    private List<Fish> allFish;

    public Fish(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
                float scale, Vector3f... collisionBoxes) {
        super(model, position, rotX, rotY, rotZ, scale, collisionBoxes);

        this.random = new Random();
        this.destination = new Vector3f(position);
    }

    public Fish(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
                float scale, Vector3f... collisionBoxes) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale, collisionBoxes);

        this.random = new Random();
        this.destination = new Vector3f(position);
    }

    @Override
    protected void update(Terrain terrain) {
        doFishBehaviour();
        stayInWater(terrain);
    }

    private void doFishBehaviour() {
        Vector3f v1 = goToCenterOfMass();
        Vector3f v2 = keepDistance();
        Vector3f v3 = averageVelocity();

        Vector3f v4 = Vector3f.add(v1, v2, null);
        Vector3f v5 = Vector3f.add(v4, v3, null);
        Vector3f v6 = goToDestination();

        Vector3f v7 = Vector3f.add(v5, v6, null);

        Vector3f v8 = Vector3f.add(super.velocity, v7, null);

        float delta = DisplayManager.getDelta();
        Vector3f finalVelocity = new Vector3f(v8.x * SPEED * delta, v8.y * SPEED * delta, v8.z * SPEED * delta);
        finalVelocity.x = Maths.clamp(finalVelocity.x, -0.1f, 0.1f);
        finalVelocity.y = Maths.clamp(finalVelocity.y, -0.1f, 0.1f);
        finalVelocity.z = Maths.clamp(finalVelocity.z, -0.1f, 0.1f);

        lookToDestination();

        Vector3f goToVelocity = Vector3f.add(super.position, finalVelocity, null);
        super.position = Maths.lerp(super.position, goToVelocity, SPEED / 150);
    }

    private Vector3f goToDestination() {
        Vector3f sub = Vector3f.sub(this.destination, super.getPosition(), null);
        return sub;
    }

    private Vector3f goToCenterOfMass() {
        Vector3f c = new Vector3f(0, 0, 0);

        for (Fish fish : this.allFish) {
            if (fish == this) {
                continue;
            }

            Vector3f sub = Vector3f.sub(fish.position, super.position, null);
            sub.negate();
            if (Maths.getDistanceBetween(fish.position, super.position) < VIEW_DISTANCE * 2) {
                c = Vector3f.sub(c, sub, null);
            }
        }

        return c;
    }

    private Vector3f keepDistance() {
        Vector3f c = new Vector3f(0, 0, 0);

        for (Fish fish : this.allFish) {
            if (fish == this) {
                continue;
            }

            if (Maths.getDistanceBetween(fish.position, super.position) < VIEW_DISTANCE / 10) {
                Vector3f sub = Vector3f.sub(fish.position, super.position, null);
                sub.scale(25);
                sub.y /= 7;
                c = Vector3f.sub(c, sub, null);
            }
        }

        return c;
    }

    private Vector3f averageVelocity() {
        Vector3f allVelocitiesCombined = new Vector3f(0, 0, 0);
        int fishCounter = 0;

        for (Fish fish : this.allFish) {
            if (fish == this) {
                continue;
            }

            if (Maths.getDistanceBetween(fish.position, super.position) < VIEW_DISTANCE) {
                Vector3f fishVelocity = fish.getVelocity();

                allVelocitiesCombined = Vector3f.add(allVelocitiesCombined, fishVelocity, null);
                fishCounter++;
            }
        }

        allVelocitiesCombined.x =  allVelocitiesCombined.x / fishCounter;
        allVelocitiesCombined.y =  allVelocitiesCombined.y / fishCounter;
        allVelocitiesCombined.z =  allVelocitiesCombined.z / fishCounter;

//        allVelocitiesCombined = Vector3f.sub(allVelocitiesCombined, super.velocity, null);
//        allVelocitiesCombined.x = (allVelocitiesCombined.x /  2) + this.random.nextInt(5);
//        allVelocitiesCombined.y = (allVelocitiesCombined.y /  2) + this.random.nextInt(5);
//        allVelocitiesCombined.z = (allVelocitiesCombined.z /  2) + this.random.nextInt(5);

        return allVelocitiesCombined;
    }

    private void lookToDestination() {
        Vector3f vector = Vector3f.sub(super.getPosition(), this.destination, null);

        float rotX = (float) Math.toDegrees(-Math.atan2(vector.y, vector.z));
        float rotY = (float) Math.toDegrees(Math.atan2(vector.x, Math.sqrt(vector.y * vector.y + vector.z * vector.z))) - 180;

        if (rotX < -90) {
            super.rotZ = Maths.lerp(super.rotZ, 180, 0.05f);
        } else {
            super.rotZ = Maths.lerp(super.rotZ, 0, 0.05f);;
        }

        super.rotX = Maths.lerp(super.rotX, rotX, 0.01f);
        super.rotY = Maths.lerp(super.rotY, rotY, 0.01f);
    }

    private void stayInWater(Terrain terrain) {
        float waterHeight = MainGameLoop.WATER_HEIGHT - 3.5f;
        int terrainSize = (int) Terrain.getSIZE() / 2;
        float terrainHeight = terrain.getHeightOfTerrain(super.position.x, super.position.z) + 2;

        if (super.position.y > waterHeight) {
            super.position.y = waterHeight;
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
