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

    private static final int PARTS = 50;
    private KinematicSegment[] tentacle;
    private Vector3f base;

    public Creature(Loader loader, World world, TexturedModel model, Vector3f position, float scale) {
        super(model, position, 0, 0, 0, scale);

        TexturedModel blackLegModel = new TexturedModel(ObjLoader.loadObjModel("leg/Leg", loader),
                new ModelTexture(loader.loadTexture("leg/LegTexture")));
        TexturedModel whiteLegModel = new TexturedModel(ObjLoader.loadObjModel("leg/Leg", loader),
                new ModelTexture(loader.loadTexture("fox/FoxTexture")));


        this.tentacle = new KinematicSegment[PARTS];

        this.tentacle[0] = new KinematicSegment(blackLegModel,
                new Vector3f(400, world.getTerrain().getHeightOfTerrain(400, 400) + 10, 400), 1);
        world.addEntityToCorrectBiome(this.tentacle[0]);

        for (int i = 1; i < this.tentacle.length; i++) {
            this.tentacle[i] = new KinematicSegment(i % 2 == 0 ? blackLegModel : whiteLegModel, this.tentacle[i-1].getPosition(), 1);
            this.tentacle[i].setPosition(Vector3f.sub(this.tentacle[i-1].getPosition(), this.tentacle[i].getEndPosition(), null));

            world.addEntityToCorrectBiome(this.tentacle[i]);
        }

        this.base = new Vector3f(400, world.getTerrain().getHeightOfTerrain(400, 400) + 10, 400);
    }

    @Override
    protected void update(Terrain terrain) {

        KinematicSegment end = this.tentacle[this.tentacle.length - 1];
        end.follow(new Vector3f(MainGameLoop.player.getPosition()));
        end.calculateEndPos();

        for (int i = this.tentacle.length - 2; i >= 0; i--) {
            this.tentacle[i].follow(new Vector3f(this.tentacle[i+1].getPosition()));
            this.tentacle[i].calculateEndPos();
        }

        this.tentacle[0].setPosition(this.base);

        for (int i = 1; i < this.tentacle.length; i++) {
            this.tentacle[i].setPosition(this.tentacle[i-1].getEndPosition());
        }
    }

    @Override
    public void onCollided(Collision collision) {

    }

    @Override
    protected void resetVerticalSpeed() {

    }
}
