

float transferFunc(float color,float gradient) {
    //return 1.0/(1.0+pow(color,gradient));
    return pow(color/(color+1.0),gradient);
}
