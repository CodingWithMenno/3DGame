package gameLoop;

public interface Scene {
    void setup();
    void resume();
    void update();
    void render();
    void cleanUp();
}
