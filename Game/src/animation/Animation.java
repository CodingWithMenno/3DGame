package animation;

import models.TexturedModel;

import java.util.List;

public class Animation {

    private List<TexturedModel> keyframes;

    public Animation(List<TexturedModel> keyframes) {
        this.keyframes = keyframes;
    }

    public List<TexturedModel> getKeyframes() {
        return keyframes;
    }
}
