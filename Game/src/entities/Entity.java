package entities;

import collisions.AABB;
import models.TexturedModel;

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entity {

	protected TexturedModel model;
	protected Vector3f position;
	protected float rotX, rotY, rotZ;
	protected float scale;

	private List<AABB> collisionBoxes;

	protected int textureIndex = 0;

	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, Vector3f... collisionBoxes) {
		this.model = model;
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

	public Entity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ,
				  float scale, Vector3f... collisionBoxes) {
		this.textureIndex = textureIndex;
		this.model = model;
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

	public float getTextureXOffset() {
		int column = this.textureIndex % this.model.getTexture().getNumberOfRows();
		return (float) column / (float) this.model.getTexture().getNumberOfRows();
	}

	public float getTextureYOffset() {
		int row = this.textureIndex / this.model.getTexture().getNumberOfRows();
		return (float) row / (float) this.model.getTexture().getNumberOfRows();
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
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

}
