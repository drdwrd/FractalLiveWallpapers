

float transferFunc(float color,float gradient) {
    //return 1.0/(1.0+pow(gradient,color));
    return 2.0*pow(1.0/(gradient+1.0),color/(color+1.0))-1.0;
}
