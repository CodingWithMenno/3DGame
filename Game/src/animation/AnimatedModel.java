package animation;

import java.util.List;

public class AnimatedModel {

    private List<Animation> animations;

    public AnimatedModel(List<Animation> animations) {
        this.animations = animations;
    }

    public List<Animation> getAnimations() {
        return animations;
    }
}
