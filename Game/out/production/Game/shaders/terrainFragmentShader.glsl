#version 400 core

in vec2 pass_textureCoordinates;
flat in vec3 surfaceNormal;
in vec3 toLightVector[5];
in vec3 toCameraVector;
in float visibility;
in float mapHeight;
in vec4 shadowCoords;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform sampler2D shadowMap;

uniform vec3 lightColour[5];
uniform vec3 attenuation[5];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

uniform float shadowMapSize;
const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

const float tiling = 100;

vec3 heightToRGB(in float height) {
    vec3 terrain_colours[3];
    terrain_colours[0] = vec3(0.59, 0.90, 0.20);
    terrain_colours[1] = vec3(1, 1, 1);
    terrain_colours[2] = vec3(0.89, 0.77, 0.50);

    if (height < 0) {
        return mix(terrain_colours[0], terrain_colours[2], abs(height) / 20);
    } else {
        return terrain_colours[0];
    }
}

void main(void) {

    float texelSize = 1.0 / shadowMapSize;
    float total = 0.0;
    for(int x = -pcfCount; x <= pcfCount; x++) {
        for(int y = -pcfCount; y <= pcfCount; y++) {
            float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
            if(shadowCoords.z > objectNearestLight) {
                total += 1.0;
            }
        }
    }

    total /= totalTexels;
    float lightFactor = 1.0 - (total * shadowCoords.w);

//    vec4 blendMapColour = texture(blendMap, pass_textureCoordinates);
    vec4 blendMapColour = vec4(mix(vec3(1, 0.5, 0), vec3(0, 0.1, 1), mapHeight / 50), 0);
    float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
    vec2 tiledCoords = pass_textureCoordinates * tiling;
    vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
    vec4 rTextureColour = texture(rTexture, tiledCoords) * blendMapColour.r;
    vec4 gTextureColour = texture(gTexture, tiledCoords) * blendMapColour.g;
    vec4 bTextureColour = texture(bTexture, tiledCoords) * blendMapColour.b;

//    vec4 heightColour = vec4(heightToRGB(mapHeight), 1.0);
//    vec4 heightTextureColour = (vec4(heightColour.r, heightColour.g, heightColour.b, 1.0) * backTextureAmount);
    vec4 finalBlendMapColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;
    vec4 totalColour = finalBlendMapColour;


	vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for(int i = 0; i < 5; i++) {
        float distance = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float dotProduct = dot(unitNormal, unitLightVector);
        float brightness = max(dotProduct, 0.0);
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalDiffuse = totalDiffuse + (brightness * lightColour[i]) / attFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i]) / attFactor;
    }
    totalDiffuse = max(totalDiffuse * lightFactor, 0.15);

	out_Color = vec4(totalDiffuse, 1.0) * totalColour + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);
}