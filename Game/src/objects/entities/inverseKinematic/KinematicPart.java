package objects.entities.inverseKinematic;

import models.TexturedModel;
import objects.entities.Entity;
import org.lwjgl.util.vector.Vector3f;

public class KinematicPart extends Entity {

    private KinematicPart parent;
    private KinematicPart child;

    private float relativeRotX, relativeRotY, relativeRotZ;

    public KinematicPart(KinematicPart parent, KinematicPart child, TexturedModel model, Vector3f position, float scale) {
        super(model, position, 0, 0, 0, scale);

        this.parent = parent;
        this.child = child;

        this.relativeRotX = 0;
        this.relativeRotY = 0;
        this.relativeRotZ = 0;
    }

    //Sets the relative x rotation in relation of the parent
    public void setRotX(float rotX) {
        this.relativeRotX = rotX;

        this.rotX = rotX;
        if (this.parent != null) {
            this.rotX += this.parent.getRotX();
        }

        if (this.child != null) {
            this.child.updateAsChildRotX();
        }
    }

    //Updates the x rotation to the new parent's x rotation
    private void updateAsChildRotX() {
        this.rotX = this.parent.getRotX() + this.relativeRotX;

        if (this.child != null) {
            this.child.updateAsChildRotX();
        }
    }

    public void setRotY(float rotY) {
        this.relativeRotY = rotY;

        this.rotY = rotY;
        if (this.parent != null) {
            this.rotY += this.parent.getRotY();
        }

        if (this.child != null) {
            this.child.updateAsChildRotY();
        }
    }

    private void updateAsChildRotY() {
        this.rotY = this.parent.getRotY() + this.relativeRotY;

        if (this.child != null) {
            this.child.updateAsChildRotY();
        }
    }

    public void setRotZ(float rotZ) {
        this.relativeRotZ = rotZ;

        this.rotZ = rotZ;
        if (this.parent != null) {
            this.rotZ += this.parent.getRotZ();
        }

        if (this.child != null) {
            this.child.updateAsChildRotZ();
        }
    }

    private void updateAsChildRotZ() {
        this.rotZ = this.parent.getRotZ() + this.relativeRotZ;

        if (this.child != null) {
            this.child.updateAsChildRotZ();
        }
    }

    public float getRelativeRotX() {
        return this.relativeRotX;
    }

    public float getRelativeRotY() {
        return this.relativeRotY;
    }

    public float getRelativeRotZ() {
        return this.relativeRotZ;
    }
}
