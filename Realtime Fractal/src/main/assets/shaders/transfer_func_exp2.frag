

float transferFunc(float color,float gradient) {
    //return pow(gradient,1.0/(1.0+color));
    float c=2.0*pow(1.0+gradient,color)-1.0;
    return c/(c+1.0);
}
