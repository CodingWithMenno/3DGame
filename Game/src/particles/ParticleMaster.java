package particles;

import entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.Loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleMaster {

    private static List<Particle> particles;
    private static ParticleRenderer renderer;

    public static void init(Loader loader, Matrix4f projectionMatrix) {
        particles = new ArrayList<>();
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }

    public static void update() {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            boolean isAlive = particle.update();

            if (!isAlive) {
                iterator.remove();
            }
        }
    }

    public static void renderParticles(Camera camera) {
        renderer.render(particles, camera);
    }

    public static void addParticle(Particle particle) {
        particles.add(particle);
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }
}
