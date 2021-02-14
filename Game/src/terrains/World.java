package terrains;

import entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;
import water.Water;

import java.util.ArrayList;
import java.util.List;

public class World {

    private static final float WATER_HEIGHT = -15;

    private Terrain terrain;
    private Water water;
    private List<Entity> entities;

    public World(Terrain terrain, Water water) {
        this.terrain = terrain;
        this.water = water;

        this.entities = new ArrayList<>();
        setAllEntities();
    }

    public void update(Vector3f playerPos) {
        setAllEntities();

        Biome currentBiome = isInBiome(playerPos.y);
        List<Biome> biomes = this.terrain.getBiomes();
        for (int i = 0; i < biomes.size(); i++) {
            if (currentBiome == biomes.get(i)) {
                biomes.get(i).resumeBackgroundSound();
                biomes.get(i).update(this.terrain, new Vector3f(playerPos), true);
            } else {
                biomes.get(i).pauseBackgroundSound();
                biomes.get(i).update(this.terrain, new Vector3f(playerPos), false);
            }
        }
    }

    public void pauseWorld() {
        List<Biome> biomes = this.terrain.getBiomes();
        for (int i = 0; i < biomes.size(); i++) {
            biomes.get(i).pauseBiome();
        }
    }

    private void setAllEntities() {
        this.entities.clear();

        for (Biome biome : this.terrain.getBiomes()) {
            this.entities.addAll(biome.getEntities());
        }
    }

    public void addEntityToCorrectBiome(Entity entity) {
        float enitiyY = entity.getPosition().getY();

        Biome biome = isInBiome(enitiyY);
        if (biome != null) {
            biome.addEntity(entity);
        }

        setAllEntities();
    }

    public List<Entity> getEntitiesFromCurrentBiome(Vector3f position) {
        Biome biome = isInBiome(position.y);
        return biome.getEntities();
    }

    public List<Entity> getEntitiesFromDistance(Vector3f position, float distance) {
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : this.entities) {
            if (Maths.getDistanceBetween(new Vector3f(entity.getPosition()), position) < distance) {
                entities.add(entity);
            }
        }
        return entities;
    }

    private Biome isInBiome(float yPos) {
        for (int i = 0; i < this.terrain.getBiomes().size(); i++) {
            if (yPos < this.terrain.getBiomes().get(i).getSeparationHeight() && !this.terrain.getBiomes().get(i).isAboveSeparation()) {
                try {
                    if (yPos > this.terrain.getBiomes().get(i - 1).getSeparationHeight()) {
                        return this.terrain.getBiomes().get(i);
                    }
                } catch (IndexOutOfBoundsException e) {
                    return this.terrain.getBiomes().get(i);
                }
            }

            if (yPos > this.terrain.getBiomes().get(i).getSeparationHeight() && this.terrain.getBiomes().get(i).isAboveSeparation()) {
                try {
                    if (yPos < this.terrain.getBiomes().get(i + 1).getSeparationHeight()) {
                        return this.terrain.getBiomes().get(i);
                    }
                } catch (IndexOutOfBoundsException e) {
                    return this.terrain.getBiomes().get(i);
                }
            }
        }
        return null;
    }

    public void cleanUp() {
        for (Biome biome : this.terrain.getBiomes()) {
            biome.cleanUp();
        }

        this.water.getWaterFrameBuffers().cleanUp();
        this.water.getWaterShader().cleanUp();
    }

    public static float getWaterHeight() {
        return WATER_HEIGHT;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Water getWater() {
        return water;
    }

    public Terrain getTerrain() {
        return terrain;
    }
}
