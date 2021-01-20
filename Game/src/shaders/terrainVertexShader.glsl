#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

flat out vec3 surfaceNormal;
out vec3 toLightVector[5];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;
flat out vec4 surfaceColour;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[5];

uniform mat4 toShadowMapSpace;

uniform float density;
const float gradient = 3;

uniform float shadowDistance;
const float transitionDistance = 10.0;

uniform vec4 plane;

uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
const float tiling = 100;

void main(void) {

	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	shadowCoords = toShadowMapSpace * worldPosition;

	gl_ClipDistance[0] = dot(worldPosition, plane);

	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;

	surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	for(int i = 0; i < 5; i++) {
	    toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0 ,0.0, 1.0)).xyz - worldPosition.xyz;

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);

	distance = distance - (shadowDistance - transitionDistance);
	distance = distance / transitionDistance;
	shadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);

	float mapHeight = worldPosition.y;
	vec4 textureAmount = vec4(0, 0, 0, 0);
	if(mapHeight < -5) {
		textureAmount.r = 1;
	}
	if(mapHeight >= -5 && mapHeight < 40) {
		textureAmount.g = 1;
	}
	if(mapHeight >= 40) {
		textureAmount.b = 1;
	}

	vec2 tiledCoords = textureCoordinates * tiling;
	vec4 rTextureColour = texture(rTexture, tiledCoords) * textureAmount.r;
	vec4 gTextureColour = texture(gTexture, tiledCoords) * textureAmount.g;
	vec4 bTextureColour = texture(bTexture, tiledCoords) * textureAmount.b;

	surfaceColour = rTextureColour + gTextureColour + bTextureColour;
}