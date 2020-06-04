
float distanceFuncPoint(vec2 p,vec2 pointPos) {
        return length(p-pointPos);
}

float distanceFuncCircle(vec2 p,vec3 circleParams) {
    return abs(length(p-circleParams.xy)-circleParams.z);
}

float distanceFuncLine(vec2 p,vec2 lineParams) {
        float k=lineParams.x*p.x-p.y+lineParams.y;
        return k*k/(lineParams.x*lineParams.x+1.0);
}

float distanceFuncSin(vec2 p,vec2 sinParams) {
    float dfx=sinParams.x*sinParams.y*cos(sinParams.y*p.x);
    return abs(sinParams.x*sin(sinParams.y*p.x)-p.y)/sqrt(1.0+dfx*dfx);
}

