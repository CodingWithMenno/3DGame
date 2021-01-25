package particles.elaborated;

import org.lwjgl.util.vector.Vector3f;
import particles.Particle;
import particles.ParticleSystem;
import particles.ParticleTexture;

import java.util.Random;

public class PollParticleSystem extends ParticleSystem {

    private static final int DISTANCE_FROM_CENTER = 200;

    private Random random;

    public PollParticleSystem(ParticleTexture texture) {
        super(texture, 0.5f, 10, 0.001f, 10);
        this.random = new Random();
    }

    protected void emitParticle(Vector3f center){
        float x = (this.random.nextInt(DISTANCE_FROM_CENTER) - DISTANCE_FROM_CENTER / 2.0f) + center.getX();
        float y = this.random.nextInt(DISTANCE_FROM_CENTER / 3) + center.getY();
        float z = (this.random.nextInt(DISTANCE_FROM_CENTER) - DISTANCE_FROM_CENTER / 2.0f) + center.getZ();

        Vector3f velocity = new Vector3f(this.random.nextFloat() * 2f - 1f, this.random.nextFloat() * 2f - 1f, this.random.nextFloat() * 2f - 1f);
        velocity.normalise();
        velocity.scale(this.speed);

        new Particle(this.texture, new Vector3f(x, y, z), velocity, this.gravityComplient, this.lifeLength, 0, 0.5f);
    }
}
