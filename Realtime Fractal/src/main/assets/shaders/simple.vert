

attribute vec2 position;

varying vec2 texCoord;

void main()
{
	texCoord = 0.5 * position + vec2(0.5, 0.5);
	gl_Position = vec4(position,0.0,1.0);
}

