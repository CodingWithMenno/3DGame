package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import terrains.Terrain;
import toolbox.Maths;

import java.util.*;

public class MasterRenderer {

    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.01f;
    public static final float FAR_PLANE = 700;

    private static final float SKY_COLOR_RED = 0.005f;
    private static final float SKY_COLOR_GREEN = 0.4f;
    private static final float SKY_COLOR_BLUE = 0.6f;

    private static final float TIME_SPEED = 200;
    private float gameTime = 12000;
    private boolean isNight = true;

    private Matrix4f projectionMatrix;

    private StaticShader shader;
    private EntityRenderer entityRenderer;

    private TerrainShader terrainShader;
    private TerrainRenderer terrainRenderer;

    private Map<TexturedModel, List<Entity>> entities;
    private List<Terrain> terrains;

    private List<Light> lightsToRender = new ArrayList();

    private ShadowMapMasterRenderer shadowMapRenderer;

    public MasterRenderer(Camera camera) {
        enableCulling();
        createProjectionMatrix();

        this.entities = new HashMap<>();
        this.terrains = new ArrayList<>();
        this.shader = new StaticShader();
        this.entityRenderer = new EntityRenderer(this.shader, this.projectionMatrix);
        this.shadowMapRenderer = new ShadowMapMasterRenderer(camera);
        this.terrainShader = new TerrainShader();
        this.terrainRenderer = new TerrainRenderer(this.terrainShader, this.projectionMatrix);
    }

    public void renderScene(List<Entity> entities, Terrain terrain, List<Light> lights, Camera camera, Vector4f clipPlane) {
        getClosestLights(lights, camera);

        //updateTime();

        for (Entity entity : entities) {
            processEntity(entity);
        }

        processTerrain(terrain);
        render(this.lightsToRender, camera, clipPlane);
    }

    private void getClosestLights(List<Light> lights, Camera camera) {
        this.lightsToRender.clear();
        Vector3f cameraPosition = camera.getPosition();
        Map<Light, Float> differences = new HashMap<>();

        for (Light light : lights) {
            differences.put(light, Maths.getDistanceBetween(cameraPosition, light.getPosition()));
        }

        List<Map.Entry<Light, Float> > list =
                new LinkedList<>(differences.entrySet());

        Collections.sort(list, Comparator.comparing(Map.Entry::getValue));

        HashMap<Light, Float> temp = new LinkedHashMap<>();
        for (Map.Entry<Light, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        this.lightsToRender.add(lights.get(0));
        for (Light light : temp.keySet()) {
            if (this.lightsToRender.size() == 5) {
                return;
            }

            this.lightsToRender.add(light);
        }
    }

//    private void updateTime() {
//        if (this.isNight) {
//            this.gameTime += DisplayManager.getDelta() * TIME_SPEED;
//
//            if (this.gameTime >= 24000) {
//                this.isNight = false;
//            }
//
//        } else {
//            this.gameTime -= DisplayManager.getDelta() * TIME_SPEED;
//
//            if (this.gameTime <= 0) {
//                this.isNight = true;
//            }
//        }
//    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        float fogDensity = calculateDensity(FAR_PLANE);
        //Light sun = lights.get(0);
        //float lightColour = Maths.map(this.gameTime, 0, 24000, 0.2f, 1);
        //sun.setColour(new Vector3f(lightColour, lightColour, lightColour));

        prepare();
        this.shader.start();

        this.shader.loadClipPlane(clipPlane);
        this.shader.loadSkyColour(SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE);
        this.shader.loadLights(lights);
        this.shader.loadViewMatrix(camera);
        this.shader.loadDensity(fogDensity);
        this.entityRenderer.render(this.entities, this.shadowMapRenderer.getToShadowMapSpaceMatrix());
        this.shader.stop();

        this.terrainShader.start();
        this.terrainShader.loadClipPlane(clipPlane);
        this.terrainShader.loadSkyColour(SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE);
        this.terrainShader.loadLights(lights);
        this.terrainShader.loadViewMatrix(camera);
        this.terrainShader.loadDensity(fogDensity);
        this.terrainRenderer.render(this.terrains, this.shadowMapRenderer.getToShadowMapSpaceMatrix());
        this.terrainShader.stop();

        this.terrains.clear();
        this.entities.clear();
    }

    private float calculateDensity(float renderDistance) {
        return 1.6f / renderDistance;
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
        GL32.glProvokingVertex(GL32.GL_FIRST_VERTEX_CONVENTION);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE, 1);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
    }

    private void createProjectionMatrix(){
        this.projectionMatrix = new Matrix4f();
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        this.projectionMatrix.m00 = x_scale;
        this.projectionMatrix.m11 = y_scale;
        this.projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        this.projectionMatrix.m23 = -1;
        this.projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        this.projectionMatrix.m33 = 0;
    }

    public void renderShadowMap(List<Entity> entityList, Light sun) {
        for (Entity entity : entityList) {
            processEntity(entity);
        }
        this.shadowMapRenderer.render(this.entities, sun);
        this.entities.clear();
    }

    public int getShadowMapTexture() {
        return this.shadowMapRenderer.getShadowMap();
    }

    public void cleanUp() {
        this.shader.cleanUp();
        this.terrainShader.cleanUp();
        this.shadowMapRenderer.cleanUp();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
