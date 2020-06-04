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

//TODO: distance max should changed with zoom according to: http://en.wikibooks.org/wiki/Fractals/Iterations_in_the_complex_plane/Julia_set#External_distance_estimation


vec4 colorFunc(float gradient);
float transferFunc(float color,float gradient);

void main()
{

    vec2 z=paramC;
    vec2 dz=vec2(1.0,0.0);
    float zmn=dot(z,z);
    float zmp=zmn;
    float i=0.0;
    float color=0.0;
    while(i<maxIterations)
    {
        dz=2.0*vec2(z.x*dz.x-z.y*dz.y,z.x*dz.y+z.y*dz.x);
        z=vec2(z.x*z.x-z.y*z.y,2.0*z.x*z.y)+texCoord;
        zmn=dot(z,z);
        if(zmn>bailout)
        {
            color=sqrt(zmn/dot(dz,dz))*log(zmn);
            color=transferFunc(color,colorGradient);
            break;
        }
        else if(i>warmupIterations)
        {
            if(abs(zmn-zmp)<bailin)
            {
                break;
            }
        }
        zmp=zmn;
        i+=1.0;
    }

    gl_FragColor=colorFunc(color);
}
