package particles;

import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class ParticleSystem {

    protected float pps;
    protected float speed;
    protected float gravityComplient;
    protected float lifeLength;
    protected ParticleTexture texture;

    public ParticleSystem(ParticleTexture texture, float pps, float speed, float gravityComplient, float lifeLength) {
        this.texture = texture;
        this.pps = pps;
        this.speed = speed;
        this.gravityComplient = gravityComplient;
        this.lifeLength = lifeLength;
    }

    public void generateParticles(Vector3f systemCenter){
        float delta = DisplayManager.getDelta();
        float particlesToCreate = this.pps * delta;
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 1;

        for(int i = 0; i < count; i++) {
            emitParticle(systemCenter);
        }

        if(Math.random() < partialParticle) {
            emitParticle(systemCenter);
        }
    }

    protected void emitParticle(Vector3f center){
        float dirX = (float) Math.random() * 2f - 1f;
        float dirZ = (float) Math.random() * 2f - 1f;
        Vector3f velocity = new Vector3f(dirX, 1, dirZ);
        velocity.normalise();
        velocity.scale(this.speed);
        new Particle(this.texture, new Vector3f(center), velocity, this.gravityComplient, this.lifeLength, 0, 1);
    }
}
