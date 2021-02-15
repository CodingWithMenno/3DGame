package terrains;

import audio.AudioSource;
import entities.Entity;
import entities.MovableEntity;
import renderEngine.DisplayManager;
import user.Settings;
import org.lwjgl.util.vector.Vector3f;
import particles.ParticleSystem;
import textures.TerrainTexture;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;

public class Biome {

    private static final float FADE_IN = 0.1f;
    private static final float FADE_OUT = 3f;

    private TerrainTexture groundTexture;
    private final int separationHeight;
    private final boolean aboveSeparation;

    private ParticleSystem particleSystem;
    private List<Entity> entities;

    private AudioSource backgroundSound;
    private int soundBuffer;

    public static BiomeBuilder builder(TerrainTexture groundTexture, int separationHeight, boolean aboveSeparation) {
        return new BiomeBuilder(groundTexture, separationHeight, aboveSeparation);
    }

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
        this.backgroundSound = new AudioSource(0);
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

        this.backgroundSound.fadeTo(0, FADE_OUT);
        if (this.backgroundSound.getVolume() == 0) {
            this.backgroundSound.pause();
        }
    }

    public void resumeBackgroundSound() {
        if (this.backgroundSound == null) {
            return;
        }

        this.backgroundSound.fadeTo(Settings.MAX_BIOME_SOUND, FADE_IN);
        if (!this.backgroundSound.isPlaying()) {
            this.backgroundSound.resume();
        }
    }

    public void continueBiome() {
        if (this.backgroundSound != null) {
            this.backgroundSound.resume();
        }
    }

    public void pauseBiome() {
        if (this.backgroundSound != null) {
            this.backgroundSound.pause();
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
