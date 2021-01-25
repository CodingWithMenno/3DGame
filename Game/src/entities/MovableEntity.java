package entities;

import animation.AnimatedModel;
import collisions.AABB;
import collisions.Collision;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;
import toolbox.Maths;

public abstract class MovableEntity extends Entity {

    protected Vector3f velocity;


    public MovableEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, Vector3f... collisionBoxes) {
        super(model, position, rotX, rotY, rotZ, scale, collisionBoxes);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public MovableEntity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, Vector3f... collisionBoxes) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale, collisionBoxes);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public MovableEntity(AnimatedModel animatedModel, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, Vector3f... collisionBoxes) {
        super(animatedModel, position, rotX, rotY, rotZ, scale, collisionBoxes);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public MovableEntity(AnimatedModel animatedModel, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, Vector3f... collisionBoxes) {
        super(animatedModel, textureIndex, position, rotX, rotY, rotZ, scale, collisionBoxes);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public void updateEntity(Terrain terrain) {
        Vector3f oldPos = new Vector3f(super.getPosition());
        update(terrain);
        this.velocity = Vector3f.sub(super.getPosition(), oldPos, null);

        for (AABB collisionBox : super.getCollisionBoxes()) {
            collisionBox.move(this.velocity);
        }
    }

    protected abstract void update(Terrain terrain);

    public boolean isMoving() {
        if (Maths.difference(this.velocity.x, 0) > 0.0000f || Maths.difference(this.velocity.y, 0) > 0.0000f || Maths.difference(this.velocity.z, 0) > 0.0000f) {
            return true;
        }
        return false;
    }

    public void onCollide(Collision collision) {
        if (collision.getEntity1() == this) {
            if (collision.isHitFromSide()) {
                revertHorizontal();
            } else {
                revertVerticalDown();
            }
        }

        onCollided(collision);
    }

    protected abstract void onCollided(Collision collision);

    public void revertPosition() {
        Vector3f revertVelocity = Vector3f.sub(super.getPosition(), this.velocity, null);

        super.setPosition(revertVelocity);

        for (AABB collisionBox : super.getCollisionBoxes()) {
            collisionBox.move((Vector3f) this.velocity.negate());
        }

        this.velocity = new Vector3f(0, 0, 0);
    }

    public void revertHorizontal() {
        super.position.x -= this.velocity.x;
        super.position.z -= this.velocity.z;

        for (AABB collisionBox : super.getCollisionBoxes()) {
            collisionBox.move(new Vector3f(-this.velocity.x, 0, -this.velocity.z));
        }

        this.velocity.x = 0;
        this.velocity.z = 0;
    }

    public void revertVerticalDown() {
        if (this.velocity.y > 0) {
            return;
        }

        super.position.y -= this.velocity.y;

        for (AABB collisionBox : super.getCollisionBoxes()) {
            collisionBox.move(new Vector3f(0, -this.velocity.y, 0));
        }

        resetVerticalSpeed();
        this.velocity.y = 0;
    }

    protected abstract void resetVerticalSpeed();

    public void increasePosition(float dx, float dy, float dz) {
        super.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

    public Vector3f getVelocity() {
        return velocity;
    }
}
