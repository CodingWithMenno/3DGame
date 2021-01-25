package guis;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    private List<GuiTexture> guiTextures;

    public GuiManager() {
        this.guiTextures = new ArrayList<>();
    }

    public void addTexture(GuiTexture guiTexture) {
        this.guiTextures.add(guiTexture);
    }

    public void update() {
        for (GuiTexture texture : this.guiTextures) {
            if (texture instanceof InteractableGui) {
                ((InteractableGui) texture).update();
            }
        }
    }

    public List<GuiTexture> getGuiTextures() {
        return guiTextures;
    }
}
