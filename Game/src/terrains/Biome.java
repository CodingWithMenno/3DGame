package terrains;

import textures.TerrainTexture;

public class Biome {

    private TerrainTexture groundTexture;
    private final int minHeight;
    private final int maxHeight;

    public Biome(TerrainTexture groundTexture, int minHeight, int maxHeight) {
        this.groundTexture = groundTexture;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public TerrainTexture getGroundTexture() {
        return groundTexture;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
