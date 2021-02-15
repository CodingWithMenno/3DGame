package toolbox;

import renderEngine.DisplayManager;

public class InGameTimer {

    private float currentTime;
    private float finalTime;
    private boolean hasFinished;

    public InGameTimer(float finalTime) {
        this.currentTime = 0.0f;
        this.finalTime = finalTime;
        this.hasFinished = false;
    }

    public void updateTimer() {
        this.currentTime += DisplayManager.getDelta();

        if (this.currentTime >= this.finalTime) {
            this.hasFinished = true;
        }
    }

    public boolean hasFinished() {
        return hasFinished;
    }
}
