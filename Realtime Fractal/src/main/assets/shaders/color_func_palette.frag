
uniform sampler2D palette;
uniform vec4 gamma;


vec4 colorFunc(float gradient) {
    return pow(texture2D(palette,vec2(gradient,0.0)),gamma);
}