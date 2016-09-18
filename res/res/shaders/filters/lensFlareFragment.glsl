#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;
uniform vec2 cameraPosition;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
    out_colour = texture(originalTexture, pass_textureCoords);
    // TODO: Make effects!
}