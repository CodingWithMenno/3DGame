package objects.entities.inverseKinematic;

import models.TexturedModel;
import objects.entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

public class KinematicSegment extends Entity {

    //With scale 1
    private static final float DEFAULT_LENGTH = 2.3f;

    private Vector3f endPosition;

    private float length;

    public KinematicSegment(TexturedModel model, Vector3f position, float length) {
        super(model, position, 0, 0, 0, length/DEFAULT_LENGTH);

        this.length = length;
        calculateEndPos();
    }

    public void calculateEndPos() {
        float dx = (float) (this.length * (Math.sin(Math.toRadians(super.getRotZ())) * Math.cos(Math.toRadians(super.getRotY()))));
        float dy = (float) (this.length * (Math.cos(Math.toRadians(super.getRotZ()))));
        float dz = (float) (this.length * (Math.sin(Math.toRadians(super.getRotZ())) * Math.sin(Math.toRadians(super.getRotY()))));

        if (Float.isNaN(dx)) {
            dx = 0;
        }

        if (Float.isNaN(dy)) {
            dy = 0;
        }

        if (Float.isNaN(dz)) {
            dz = 0;
        }

        this.endPosition = new Vector3f(this.position.x - dx, this.position.y + dy, this.position.z + dz);
    }


    @Override
    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
        calculateEndPos();
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

    public Vector3f getEndPosition() {
        return endPosition;
    }
}
