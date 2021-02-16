package entities;

import animation.AnimatedModel;
import collisions.Collision;
import collisions.OBB;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;
import toolbox.Maths;

public abstract class MovableEntity extends Entity {

    protected Vector3f velocity;


    public MovableEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, OBB collisionBox) {
        super(model, position, rotX, rotY, rotZ, scale, collisionBox);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public MovableEntity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, OBB collisionBox) {
        super(model, textureIndex, position, rotX, rotY, rotZ, scale, collisionBox);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public MovableEntity(AnimatedModel animatedModel, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, OBB collisionBox) {
        super(animatedModel, position, rotX, rotY, rotZ, scale, collisionBox);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public MovableEntity(AnimatedModel animatedModel, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
                         float scale, OBB collisionBox) {
        super(animatedModel, textureIndex, position, rotX, rotY, rotZ, scale, collisionBox);
        this.velocity = new Vector3f(0, 0, 0);
    }

    public void updateEntity(Terrain terrain) {
        Vector3f oldPos = new Vector3f(super.getPosition());
        update(terrain);
        this.velocity = Vector3f.sub(super.getPosition(), oldPos, null);

        for (OBB collisionBox : super.getCollisionBoxes()) {
            collisionBox.move(new Vector3f(this.velocity));
        }

        this.position = Maths.clamp(new Vector3f(this.position), new Vector3f(1, -1000, 1), new Vector3f(Terrain.getSIZE() - 1, 1000, Terrain.getSIZE() - 1));
    }

    protected abstract void update(Terrain terrain);

    public boolean isMovingAbove(float velocity) {
        if (Maths.difference(this.velocity.x, 0) > velocity || Maths.difference(this.velocity.y, 0) > velocity || Maths.difference(this.velocity.z, 0) > velocity) {
            return true;
        }
        return false;
    }

    public void onCollide(Collision collision) {
        if (collision.isHitFromSide()) {
            revertHorizontal();
        } else {
            revertVerticalDown();
        }

        onCollided(collision);
    }

    protected abstract void onCollided(Collision collision);

    public void revertPosition() {
        Vector3f revertVelocity = Vector3f.sub(super.getPosition(), this.velocity, null);

        super.setPosition(revertVelocity);

        for (OBB collisionBox : super.getCollisionBoxes()) {
            collisionBox.move(new Vector3f((Vector3f) this.velocity.negate()));
        }

        this.velocity = new Vector3f(0, 0, 0);
    }

    public void revertHorizontal() {
        super.position.x -= this.velocity.x;
        super.position.z -= this.velocity.z;

        for (OBB collisionBox : super.getCollisionBoxes()) {
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

        for (OBB collisionBox : super.getCollisionBoxes()) {
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

        for (OBB box : super.getCollisionBoxes()) {
            if (dx != 0) {
                box.rotX(dx);
            }
            if (dy != 0) {
                box.rotY(dy);
            }
            if (dz != 0) {
                box.rotZ(dz);
            }
        }
    }

    public Vector3f getVelocity() {
        return velocity;
    }
}
