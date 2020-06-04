package drwdrd.adev.ui.ColorPicker;

import android.graphics.Color;

public class ColorInfo {
    protected float hue;
    protected float saturation;
    protected float lightness;
    protected int color;

    public ColorInfo(int color) {
        float[] hsl=ColorToHSL(color);
        this.color=color;
        this.hue=hsl[0];
        this.saturation=hsl[1];
        this.lightness=hsl[2];
    }

    public int getColor() {
        return this.color;
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getLightness() {
        return lightness;
    }

    public String getHtmlCode() {
        return "#" + Integer.toHexString(color);
    }

    public ColorInfo(float hue,float saturation,float lightness) {
        this.hue=hue;
        this.saturation=saturation;
        this.lightness=lightness;
        this.color=HSLToColor(hue,saturation,lightness);
    }

    private static float hue2rgb(float p,float q,float t) {
        if(t<0f) t+=1f;
        if(t>1f) t-=1f;
        if(t<1f/6f) return p+(q-p)*6f*t;
        if(t<1f/2f) return q;
        if(t<2f/3f) return p+(q-p)*(2f/3f-t)*6f;
        return p;
    }

    public static int HSLToColor(float h,float s,float l) {
        float r,g,b;

        if(s==0f) {
            r = g = b = l; // achromatic
        } else {
            float q=l<0.5f?l*(1f+s):l+s-l*s;
            float p=2f*l-q;
            r=hue2rgb(p,q,h+1f/3f);
            g=hue2rgb(p,q,h);
            b=hue2rgb(p,q,h-1f/3f);
        }
        return Color.rgb(Math.round(255f * r), Math.round(255f * g), Math.round(255f * b));
    }

    public static float[] ColorToHSL(int color) {
        float r = (float) Color.red(color) / 255f;
        float g = (float) Color.green(color) / 255f;
        float b = (float) Color.blue(color) / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h, s, l = (max + min) / 2f;

        if (max == min) {
            h = s = 0f; // achromatic
        } else {
            float d = max - min;
            s = l > 0.5f ? d / (2f - max - min) : d / (max + min);
            if (max == r) h = (g - b) / d + (g < b ? 6 : 0);
            else if (max == g) h = (b - r) / d + 2;
            else h = (r - g) / d + 4;       // max==b
            h /= 6f;
        }
        return new float[]{h, s, l};
    }

    public static int mix(int startColor,int endColor,float d) {
        float sr=Color.red(startColor);
        float sg=Color.green(startColor);
        float sb=Color.blue(startColor);

        float er=Color.red(endColor);
        float eg=Color.green(endColor);
        float eb=Color.blue(endColor);

        int r=(int)(sr*(1.0f-d)+d*er);
        int g=(int)(sg*(1.0f-d)+d*eg);
        int b=(int)(sb*(1.0f-d)+d*eb);
        return Color.rgb(r,g,b);
    }
}
