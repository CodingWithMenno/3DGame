package entities.elaborated;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import renderEngine.ObjLoader;
import terrains.Terrain;
import terrains.World;
import textures.ModelTexture;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BirdGroup {

    private List<Bird> birds;
    private Vector3f destination;

    private Random random;

    public BirdGroup(Vector3f startPosition, int birdsInGroup, Loader loader, World world) {
        this.random = new Random();

        TexturedModel birdModel = new TexturedModel(ObjLoader.loadObjModel("bird/Bird", loader),
                new ModelTexture(loader.loadTexture("bird/BirdTexture")));
        birdModel.getTexture().setNumberOfRows(2);
        Vector3f birdPosition = new Vector3f(startPosition.x, world.getTerrain().getHeightOfTerrain(startPosition.x, startPosition.z) + 25, startPosition.z);

        this.birds = new ArrayList<>();
        for (int i = 0; i < birdsInGroup * 4; i += 4) {
            Bird bird = new Bird(birdModel, this.random.nextInt(4),
                    new Vector3f(birdPosition.x + this.random.nextInt(100), birdPosition.y + this.random.nextInt(100), birdPosition.z + this.random.nextInt(100)),
                    0, this.random.nextInt(360), 0, 0.01f, this);

            world.addEntityToCorrectBiome(bird);
            this.birds.add(bird);
        }
    }

    public void setNewDestination(Terrain terrain, Vector3f birdPosition, int maxDestinationDistance, float minHeight, float maxHeight) {
        float x = (float) ((this.random.nextInt(maxDestinationDistance) - maxDestinationDistance / 2.0) + birdPosition.x);
        float z = (float) ((this.random.nextInt(maxDestinationDistance) - maxDestinationDistance / 2.0) + birdPosition.z);
        float y = (float) ((this.random.nextInt(maxDestinationDistance) - maxDestinationDistance / 2.0) + birdPosition.y);

        x = Maths.clamp(x, 40, Terrain.getSIZE() - 40);
        z = Maths.clamp(z, 40, Terrain.getSIZE() - 40);
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
