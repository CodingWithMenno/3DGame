package terrains;

import textures.TerrainTexture;

public class Biome {

    private TerrainTexture groundTexture;
    private final int separationHeight;

    public Biome(TerrainTexture groundTexture, int separationHeight) {
        this.groundTexture = groundTexture;
        this.separationHeight = separationHeight;
    }

    public TerrainTexture getGroundTexture() {
        return groundTexture;
    }

    public int getSeparationHeight() {
        return separationHeight;
    }
}
