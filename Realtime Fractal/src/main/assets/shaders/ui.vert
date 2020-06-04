

attribute vec2 position;

uniform mat3 modelMatrix;

varying vec2 texCoord;

void main()
{
	texCoord= 0.5 * position + vec2(0.5, 0.5);
	vec3 p = modelMatrix * vec3(position, 1.0);
	gl_Position=vec4(p.xy, 0.0 ,1.0);
}

