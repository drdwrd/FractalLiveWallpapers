

attribute vec3 position;
attribute vec2 tex;

uniform mat4 modelViewProjectionMatrix;

varying vec2 texCoord;

void main()
{
	texCoord=tex;
	gl_Position=modelViewProjectionMatrix*vec4(position,1.0);
}

