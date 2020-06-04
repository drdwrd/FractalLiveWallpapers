precision mediump float;


uniform sampler2D icon;
uniform vec4 color;

varying vec2 texCoord;



void main()
{
	gl_FragColor=mix(color,color * texture2D(icon,texCoord),color.a);
//	gl_FragColor=vec4(1.0,1.0,0.0,1.0);
}
