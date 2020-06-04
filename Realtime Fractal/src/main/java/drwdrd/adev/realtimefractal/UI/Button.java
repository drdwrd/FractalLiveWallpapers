package drwdrd.adev.realtimefractal.UI;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

public class Button {

    private RectF rectangle = null;
    private String icon = null;
    private String iconPressed = null;
    private int backgroundColor;
    private int backgroundColorPressed;
    private boolean toggleable;
    private boolean pressed = false;
    private boolean checked = false;
    private int id;

    public Button(int id, String icon, String iconPressed, boolean toggleable) {
        this.id=id;
        this.icon = icon;
        this.iconPressed = (iconPressed != null ? iconPressed : icon);
        this.backgroundColor = Color.argb(255,255,255,255);
        this.backgroundColorPressed = (iconPressed.equals(icon)  ? Color.argb(255,255,255,255) : Color.argb(153,255,255,255));
        this.toggleable = toggleable;
    }

    public int getId() {
        return id;
    }

    public void setRectangle(int left,int top,int right,int bottom) {
        this.rectangle = new  RectF(left, top, right, bottom);
    }

    public void setRectangle(RectF rect) {
        this.rectangle = rect;
    }

    public RectF getRectangle() {
        return rectangle;
    }

    public String getIcon() {
        if(pressed||checked) {
            return iconPressed;
        } else {
            return icon;
        }
    }

    public void setBackgroundColor(int backgroundColor, int backgroundColorPressed) {
        this.backgroundColor = backgroundColor;
        this.backgroundColorPressed = backgroundColorPressed;
    }

    public int getBackgroundColor() {
        if(pressed||checked) {
            return backgroundColorPressed;
        } else {
            return backgroundColor;
        }
    }

    public void setPressed(boolean pressed) {
        this.pressed=pressed;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isToggleable() {
        return toggleable;
    }
}
