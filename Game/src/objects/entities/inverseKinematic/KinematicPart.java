package entities.inverseKinematic;

import models.TexturedModel;

import java.util.List;

public class KinematicPart {

    private TexturedModel model;

    private List<KinematicPart> children;
    private float rotX, rotY, rotZ;

}
