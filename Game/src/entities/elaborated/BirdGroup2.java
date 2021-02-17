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

public class BirdGroup2 {

    private List<Bird2> birds;

    public BirdGroup2(Vector3f startPosition, int birdsInGroup, Loader loader, World world) {
        Random random = new Random();

        TexturedModel birdModel = new TexturedModel(ObjLoader.loadObjModel("fish/Fish", loader),
                new ModelTexture(loader.loadTexture("fish/FishTexture")));
        birdModel.getTexture().setNumberOfRows(2);
        Vector3f birdPosition = new Vector3f(startPosition.x, world.getTerrain().getHeightOfTerrain(startPosition.x, startPosition.z) + 25, startPosition.z);

        this.birds = new ArrayList<>();
        for (int i = 0; i < birdsInGroup * 4; i += 4) {
            Bird2 bird = new Bird2(birdModel, random.nextInt(4),
//                    new Vector3f(birdPosition.x + random.nextInt(50), birdPosition.y + random.nextInt(50), birdPosition.z + random.nextInt(50)),
                    new Vector3f(birdPosition.x + i, birdPosition.y + i, birdPosition.z),
//                    random.nextInt(360), random.nextInt(360), 0, 1f, this);
                    0, 0, 0, 1f, this);

            world.addEntityToCorrectBiome(bird);
            this.birds.add(bird);
        }
    }

    public List<Bird2> getBirds() {
        return this.birds;
    }
}
