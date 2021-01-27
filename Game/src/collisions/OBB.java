package collisions;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;

public abstract class OBB {

    protected Vector3f center;
    protected Vector3f dimensions;

    protected List<Vector3f> nodes;

    protected float rotX, rotY, rotZ;

    public OBB(Vector3f center, Vector3f dimensions) {
        this.center = center;
        this.dimensions = dimensions;
        this.nodes = new ArrayList<>();
        this.rotX = 0;
        this.rotY = 0;
        this.rotZ = 0;
    }

    public abstract boolean isIntersecting(Vector3f point);

    public abstract void rotateX(double rotation);
    public abstract void rotateY(double rotation);
    public abstract void rotateZ(double rotation);

    public void setRotX(float rotX) {
        rotateX(rotX);
        this.rotX = rotX;
    }

    public void setRotY(float rotY) {
        rotateY(rotY);
        this.rotY = rotY;
    }

    public void setRotZ(float rotZ) {
        rotateZ(rotZ);
        this.rotZ = rotZ;
    }

    public List<Vector3f> getNodes() {
        return nodes;
    }
}
