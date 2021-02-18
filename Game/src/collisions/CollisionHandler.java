package collisions;

import objects.entities.Entity;
import objects.entities.MovableEntity;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;

public class CollisionHandler {

    private List<Entity> entities;

    private List<MovableEntity> movableEntities;

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
        this.movableEntities = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity instanceof MovableEntity) {
                this.movableEntities.add((MovableEntity) entity);
            }
        }
    }

    public void checkCollisions() {
        for (MovableEntity movableEntity : this.movableEntities) {
            if (!movableEntity.isMovingAbove(0)) {
                 continue;
            }

            for (Entity entity : this.entities) {
                if (entity == movableEntity || !entity.hasCollisions()) {
                    continue;
                }

                if (Maths.getDistanceBetween(new Vector3f(movableEntity.getPosition()), new Vector3f(entity.getPosition())) > 250) {
                    continue;
                }

                for (OBB box1 : movableEntity.getCollisionBoxes()) {
                    for (OBB box2 : entity.getCollisionBoxes()) {
                        int collisionValue = box2.isIntersecting(box1);

                        if (collisionValue == 1) {
                            movableEntity.onCollided(new Collision(movableEntity, entity, true, false));
                        } else if (collisionValue == 2) {
                            movableEntity.onCollided(new Collision(movableEntity, entity, false, true));
                        }
                    }
                }
            }
        }
    }
}
