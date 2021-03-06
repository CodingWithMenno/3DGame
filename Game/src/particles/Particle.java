package particles;


import objects.Camera;
import objects.entities.Entity;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Particle {

    private static final float GRAVITY = Entity.GRAVITY;

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float rotation;
    private float scale;

    private ParticleTexture texture;
    private Vector2f texOffset1 = new Vector2f();
    private Vector2f texOffset2 = new Vector2f();
    private float blend;

    private float elapsedTime = 0;
    private float distanceFromCamera;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength,
                    float rotation, float scale) {

        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;

        ParticleMaster.addParticle(this);
    }

    //Returns false if particle lives longer then its lifeLength
    protected boolean update(Camera camera) {
        this.velocity.y += GRAVITY * this.gravityEffect * DisplayManager.getDelta();

        Vector3f change = new Vector3f(velocity);
        change.scale(DisplayManager.getDelta());

        this.position = Vector3f.add(change, this.position, null);
        this.distanceFromCamera = Vector3f.sub(camera.getPosition(), this.position, null).lengthSquared();
        updateTextureCoordInfo();
        this.elapsedTime += DisplayManager.getDelta();
        return this.elapsedTime < this.lifeLength;
    }

    private void updateTextureCoordInfo() {
        float lifeFactor = this.elapsedTime / this.lifeLength;
        int stageCount = this.texture.getNumberOfRows() * this.texture.getNumberOfRows();
        float atlasProgression = lifeFactor * stageCount;

        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
        this.blend = atlasProgression % 1;
        setTextureOffset(this.texOffset1, index1);
        setTextureOffset(this.texOffset2, index2);
    }

    private void setTextureOffset(Vector2f offset, int index) {
        int column = index % this.texture.getNumberOfRows();
        int row = index / this.texture.getNumberOfRows();
        offset.x = (float) column / this.texture.getNumberOfRows();
        offset.y = (float) row / texture.getNumberOfRows();
    }

    public Vector2f getTexOffset1() {
        return texOffset1;
    }

    public Vector2f getTexOffset2() {
        return texOffset2;
    }

    public float getBlend() {
        return blend;
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public float getDistanceFromCamera() {
        return distanceFromCamera;
    }
}
