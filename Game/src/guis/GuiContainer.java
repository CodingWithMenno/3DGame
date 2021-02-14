package guis;

import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiContainer extends InteractableGui {

    private List<GuiElement> guiElements;
    private int transparency = 0;
    private boolean isEnabled;

    public GuiContainer(int defaultTexture, int hoverTexture, int clickedTexture, Vector2f position, Vector2f scale) {
        super(defaultTexture, hoverTexture, clickedTexture, position, scale);
        this.guiElements = new ArrayList<>();
    }

    public void addTextures(GuiElement... guiElements) {
        Collections.addAll(this.guiElements, guiElements);
    }

    public List<GuiElement> getGuiElements() {
        return guiElements;
    }

    public int getTransparency() {
        return transparency;
    }

    public void setTransparency(int transparency) {
        this.transparency = transparency;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    protected void onClick() {
    }

    @Override
    protected void onEnter() {
    }

    @Override
    protected void onExit() {
    }
}
