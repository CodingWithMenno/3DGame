package particles;

import objects.Camera;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.Loader;

import java.util.*;

public class ParticleMaster {

    private static Map<ParticleTexture, List<Particle>> particles;
    private static ParticleRenderer renderer;

    public static void init(Loader loader, Matrix4f projectionMatrix) {
        particles = new HashMap<>();
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }

    public static void update(Camera camera) {
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();

            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                boolean isAlive = particle.update(camera);

                if (!isAlive) {
                    iterator.remove();

                    if (list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
            InsertionSort.sortHighToLow(list);
        }
    }

    public static void renderParticles(Camera camera) {
        renderer.render(particles, camera);
    }

    public static void addParticle(Particle particle) {
        List<Particle> list = particles.get(particle.getTexture());

        if (list == null) {
            list = new ArrayList<>();
            particles.put(particle.getTexture(), list);
        }

        list.add(particle);
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }
}
