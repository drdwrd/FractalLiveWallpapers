package drwdrd.adev.ui.ColorPicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import drwdrd.adev.ui.R;

public class ColorPickerDialog extends Dialog {

    private ColorInfo colorValue = new ColorInfo(Color.BLACK);
    private ColorInfo defaultColorValue = new ColorInfo(Color.BLACK);
    private HSMapView hsMap=null;
    private LMapView lMap=null;
    private ColorView colorPreview=null;
    private TextView htmlCodeText=null;

    public ColorPickerDialog(Context context, int defaultColorValue) {
        super(context);
        this.defaultColorValue=new ColorInfo(defaultColorValue);
        this.colorValue=new ColorInfo(defaultColorValue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View layout=getLayoutInflater().inflate(R.layout.colorpicker_dialog, null);
        setContentView(layout);

        hsMap=(HSMapView)layout.findViewById(R.id.hsMap);
        hsMap.setColor(defaultColorValue);

        lMap=(LMapView)layout.findViewById(R.id.lMap);
        lMap.setColor(defaultColorValue);

        colorPreview=(ColorView)layout.findViewById(R.id.colorPreview);
        colorPreview.setPreviewColor(defaultColorValue.getColor());

        htmlCodeText=(TextView)layout.findViewById(R.id.htmlCodeText);
        htmlCodeText.setText(defaultColorValue.getHtmlCode());

        hsMap.setListener(new ColorComponentView.OnColorChangedListener() {
            @Override
            public void onColorChanged(ColorInfo color) {
                colorValue = color;
                lMap.setColor(color);
                colorPreview.setPreviewColor(color.getColor());
                htmlCodeText.setText(color.getHtmlCode());
            }
        });

        lMap.setListener(new ColorComponentView.OnColorChangedListener() {
            @Override
            public void onColorChanged(ColorInfo color) {
                colorValue = color;
                hsMap.setColor(color);
                colorPreview.setPreviewColor(color.getColor());
                htmlCodeText.setText(color.getHtmlCode());
            }
        });

        Button acceptButton=(Button)layout.findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultColorValue=colorValue;
                dismiss();
            }
        });

        Button cancelButton=(Button)layout.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        setTitle("Color Picker");
    }

    public int getColor() {
        return defaultColorValue.getColor();
    }

    public void setColor(int defaultColor) {
        this.defaultColorValue=new ColorInfo(defaultColor);
        this.colorValue=new ColorInfo(defaultColor);
    }
}
