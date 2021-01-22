package textures;

public class TerrainTexturePack {

    private TerrainTexture rTexture;
    private TerrainTexture gTexture;
    private TerrainTexture bTexture;
    private TerrainTexture aTexture;

    public TerrainTexturePack(TerrainTexture rTexture, TerrainTexture gTexture, TerrainTexture bTexture, TerrainTexture aTexture) {
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
        this.aTexture = aTexture;
    }

    public TerrainTexture getrTexture() {
        return rTexture;
    }

    public TerrainTexture getgTexture() {
        return gTexture;
    }

    public TerrainTexture getbTexture() {
        return bTexture;
    }

    public TerrainTexture getaTexture() {
        return aTexture;
    }
}
