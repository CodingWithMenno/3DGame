package entities;

import entities.elaborated.Player;
import input.Inputs;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.World;
import toolbox.Maths;

public class Camera {

	private static float AUTO_ZOOM = 5;
	private static float DEFAULT_PITCH = 12;
	private static float MAX_ZOOM_IN = 8;
	private static float MAX_ZOOM_OUT = 100;
	private static float ROTATION_SPEED = 15;

	private float distanceFromEntity = 14;
	private float angleAroundEntity = 0;
	private Entity entityToFollow;
	private float subtractRotation = 0;
	private float targetRotation = 0;

	private Vector3f position;
	private float pitch = DEFAULT_PITCH;
	private float yaw;

	private Terrain terrain;

	public Camera(Entity entityToFollow, Terrain terrain) {
		this.terrain = terrain;
		this.entityToFollow = entityToFollow;
		Vector3f pos = this.entityToFollow.getPosition();
		this.position = new Vector3f(pos.x, pos.y + 8, pos.z - 20);
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();

		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();

		Vector3f newPos = calculateCameraPosition(horizontalDistance, verticalDistance);

		float terrainHeight = this.terrain.getHeightOfTerrain(newPos.x, newPos.z);
		float heightDifference = Maths.difference(newPos.y, terrainHeight + 1);
		if (newPos.y < terrainHeight + 1f) {
			newPos.y = terrainHeight + 1f;

			this.pitch += heightDifference;
		}

		heightDifference = Maths.difference(newPos.y, -12);
		if (newPos.y < World.getWaterHeight() + 1) {
			newPos.y = World.getWaterHeight() + 1;
			this.pitch += heightDifference;
		}

		this.pitch = Maths.clamp(this.pitch, -20, 85);

		this.position = newPos;
		this.position = Maths.clamp(new Vector3f(this.position), new Vector3f(1, -1000, 1), new Vector3f(Terrain.getSIZE() - 1, 1000, Terrain.getSIZE() - 1));
		if (this.position.y < terrainHeight + 1f) {
			this.position.y = terrainHeight + 1f;
		}

		this.yaw = 180 - (this.targetRotation + this.angleAroundEntity);
	}

	private Vector3f calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float target = this.entityToFollow.getRotY() - this.subtractRotation;
		this.targetRotation = Maths.lerp(this.targetRotation, target, ROTATION_SPEED * DisplayManager.getDelta());

		float theta = this.targetRotation + this.angleAroundEntity;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		float toPosX = this.entityToFollow.getPosition().x - offsetX;
		float toPosZ = this.entityToFollow.getPosition().z - offsetZ;
		float toPosY = (this.entityToFollow.getPosition().y + verticalDistance) + 4;

		return new Vector3f(toPosX, toPosY, toPosZ);
	}

	private float calculateHorizontalDistance() {
		return (float) (this.distanceFromEntity * Math.cos(Math.toRadians(this.pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (this.distanceFromEntity * Math.sin(Math.toRadians(this.pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.02f;
		this.distanceFromEntity -= zoomLevel;
		this.distanceFromEntity = Maths.clamp(this.distanceFromEntity, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}

	private void calculatePitch() {
		float pitchChange = Mouse.getDY() * Inputs.SENSITIVITY;
		this.pitch -= pitchChange;
	}

	private void calculateAngleAroundPlayer() {
		if (Keyboard.isKeyDown(Inputs.FREE_CAMERA_ANGLE)) {
			float angleChange = Mouse.getDX() * Inputs.SENSITIVITY * 3f;
			this.angleAroundEntity -= angleChange;
		}

		if (this.entityToFollow instanceof MovableEntity) {
			if (((MovableEntity) this.entityToFollow).isMovingAbove(0.01f)) {
				this.angleAroundEntity = Maths.lerp(this.angleAroundEntity, 0, AUTO_ZOOM * DisplayManager.getDelta());
			}
		}

		this.angleAroundEntity = Maths.clamp(this.angleAroundEntity, -180, 180);
	}

	public void invertPitch() {
		this.pitch = -pitch;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setSubtractRotation(float subtractRotation) {
		this.subtractRotation = subtractRotation;
	}
}
