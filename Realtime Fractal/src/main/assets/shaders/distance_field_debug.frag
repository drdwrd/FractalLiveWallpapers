#ifdef GL_FRAGMENT_PRECISION_HIGH
    precision highp float;
#else
    precision mediump float;
#endif


uniform vec2 paramC;

varying vec2 texCoord;

float distanceFunc(vec2 p);

void main()
{
    float color=distanceFunc(texCoord);
    gl_FragColor=vec4(color,color,color,1.0);
}
