package collisions;

import javafx.util.Pair;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;

public class Box extends OBB {

    private static final int POINTS_IN_LINE = 20;


    public Box(Vector3f center, Vector3f dimensions) {
        super(center, dimensions);
        setNodes();

        super.edges.add(new Vector2f(0, 1));
        super.edges.add(new Vector2f(1, 3));
        super.edges.add(new Vector2f(3, 2));
        super.edges.add(new Vector2f(2, 0));
        super.edges.add(new Vector2f(4, 5));
        super.edges.add(new Vector2f(5, 7));
        super.edges.add(new Vector2f(7, 6));
        super.edges.add(new Vector2f(6, 4));
        super.edges.add(new Vector2f(0, 4));
        super.edges.add(new Vector2f(1, 5));
        super.edges.add(new Vector2f(2, 6));
        super.edges.add(new Vector2f(3, 7));
    }


    @Override
    public boolean isIntersecting(Vector3f point) {
        Pair<Vector3f, Vector3f> pAndSize = getPAndSize(point);
        Vector3f p = pAndSize.getKey();
        Vector3f size = pAndSize.getValue();

        return (p.x <= size.x && p.y <= size.y && p.z <= size.z);
    }


    @Override
    public boolean isIntersecting(OBB obb) {
        List<Vector3f> points = getClosestPointsFromEdges(obb);


        for (Vector3f point : points) {
            if (isIntersecting(point)) {
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean isOnTopOf(OBB obb) {
        return (isIntersecting(obb) && this.center.y > obb.center.y);
    }


    @Override
    protected void rotateX(double rotation) {
        double sinTheta = Math.sin(rotation);
        double cosTheta = Math.cos(rotation);

        for(int i = 0; i < super.nodes.size(); i++) {
            Vector3f vector = super.nodes.get(i);

            double y = vector.getY() - super.center.y;
            double z = vector.getZ() - super.center.z;

            vector.setY((float) (y * cosTheta - z * sinTheta + super.center.y));
            vector.setZ((float) (z * cosTheta + y * sinTheta + super.center.z));
        }
    }


    @Override
    protected void rotateY(double rotation) {
        double sinTheta = Math.sin(rotation);
        double cosTheta = Math.cos(rotation);

        for(int i = 0; i < super.nodes.size(); i++) {
            Vector3f vector = super.nodes.get(i);

            double x = vector.getX() - super.center.x;
            double z = vector.getZ() - super.center.z;

            vector.setX((float) (x * cosTheta - z * sinTheta + super.center.x));
            vector.setZ((float) (z * cosTheta + x * sinTheta + super.center.z));
        }
    }


    @Override
    protected void rotateZ(double rotation) {
        double sinTheta = Math.sin(rotation);
        double cosTheta = Math.cos(rotation);

        for(int i = 0; i < super.nodes.size(); i++) {
            Vector3f vector = super.nodes.get(i);

            double x = vector.getX() - super.center.x;
            double y = vector.getY() - super.center.y;

            vector.setX((float) (x * cosTheta - y * sinTheta + super.center.x));
            vector.setY((float) (y * cosTheta + x * sinTheta + super.center.y));
        }
    }


    @Override
    protected void setNodes() {
        super.nodes.add(new Vector3f(super.center.x - super.dimensions.x, super.center.y - super.dimensions.y, super.center.z - super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x - super.dimensions.x, super.center.y - super.dimensions.y, super.center.z + super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x - super.dimensions.x, super.center.y + super.dimensions.y, super.center.z - super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x - super.dimensions.x, super.center.y + super.dimensions.y, super.center.z + super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x + super.dimensions.x, super.center.y - super.dimensions.y, super.center.z - super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x + super.dimensions.x, super.center.y - super.dimensions.y, super.center.z + super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x + super.dimensions.x, super.center.y + super.dimensions.y, super.center.z - super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x + super.dimensions.x, super.center.y + super.dimensions.y, super.center.z + super.dimensions.z));
    }


    private Pair<Vector3f, Vector3f> getPAndSize(Vector3f point) {
        Vector3f dir1 = Vector3f.sub(super.nodes.get(6), super.nodes.get(2), null);
        float size1 = dir1.length();
        dir1.x /= size1;
        dir1.y /= size1;
        dir1.z /= size1;

        Vector3f dir2 = Vector3f.sub(super.nodes.get(3), super.nodes.get(2), null);
        float size2 = dir2.length();
        dir2.x /= size2;
        dir2.y /= size2;
        dir2.z /= size2;

        Vector3f dir3 = Vector3f.sub(super.nodes.get(0), super.nodes.get(2), null);
        float size3 = dir3.length();
        dir3.x /= size3;
        dir3.y /= size3;
        dir3.z /= size3;

        Vector3f dirVec = Vector3f.sub(point, super.center, null);

        float px = Math.abs(Vector3f.dot(dirVec, dir1)) * 2;
        float py = Math.abs(Vector3f.dot(dirVec, dir2)) * 2;
        float pz = Math.abs(Vector3f.dot(dirVec, dir3)) * 2;

        return new Pair<>(new Vector3f(px, py, pz), new Vector3f(size1, size2, size3));
    }


    private List<Vector3f> getClosestPointsFromEdges(OBB obb) {
        List<Vector3f> finalPoints = new ArrayList<>();
        for (Vector2f edge : obb.edges) {
            List<Vector3f> points = new ArrayList<>();
            Vector3f point = new Vector3f(obb.nodes.get((int) edge.x));

            float xDif = Maths.difference(point.x, obb.nodes.get((int) edge.y).x);
            float xSteps = xDif / POINTS_IN_LINE;

            float yDif = Maths.difference(point.y, obb.nodes.get((int) edge.y).y);
            float ySteps = yDif / POINTS_IN_LINE;

            float zDif = Maths.difference(point.z, obb.nodes.get((int) edge.y).z);
            float zSteps = zDif / POINTS_IN_LINE;

            for (int i = 0; i < POINTS_IN_LINE; i++) {
                points.add(new Vector3f(point));

                point.x += xSteps;
                point.y += ySteps;
                point.z += zSteps;
            }

            Vector3f closestPoint = null;
            float distance = 1000000000;
            for (Vector3f p : points) {
                Vector3f distanceVec = Vector3f.sub(this.center, p, null);

                if (distanceVec.length() < distance) {
                    distance = distanceVec.length();
                    closestPoint = p;
                }
            }

            if (closestPoint != null) {
                finalPoints.add(closestPoint);
            }
        }

        return finalPoints;
    }
}
