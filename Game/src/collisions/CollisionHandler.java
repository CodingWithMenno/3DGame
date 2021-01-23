package collisions;

import entities.Entity;
import entities.MovableEntity;

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
            if (!movableEntity.isMoving()) {
                 continue;
            }

            for (Entity entity : this.entities) {
                if (entity == movableEntity || !entity.hasCollisions()) {
                    continue;
                }

                for (AABB box1 : movableEntity.getCollisionBoxes()) {
                    for (AABB box2 : entity.getCollisionBoxes()) {
                        if (box1.isOnTopOf(box2)) {
                            movableEntity.revertVerticalDown();
                        } else if (box1.isStandingAgainst(box2)) {
                            movableEntity.revertHorizontal();
                        }
                    }
                }
            }
        }
    }
}
