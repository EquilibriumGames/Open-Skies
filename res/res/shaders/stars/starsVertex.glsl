#version 130

layout(location = 0) in vec2 position;
layout(location = 1) in mat4 modelMatrix;
layout(location = 5) in vec3 colourOffset;

varying vec2 pass_textureCoords;
varying vec3 pass_colourOffset;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform vec4 clipPlane;

void main(void) {
	mat4 modelViewMatrix = viewMatrix * modelMatrix;
	gl_ClipDistance[0] = dot(modelMatrix * vec4(position, 0.0, 1.0), clipPlane);
	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

	pass_textureCoords = position + vec2(0.5, 0.5);
	pass_textureCoords.y = 1.0 - pass_textureCoords.y;

	pass_colourOffset = colourOffset;
}
