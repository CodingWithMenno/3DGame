package engineTester;

import animation.AnimatedModel;
import animation.Animation;
import collisions.CollisionHandler;
import entities.*;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import org.lwjgl.util.vector.Vector4f;
import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	/** TODO :
	 * 		Entities:
	 * 			-Normal mapping
	 * 			-Vissen/vogels maken met boids algoritme
	 * 		.
	 * 		Overig:
	 * 			-Goede GUI library maken (met animatie support & het resizen van het display)
	 * 			-Geluid implementeren in entities
	 * 			-Particle systeem maken
	 * 			-Zon en wolken toevoegen (goede day-night cycle maken)
	 * 			-Effecten toepassen (Post-Processing, Bloom, Lens flare, etc.)
	 * 			-Kleine random generated wereld genereren (i.p.v. heightmap + blendmap) (blendmap bepalen aan de hand van de hoogte)
	 * 		.
	 * 		Optioneel / Verbeteren:
	 * 			-Water low poly maken
	 * 			-Lampen die ingerendered/uitgerendered worden laten in/uit faden
	 * 			-Collision detectie verbeteren / physics verbeteren (OBB implementeren & je kan worden geduwd door andere entities)
	 * 			-Animaties met collada files (interpolaten tussen frames en werken met skeletten)
	 * 		.
	 * 		Voor betere performance:
	 * 			-Nieuwe objLoader gebruiken (zie normal mapping filmpje)
	 * 			-De vertexen/fragments van het water alleen renderen als ze hoger zijn dan het terrein
	 */

	public static float WATER_HEIGHT = -15;

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		//*************WORLD SETUP**************
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("ground/GrassTexture"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("ground/DirtTexture"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("ground/GrassTexture"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("ground/StoneTexture"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("BlendMap"));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);


		//**********PLAYER SETUP*******************
		List<Entity> entities = new ArrayList<>();
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

		Player player = new Player(foxAnimatedModel, new Vector3f(150, -18.5f, -200), 0, 0, 0, 0.4f, dimensions);
		entities.add(player);

		Camera camera = new Camera(player, terrain);

		MasterRenderer renderer = new MasterRenderer(camera);


		//***********ENTITIES SETUP****************
		TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree/Tree", loader),
				new ModelTexture(loader.loadTexture("tree/TreeTexture")));
		dimensions = ObjLoader.getLastDimensions();

		TexturedModel grassModel = new TexturedModel(ObjLoader.loadObjModel("grass/GrassModel", loader),
				new ModelTexture(loader.loadTexture("grass/GrassTexture")));
		grassModel.getTexture().setHasTransparency(true);
		grassModel.getTexture().setUseFakeLighting(true);
		grassModel.getTexture().setNumberOfRows(2);

		Random random = new Random();
		for (int i = 0; i < 300; i++) {
			float x = random.nextFloat() * Terrain.getSIZE();
			float z = random.nextFloat() * terrain.getZ();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y <= WATER_HEIGHT) {continue;}
			entities.add(new Entity(treeModel, new Vector3f(x, y, z), 0, random.nextInt(360), 0, 0.2f, dimensions));
		}
		for (int i = 0; i < 1000; i++) {
			float x = random.nextFloat() * Terrain.getSIZE();
			float z = random.nextFloat() * terrain.getZ();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y <= WATER_HEIGHT) {continue;}
			entities.add(new Entity(grassModel, random.nextInt(5), new Vector3f(x, y, z), 0, random.nextInt(360), 0, 1));
		}

		TexturedModel postModel = new TexturedModel(ObjLoader.loadObjModel("lamp/LampPost", loader),
				new ModelTexture(loader.loadTexture("lamp/LampPostTexture")));
		entities.add(new Entity(postModel, new Vector3f(100, terrain.getHeightOfTerrain(100, -150), -150), 0, 0, 0, 1f));

		List<Light> lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(1000, 300000, -100000), new Vector3f(0.7f, 0.7f, 0.7f)));
		lights.add(new Light(new Vector3f(103.2f, terrain.getHeightOfTerrain(100, -150) + 4.5f, -150), new Vector3f(1f, 1f, 0), new Vector3f(1f, 0.01f, 0.002f)));


//		List<Fish> allFish = new ArrayList<>();
//		TexturedModel fishModel = new TexturedModel(ObjLoader.loadObjModel("fish/Fish", loader),
//				new ModelTexture(loader.loadTexture("fish/FishTexture")));
//		fishModel.getTexture().setNumberOfRows(2);
//
//		dimensions = ObjLoader.getLastDimensions();
//		for (int i = 0; i < 30; i += 3) {
//			Fish fish = new Fish(fishModel, random.nextInt(5), new Vector3f(150 - random.nextInt(100), 5 - i, -200 + i * 2), 0, 0, 0, 1f, dimensions);
//			allFish.add(fish);
//			entities.add(fish);
//		}
//		FishGroup fishGroup = new FishGroup(allFish);


		//**********WATER SETUP****************
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waterTiles = new ArrayList<>();
		WaterTile water = new WaterTile(Terrain.getSIZE() / 2, -Terrain.getSIZE() / 2, WATER_HEIGHT);
		waterTiles.add(water);


		//**************GUI SETUP****************
//		List<GuiTexture> guiTextures = new ArrayList<>();
//		GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
//		GuiTexture reflection = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		guiTextures.add(shadowMap);
//		guiTextures.add(reflection);
//
//		GuiRenderer guiRenderer = new GuiRenderer(loader);


		//**********COLLISION SETUP******
		CollisionHandler collisionHandler = new CollisionHandler(entities);


		//**********GAME LOOP**************

		while(!Display.isCloseRequested()) {
			camera.move();
			player.updateEntity(terrain);
			player.updateAnimation();
//			fishGroup.updateAllFish(terrain);

			collisionHandler.checkCollisions();

			renderer.renderShadowMap(entities, lights.get(0));

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()));
			camera.getPosition().y += distance;
			camera.invertPitch();

			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, -1, 0, water.getHeight() + 0.5f));

			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, -1, 0, 100000));
			waterRenderer.render(waterTiles, camera, lights.get(0));
//			guiRenderer.render(guiTextures);

			DisplayManager.updateDisplay();
		}


		//********CLEAN UP***************
		buffers.cleanUp();
		waterShader.cleanUp();
//		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
