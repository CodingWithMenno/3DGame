package collisions;

import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

public class AABB {

    private Vector3f center;
    private Vector3f dimensions; //width, height, depth

    public AABB(Vector3f center, Vector3f dimensions) {
        this.center = new Vector3f(center);
        this.dimensions = new Vector3f(dimensions);

        this.dimensions.x /= 2.5;
        this.dimensions.y /= 2.5;
        this.dimensions.z /= 2.5;

        this.center.x += this.dimensions.x;
        this.center.y += this.dimensions.y;
    }

    public boolean isStandingAgainst(AABB box2) {
        Vector3f distance = Vector3f.sub(this.center, box2.center, null);
        distance.x = Math.abs(distance.x);
        distance.y = Math.abs(distance.y);
        distance.z = Math.abs(distance.z);

        distance.x -= (this.dimensions.x + box2.dimensions.x);
        distance.y -= (this.dimensions.y + box2.dimensions.y);
        distance.z -= (this.dimensions.z + box2.dimensions.z);

        return (distance.x < 0 && distance.y < 0 && distance.z < 0);
    }

    public boolean isOnTopOf(AABB box2) {
        Vector3f distance = Vector3f.sub(this.center, box2.center, null);

        distance.x = Math.abs(distance.x);
        distance.y = Math.abs(distance.y);
        distance.z = Math.abs(distance.z);

        distance.x -= (this.dimensions.x + box2.dimensions.x);
        distance.y -= (this.dimensions.y + box2.dimensions.y);
        distance.z -= (this.dimensions.z + box2.dimensions.z);

        float yDifference = Maths.difference(0, distance.y);
        if (distance.x < 0 && yDifference < 0.5f && distance.z < 0) {
            if (this.center.y > box2.center.y) {
                return true;
            }
        }

        return false;
    }

    public void move(Vector3f velocity) {
        this.center.x += velocity.x;
        this.center.y += velocity.y;
        this.center.z += velocity.z;
    }

    public void setNewCenter(Vector3f newPosition) {
        this.center = newPosition;
    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getDimensions() {
        return dimensions;
    }
}
