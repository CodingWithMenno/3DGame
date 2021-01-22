package particles;


import entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Particle {

    private static final float GRAVITY = Entity.GRAVITY;

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float rotation;
    private float scale;

    private float elapsedTime = 0;

    public Particle(Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;

        ParticleMaster.addParticle(this);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    //Returns false if particle lives longer then its lifeLength
    protected boolean update() {
        this.velocity.y += GRAVITY * this.gravityEffect * DisplayManager.getDelta();

        Vector3f change = new Vector3f(velocity);
        change.scale(DisplayManager.getDelta());

        this.position = Vector3f.add(change, this.position, null);
        this.elapsedTime += DisplayManager.getDelta();
        return this.elapsedTime < this.lifeLength;
    }
}
