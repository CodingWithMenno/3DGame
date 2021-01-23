package particles;

public class ParticleTexture {

    private int textureID;
    private int numberOfRows;
    private boolean isGlowing;

    public ParticleTexture(int textureID, int numberOfRows, boolean isGlowing) {
        this.textureID = textureID;
        this.numberOfRows = numberOfRows;
        this.isGlowing = isGlowing;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public boolean isGlowing() {
        return isGlowing;
    }
}
