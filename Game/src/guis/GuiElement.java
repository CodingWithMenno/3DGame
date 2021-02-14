package guis;

import org.lwjgl.util.vector.Vector2f;
import renderEngine.DisplayManager;

public class GuiElement {

    protected int texture;
    protected Vector2f position;
    protected Vector2f scale;

    public GuiElement(int texture, Vector2f position, Vector2f scale) {
        this.texture = texture;
        this.position = position;
        this.scale = scale;

        this.scale.x /= (DisplayManager.getWIDTH() / DisplayManager.getHEIGHT());
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }
}
