

attribute vec2 position;

uniform vec2 aspect;
uniform mat3 texMatrix;

varying vec2 texCoord;

void main()
{
    vec3 t = texMatrix * vec3(position, 1.0);
	texCoord = t.xy;
	gl_Position = vec4(aspect * position, 0.0, 1.0);
}

