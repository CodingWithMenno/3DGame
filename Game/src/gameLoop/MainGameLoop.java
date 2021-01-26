package gameLoop;

import animation.AnimatedModel;
import animation.Animation;
import collisions.CollisionHandler;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.elaborated.Player;
import guis.*;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particles.ParticleMaster;
import particles.ParticleTexture;
import particles.elaborated.PollParticleSystem;
import particles.elaborated.SnowParticleSystem;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ObjLoader;
import terrains.Biome;
import terrains.BiomeBuilder;
import terrains.Terrain;
import terrains.World;
import textures.ModelTexture;
import textures.TerrainTexture;
import water.Water;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop implements Scene {

    private MasterRenderer renderer;
    private Loader loader;

    private Camera camera;
    private Player player;
    private World world;

    private GuiManager guiManager;
    private GuiRenderer guiRenderer;

    private CollisionHandler collisionHandler;

    private List<Light> lights;

    @Override
    public void setup() {
        Mouse.setGrabbed(true);
        this.loader = new Loader();
        Terrain terrain = new Terrain(0, -1, this.loader);
        Random random = new Random();


        //*************PLAYER SETUP**************
        this.player = setupPlayer(this.loader);

        this.camera = new Camera(this.player, terrain);
        this.renderer = new MasterRenderer(this.camera);
        ParticleMaster.init(this.loader, this.renderer.getProjectionMatrix());


        //***********WORLD SETUP****************
        this.world = setupWorld(terrain, this.loader, this.renderer, random, World.getWaterHeight(), Terrain.getSIZE());


        //**********LIGHTS SETUP*****************
        TexturedModel postModel = new TexturedModel(ObjLoader.loadObjModel("lamp/LampPost", this.loader),
                new ModelTexture(this.loader.loadTexture("lamp/LampPostTexture")));
        Vector3f dimensions = ObjLoader.getLastDimensions();
        this.world.addEntityToCorrectBiome(new Entity(postModel, 2, new Vector3f(100, terrain.getHeightOfTerrain(100, -150), -150), 0, 0, 0, 1f, dimensions));

        this.lights = new ArrayList<>();
        this.lights.add(new Light(new Vector3f(1000, 500000, -100000), new Vector3f(0.7f, 0.7f, 0.7f)));
        this.lights.add(new Light(new Vector3f(103.2f, terrain.getHeightOfTerrain(100, -150) + 4.5f, -150), new Vector3f(1f, 1f, 0), new Vector3f(1f, 0.01f, 0.002f)));


        //**************GUI SETUP****************
        this.guiManager = new GuiManager();
//        GuiTexture button = new Button(this.loader.loadTexture("Health"), this.loader.loadTexture("water/WaterDUDV"), this.loader.loadTexture("water/WaterNormal"), new Vector2f(-0.5f, -0.5f), new Vector2f(0.25f, 0.25f));
//        this.guiManager.addTexture(button);

        this.guiRenderer = new GuiRenderer(this.loader);


        //***********COLLISION SETUP************
        this.collisionHandler = new CollisionHandler();
    }

    @Override
    public void resume() {
        Mouse.setGrabbed(true);
    }

    @Override
    public void update() {
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            MainManager.stackScene(new PauseScreen());
        }

        this.camera.move();
        this.player.updateEntity(this.world.getTerrain());
        this.player.updateAnimation();
        this.world.update(new Vector3f(this.player.getPosition()));
        this.guiManager.update();
        ParticleMaster.update(this.camera);

        List<Entity> entities = new ArrayList<>(this.world.getEntities());
        entities.add(this.player);
        this.collisionHandler.setEntities(entities);
        this.collisionHandler.checkCollisions();
    }

    @Override
    public void render() {
        //Getting all the entities in the game
        List<Entity> entities = new ArrayList<>(this.world.getEntitiesFromDistance(new Vector3f(this.camera.getPosition()), MasterRenderer.FAR_PLANE));
        entities.add(this.player);

        //Shadow rendering
        this.renderer.renderShadowMap(entities, this.lights.get(0));

        //Water reflection and refraction rendering
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        this.world.getWater().getWaterFrameBuffers().bindReflectionFrameBuffer();
        float distance = 2 * (this.camera.getPosition().y - World.getWaterHeight());
        this.camera.getPosition().y -= distance;
        this.camera.invertPitch();
        this.renderer.renderScene(entities, this.world.getTerrain(), this.lights, this.camera, new Vector4f(0, 1, 0, -World.getWaterHeight()));
        this.camera.getPosition().y += distance;
        this.camera.invertPitch();

        this.world.getWater().getWaterFrameBuffers().bindRefractionFrameBuffer();
        this.renderer.renderScene(entities, this.world.getTerrain(), this.lights, this.camera, new Vector4f(0, -1, 0, World.getWaterHeight() + 0.5f));

        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        this.world.getWater().getWaterFrameBuffers().unbindCurrentFrameBuffer();

        //World and water rendering
        this.renderer.renderScene(entities, this.world.getTerrain(), this.lights, this.camera, new Vector4f(0, -1, 0, 100000));
        this.world.getWater().getWaterRenderer().render(this.world.getWater().getWaterTiles(), this.camera, this.lights.get(0));

        //Particle and GUI rendering
        ParticleMaster.renderParticles(this.camera);
        this.guiRenderer.render(this.guiManager.getGuiTextures());
    }

    @Override
    public void cleanUp() {
        ParticleMaster.cleanUp();
        this.world.getWater().getWaterFrameBuffers().cleanUp();
        this.world.getWater().getWaterShader().cleanUp();
        this.guiRenderer.cleanUp();
        this.renderer.cleanUp();
        this.loader.cleanUp();
    }

    private static Player setupPlayer(Loader loader) {
        ModelTexture foxTexture = new ModelTexture(loader.loadTexture("fox/FoxTexture"));
        TexturedModel foxModel = new TexturedModel(ObjLoader.loadObjModel("fox/Fox", loader), foxTexture);
        List<TexturedModel> foxIdle = new ArrayList<>();
        foxIdle.add(foxModel);
        Animation foxIdleAnimation = new Animation(foxIdle, 10);

        Vector3f dimensions = ObjLoader.getLastDimensions();

        List<TexturedModel> foxModels = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            foxModels.add(new TexturedModel(ObjLoader.loadObjModel("fox/VillagerFox_animations_00000" + i, loader), foxTexture));
        }
        for (int i = 10; i < 32; i++) {
            foxModels.add(new TexturedModel(ObjLoader.loadObjModel("fox/VillagerFox_animations_0000" + i, loader), foxTexture));
        }

        Animation foxRunningAnimation = new Animation(foxModels, 1);
        AnimatedModel foxAnimatedModel = new AnimatedModel(foxIdleAnimation, foxRunningAnimation);

        return new Player(foxAnimatedModel, new Vector3f(500, -500, -200), 0, 0, 0, 0.4f, dimensions);
    }


    private static World setupWorld(Terrain terrain, Loader loader, MasterRenderer renderer, Random random, float waterHeight, float terrainSize) {
        //Dirt biome
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("ground/DirtTexture"));
        BiomeBuilder dirtBiomeBuilder = new BiomeBuilder(rTexture, -5, false);
        TexturedModel grassModel = new TexturedModel(ObjLoader.loadObjModel("grass/GrassModel", loader),
                new ModelTexture(loader.loadTexture("grass/GrassTexture")));
        grassModel.getTexture().setHasTransparency(true);
        grassModel.getTexture().setUseFakeLighting(true);
        grassModel.getTexture().setNumberOfRows(2);
        Entity grassEntity = new Entity(grassModel, random.nextInt(5), new Vector3f(0, 0, 0), 0, random.nextInt(360), 0, 1);
        dirtBiomeBuilder.addRandomEntities(terrain, waterHeight, grassEntity, 1000);
        Biome rBiome = dirtBiomeBuilder.buildBiome();

        //Grass biome
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("ground/GrassTexture"));
        BiomeBuilder grassBiomeBuilder = new BiomeBuilder(gTexture, 80, false);
        grassBiomeBuilder.addRandomEntities(terrain, waterHeight, grassEntity, 1000);
        TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree/Tree", loader),
                new ModelTexture(loader.loadTexture("tree/TreeTexture")));
        Vector3f dimensions = ObjLoader.getLastDimensions();
        Entity treeEntity = new Entity(treeModel, new Vector3f(0, 0, 0), 0, random.nextInt(360), 0, 0.2f, dimensions);
        grassBiomeBuilder.addRandomEntities(terrain, waterHeight, treeEntity, 100);
        ParticleTexture pollTexture = new ParticleTexture(loader.loadTexture("particles/PollTexture"), 1, false);
        grassBiomeBuilder.addParticleSystem(new PollParticleSystem(pollTexture));
        Biome gBiome = grassBiomeBuilder.buildBiome();

        //Stone biome
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("ground/StoneTexture"));
        Biome bBiome = new Biome(bTexture, 150, false);

        //Snow biome
        TerrainTexture aTexture = new TerrainTexture(loader.loadTexture("ground/SnowTexture"));
        BiomeBuilder snowBiomeBuilder = new BiomeBuilder(aTexture, 150, true);
        ParticleTexture snowTexture = new ParticleTexture(loader.loadTexture("particles/SnowTexture"), 1, false);
        snowBiomeBuilder.addParticleSystem(new SnowParticleSystem(snowTexture));
        Biome aBiome = snowBiomeBuilder.buildBiome();

        //Adding all the biomes
        terrain.addBiomes(rBiome, gBiome, bBiome, aBiome);
        Water water = new Water(loader, renderer, waterHeight, terrainSize);
        return new World(terrain, water);
    }
}
