

float transferFunc(float color,float gradient) {
    //return pow(1.0/(1.0+color),gradient);
    float c=2.0*pow(1.0+color,gradient)-1.0;
    return c/(c+1.0);
}
