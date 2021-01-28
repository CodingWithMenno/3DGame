package terrains;

import audio.AudioSource;
import entities.Entity;
import entities.MovableEntity;
import input.Settings;
import org.lwjgl.util.vector.Vector3f;
import particles.ParticleSystem;
import textures.TerrainTexture;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;

public class Biome {

    private TerrainTexture groundTexture;
    private final int separationHeight;
    private final boolean aboveSeparation;

    private ParticleSystem particleSystem;
    private List<Entity> entities;

    private AudioSource backgroundSound;
    private int soundBuffer;

    public Biome(TerrainTexture groundTexture, int separationHeight, boolean aboveSeparation) {
        this.groundTexture = groundTexture;
        this.separationHeight = separationHeight;
        this.aboveSeparation = aboveSeparation;
        this.entities = new ArrayList<>();
    }

    public Biome(TerrainTexture groundTexture, int separationHeight, boolean aboveSeparation, List<Entity> entities) {
        this.groundTexture = groundTexture;
        this.separationHeight = separationHeight;
        this.aboveSeparation = aboveSeparation;
        this.entities = entities;
    }

    public Biome(TerrainTexture groundTexture, int separationHeight, boolean aboveSeparation, List<Entity> entities, ParticleSystem particleSystem) {
        this(groundTexture, separationHeight, aboveSeparation, entities);
        this.particleSystem = particleSystem;
    }

    public Biome(TerrainTexture groundTexture, int separationHeight, boolean aboveSeparation, List<Entity> entities, int backgroundSound) {
        this(groundTexture, separationHeight, aboveSeparation, entities);
        this.backgroundSound = new AudioSource();
        this.soundBuffer = backgroundSound;
        this.backgroundSound.setLooping(true);

        this.backgroundSound.play(this.soundBuffer);
    }

    public Biome(TerrainTexture groundTexture, int separationHeight, boolean aboveSeparation, List<Entity> entities, ParticleSystem particleSystem, int backgroundSound) {
        this(groundTexture, separationHeight, aboveSeparation, entities, backgroundSound);
        this.particleSystem = particleSystem;
    }

    public void update(Terrain terrain, Vector3f playerPos, boolean isInBiome) {
        for (Entity entity : this.entities) {
            entity.updateAnimation();

            if (entity instanceof MovableEntity) {
                ((MovableEntity) entity).updateEntity(terrain);
            }
        }

        if (this.particleSystem != null && isInBiome) {
            this.particleSystem.generateParticles(playerPos);
        }
    }

    public void pauseBackgroundSound() {
        if (this.backgroundSound == null) {
            return;
        }

        this.backgroundSound.setVolume(Maths.lerp(this.backgroundSound.getVolume(), 0, 0.03f));
        if (this.backgroundSound.getVolume() == 0) {
            this.backgroundSound.pause();
        }
    }

    public void resumeBackgroundSound() {
        if (this.backgroundSound == null) {
            return;
        }

        this.backgroundSound.setVolume(Maths.lerp(this.backgroundSound.getVolume(), Settings.MAX_BIOME_SOUND, 0.001f));
        if (!this.backgroundSound.isPlaying()) {
            this.backgroundSound.resume();
        }
    }

    public void cleanUp() {
        if (this.backgroundSound != null) {
            this.backgroundSound.delete();
        }
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public TerrainTexture getGroundTexture() {
        return groundTexture;
    }

    public int getSeparationHeight() {
        return separationHeight;
    }

    public boolean isAboveSeparation() {
        return aboveSeparation;
    }
}
