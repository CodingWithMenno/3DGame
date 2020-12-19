package engineTester;

import entities.Light;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		RawModel model = ObjLoader.loadObjModel("tree/Tree", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree/TreeTexture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(0);

		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));
		
		Camera camera = new Camera();

		Random random = new Random();
		List<Entity> entities = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			int x = (int) (camera.getPosition().x + random.nextInt(100) - 50);
			int y = (int) (camera.getPosition().y + random.nextInt(100) - 50);
			int z = (int) (camera.getPosition().z + random.nextInt(50) - 100);
			entities.add(new Entity(staticModel, new Vector3f(x,y,z),0,0,0,0.1f));
		}

		MasterRenderer renderer = new MasterRenderer();

		while(!Display.isCloseRequested()) {

			for (Entity entity : entities) {
				renderer.processEntity(entity);
				entity.increaseRotation(0, 1f, 0);
			}

			camera.move();
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
