package engineTester;

import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
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
import entities.Camera;
import entities.Entity;
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
	 * 		MAP:
	 * 			-Blendmap en heightMap maken
	 * 		OVERIG:
	 * 			-Collision detectie met entities
	 * 			-Animatie support voor de GUI's maken
	 * 			-Animations voor entities support maken
	 * 			-Geluid toevoegen aan de game
	 * 			-Support voor meer dan 5 lampen in de wereld (alle lampen in de MasterRenderer opslaan en alleen de 5 dichtstbijzijnde opslaan)
	 * 			-Shaduwen toevoegen (ook voor het terrein)
	 */

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();

		//*************WORLD SETUP**************
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("ground/GrassTexture"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("ground/DirtTexture"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("ground/StoneTexture"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("ground/path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("BlendMap"));

		Terrain terrain = new Terrain(0, -1, loader, "HeightMap", texturePack, blendMap);


		//***********ENTITIES SETUP****************
		TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree/Tree", loader),
				new ModelTexture(loader.loadTexture("tree/TreeTexture")));

		TexturedModel grassModel = new TexturedModel(ObjLoader.loadObjModel("grass/GrassModel", loader),
				new ModelTexture(loader.loadTexture("grass/GrassTexture")));
		grassModel.getTexture().setHasTransparency(true);
		grassModel.getTexture().setUseFakeLighting(true);
		grassModel.getTexture().setNumberOfRows(2);

		Random random = new Random();
		List<Entity> entities = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			float x = random.nextFloat() * Terrain.getSIZE();
			float z = random.nextFloat() * terrain.getZ();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y <= -13) {continue;}
			entities.add(new Entity(treeModel, new Vector3f(x, y, z), 0, random.nextInt(360), 0, 0.2f));
		}
		for (int i = 0; i < 1000; i++) {
			float x = random.nextFloat() * Terrain.getSIZE();
			float z = random.nextFloat() * terrain.getZ();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y <= -13) {continue;}
			entities.add(new Entity(grassModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextInt(360), 0, 1));
		}

		TexturedModel postModel = new TexturedModel(ObjLoader.loadObjModel("lamp/LampPost", loader),
				new ModelTexture(loader.loadTexture("lamp/LampPostTexture")));
		entities.add(new Entity(postModel, new Vector3f(100, terrain.getHeightOfTerrain(100, -150), -150), 0, 0, 0, 1f));

		List<Light> lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(0, 10000, -1000), new Vector3f(1f, 1f, 1f)));
		lights.add(new Light(new Vector3f(103.2f, terrain.getHeightOfTerrain(100, -150) + 4.5f, -150), new Vector3f(1f, 1f, 0), new Vector3f(1f, 0.01f, 0.002f)));

		TexturedModel foxModel = new TexturedModel(ObjLoader.loadObjModel("fox/Fox", loader),
				new ModelTexture(loader.loadTexture("fox/FoxTexture")));
		Player player = new Player(foxModel, new Vector3f(80, 5, -150), 0, 0, 0, 0.4f);
		entities.add(player);

		Camera camera = new Camera(player, terrain);


		//**********WATER SETUP****************
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waterTiles = new ArrayList<>();
		WaterTile water = new WaterTile(Terrain.getSIZE() / 2, -Terrain.getSIZE() / 2, -13);
		waterTiles.add(water);


		//**************GUI SETUP****************
//		List<GuiTexture> guiTextures = new ArrayList<>();
//		GuiTexture refraction = new GuiTexture(buffers.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		GuiTexture reflection = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		guiTextures.add(refraction);
//		guiTextures.add(reflection);
//
//		GuiRenderer guiRenderer = new GuiRenderer(loader);


		//**********GAME LOOP**************
		while(!Display.isCloseRequested()) {
			camera.move();
			player.move(terrain);

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 0.5f));
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
