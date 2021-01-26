package gameLoop;

import guis.GuiManager;
import guis.GuiRenderer;
import guis.GuiTexture;
import guis.elaborated.Button;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
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
        GuiTexture button = new Button(this.loader.loadTexture("Health"), this.loader.loadTexture("water/WaterDUDV"), this.loader.loadTexture("water/WaterNormal"), new Vector2f(-0.5f, -0.5f), new Vector2f(0.25f, 0.25f));
        this.guiManager.addTexture(button);

        this.guiRenderer = new GuiRenderer(this.loader);
    }

    @Override
    public void resume() {
        Mouse.setGrabbed(false);
    }

    @Override
    public void update() {
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            MainManager.goBackAScene();
        }

        this.guiManager.update();
    }

    @Override
    public void render() {
        this.guiRenderer.render(this.guiManager.getGuiTextures());
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
