package entities;

import animation.AnimatedModel;
import collisions.AABB;
import collisions.Collision;
import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;
import renderEngine.ObjLoader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class Entity implements Cloneable {

	public static final float GRAVITY = -50;

	private TexturedModel staticModel;
	private AnimatedModel animatedModel;
	private final boolean isAnimated;

	protected Vector3f position;
	protected float rotX, rotY, rotZ;
	protected float scale;

	private List<AABB> collisionBoxes;

	protected int textureIndex = 0;

	private void init(Vector3f position, float rotX, float rotY, float rotZ,
					float scale, Vector3f... collisionBoxes) {

		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;

		this.collisionBoxes = new ArrayList<>();
		if (collisionBoxes.length == 0) {
			return;
		}

		for (Vector3f collisionBox : collisionBoxes) {
			Vector3f box = new Vector3f(collisionBox);
			box.x *= scale;
			box.y *= scale;
			box.z *= scale;
			this.collisionBoxes.add(new AABB(this.position, box));
		}
	}

	public Entity(TexturedModel staticModel, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, Vector3f... collisionBoxes) {
		this.staticModel = staticModel;
		this.isAnimated = false;
		init(position, rotX, rotY, rotZ, scale, collisionBoxes);
	}

	public Entity(TexturedModel staticModel, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, Vector3f... collisionBoxes) {
		this(staticModel, position, rotX, rotY, rotZ, scale, collisionBoxes);
		this.textureIndex = textureIndex;
	}

	public Entity(AnimatedModel animatedModel, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, Vector3f... collisionBoxes) {
		this.animatedModel = animatedModel;
		this.isAnimated = true;
		init(position, rotX, rotY, rotZ, scale, collisionBoxes);
	}

	public Entity(AnimatedModel animatedModel, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, Vector3f... collisionBoxes) {
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

	//Override this method
	public void onCollide(Collision collision) {
		//Do something like play a sound and perform hit animation
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
			for (AABB box : this.collisionBoxes) {
				e.collisionBoxes.add((AABB) box.clone());
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

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		for (AABB collisionBox : this.collisionBoxes) {
			collisionBox.setNewCenter(new Vector3f(position));
		}

		this.position = new Vector3f(position);
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public List<AABB> getCollisionBoxes() {
		return collisionBoxes;
	}

	public boolean hasCollisions() {
		return !this.collisionBoxes.isEmpty();
	}

	public int getTextureIndex() {
		return textureIndex;
	}
}
