package objects.entities;

import animation.AnimatedModel;
import collisions.OBB;
import models.TexturedModel;

import objects.GameObject;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Entity extends GameObject implements Cloneable {

	public static final float GRAVITY = -70;

	private TexturedModel staticModel;
	private AnimatedModel animatedModel;
	private final boolean isAnimated;

	protected float scale;

	private List<OBB> collisionBoxes;

	protected int textureIndex = 0;

	private void init(float scale, OBB... collisionBoxes) {
		this.scale = scale;

		this.collisionBoxes = new ArrayList<>();
		if (collisionBoxes.length == 0) {
			return;
		}

		for (OBB collisionBox : collisionBoxes) {
			collisionBox.rotX(this.rotX);
			collisionBox.rotY(this.rotY);
			collisionBox.rotZ(this.rotZ);
			this.collisionBoxes.add(collisionBox);
		}
	}

	public Entity(TexturedModel staticModel, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, OBB... collisionBoxes) {
		super(position, rotX, rotY, rotZ);
		this.staticModel = staticModel;
		this.isAnimated = false;
		init(scale, collisionBoxes);
	}

	public Entity(TexturedModel staticModel, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, OBB... collisionBoxes) {
		this(staticModel, position, rotX, rotY, rotZ, scale, collisionBoxes);
		this.textureIndex = textureIndex;
	}

	public Entity(AnimatedModel animatedModel, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, OBB... collisionBoxes) {
		super(position, rotX, rotY, rotZ);
		this.animatedModel = animatedModel;
		this.isAnimated = true;
		init(scale, collisionBoxes);
	}

	public Entity(AnimatedModel animatedModel, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, OBB... collisionBoxes) {
		this(animatedModel, position, rotX, rotY, rotZ, scale, collisionBoxes);
		this.textureIndex = textureIndex;
	}

	public void updateAnimation() {
		if (this.isAnimated) {
			this.animatedModel.updateCurrentAnimation();
		}
	}

	public void setAnimation(int animationNumber) {
		if (this.isAnimated) {
			this.animatedModel.setCurrentAnimation(animationNumber);
		}
	}

	public float getTextureXOffset() {
		if (!this.isAnimated) {
			int column = this.textureIndex % this.staticModel.getTexture().getNumberOfRows();
			return (float) column / (float) this.staticModel.getTexture().getNumberOfRows();
		} else {
			int column = this.textureIndex % this.animatedModel.getCurrentModel().getTexture().getNumberOfRows();
			return (float) column / (float) this.animatedModel.getCurrentModel().getTexture().getNumberOfRows();
		}
	}

	public float getTextureYOffset() {
		if (!this.isAnimated) {
			int row = this.textureIndex / this.staticModel.getTexture().getNumberOfRows();
			return (float) row / (float) this.staticModel.getTexture().getNumberOfRows();
		} else {
			int row = this.textureIndex / this.animatedModel.getCurrentModel().getTexture().getNumberOfRows();
			return (float) row / (float) this.animatedModel.getCurrentModel().getTexture().getNumberOfRows();
		}
	}

	public TexturedModel getModel() {
		if (!this.isAnimated) {
			return this.staticModel;
		}

		return this.animatedModel.getCurrentModel();
	}

	@Override
	public Object clone() {
		try {
			Entity e = (Entity) super.clone();
			e.collisionBoxes = new ArrayList<>();
			for (OBB box : this.collisionBoxes) {
				e.collisionBoxes.add((OBB) box.clone());
			}
			return e;

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setModel(TexturedModel staticModel) {
		this.staticModel = staticModel;
	}

	public void setPosition(Vector3f position) {
		for (OBB collisionBox : this.collisionBoxes) {
			collisionBox.setNewCenter(new Vector3f(position.x, position.y + 2, position.z));
		}

		this.position = new Vector3f(position);
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;

		for (OBB box : this.collisionBoxes) {
			box.rotX(rotX);
		}
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;

		for (OBB box : this.collisionBoxes) {
			box.rotY(rotY);
		}
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;

		for (OBB box : this.collisionBoxes) {
			box.rotZ(rotZ);
		}
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public List<OBB> getCollisionBoxes() {
		return collisionBoxes;
	}

	public boolean hasCollisions() {
		return !this.collisionBoxes.isEmpty();
	}

	public int getTextureIndex() {
		return textureIndex;
	}
}
