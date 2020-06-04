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
	float waveDisp=0.5+0.5*sin(colorFrequency*(abs(texCoord.x-0.5)+abs(texCoord.y-0.5))-time);
	float ci=(1024.0*cl.x+32.0*cl.y+cl.z)/1057.0;
	vec2 ptexCoord=vec2(ci+waveDisp,0.0);
	gl_FragColor=brightness*ci*texture2D(palette,ptexCoord);
}
