package gameLoop;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import renderEngine.DisplayManager;

import java.util.EmptyStackException;
import java.util.Stack;

public class SceneManager {

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
                renderTransparency();
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
        for (Scene sceneToRemove : scenes) {
            sceneToRemove.cleanUp();
        }

        scenes.clear();

        scenes.push(scene);
        scenes.peek().setup();
    }

    public static void stackScene(Scene scene) {
        if (scene instanceof TransparentScene && scenes.peek() instanceof TransparentScene) {
            System.out.println("Cannot stack a transparent scene on a transparent scene");
            return;
        }

        scenes.peek().pause();
        scenes.push(scene);
        scenes.peek().setup();
    }

    public static void goBackAScene() {
        scenes.pop().cleanUp();
        scenes.peek().resume();
    }

    private static void renderTransparency() {
        if (scenes.peek() instanceof TransparentScene) {
            for (int i = 0; i < scenes.size(); i++) {
                try {
                    if (scenes.get(i) == scenes.peek()) {
                        scenes.get(i - 1).render();
                    }
                } catch (IndexOutOfBoundsException e) {
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                }
            }
        }
    }
}
