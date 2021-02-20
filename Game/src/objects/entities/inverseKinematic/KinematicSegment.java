package objects.entities.inverseKinematic;

import models.TexturedModel;
import objects.entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

public class KinematicSegment extends Entity {

    //With scale 1
    private static final float DEFAULT_LENGTH = 11.2f;

    private Vector3f endPosition;

    private float length;

    private KinematicSegment parent;

    public KinematicSegment(KinematicSegment parent, TexturedModel model, Vector3f position, float length) {
        super(model, position, 0, 0, 0, length/DEFAULT_LENGTH);

        this.length = length;
        calculateEndPos();

        this.parent = parent;

        if (this.parent != null) {
            this.position = Vector3f.sub(this.parent.position, this.endPosition, null);
        }
    }

    public void calculateEndPos() {
        float dx = (float) (this.length * Math.cos(Math.toRadians(super.getRotX() - 90)));
        float dy = (float) (this.length * Math.sin(Math.toRadians(super.getRotX() - 90)));
        float dz = (float) (this.length * Math.cos(Math.toRadians(super.getRotZ() - 90)));

        this.endPosition = new Vector3f(this.position.x + dx, this.position.y + dy, this.position.z + dz);
    }

    public void follow(Vector3f position) {
        Vector3f target = new Vector3f(position);
        Vector3f direction = Vector3f.sub(target, this.position, null);

        float yRotation = (float) Math.atan((target.z - this.position.z) / (target.x - this.position.x));

        double sinTheta = Math.sin(-yRotation);
        double cosTheta = Math.cos(-yRotation);

        Vector3f rotatedTarget = new Vector3f(target);

        double x = rotatedTarget.getX() - this.position.x;
        double z = rotatedTarget.getZ() - this.position.z;

        rotatedTarget.setX((float) (x * cosTheta - z * sinTheta + this.position.x));
        rotatedTarget.setZ((float) (z * cosTheta + x * sinTheta + this.position.z));


        Vector3f angle = Maths.getAngle(this.position, rotatedTarget);
        this.rotZ = angle.z;
        this.rotY = (float) Math.toDegrees(-yRotation);

        direction.normalise(direction);
        direction.scale(this.length);
        direction.negate();

        this.position = Vector3f.add(target, direction, null);
    }

    public void update(Vector3f target) {
        if (this.parent != null) {
            follow(this.parent.getPosition());
        } else {
            follow(new Vector3f(target));
        }

        calculateEndPos();
    }

    public KinematicSegment getParent() {
        return parent;
    }
}
