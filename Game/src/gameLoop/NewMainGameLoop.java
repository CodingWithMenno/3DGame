package gameLoop;

import animation.AnimatedModel;
import animation.Animation;
import collisions.CollisionHandler;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particles.ParticleMaster;
import particles.ParticleTexture;
import particles.SnowParticleSystem;
import renderEngine.DisplayManager;
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

public class NewMainGameLoop {

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        Terrain terrain = new Terrain(0, -1, loader);
        Random random = new Random();


        //*************PLAYER SETUP**************
        Player player = setupPlayer(loader);

        Camera camera = new Camera(player, terrain);
        MasterRenderer renderer = new MasterRenderer(camera);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());

        
        //***********WORLD SETUP****************
        World world = setupWorld(terrain, loader, renderer, random, World.getWaterHeight(), Terrain.getSIZE());
        Water water = world.getWater();


        //**********LIGHT SETUP*****************
        TexturedModel postModel = new TexturedModel(ObjLoader.loadObjModel("lamp/LampPost", loader),
                new ModelTexture(loader.loadTexture("lamp/LampPostTexture")));
        Vector3f dimensions = ObjLoader.getLastDimensions();
        world.addEntityToCorrectBiome(new Entity(postModel, 2, new Vector3f(100, terrain.getHeightOfTerrain(100, -150), -150), 0, 0, 0, 1f, dimensions));

        List<Light> lights = new ArrayList<>();
        lights.add(new Light(new Vector3f(1000, 500000, -100000), new Vector3f(0.7f, 0.7f, 0.7f)));
        lights.add(new Light(new Vector3f(103.2f, terrain.getHeightOfTerrain(100, -150) + 4.5f, -150), new Vector3f(1f, 1f, 0), new Vector3f(1f, 0.01f, 0.002f)));


        //**************GUI SETUP****************
//		List<GuiTexture> guiTextures = new ArrayList<>();
//		GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
//		GuiTexture reflection = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		guiTextures.add(shadowMap);
//		guiTextures.add(reflection);
//
//		GuiRenderer guiRenderer = new GuiRenderer(loader);


        //***********COLLISION SETUP************
        CollisionHandler collisionHandler = new CollisionHandler();


        //***********MAIN GAME LOOP**************
        while(!Display.isCloseRequested()) {
            //updating
            camera.move();
            player.updateEntity(terrain);
            player.updateAnimation();
            world.update(new Vector3f(player.getPosition()));
            ParticleMaster.update(camera);

            List<Entity> entities = new ArrayList<>(world.getEntities());
            entities.add(player);
            collisionHandler.setEntities(entities);
            collisionHandler.checkCollisions();

            //rendering
            renderer.renderShadowMap(entities, lights.get(0));

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            water.getWaterFrameBuffers().bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - World.getWaterHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, 1, 0, -World.getWaterHeight()));
            camera.getPosition().y += distance;
            camera.invertPitch();

            water.getWaterFrameBuffers().bindRefractionFrameBuffer();
            renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, -1, 0, World.getWaterHeight() + 0.5f));

            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            water.getWaterFrameBuffers().unbindCurrentFrameBuffer();
            renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, -1, 0, 100000));
            water.getWaterRenderer().render(water.getWaterTiles(), camera, lights.get(0));

            ParticleMaster.renderParticles(camera);
//			guiRenderer.render(guiTextures);
            DisplayManager.updateDisplay();
        }


        //***********CLEAN UP************
        ParticleMaster.cleanUp();
        water.getWaterFrameBuffers().cleanUp();
        water.getWaterShader().cleanUp();
//        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
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


        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("ground/GrassTexture"));
        BiomeBuilder grassBiomeBuilder = new BiomeBuilder(gTexture, 80, false);
        grassBiomeBuilder.addRandomEntities(terrain, waterHeight, grassEntity, 1000);
        TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree/Tree", loader),
                new ModelTexture(loader.loadTexture("tree/TreeTexture")));
        Vector3f dimensions = ObjLoader.getLastDimensions();
        Entity treeEntity = new Entity(treeModel, new Vector3f(0, 0, 0), 0, random.nextInt(360), 0, 0.2f, dimensions);
        grassBiomeBuilder.addRandomEntities(terrain, waterHeight, treeEntity, 100);
        Biome gBiome = grassBiomeBuilder.buildBiome();


        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("ground/StoneTexture"));
        Biome bBiome = new Biome(bTexture, 120, false);


        TerrainTexture aTexture = new TerrainTexture(loader.loadTexture("ground/SnowTexture"));
        BiomeBuilder snowBiomeBuilder = new BiomeBuilder(aTexture, 120, true);
        ParticleTexture snowTexture = new ParticleTexture(loader.loadTexture("particles/SnowTexture"), 1, false);
        snowBiomeBuilder.addParticleSystem(new SnowParticleSystem(snowTexture));
        Biome aBiome = snowBiomeBuilder.buildBiome();


        terrain.addBiomes(rBiome, gBiome, bBiome, aBiome);
        Water water = new Water(loader, renderer, waterHeight, terrainSize);
        return new World(terrain, water);
    }
}
