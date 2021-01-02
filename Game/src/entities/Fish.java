package entities;

import collisions.AABB;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;

public class Fish extends MovableEntity {

    public Fish(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, Vector3f... collisionBoxes) {
        super(model, position, rotX, rotY, rotZ, scale, collisionBoxes);
    }

    public Fish(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, Vector3f... collisionBoxes) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale, collisionBoxes);
    }

    @Override
    protected void update(Terrain terrain) {

    }

    @Override
    protected void resetGravity() {

    }
}
