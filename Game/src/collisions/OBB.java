package collisions;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;

public abstract class OBB implements Cloneable {

    protected Vector3f center;
    protected Vector3f dimensions;

    protected List<Vector3f> nodes;
    protected List<Vector2f> edges;

    protected float rotX, rotY, rotZ;

    public OBB(Vector3f center, Vector3f dimensions) {
        this.center = center;
        this.dimensions = dimensions;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.rotX = 0;
        this.rotY = 0;
        this.rotZ = 0;
    }

    public abstract boolean isIntersecting(Vector3f point);
    public abstract boolean isIntersecting(OBB obb);
    public abstract boolean isOnTopOf(OBB obb);

    protected abstract void rotateX(double rotation);
    protected abstract void rotateY(double rotation);
    protected abstract void rotateZ(double rotation);

    protected abstract void setNodes();

    public void rotX(float rotX) {
        rotateX(rotX);
        this.rotX = rotX;
    }

    public void rotY(float rotY) {
        rotateY(rotY);
        this.rotY = rotY;
    }

    public void rotZ(float rotZ) {
        rotateZ(rotZ);
        this.rotZ = rotZ;
    }

    public void move(Vector3f velocity) {
        Vector3f newCenter = new Vector3f(this.center);
        newCenter.x += velocity.x;
        newCenter.y += velocity.y;
        newCenter.z += velocity.z;

        newCenter = Maths.clamp(new Vector3f(newCenter), new Vector3f(1, -1000, 1), new Vector3f(Terrain.getSIZE() - 1, 1000, Terrain.getSIZE() - 1));

        Vector3f finalVelocity = Vector3f.sub(this.center, newCenter, null);
        this.center = newCenter;
        for (Vector3f node : this.nodes) {
            Vector3f.add(node, finalVelocity, node);
        }
    }

    public void setNewCenter(Vector3f newCenter) {
        this.center = newCenter;
        setNodes();
    }

    public Vector3f getCenter() {
        return center;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
