package entities.elaborated;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import renderEngine.ObjLoader;
import terrains.Terrain;
import terrains.World;
import textures.ModelTexture;
import toolbox.GameTimer;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BirdGroup {

    private List<Bird> birds;
    private Vector3f destination;

    private GameTimer destinationTimer;
    private static final int DESTINATION_TIME = 15;

    private Random random;

    public BirdGroup(Vector3f startPosition, int birdsInGroup, Loader loader, World world) {
        this.random = new Random();

        TexturedModel birdModel = new TexturedModel(ObjLoader.loadObjModel("bird/Bird", loader),
                new ModelTexture(loader.loadTexture("bird/BirdTexture")));
        birdModel.getTexture().setNumberOfRows(2);
        Vector3f birdPosition = new Vector3f(startPosition.x, world.getTerrain().getHeightOfTerrain(startPosition.x, startPosition.z) + 40, startPosition.z);

        this.birds = new ArrayList<>();
        for (int i = 0; i < birdsInGroup * 4; i += 4) {
            Bird bird = new Bird(birdModel, this.random.nextInt(4),
                    new Vector3f(birdPosition.x + this.random.nextInt(100), birdPosition.y + this.random.nextInt(50), birdPosition.z + this.random.nextInt(100)),
                    this.random.nextInt(90) - 45, this.random.nextInt(360), 0, 0.01f, this);

            world.addEntityToCorrectBiome(bird);
            this.birds.add(bird);
        }

        this.destinationTimer = new GameTimer(DESTINATION_TIME * this.birds.size());
    }

    public boolean updateDestinationTimer() {
        this.destinationTimer.updateTimer();

        if (this.destinationTimer.hasFinished()) {
            return true;
        }

        return false;
    }

    public void setNewDestination(Terrain terrain, float minHeight, float maxHeight, float borderDistance) {
        this.destinationTimer = new GameTimer(DESTINATION_TIME * this.birds.size());

        float x = (float) ((this.random.nextInt((int) Terrain.getSIZE())));
        float z = (float) ((this.random.nextInt((int) Terrain.getSIZE())));
        float y = (float) ((this.random.nextInt((int) maxHeight)));

        x = Maths.clamp(x, borderDistance, Terrain.getSIZE() - borderDistance);
        z = Maths.clamp(z, borderDistance, Terrain.getSIZE() - borderDistance);
        y = Maths.clamp(y, Math.max(terrain.getHeightOfTerrain(x, z) + minHeight, World.getWaterHeight()) + minHeight, maxHeight);

        this.destination = new Vector3f(x, y, z);
    }

    public Vector3f getDestination() {
        return destination;
    }

    public List<Bird> getBirds() {
        return this.birds;
    }
}
