package entities;

import collisions.AABB;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;

public abstract class MovableEntity extends Entity {

    protected Vector3f velocity;

    public MovableEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, AABB... collisionBoxes) {
        super(model, position, rotX, rotY, rotZ, scale, collisionBoxes);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public MovableEntity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale, AABB... collisionBoxes) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale, collisionBoxes);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public void moveEntity(Terrain terrain) {
        Vector3f oldPos = new Vector3f(super.getPosition());
        move(terrain);
        this.velocity = Vector3f.sub(super.getPosition(), oldPos, null);

        for (AABB collisionBox : super.getCollisionBoxes()) {
            collisionBox.move(this.velocity);
        }
    }

    protected abstract void move(Terrain terrain);

    public boolean isMoving() {
        if (this.velocity.x != 0 || this.velocity.y != 0 || this.velocity.z != 0) {
            return true;
        }
        return false;
    }

    public void revertPosition() {
        Vector3f revertVelocity = Vector3f.sub(super.getPosition(), this.velocity, null);

        super.setPosition(revertVelocity);

        for (AABB collisionBox : super.getCollisionBoxes()) {
            collisionBox.move((Vector3f) this.velocity.negate());
        }

        this.velocity = new Vector3f(0, 0, 0);
    }

    public Vector3f getVelocity() {
        return velocity;
    }
}
