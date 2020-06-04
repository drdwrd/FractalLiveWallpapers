package drwdrd.adev.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DialogPreferenceWithButton extends DialogPreference {

    private View view=null;

    private String buttonText=null;
    private int buttonIconId=0;
    private boolean hasButton=false;
    private View.OnClickListener buttonOnClickListener=null;

    public DialogPreferenceWithButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void enableButton(boolean enable) {
        this.hasButton=enable;
    }

    public void setButtonText(String text) {
        this.buttonText=text;
    }

    public void setButtonIcon(int iconId) {
        this.buttonIconId=iconId;
    }

    public void setButtonOnClickListener(View.OnClickListener onClickListener) {
        this.buttonOnClickListener=onClickListener;
    }

    @Override
    public void onBindView(View view) {
        this.view=view;
        if(hasButton) {
            setButton();
        }
        super.onBindView(view);
    }

    private void setButton() {
        if(view == null) return;
        View button=null;
        if(buttonText!=null) {
            Button textButton = new Button(getContext());
            textButton.setText(buttonText);
            if (buttonIconId != 0) {
                textButton.setCompoundDrawablesWithIntrinsicBounds(buttonIconId, 0, 0, 0);
            }
            button = textButton;
        } else {
            ImageButton imageButton=new ImageButton(getContext());
            imageButton.setImageResource(buttonIconId);
            button=imageButton;
        }
        button.setFocusable(false);
        button.setOnClickListener(buttonOnClickListener);
        LinearLayout widgetFrameView = ((LinearLayout)view.findViewById(android.R.id.widget_frame));
        if(widgetFrameView == null) return;
        widgetFrameView.setVisibility(View.VISIBLE);
        widgetFrameView.setPadding(widgetFrameView.getPaddingLeft(),widgetFrameView.getPaddingTop(),widgetFrameView.getPaddingRight(),widgetFrameView.getPaddingBottom());
        // remove already create preview image
        int count = widgetFrameView.getChildCount();
        if (count > 0) {
            widgetFrameView.removeViews(0, count);
        }
        widgetFrameView.addView(button);
        widgetFrameView.setMinimumWidth(0);
        notifyChanged();
    }
}
