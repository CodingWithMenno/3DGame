package user;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import renderEngine.DisplayManager;

public class Input {

    public static Vector2f getRelativeMousePos() {
        float x = (Mouse.getX() / DisplayManager.getScaledWidth() / DisplayManager.getDefaultWidth()) * 2f - 1f;
        float y = (Mouse.getY() / DisplayManager.getScaledHeight() / DisplayManager.getDefaultHeight()) * 2f - 1f;

        return new Vector2f(x, y);
    }

}
