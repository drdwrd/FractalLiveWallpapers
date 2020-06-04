#ifdef GL_FRAGMENT_PRECISION_HIGH
    precision highp float;
#else
    precision mediump float;
#endif


uniform vec2 paramC;
uniform float bailout;
uniform float bailin;
uniform float maxIterations;
uniform float warmupIterations;
uniform float colorGradient;

varying vec2 texCoord;

float distanceFunc(vec2 p);
vec4 colorFunc(float gradient);
float transferFunc(float color,float gradient);

void main()
{

    vec2 zn=paramC;
    vec2 zp=zn;
    float color=10000000.0;
    float i=0.0;
	while(i<maxIterations)
	{
        zn.x=zp.x*zp.x-zp.y*zp.y+texCoord.x;
        zn.y=2.0*zp.x*zp.y+texCoord.y;
        float dn=length(zn);
        float dnp=distance(zp,zn);

        color=min(color,distanceFunc(zn));

        if(dn>bailout)
        {
            break;
        }
        else if(i>warmupIterations)
        {
            if(dnp<bailin)
            {
                break;
            }
        }
        zp=zn;
        i+=1.0;
	}

    color=transferFunc(color,colorGradient);
    gl_FragColor=colorFunc(color);
}
