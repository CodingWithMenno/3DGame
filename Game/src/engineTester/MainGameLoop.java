package engineTester;

import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	/** TODO :
	 * 		MAP:
	 * 			-Het terrain meer low poly maken (verbeteren)
	 * 			-Automatisch de height map laten genereren (noise generation)
	 * 			-Fog aan render distance laten aanpassen
	 * 			-Water toevoegen onder een bepaald y level
	 * 			-Biomes toevoegen
	 * 		OVERIG:
	 * 			-Fog aan render distance laten aanpassen
	 * 			-Collision detectie met entities
	 * 			-Animatie support voor de GUI's maken
	 * 			-Animations voor entities support maken
	 * 			-Geluid toevoegen aan de game
	 */

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();

		//region world thingies
		Terrain terrain = new Terrain(0, -1, loader, "HeightMap");

		TexturedModel treeModel = new TexturedModel(ObjLoader.loadObjModel("tree/Tree", loader),
				new ModelTexture(loader.loadTexture("tree/TreeTexture")));

		TexturedModel grassModel = new TexturedModel(ObjLoader.loadObjModel("grass/GrassModel", loader),
				new ModelTexture(loader.loadTexture("grass/GrassTexture")));
		grassModel.getTexture().setHasTransparency(true);
		grassModel.getTexture().setUseFakeLighting(true);
		grassModel.getTexture().setNumberOfRows(2);

		Random random = new Random();
		List<Entity> trees = new ArrayList<>();
		List<Entity> grassList = new ArrayList<>();
		for (int i = 0; i < 750; i++) {
			float x = random.nextFloat() * Terrain.getSIZE();
			float z = random.nextFloat() * terrain.getZ();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y > 10 || y < 0) {continue;}
			trees.add(new Entity(treeModel, new Vector3f(x, y, z), 0, random.nextInt(360), 0, 0.2f));
		}
		for (int i = 0; i < 10000; i++) {
			float x = random.nextFloat() * Terrain.getSIZE();
			float z = random.nextFloat() * terrain.getZ();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y > 10 || y < 0) {continue;}
			grassList.add(new Entity(grassModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextInt(360), 0, 1));
		}

		List<Light> lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.4f, 0.4f, 0.4f)));
		lights.add(new Light(new Vector3f(100, 5, -150), new Vector3f(1, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(100, 5, -120), new Vector3f(0, 0, 1), new Vector3f(1, 0.01f, 0.002f)));
		//endregion

		TexturedModel foxModel = new TexturedModel(ObjLoader.loadObjModel("fox/Fox", loader),
				new ModelTexture(loader.loadTexture("fox/FoxTexture")));
		Player player = new Player(foxModel, new Vector3f(100, 5, -150), 0, 0, 0, 0.4f);

		Camera camera = new Camera(player);

		List<GuiTexture> guiTextures = new ArrayList<>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("Health"), new Vector2f(0f, -0.9f), new Vector2f(0.25f, 0.25f));
		guiTextures.add(gui);

		GuiRenderer guiRenderer = new GuiRenderer(loader);

		while(!Display.isCloseRequested()) {
			camera.move();
			player.move(terrain);

			renderer.processEntity(player);

			for (Entity tree : trees) {
				renderer.processEntity(tree);
			}

			for (Entity grass : grassList) {
				renderer.processEntity(grass);
			}

			renderer.processTerrain(terrain);
			renderer.render(lights, camera);
			guiRenderer.render(guiTextures);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
