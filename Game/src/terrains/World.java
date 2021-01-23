package terrains;

import entities.Entity;
import entities.Player;
import org.lwjgl.util.vector.Vector3f;
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

    public void update(Player player) {
        setAllEntities();

        for (Biome biome : this.terrain.getBiomes()) {
            biome.update(this.terrain, new Vector3f(player.getPosition()));
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

    public List<Entity> getEntitiesFromBiome(Vector3f position) {
        Biome biome = isInBiome(position.y);
        return biome.getEntities();
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

    public static float getWaterHeight() {
        return WATER_HEIGHT;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
