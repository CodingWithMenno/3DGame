package gameLoop;

import animation.AnimatedModel;
import animation.Animation;
import audio.AudioMaster;
import collisions.Box;
import collisions.CollisionHandler;
import collisions.OBB;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.elaborated.*;
import guis.*;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL10;
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
import terrains.Terrain;
import terrains.World;
import textures.ModelTexture;
import textures.TerrainTexture;
import user.Settings;
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


    //Collision Box testing
//    private List<Entity> playerBox;
//    private Entity testEntity;
//    private List<Entity> testEntityBox;


    @Override
    public void setup() {
        Mouse.setGrabbed(true);
        this.loader = new Loader();
        Terrain terrain = new Terrain(0, 0, this.loader);
        Random random = new Random();


        //*************AUDIO SETUP**************
        AudioMaster.init();
        AudioMaster.setListenerData(new Vector3f(0, 0, 0));
        AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);


        //*************PLAYER SETUP**************
        this.player = setupPlayer(this.loader);
        this.camera = new Camera(this.player, terrain);
        this.player.setCamera(this.camera);

        this.renderer = new MasterRenderer(this.camera);
        ParticleMaster.init(this.loader, this.renderer.getProjectionMatrix());


        //***********WORLD SETUP****************
        this.world = setupWorld(terrain, this.loader, this.renderer, random, World.getWaterHeight(), Terrain.getSIZE());


        //**********LIGHTS SETUP*****************
        TexturedModel postModel = new TexturedModel(ObjLoader.loadObjModel("lamp/LampPost", this.loader),
                new ModelTexture(this.loader.loadTexture("lamp/LampPostTexture")));
        Vector3f postPosition = new Vector3f(100, terrain.getHeightOfTerrain(100, 150), 150);
        OBB postCollisionBox = new Box(postPosition, new Vector3f(2.5f, 3.8f, 0.5f));
        Entity post = new Entity(postModel, postPosition, 0, 90, 0, 1f, postCollisionBox);
        //post.setRotY(90);
        this.world.addEntityToCorrectBiome(post);

        this.lights = new ArrayList<>();
        this.lights.add(new Light(new Vector3f(1000, 500000, -100000), new Vector3f(0.7f, 0.7f, 0.7f)));
        this.lights.add(new Light(new Vector3f(103.2f, terrain.getHeightOfTerrain(100, 150) + 4.5f, 150), new Vector3f(1f, 1f, 0), new Vector3f(1f, 0.01f, 0.002f)));


        //Birds setup
        for (int i = 1; i < 2; i++) {
            new BirdGroup(new Vector3f(400 * i, 400 * i, 400 * i), 50, this.loader, this.world);
        }

//        TexturedModel birdModel = new TexturedModel(ObjLoader.loadObjModel("fish/Fish", loader),
//                new ModelTexture(loader.loadTexture("fish/FishTexture")));
//        birdModel.getTexture().setNumberOfRows(2);
//        Vector3f birdPosition = new Vector3f(20, world.getTerrain().getHeightOfTerrain(100, 120) + 25, 20);
//
//        for (int i = 0; i < 500 * 4; i += 4) {
//            Bird2 bird = new Bird2(birdModel, random.nextInt(4), new Vector3f(birdPosition.x + random.nextInt(200), birdPosition.y + random.nextInt(200), birdPosition.z + random.nextInt(200)), 0, 0, 0, 1f);
//            world.addEntityToCorrectBiome(bird);
//        }


        //**********COLLISION BOX TESTING*********
//        this.playerBox = new ArrayList<>();
//        for (int i = 0; i < this.player.getCollisionBoxes().get(0).getNodes().size(); i++) {
//            Entity newEntity = new Entity(postModel, 1, new Vector3f(0, 0, 0), 0, 0, 0, 0.1f);
//            this.playerBox.add(newEntity);
//            this.world.addEntityToCorrectBiome(newEntity);
//        }
//
//        TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree/Tree", loader),
//                new ModelTexture(loader.loadTexture("tree/TreeTexture")));
//        this.testEntity = new Entity(treeModel, new Vector3f(100, terrain.getHeightOfTerrain(100, 100), 100), 0, random.nextInt(360), 0, 0.2f, new Vector3f(10, 30, 10));
//        this.world.addEntityToCorrectBiome(this.testEntity);
//
//        this.testEntityBox = new ArrayList<>();
//        for (int i = 0; i < this.testEntity.getCollisionBoxes().get(0).getCollisionPoints().size(); i++) {
//            Entity newEntity = new Entity(postModel, 1, new Vector3f(0, 0, 0), 0, 0, 0, 0.1f);
//            this.testEntityBox.add(newEntity);
//            this.world.addEntityToCorrectBiome(newEntity);
//        }


        //**************GUI SETUP****************
        this.guiManager = new GuiManager();
//        Button button = new Button(this.loader.loadTexture("button/button0"), this.loader.loadTexture("button/button1"), this.loader.loadTexture("button/button2"), new Vector2f(-0.5f, -0.5f), new Vector2f(0.25f, 0.25f));
//        this.guiManager.addTexture(button);

        this.guiRenderer = new GuiRenderer(this.loader);


        //***********COLLISION SETUP************
        this.collisionHandler = new CollisionHandler();
    }

    @Override
    public void resume() {
        Mouse.setGrabbed(true);
        this.world.continueWorld();
    }

    @Override
    public void pause() {
        this.world.pauseWorld();
    }

    @Override
    public void update() {
        //Collision Box testing
//        for (int i = 0; i < this.playerBox.size(); i++) {
//            OBB obb = this.player.getCollisionBoxes().get(0);
//            this.playerBox.get(i).setPosition(new Vector3f(obb.getNodes().get(i)));
//            this.playerBox.get(i).setRotY(obb.getRotY());
//            this.playerBox.get(i).setRotX(obb.getRotX());
//            this.playerBox.get(i).setRotZ(obb.getRotZ());
//        }
//
//        for (int i = 0; i < this.testEntityBox.size(); i++) {
//            OBB obb = this.testEntity.getCollisionBoxes().get(0);
//            this.testEntityBox.get(i).setPosition(new Vector3f(obb.getCollisionPoints().get(i)));
//            this.testEntityBox.get(i).setRotY(obb.getRotY());
//            this.testEntityBox.get(i).setRotX(obb.getRotX());
//            this.testEntityBox.get(i).setRotZ(obb.getRotZ());
//        }


        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            SceneManager.stackScene(new PauseScreen());
            return;
        }

        this.camera.move();
        this.player.updateEntity(this.world.getTerrain());
        this.player.updateAnimation();
        this.world.update(new Vector3f(this.player.getPosition()));
        this.guiManager.update();
        ParticleMaster.update(this.camera);

        AudioMaster.setListenerData(new Vector3f(this.camera.getPosition()));
        this.world.updateBackGroundSoundsPos(new Vector3f(this.camera.getPosition()));

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

        if (Settings.USE_WATER_REFLECTION) {
            this.world.getWater().getWaterFrameBuffers().bindReflectionFrameBuffer();
            float distance = 2 * (this.camera.getPosition().y - World.getWaterHeight());
            this.camera.getPosition().y -= distance;
            this.camera.invertPitch();
            this.renderer.renderScene(entities, this.world.getTerrain(), this.lights, this.camera, new Vector4f(0, 1, 0, -World.getWaterHeight()));

            this.camera.getPosition().y += distance;
            this.camera.invertPitch();
        }
        this.world.getWater().getWaterFrameBuffers().bindRefractionFrameBuffer();
        this.renderer.renderScene(entities, this.world.getTerrain(), this.lights, this.camera, new Vector4f(0, -1, 0, World.getWaterHeight() + 0.5f));

        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        this.world.getWater().getWaterFrameBuffers().unbindCurrentFrameBuffer();

        //World and water rendering
        this.renderer.renderScene(entities, this.world.getTerrain(), this.lights, this.camera, new Vector4f(0, -1, 0, 100000));
        this.world.getWater().getWaterRenderer().render(this.world.getWater().getWaterTiles(), this.camera, this.lights.get(0), Settings.USE_WATER_REFLECTION);

        //Particle and GUI rendering
        ParticleMaster.renderParticles(this.camera);
        this.guiRenderer.render(this.guiManager.getGuiElements());
    }

    @Override
    public void cleanUp() {
        ParticleMaster.cleanUp();
        this.world.cleanUp();
        this.guiRenderer.cleanUp();
        this.renderer.cleanUp();
        this.loader.cleanUp();
        AudioMaster.cleanUp();
    }

    private Player setupPlayer(Loader loader) {
        ModelTexture foxTexture = new ModelTexture(loader.loadTexture("fox/FoxTexture"));
        TexturedModel foxModel = new TexturedModel(ObjLoader.loadObjModel("fox/Fox", loader), foxTexture);
        List<TexturedModel> foxIdle = new ArrayList<>();
        foxIdle.add(foxModel);
        Animation foxIdleAnimation = new Animation(foxIdle, 10);

        List<TexturedModel> foxModels = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            foxModels.add(new TexturedModel(ObjLoader.loadObjModel("fox/VillagerFox_animations_00000" + i, loader), foxTexture));
        }
        for (int i = 10; i < 32; i++) {
            foxModels.add(new TexturedModel(ObjLoader.loadObjModel("fox/VillagerFox_animations_0000" + i, loader), foxTexture));
        }

        Animation foxRunningAnimation = new Animation(foxModels, 1);
        AnimatedModel foxAnimatedModel = new AnimatedModel(foxIdleAnimation, foxRunningAnimation);

        Vector3f playerPosition = new Vector3f(400, 0, 400);
        float playerScale = 0.4f;
        OBB playerCollisionBox = new Box(playerPosition, new Vector3f(3 * playerScale, 5 * playerScale, 2.5f * playerScale));
        return new Player(foxAnimatedModel, playerPosition, 0, 0, 0, playerScale, playerCollisionBox);
    }


    private World setupWorld(Terrain terrain, Loader loader, MasterRenderer renderer, Random random, float waterHeight, float terrainSize) {
        //Dirt biome
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("ground/DirtTexture"));
        TexturedModel grassModel = new TexturedModel(ObjLoader.loadObjModel("grass/GrassModel", loader),
                new ModelTexture(loader.loadTexture("grass/GrassTexture")));
        grassModel.getTexture().setHasTransparency(true);
        grassModel.getTexture().setUseFakeLighting(true);
        grassModel.getTexture().setNumberOfRows(2);
        Entity grassEntity = new Entity(grassModel, random.nextInt(5), new Vector3f(0, 0, 0), 0, random.nextInt(360), 0, 1);
        Biome rBiome = Biome.builder(rTexture, -5, false)
                .addRandomEntities(terrain, waterHeight, grassEntity, 1000)
                .addBackgroundSound(AudioMaster.loadSound("audio/sounds/Water"))
                .buildBiome();

        //Grass biome
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("ground/GrassTexture"));
        TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree/Tree", loader),
                new ModelTexture(loader.loadTexture("tree/TreeTexture")));
        Vector3f treePos = new Vector3f(0, 0, 0);
        float treeScale = 0.2f;
        OBB treeCollisionBox = new Box(treePos, new Vector3f(10 * treeScale, 26 * treeScale, 10 * treeScale));
        Entity treeEntity = new Entity(treeModel, new Vector3f(treePos), 0, random.nextInt(360), 0, treeScale, treeCollisionBox);
        ParticleTexture pollTexture = new ParticleTexture(loader.loadTexture("particles/PollTexture"), 1, false);
        Biome gBiome = Biome.builder(gTexture, 80, false)
                .addBackgroundSound(AudioMaster.loadSound("audio/sounds/Forest"))
                .addRandomEntities(terrain, waterHeight, grassEntity, 1000)
                .addRandomEntities(terrain, waterHeight, treeEntity, 100)
                .addParticleSystem(new PollParticleSystem(pollTexture))
                .buildBiome();

        //Stone biome
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("ground/StoneTexture"));
        Biome bBiome = Biome.builder(bTexture, 150, false)
                .buildBiome();

        //Snow biome
        TerrainTexture aTexture = new TerrainTexture(loader.loadTexture("ground/SnowTexture"));
        ParticleTexture snowTexture = new ParticleTexture(loader.loadTexture("particles/SnowTexture"), 1, false);
        Biome aBiome = Biome.builder(aTexture, 150, true)
                .addBackgroundSound(AudioMaster.loadSound("audio/sounds/Snow"))
                .addParticleSystem(new SnowParticleSystem(snowTexture))
                .buildBiome();

        //Adding all the biomes
        terrain.addBiomes(rBiome, gBiome, bBiome, aBiome);
        Water water = new Water(loader, renderer, waterHeight, terrainSize);
        return new World(terrain, water);
    }
}
