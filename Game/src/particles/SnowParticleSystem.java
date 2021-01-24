package particles;

import entities.Player;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class SnowParticleSystem extends ParticleSystem {

    private static final int DISTANCE_FROM_CENTER = 150;

    private Random random;

    public SnowParticleSystem(ParticleTexture texture) {
        super(texture, 200, 8, 0.01f, 5);
        this.random = new Random();
    }

    protected void emitParticle(Vector3f center){
        float x = (this.random.nextInt(DISTANCE_FROM_CENTER) - DISTANCE_FROM_CENTER / 2.0f) + center.getX();
        float y = (this.random.nextInt(DISTANCE_FROM_CENTER / 3) + 20) + center.getY();
        float z = (this.random.nextInt(DISTANCE_FROM_CENTER) - DISTANCE_FROM_CENTER / 2.0f) + center.getZ();

        Vector3f velocity = new Vector3f(0, -1, 0);
        velocity.normalise();
        velocity.scale(this.speed);

        new Particle(this.texture, new Vector3f(x, y, z), velocity, this.gravityComplient, this.lifeLength, 0, 1);
    }
}
