package gameLoop;

import org.lwjgl.opengl.Display;
import renderEngine.DisplayManager;

public class MainManager {

    private static Scene currentScene;

    public static void main(String[] args) {
        DisplayManager.createDisplay();

        currentScene.setup();

        while(!Display.isCloseRequested()) {
            //updating
            currentScene.update();

            //rendering
            currentScene.render();
            DisplayManager.updateDisplay();
        }

        currentScene.cleanUp();
        DisplayManager.closeDisplay();
    }

    public static void changeScene(Scene scene) {
        currentScene.cleanUp();
        currentScene = scene;
        currentScene.setup();
    }
}
