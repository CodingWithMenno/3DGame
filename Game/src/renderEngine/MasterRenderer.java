package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    private StaticShader shader;
    private EntityRenderer entityRenderer;

    private TerrainShader terrainShader;
    private TerrainRenderer terrainRenderer;

    private Map<TexturedModel, List<Entity>> entities;
    private List<Terrain> terrains;

    public MasterRenderer() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        createProjectionMatrix();

        this.entities = new HashMap<>();
        this.terrains = new ArrayList<>();
        this.shader = new StaticShader();
        this.entityRenderer = new EntityRenderer(this.shader, this.projectionMatrix);
        this.terrainShader = new TerrainShader();
        this.terrainRenderer = new TerrainRenderer(this.terrainShader, this.projectionMatrix);
    }

    public void render(Light sun, Camera camera) {
        prepare();
        this.shader.start();
        this.shader.loadLight(sun);
        this.shader.loadViewMatrix(camera);
        this.entityRenderer.render(this.entities);
        this.shader.stop();

        this.terrainShader.start();
        this.terrainShader.loadLight(sun);
        this.terrainShader.loadViewMatrix(camera);
        this.terrainRenderer.render(this.terrains);
        this.terrainShader.stop();

        this.terrains.clear();
        this.entities.clear();
    }

    public void processTerrain(Terrain terrain) {
        this.terrains.add(terrain);
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = this.entities.get(entityModel);

        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            this.entities.put(entityModel, newBatch);
        }
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1);
    }

    private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.m00 = x_scale;
        this.projectionMatrix.m11 = y_scale;
        this.projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        this.projectionMatrix.m23 = -1;
        this.projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        this.projectionMatrix.m33 = 0;
    }

    public void cleanUp() {
        this.shader.cleanUp();
        this.terrainShader.cleanUp();
    }
}
