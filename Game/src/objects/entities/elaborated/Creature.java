package objects.entities.elaborated;

import collisions.Collision;
import gameLoop.MainGameLoop;
import models.TexturedModel;
import objects.entities.MovableEntity;
import objects.entities.KinematicSegment;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.ObjLoader;
import terrains.Terrain;
import terrains.World;
import textures.ModelTexture;
import toolbox.Maths;

import java.util.HashMap;
import java.util.Map;

public class Creature extends MovableEntity {

    private static final int PARTS_PER_LEG = 2;
    private static final int LEG_LENGTH = 3;
    private Map<Vector3f, KinematicSegment[]> legs;
    private boolean[] isGoingToTarget;

    private float currentSpeed = 1;

    public Creature(Loader loader, World world, TexturedModel model, Vector3f position, float scale) {
        super(model, position, 0, 0, 0, scale);

        initLegs(loader, world);
    }

    private void initLegs(Loader loader, World world) {
        TexturedModel blackLegModel = new TexturedModel(ObjLoader.loadObjModel("spider/Leg", loader),
                new ModelTexture(loader.loadTexture("spider/LegTexture")));
        TexturedModel whiteLegModel = new TexturedModel(ObjLoader.loadObjModel("spider/Leg", loader),
                new ModelTexture(loader.loadTexture("fox/FoxTexture")));

        this.isGoingToTarget = new boolean[8];
        for (boolean isGoing : this.isGoingToTarget) {
            isGoing = false;
        }

        this.legs = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            Vector3f base;
            if (i < 4) {
                base = new Vector3f(this.position.x - 1.5f * this.scale, this.position.y - 1.5f * this.scale, this.position.z + (i * 2 * this.scale) - 3f * this.scale);
            } else {
                base = new Vector3f(this.position.x + 1.5f * this.scale, this.position.y - 1.5f * this.scale, this.position.z + (i * 2 * this.scale) - 11f * this.scale);
            }

            KinematicSegment[] leg = new KinematicSegment[PARTS_PER_LEG];

            leg[0] = new KinematicSegment(whiteLegModel, new Vector3f(base), LEG_LENGTH * this.scale);
            world.addEntityToCorrectBiome(leg[0]);

            for (int j = 1; j < PARTS_PER_LEG; j++) {
                leg[j] = new KinematicSegment(i % 2 == 0 ? whiteLegModel : blackLegModel, leg[j-1].getPosition(), LEG_LENGTH * this.scale);
                leg[j].setPosition(Vector3f.sub(leg[j-1].getPosition(), leg[j].getEndPosition(), null));
                world.addEntityToCorrectBiome(leg[j]);
            }

            this.legs.put(base, leg);
        }
    }

    @Override
    protected void update(Terrain terrain) {
        float dx = 0;
        float dy = terrain.getHeightOfTerrain(this.position.x, this.position.z) + 5 - this.position.y;
        float dz = this.currentSpeed * DisplayManager.getDelta();
        increasePosition(dx, dy, dz);


        int counter = 0;
        for (Vector3f base : this.legs.keySet()) {
            if (counter < this.legs.size() / 2) {
                base.set(new Vector3f(this.position.x - 1.5f * this.scale, this.position.y - 1.5f * this.scale, this.position.z + (counter * 2 * this.scale) - 3f * this.scale));
            } else {
                base.set(new Vector3f(this.position.x + 1.5f * this.scale, this.position.y - 1.5f * this.scale, this.position.z + (counter * 2 * this.scale) - 11f * this.scale));
            }

            Vector3f optimalPosition = new Vector3f(base.x + (counter < this.legs.size() / 2.0 ? - 3 : + 3), terrain.getHeightOfTerrain(base.x, base.z), base.z);

            KinematicSegment[] leg = this.legs.get(base);
            KinematicSegment end = leg[leg.length - 1];

            if (Maths.getDistanceBetween(end.getEndPosition(), optimalPosition) > 3 || this.isGoingToTarget[counter]) {
                this.isGoingToTarget[counter] = true;
                end.follow(Maths.lerp(end.getEndPosition(), optimalPosition, 0.2f));

                if (Maths.getDistanceBetween(end.getEndPosition(), optimalPosition) < 0.1) {
                    this.isGoingToTarget[counter] = false;
                }
            } else {
                end.follow(new Vector3f(end.getEndPosition().x - dx, end.getEndPosition().y - dy, end.getEndPosition().z - dz));
            }
            end.calculateEndPos();

            for (int i = leg.length - 2; i >= 0; i--) {
                leg[i].follow(new Vector3f(leg[i+1].getPosition()));
                leg[i].calculateEndPos();
            }

            leg[0].setPosition(base);
            for (int i = 1; i < leg.length; i++) {
                leg[i].setPosition(leg[i-1].getEndPosition());
            }

            counter++;
        }
    }

    @Override
    public void onCollided(Collision collision) {

    }

    @Override
    protected void resetVerticalSpeed() {

    }
}
