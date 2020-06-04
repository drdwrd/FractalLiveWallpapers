precision mediump float;

uniform sampler2D colormap;
uniform sampler2D palette;

uniform float time;
uniform float colorFrequency;
uniform float brightness;

varying vec2 texCoord;

void main()
{
	vec4 cl=texture2D(colormap,texCoord);
	float ci=(1024.0*cl.x+32.0*cl.y+cl.z)/1057.0;
	vec2 ptexCoord=vec2(colorFrequency*ci+time,0.0);
	gl_FragColor=brightness*ci*texture2D(palette,ptexCoord);
}
