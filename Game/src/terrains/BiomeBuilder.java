package terrains;

import entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import particles.ParticleSystem;
import textures.TerrainTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeBuilder {

    private TerrainTexture groundTexture;
    private int separationHeight;
    private boolean aboveSeparation;

    private ParticleSystem particleSystem;
    private List<Entity> entities;

    private Random random;

    public BiomeBuilder(TerrainTexture groundTexture, int separationHeight, boolean aboveSeparation) {
        this.groundTexture = groundTexture;
        this.separationHeight = separationHeight;
        this.aboveSeparation = aboveSeparation;
        this.entities = new ArrayList<>();
        this.random = new Random();
    }

    public BiomeBuilder addEntity(Entity entity) {
        this.entities.add(entity);
        return this;
    }

    public BiomeBuilder addRandomEntities(Terrain terrain, float waterHeight, Entity entity, int totalEntities) {
        for (int i = 0; i < totalEntities; i++) {
            float x = random.nextFloat() * Terrain.getSIZE();
            float z = random.nextFloat() * Terrain.getSIZE();
            float y = terrain.getHeightOfTerrain(x, z);

            if (y <= waterHeight) { continue; }
            if (y > this.separationHeight && !this.aboveSeparation) { continue; }
            if (y < this.separationHeight && this.aboveSeparation) { continue; }

            Entity newEntity = (Entity) entity.clone();
            newEntity.setPosition(new Vector3f(x, y, z));
            this.entities.add(newEntity);
        }

        return this;
    }

    public BiomeBuilder addParticleSystem(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
        return this;
    }

    public Biome buildBiome() {
        if (this.particleSystem == null) {
            return new Biome(this.groundTexture, this.separationHeight, this.aboveSeparation, this.entities);
        }

        return new Biome(this.groundTexture, this.separationHeight, this.aboveSeparation, this.entities, this.particleSystem);
    }

    public int getSeparationHeight() {
        return separationHeight;
    }
}
