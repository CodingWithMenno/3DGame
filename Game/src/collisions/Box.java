package collisions;

import org.lwjgl.util.vector.Vector3f;

public class Box extends OBB {

    public Box(Vector3f center, Vector3f dimensions) {
        super(center, dimensions);

        super.nodes.add(new Vector3f(super.center.x - super.dimensions.x, super.center.y - super.dimensions.y, super.center.z - super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x - super.dimensions.x, super.center.y - super.dimensions.y, super.center.z + super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x - super.dimensions.x, super.center.y + super.dimensions.y, super.center.z - super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x - super.dimensions.x, super.center.y + super.dimensions.y, super.center.z + super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x + super.dimensions.x, super.center.y - super.dimensions.y, super.center.z - super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x + super.dimensions.x, super.center.y - super.dimensions.y, super.center.z + super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x + super.dimensions.x, super.center.y + super.dimensions.y, super.center.z - super.dimensions.z));
        super.nodes.add(new Vector3f(super.center.x + super.dimensions.x, super.center.y + super.dimensions.y, super.center.z + super.dimensions.z));
    }

    @Override
    public boolean isIntersecting(Vector3f point) {
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

        double px = Math.abs(Vector3f.dot(dirVec, dir1)) * 2;
        double py = Math.abs(Vector3f.dot(dirVec, dir2)) * 2;
        double pz = Math.abs(Vector3f.dot(dirVec, dir3)) * 2;

        return (px <= size1 && py <= size2 && pz <= size3);
    }

    @Override
    public void rotateX(double rotation) {
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
    public void rotateY(double rotation) {
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
    public void rotateZ(double rotation) {
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


}
