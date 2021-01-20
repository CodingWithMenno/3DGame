package animation;

import models.TexturedModel;

import java.util.List;

public class Animation {

    private float animationLength;
    private List<TexturedModel> keyframes;

    public Animation(List<TexturedModel> keyframes, float animationLength) {
        this.keyframes = keyframes;
        this.animationLength = animationLength;
    }

    public float getAnimationLength() {
        return animationLength;
    }

    public List<TexturedModel> getKeyframes() {
        return keyframes;
    }

    public void setAnimationLength(float animationLength) {
        this.animationLength = animationLength;
    }
}
