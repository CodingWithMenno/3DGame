package objects.entities.inverseKinematic;

import models.TexturedModel;
import objects.entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

public class KinematicPart extends Entity {

    //With scale 1
    private static final float DEFAULT_LENGTH = 7.2f;
    private static final float DEFAULT_WIDTH = 2f;

    private KinematicPart parent;
    private KinematicPart child;

    private float relativeRotX, relativeRotY, relativeRotZ;

    public KinematicPart(KinematicPart parent, TexturedModel model, Vector3f position, float scale) {
        super(model, position, 0, 0, 0, scale);

        this.parent = parent;

        this.relativeRotX = 0;
        this.relativeRotY = 0;
        this.relativeRotZ = 0;

        if (this.parent != null) {
            this.scale = this.parent.getScale();

            this.position = new Vector3f(0, 0, 0);
            this.position.x = this.parent.getPosition().getX();
            this.position.z = this.parent.getPosition().getZ();
            this.position.y = this.parent.getPosition().getY() + DEFAULT_LENGTH * this.scale;
        }
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

    public void setChild(KinematicPart child) {
        this.child = child;
    }

    @Override
    public Vector3f getTranslation() {
        if (this.parent != null) {
            /*
            -Positie van parent + DEFAULT_LENGTH te weten komen en daarom roteren

            -positie parent pakken en dx, dz en dy uitrekenen met de parent locatie
             */


            float dx = (float) ((DEFAULT_LENGTH * this.scale) * Math.sin(Math.toRadians(this.parent.getRotX())));
            float dz = (float) ((DEFAULT_LENGTH * this.scale) * Math.cos(Math.toRadians(this.parent.getRotZ())));
            float dy = (float) ((DEFAULT_LENGTH * this.scale) * Math.sin(Math.toRadians(-this.parent.getRotZ())));

            return new Vector3f(
                this.parent.getPosition().getX() - dx,
                this.parent.getPosition().getY(),
                this.parent.getPosition().getZ()
            );
        }

        return new Vector3f(
            this.position.x,
            this.position.y,
            this.position.z
        );
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
