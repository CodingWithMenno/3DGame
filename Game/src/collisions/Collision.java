package collisions;

import objects.entities.Entity;
import objects.entities.MovableEntity;
import org.lwjgl.util.vector.Vector3f;

public class Collision {

    private Entity entity1;
    private Vector3f velocity1;

    private Entity entity2;
    private Vector3f velocity2;

    private boolean hitFromSide;
    private boolean hitFromTop;

    public Collision(Entity entity1, Entity entity2, boolean hitFromSide, boolean hitFromTop) {
        this.entity1 = entity1;
        this.entity2 = entity2;
        this.hitFromSide = hitFromSide;
        this.hitFromTop = hitFromTop;

        if (this.entity1 instanceof MovableEntity) {
            this.velocity1 = new Vector3f(((MovableEntity) this.entity1).getVelocity());
        }

        if (this.entity2 instanceof MovableEntity) {
            this.velocity2 = new Vector3f(((MovableEntity) this.entity2).getVelocity());
        }
    }

    public Entity getEntity1() {
        return entity1;
    }

    public Vector3f getVelocity1() {
        return velocity1;
    }

    public Entity getEntity2() {
        return entity2;
    }

    public Vector3f getVelocity2() {
        return velocity2;
    }

    public boolean isHitFromSide() {
        return hitFromSide;
    }

    public boolean isHitFromTop() {
        return hitFromTop;
    }
}
