package terrains;

import objects.entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import toolbox.GameTimer;
import toolbox.Maths;
import water.Water;

import java.util.ArrayList;
import java.util.List;

public class World {

    private static final float WATER_HEIGHT = -15;

    private Terrain terrain;
    private Water water;
    private List<Entity> entities;

    private Biome currentBiome;

    private GameTimer backgroundSoundTimer;
    private static final float TIME_TO_START_BACKGROUND_SOUNDS = 1.5f;

    public World(Terrain terrain, Water water) {
        this.terrain = terrain;
        this.water = water;

        this.entities = new ArrayList<>();
        setAllEntities();

        this.currentBiome = this.terrain.getBiomes().get(0);

        this.backgroundSoundTimer = new GameTimer(TIME_TO_START_BACKGROUND_SOUNDS);
    }

    public void update(Vector3f playerPos) {
        setAllEntities();

        Biome currentBiome = isInBiome(playerPos.y);
        if (this.currentBiome != currentBiome) {
            this.currentBiome = currentBiome;
            this.backgroundSoundTimer = new GameTimer(TIME_TO_START_BACKGROUND_SOUNDS);
        }

        this.backgroundSoundTimer.updateTimer();

        List<Biome> biomes = this.terrain.getBiomes();
        for (int i = 0; i < biomes.size(); i++) {
            if (this.currentBiome == biomes.get(i)) {
                biomes.get(i).update(this.terrain, new Vector3f(playerPos), true);
                biomes.get(i).playBackgroundSounds(0);
                continue;
            } else {
                biomes.get(i).update(this.terrain, new Vector3f(playerPos), false);
            }


            //Sound fading between biomes
            if (biomes.get(i).isAboveSeparation()) {
                if (playerPos.y < biomes.get(i).getSeparationHeight()) {
                    biomes.get(i).playBackgroundSounds(Maths.difference(playerPos.y, biomes.get(i).getSeparationHeight()));
                } else {
                    try {
                        biomes.get(i).playBackgroundSounds(Maths.difference(playerPos.y, biomes.get(i + 1).getSeparationHeight()));
                    } catch (IndexOutOfBoundsException e) {
                        biomes.get(i).playBackgroundSounds(0);
                    }
                }
            } else {
                if (playerPos.y < biomes.get(i).getSeparationHeight()) {
                    try {
                        biomes.get(i).playBackgroundSounds(Maths.difference(playerPos.y, biomes.get(i - 1).getSeparationHeight()));
                    } catch (IndexOutOfBoundsException e) {
                        biomes.get(i).playBackgroundSounds(0);
                    }
                } else {
                    biomes.get(i).playBackgroundSounds(Maths.difference(playerPos.y, biomes.get(i).getSeparationHeight()));
                }
            }
        }
    }

    public void updateBackGroundSoundsPos(Vector3f pos) {
        for (Biome biome : this.terrain.getBiomes()) {
            biome.setSoundsPosition(new Vector3f(pos));
        }
    }

    public void pauseWorld() {
        List<Biome> biomes = this.terrain.getBiomes();
        for (int i = 0; i < biomes.size(); i++) {
            biomes.get(i).pauseBiome();
        }
    }

    public void continueWorld() {
        List<Biome> biomes = this.terrain.getBiomes();
        for (int i = 0; i < biomes.size(); i++) {
            biomes.get(i).continueBiome();
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
