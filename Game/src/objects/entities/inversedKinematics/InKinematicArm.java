package objects.entities.inversedKinematics;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.World;

import java.util.List;

public class InKinematicArm {

    private final InKinematicSegment segments[];

    private Vector3f basePoint;
    private boolean isAttached;

    public InKinematicArm(World world, int totalSegments, Vector3f basePoint, boolean isAttached, float armLength, TexturedModel model, float modelDefaultLength) {
        this.basePoint = basePoint;
        this.isAttached = isAttached;

        this.segments = new InKinematicSegment[totalSegments];
        this.segments[0] = new InKinematicSegment(model, basePoint, armLength / totalSegments, modelDefaultLength);
        world.addEntityToCorrectBiome(this.segments[0]);

        for (int i = 1; i < totalSegments; i++) {
            InKinematicSegment segment = new InKinematicSegment(model, this.segments[i-1].getPosition(), armLength / totalSegments, modelDefaultLength);
            segment.setPosition(Vector3f.sub(this.segments[i-1].getPosition(), segment.getEndPosition(), null));

            this.segments[i] = segment;
            world.addEntityToCorrectBiome(this.segments[i]);
        }
    }

    public InKinematicArm(List<InKinematicSegment> segments, Vector3f basePoint, boolean isAttached) {
        this.basePoint = basePoint;
        this.isAttached = isAttached;

        this.segments = new InKinematicSegment[segments.size()];
        for (int i = 0; i < segments.size(); i++) {
            this.segments[i] = segments.get(i);
        }
    }

    public void reachForPoint(Vector3f point) {
        InKinematicSegment end = this.segments[this.segments.length - 1];
        end.follow(point);
        end.calculateEndPos();

        for (int i = this.segments.length - 2; i >= 0; i--) {
            this.segments[i].follow(new Vector3f(this.segments[i+1].getPosition()));
            this.segments[i].calculateEndPos();
        }

        if (!this.isAttached) {
            return;
        }

        this.segments[0].setPosition(this.basePoint);
        for (int i = 1; i < this.segments.length; i++) {
            this.segments[i].setPosition(this.segments[i-1].getEndPosition());
        }
    }

    public void setBasePoint(Vector3f newBasePoint) {
        this.basePoint = newBasePoint;
    }
}
