package entities;

import engineTester.MainGameLoop;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;
import toolbox.Maths;

import java.util.Random;

public class Fish extends MovableEntity {

    private Random random;

    private Vector3f destination;

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
        setDestination();
        goToDestination();
        stayInWater(terrain);
    }

    private void goToDestination() {
        Vector3f vector = Vector3f.sub(super.getPosition(), this.destination, null);

        float rotX = (float) Math.toDegrees(-Math.atan2(vector.y, vector.z));
        float rotY = (float) Math.toDegrees(Math.atan2(vector.x, Math.sqrt(vector.y * vector.y + vector.z * vector.z))) - 180;

        super.rotX = rotX;
        super.rotY = rotY;
    }

    private void setDestination() {
        int terrainSize = (int) Terrain.getSIZE() / 2;
        int height = (int) ((MainGameLoop.WATER_HEIGHT - 3.5f) + Terrain.getMaxHeight());

        float difference = Maths.getDistanceBetween(this.position, this.destination);

        if (difference < 5) {
            this.destination = new Vector3f(-random.nextInt(terrainSize), -random.nextInt(height), -random.nextInt(terrainSize));
        }
    }

    private void stayInWater(Terrain terrain) {
        float waterHeight = MainGameLoop.WATER_HEIGHT - 3.5f;
        int terrainSize = (int) Terrain.getSIZE() / 2;

        float terrainHeight = terrain.getHeightOfTerrain(super.position.x, super.position.z);
        if (super.position.y < terrainHeight) {
            super.position.y = Maths.lerp(super.position.y, terrainHeight, 0.5f);
        } else if (super.position.y > waterHeight) {
            super.position.y = Maths.lerp(super.position.y, waterHeight, 0.5f);
        }

        if (super.position.x < 0) {
            super.position.x  = Maths.lerp(super.position.x, 0, 0.5f);
        } else if (super.position.x > terrainSize) {
            super.position.x = Maths.lerp(super.position.x, terrainSize, 0.5f);
        }

        if (super.position.z > 0) {
            super.position.z  = Maths.lerp(super.position.z, 0, 0.5f);
        } else if (super.position.z < -terrainSize) {
            super.position.z = Maths.lerp(super.position.z, -terrainSize, 0.5f);
        }
    }

    @Override
    protected void resetGravity() {
    }
}
