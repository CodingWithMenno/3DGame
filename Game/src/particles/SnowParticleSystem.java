package particles;

import entities.Player;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class SnowParticleSystem extends ParticleSystem {

    private static final int DISTANCE_FROM_PLAYER = 150;

    private float occurrenceHeight;
    private Player player;

    private Random random;

    public SnowParticleSystem(ParticleTexture texture, float occurrenceHeight, Player player) {
        super(texture, 200, 10, 0.1f, 5);
        this.occurrenceHeight = occurrenceHeight;
        this.player = player;
        this.random = new Random();
    }

    protected void emitParticle(Vector3f center){
        Vector3f playerPos = this.player.getPosition();

        if (playerPos.getY() >= this.occurrenceHeight) {
            float x = (this.random.nextInt(DISTANCE_FROM_PLAYER) - DISTANCE_FROM_PLAYER / 2.0f) + playerPos.getX();
            float y = (this.random.nextInt(DISTANCE_FROM_PLAYER / 3) + 20) + playerPos.getY();
            float z = (this.random.nextInt(DISTANCE_FROM_PLAYER) - DISTANCE_FROM_PLAYER / 2.0f) + playerPos.getZ();

            Vector3f velocity = new Vector3f(0, -1, 0);
            velocity.normalise();
            velocity.scale(this.speed);

            new Particle(this.texture, new Vector3f(x, y, z), velocity,this.gravityComplient, this.lifeLength, 0, 1);
        }
    }
}
