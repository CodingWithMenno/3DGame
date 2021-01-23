package terrains;

import entities.Entity;
import entities.MovableEntity;
import org.lwjgl.util.vector.Vector3f;
import particles.ParticleSystem;
import textures.TerrainTexture;

import java.util.ArrayList;
import java.util.List;

public class Biome {

    private TerrainTexture groundTexture;
    private final int separationHeight;
    private final boolean aboveSeparation;

    private ParticleSystem particleSystem;
    private List<Entity> entities;
    //private Music backGroundMusic;

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

    public void update(Terrain terrain, Vector3f particleCenter) {
        for (Entity entity : this.entities) {
            entity.updateAnimation();

            if (entity instanceof MovableEntity) {
                ((MovableEntity) entity).updateEntity(terrain);
            }
        }

        if (this.particleSystem != null) {
            this.particleSystem.generateParticles(particleCenter);
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
