package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;
import toolbox.Maths;

public class Camera {

	private static float MOUSE_SENSITIVITY = 0.05f;
	private static float AUTO_ZOOM = 0.005f;
	private static float DEFAULT_PITCH = 12;
	private static float MAX_ZOOM_IN = 8;
	private static float MAX_ZOOM_OUT = 56;

	private float distanceFromEntity = 14;
	private float angleAroundEntity = 0;
	private boolean entityIsMoving = false;
	private Entity entityToFollow;

	private Vector3f position;
	private float pitch = DEFAULT_PITCH;
	private float yaw;
	private float roll;

	private Terrain terrain;

	public Camera(Entity entityToFollow, Terrain terrain) {
		this.terrain = terrain;
		this.entityToFollow = entityToFollow;
		Vector3f pos = this.entityToFollow.getPosition();
		this.position = new Vector3f(pos.x, pos.y + 8, pos.z - 20);
	}

	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();

		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();

		Vector3f newPos = calculateCameraPosition(horizontalDistance, verticalDistance);

		if (Maths.difference(newPos.x, this.position.x) > 0.1f || Maths.difference(newPos.z, this.position.z) > 0.1f) {
			this.entityIsMoving = true;
		} else {
			this.entityIsMoving = false;
		}

		float terrainHeight = this.terrain.getHeightOfTerrain(newPos.x, newPos.z);
		float heightDifference = Maths.difference(newPos.y, terrainHeight + 1);
		if (newPos.y < terrainHeight + 1f) {
			newPos.y = terrainHeight + 1f;

			this.pitch += heightDifference;
		}

		heightDifference = Maths.difference(newPos.y, -12);
		if (newPos.y < -12) { //Zodat de camera niet onder het water komt
			newPos.y = -12;
			this.pitch += heightDifference;
		}

		this.position = newPos;

		this.yaw = 180 - (this.entityToFollow.getRotY() + this.angleAroundEntity);
	}

	private Vector3f calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = this.entityToFollow.getRotY() + this.angleAroundEntity;
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
		float pitchChange = Mouse.getDY() * MOUSE_SENSITIVITY;
		this.pitch -= pitchChange;

		if (pitchChange == 0 && this.pitch != DEFAULT_PITCH && this.entityIsMoving) {
			this.pitch = Maths.lerp(this.pitch, DEFAULT_PITCH, AUTO_ZOOM);
		}

		this.pitch = Maths.clamp(this.pitch, -6, 85);
	}

	private void calculateAngleAroundPlayer() {
		float angleChange = Mouse.getDX() * MOUSE_SENSITIVITY * 3f;
		this.angleAroundEntity -= angleChange;

		if (angleChange == 0 && this.angleAroundEntity != 0 && this.entityIsMoving) {
			this.angleAroundEntity = Maths.lerp(this.angleAroundEntity, 0, AUTO_ZOOM);
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

	public float getRoll() {
		return roll;
	}
}
