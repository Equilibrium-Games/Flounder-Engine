#version 130

#include "maths.glsl"

varying vec2 pass_textureCoords;

layout(location = 0) out vec4 out_colour;

layout(binding = 0) uniform sampler2D fontTexture;

uniform vec4 colour;
uniform vec3 borderColour;
uniform vec2 borderSizes;
uniform vec2 edgeData;

void main(void) {
    float dist = texture(fontTexture, pass_textureCoords).a;
    float alpha = smoothlyStep((1.0 - edgeData.x) - edgeData.y, 1.0 - edgeData.x, dist);
    float outlineAlpha = smoothlyStep((1.0 - borderSizes.x) - borderSizes.y, 1.0 - borderSizes.x, dist);
    float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
	vec3 overallColour = mix(borderColour, colour.rgb, alpha / overallAlpha);

    out_colour = vec4(overallColour, overallAlpha);
    out_colour.a *= colour.a;
}