package guis;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    private List<GuiContainer> guiContainers;

    public GuiManager() {
        this.guiContainers = new ArrayList<>();
    }

    public void addContainer(GuiContainer guiContainer) {
        this.guiContainers.add(guiContainer);
    }

    public void update() {
        for (GuiContainer container : this.guiContainers) {
            container.update();
        }
    }

    public List<GuiElement> getGuiElements() {
        List<GuiElement> elements = new ArrayList<>();
        for (GuiContainer container : this.guiContainers) {
            elements.add(container);
            elements.addAll(container.getGuiElements());
        }

        return elements;
    }
}
