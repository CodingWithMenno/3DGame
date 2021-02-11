package collisions;

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

//    protected List<Vector3f> collisionPoints;

    public OBB(Vector3f center, Vector3f dimensions) {
        this.center = center;
        this.dimensions = dimensions;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.rotX = 0;
        this.rotY = 0;
        this.rotZ = 0;

//       this.collisionPoints = new ArrayList<>();
    }

    public abstract boolean isIntersecting(Vector3f point);
    //returns 0 if no collision, 1 if normal collision, 2 if obb2 is on top of this
    public abstract int isIntersecting(OBB obb2);

    protected abstract void rotateX(double rotation);
    protected abstract void rotateY(double rotation);
    protected abstract void rotateZ(double rotation);

    protected abstract void setNodes();

    public void rotX(float rotX) {
        rotateX((float) Math.toRadians(rotX));
        this.rotX = (this.rotX + rotX);
    }

    public void rotY(float rotY) {
        rotateY((float) Math.toRadians(rotY));
        this.rotY = (this.rotY + rotY);
    }

    public void rotZ(float rotZ) {
        rotateZ((float) Math.toRadians(rotZ));
        this.rotZ = (this.rotZ + rotZ);
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
            Vector3f.sub(node, finalVelocity, node);
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

    public List<Vector3f> getNodes() {
        return nodes;
    }

    public Vector3f getDimensions() {
        return dimensions;
    }

//    public List<Vector3f> getCollisionPoints() {
//        return collisionPoints;
//    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        OBB obb = (OBB) super.clone();

        obb.center = new Vector3f(this.center);
        obb.dimensions = new Vector3f(this.dimensions);

        obb.nodes = new ArrayList<>();
        for (Vector3f node : this.nodes) {
            obb.nodes.add(new Vector3f(node));
        }

        obb.edges = new ArrayList<>();
        for (Vector2f edge : this.edges) {
            obb.edges.add(new Vector2f(edge));
        }

        return obb;
    }
}
