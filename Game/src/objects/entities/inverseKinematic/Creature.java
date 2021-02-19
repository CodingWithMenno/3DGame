package objects.entities.inverseKinematic;

import collisions.Collision;
import gameLoop.MainGameLoop;
import models.TexturedModel;
import objects.entities.MovableEntity;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import renderEngine.ObjLoader;
import terrains.Terrain;
import terrains.World;
import textures.ModelTexture;

public class Creature extends MovableEntity {

    private KinematicSegment leg;

    public Creature(Loader loader, World world, TexturedModel model, Vector3f position, float scale) {
        super(model, position, 0, 0, 0, scale);

        TexturedModel blackLegModel = new TexturedModel(ObjLoader.loadObjModel("leg/Leg", loader),
                new ModelTexture(loader.loadTexture("leg/LegTexture")));
        TexturedModel whiteLegModel = new TexturedModel(ObjLoader.loadObjModel("leg/Leg", loader),
                new ModelTexture(loader.loadTexture("fox/FoxTexture")));


        KinematicSegment current = new KinematicSegment(null, blackLegModel,
                new Vector3f(400, world.getTerrain().getHeightOfTerrain(400, 400) + 10, 400), 3);
        world.addEntityToCorrectBiome(current);

        for (int i = 1; i < 3; i++) {
            KinematicSegment next = new KinematicSegment(current, i % 2 == 0 ? blackLegModel : whiteLegModel,
                    new Vector3f(400, world.getTerrain().getHeightOfTerrain(400, 400) + 10, 400), 3);
            world.addEntityToCorrectBiome(next);

            current = next;
        }

        this.leg = current;
    }

    @Override
    protected void update(Terrain terrain) {

        KinematicSegment next = this.leg;
        while (next != null) {
            next.update(MainGameLoop.player.getPosition());
            next = next.getParent();
        }

        this.leg.update(MainGameLoop.player.getPosition());
    }

    @Override
    public void onCollided(Collision collision) {

    }

    @Override
    protected void resetVerticalSpeed() {

    }
}
