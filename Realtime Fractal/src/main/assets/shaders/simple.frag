

uniform sampler2D renderTexture;

varying vec2 texCoord;

void main()
{
	gl_FragColor=texture2D(renderTexture,texCoord);
}
