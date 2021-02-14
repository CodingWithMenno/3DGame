package gameLoop;

import guis.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import renderEngine.Loader;

public class PauseScreen implements TransparentScene, Comparable {

    private Loader loader;

    private GuiManager guiManager;
    private GuiRenderer guiRenderer;

    @Override
    public void setup() {
        Mouse.setGrabbed(false);
        this.loader = new Loader();

        this.guiManager = new GuiManager();
        GuiContainer container = new GuiContainer(0, 0, 0, new Vector2f(0, 0), new Vector2f(1, 1));

        Button button = new Button(this.loader.loadTexture("button/button0"), this.loader.loadTexture("button/button1"), this.loader.loadTexture("button/button2"), new Vector2f(-0.5f, -0.5f), new Vector2f(0.25f, 0.25f));
        button.setOnClickAction(() -> {
            SceneManager.changeScene(new MainGameLoop());
        });
        container.addTextures(button);

        this.guiManager.addContainer(container);

        this.guiRenderer = new GuiRenderer(this.loader);
    }

    @Override
    public void resume() {
        Mouse.setGrabbed(false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void update() {
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            SceneManager.goBackAScene();
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            SceneManager.goBackAScene();
        }

        this.guiManager.update();
    }

    @Override
    public void render() {
        this.guiRenderer.render(this.guiManager.getGuiElements());
    }

    @Override
    public void cleanUp() {
        this.guiRenderer.cleanUp();
        this.loader.cleanUp();
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
