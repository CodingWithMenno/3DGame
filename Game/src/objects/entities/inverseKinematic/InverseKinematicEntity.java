package objects.entities.elaborated;

import collisions.Collision;
import objects.entities.MovableEntity;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;

public class InverseKinematicEntity extends MovableEntity {




    public InverseKinematicEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    @Override
    protected void update(Terrain terrain) {

    }

    @Override
    public void onCollided(Collision collision) {

    }

    @Override
    protected void resetVerticalSpeed() {

    }
}
