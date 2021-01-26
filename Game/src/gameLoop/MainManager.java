package gameLoop;

import org.lwjgl.opengl.Display;
import renderEngine.DisplayManager;

import java.util.EmptyStackException;
import java.util.Stack;

public class MainManager {

    private static Stack<Scene> scenes;

    public static void main(String[] args) {
        DisplayManager.createDisplay();

        scenes = new Stack<>();
        scenes.push(new MainGameLoop());
        scenes.peek().setup();


        while(!Display.isCloseRequested()) {
            try {
                //updating
                scenes.peek().update();

                //rendering
                if (scenes.peek() instanceof TransparentScene) {
                    for (int i = 0; i < scenes.size(); i++) {
                        if (scenes.get(i) == scenes.peek()) {
                            scenes.get(i - 1).render();
                        }
                    }
                }

                scenes.peek().render();
            } catch (EmptyStackException e) {
                break;
            }

            DisplayManager.updateDisplay();
        }


        for (Scene scene : scenes) {
            scene.cleanUp();
        }

        DisplayManager.closeDisplay();
    }

    public static void changeScene(Scene scene) {
        scenes.pop().cleanUp();
        scenes.push(scene);
        scenes.peek().setup();
    }

    public static void stackScene(Scene scene) {
        scenes.push(scene);
        scenes.peek().setup();
    }

    public static void goBackAScene() {
        scenes.pop().cleanUp();
        scenes.peek().resume();
    }


}
