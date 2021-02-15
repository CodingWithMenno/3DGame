package guis;

import org.lwjgl.util.vector.Vector2f;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;

public class GuiContainer extends InteractableGui {

    private List<GuiElement> guiElements;
    private boolean isEnabled;

    public GuiContainer(int defaultTexture, Vector2f position, Vector2f scale) {
        super(defaultTexture, position, scale);
        this.guiElements = new ArrayList<>();

        this.isEnabled = true;
    }

    public void addTextures(GuiElement... guiElements) {
        for (GuiElement element : guiElements) {
            element.position = Maths.clamp(element.position, new Vector2f(super.position.x - super.scale.x, super.position.y - super.scale.y), new Vector2f(super.position.x + super.scale.x, super.position.y + super.scale.y));
            element.scale = Maths.clamp(element.scale, new Vector2f(0, 0), super.scale);

            if (element instanceof InteractableGui) {
                ((InteractableGui) element).updateHitbox();
            }

            this.guiElements.add(element);
        }
    }

    public void update() {
        if (!this.isEnabled) {
            return;
        }

        for (GuiElement element : this.guiElements) {
            if (element instanceof InteractableGui) {
                ((InteractableGui) element).update();
            }
        }
    }

    public List<GuiElement> getGuiElements() {
        return guiElements;
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
