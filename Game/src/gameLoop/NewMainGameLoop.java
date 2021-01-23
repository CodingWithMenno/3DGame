package gameLoop;

import animation.AnimatedModel;
import animation.Animation;
import collisions.CollisionHandler;
import entities.Camera;
import entities.Entity;
import entities.Player;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import particles.SnowParticleSystem;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ObjLoader;
import terrains.Biome;
import terrains.BiomeBuilder;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewMainGameLoop {

    private static final float WATER_HEIGHT = -15;

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        Random random = new Random();
        Terrain terrain = new Terrain(0, -1, loader);

        //***********PLAYER SETUP*************
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

        Player player = new Player(foxAnimatedModel, new Vector3f(500, -500, -200), 0, 0, 0, 0.4f, dimensions);

        Camera camera = new Camera(player, terrain);
        MasterRenderer renderer = new MasterRenderer(camera);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());

        //***********WORLD SETUP***************
        //DIRT BIOME
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("ground/DirtTexture"));
        BiomeBuilder dirtBiomeBuilder = new BiomeBuilder(rTexture, -5, false);

        TexturedModel grassModel = new TexturedModel(ObjLoader.loadObjModel("grass/GrassModel", loader),
                new ModelTexture(loader.loadTexture("grass/GrassTexture")));
        grassModel.getTexture().setHasTransparency(true);
        grassModel.getTexture().setUseFakeLighting(true);
        grassModel.getTexture().setNumberOfRows(2);
        Entity grassEntity = new Entity(grassModel, random.nextInt(5), new Vector3f(0, 0, 0), 0, random.nextInt(360), 0, 1);

        dirtBiomeBuilder.addRandomEntities(terrain, WATER_HEIGHT, grassEntity, 200);
        Biome dirtBiome = dirtBiomeBuilder.buildBiome();


        //GRASS BIOME
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("ground/GrassTexture"));
        BiomeBuilder grassBiomeBuilder = new BiomeBuilder(gTexture, 80, false);

        TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree/Tree", loader),
                new ModelTexture(loader.loadTexture("tree/TreeTexture")));
        dimensions = ObjLoader.getLastDimensions();
        Entity treeEntity = new Entity(treeModel, new Vector3f(0, 0, 0), 0, random.nextInt(360), 0, 0.2f, dimensions);

        grassBiomeBuilder.addRandomEntities(terrain, WATER_HEIGHT, treeEntity, 100);
        grassBiomeBuilder.addRandomEntities(terrain, WATER_HEIGHT, grassEntity, 300);
        Biome grassBiome = grassBiomeBuilder.buildBiome();


        //STONE BIOME
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("ground/StoneTexture"));
        BiomeBuilder stoneBiomeBuilder = new BiomeBuilder(bTexture, 120, false);
        Biome stoneBiome = stoneBiomeBuilder.buildBiome();


        //SNOW BIOME
        TerrainTexture aTexture = new TerrainTexture(loader.loadTexture("ground/SnowTexture"));
        BiomeBuilder snowBiomeBuilder = new BiomeBuilder(aTexture, 120, true);
        ParticleTexture snowParticleTexture = new ParticleTexture(loader.loadTexture("particles/SnowTexture"), 1, false);
        ParticleSystem snowParticleSystem = new SnowParticleSystem(snowParticleTexture, snowBiomeBuilder.getSeparationHeight() + 10, player);
        snowBiomeBuilder.addParticleSystem(snowParticleSystem);
        Biome snowBiome = snowBiomeBuilder.buildBiome();


        terrain.addBiomes(dirtBiome, grassBiome, stoneBiome, snowBiome);


        //************WATER SETUP***********
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waterTiles = new ArrayList<>();
        WaterTile water = new WaterTile(Terrain.getSIZE() / 2, -Terrain.getSIZE() / 2, WATER_HEIGHT);
        waterTiles.add(water);


        //***********COLLISION SETUP********
        List<Entity> allEntities = new ArrayList<>();
        for (Biome biome : terrain.getBiomes()) {
            allEntities.addAll(biome.getEntities());
        }
        allEntities.add(player);

        CollisionHandler collisionHandler = new CollisionHandler(allEntities);
    }
}
