package guis.elaborated;

import guis.InteractableGui;
import org.lwjgl.util.vector.Vector2f;

public class Button extends InteractableGui {

    public Button(int defaultTexture, int hoverTexture, int clickedTexture, Vector2f position, Vector2f scale) {
        super(defaultTexture, hoverTexture, clickedTexture, position, scale);
    }

    @Override
    protected void onClick() {
        System.out.println("clicked");
    }

    @Override
    protected void onEnter() {
        System.out.println("Entered");
    }

    @Override
    protected void onExit() {
        System.out.println("Exited");
    }
}
