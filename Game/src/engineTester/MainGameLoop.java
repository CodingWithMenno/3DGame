package engineTester;

import entities.Light;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		RawModel model = ObjLoader.loadObjModel("tree/Tree", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree/TreeTexture")));
		ModelTexture texture = staticModel.getTexture();

		Entity entity = new Entity(staticModel, new Vector3f(0, 0, -25), 0, 0, 0, 0.1f);
		Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("grass")));
		Terrain terrain2 = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("grass")));

		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();

		while(!Display.isCloseRequested()) {
			camera.move();

			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processEntity(entity);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
