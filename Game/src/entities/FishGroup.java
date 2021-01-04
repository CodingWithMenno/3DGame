package entities;

import engineTester.MainGameLoop;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;
import toolbox.Maths;

import java.util.List;
import java.util.Random;

public class FishGroup {

    private List<Fish> fish;
    private Vector3f destination;

    private Random random;

    public FishGroup(List<Fish> fish) {
        this.random = new Random();

        this.destination = new Vector3f(80, -20, -20);
        this.fish = fish;

        for (Fish newFish : this.fish) {
            newFish.setAllFish(this.fish);
            newFish.setDestination(this.destination);
        }
    }

    public void updateAllFish(Terrain terrain) {
        float closestDistance = 100;
        for (Fish fish : this.fish) {
            fish.updateEntity(terrain);

            float distance = Maths.getDistanceBetween(fish.getPosition(), this.destination);
            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }

        if (closestDistance < 15) {
            generateNewDestination(terrain);
        }
    }

    private void generateNewDestination(Terrain terrain) {
        int x = 0;
        int z = 0;
        int terrainHeight = 0;
        while (x < 2 || z > -5 || terrainHeight >= MainGameLoop.WATER_HEIGHT - 3.5f) {
            x = (int) (this.fish.get(0).getPosition().x + this.random.nextInt(50) - 25);
            z = (int) (this.fish.get(0).getPosition().z + this.random.nextInt(50) - 25);

            terrainHeight = (int) terrain.getHeightOfTerrain(x, z) + 2;
        }

        int waterDifference = (int) Maths.difference(terrainHeight, MainGameLoop.WATER_HEIGHT - 3.5f);

        int y = terrainHeight + this.random.nextInt(waterDifference);

        this.destination = new Vector3f(x, y, z);

        for (Fish fish : this.fish) {
            fish.setDestination(this.destination);
        }
    }
}
