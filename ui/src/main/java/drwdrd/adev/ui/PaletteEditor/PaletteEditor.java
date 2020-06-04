package drwdrd.adev.ui.PaletteEditor;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import drwdrd.adev.ui.ColorPicker.ColorPickerDialog;
import drwdrd.adev.ui.ColorPicker.ColorView;
import drwdrd.adev.ui.R;

public class PaletteEditor extends Dialog {

    private static final float BUTTON_WIDTH = 100.0f;
    private PaletteView paletteView=null;
    private PaletteInfo defaultPalette=null;
    private ArrayList<ColorView> keyColors=null;
    private ArrayList<CheckBox> checkBoxes=null;
    private float density=1f;


    public PaletteEditor(Context context,PaletteInfo paletteInfo) {
        super(context);
        this.defaultPalette=new PaletteInfo(paletteInfo);
    }

    public void setPalette(PaletteInfo paletteInfo) {
        this.defaultPalette=new PaletteInfo(paletteInfo);
    }

    public PaletteInfo getPalette() {
        return defaultPalette;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        density = getContext().getResources().getDisplayMetrics().density;


        View layout=getLayoutInflater().inflate(R.layout.palette_editor, null);
        setContentView(layout);

        paletteView=(PaletteView)layout.findViewById(R.id.paletteView);

        LinearLayout keyColorsLayout=(LinearLayout)layout.findViewById(R.id.keyColors);
        LinearLayout checkBoxesLayout=(LinearLayout)layout.findViewById(R.id.checkBoxes);

        int width = (int)(BUTTON_WIDTH * density + 0.5f);
        int margin = (int)(1f*density+0.5f);

        keyColors=new ArrayList<ColorView>();
        checkBoxes=new ArrayList<CheckBox>();

        for(int i=0;i<defaultPalette.getKeyColors().size();i++) {
            final ColorView keyColor=new ColorView(getContext());
            final int idx=i;
            keyColors.add(i, keyColor);
            LinearLayout.LayoutParams linearLayoutParams=new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            linearLayoutParams.setMargins(0,( i == 0 ) ? 0 : margin,0,( i == defaultPalette.getKeyColors().size()-1 ) ? 0: margin);
            keyColor.setLayoutParams(linearLayoutParams);
            keyColor.setPreviewColor(defaultPalette.getKeyColor(i).color);
            keyColor.setEnabled(defaultPalette.getKeyColor(i).enabled);
            keyColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int color=defaultPalette.getKeyColor(idx).color;
                    ColorPickerDialog colorPickerDialog=new ColorPickerDialog(getContext(),color);
                    colorPickerDialog.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            ColorPickerDialog colorPickerDialog=(ColorPickerDialog)dialogInterface;
                            keyColor.setPreviewColor(colorPickerDialog.getColor());
                            paletteView.setColor(idx,colorPickerDialog.getColor());
                        }
                    });
                    colorPickerDialog.show();
                }
            });
            keyColorsLayout.addView(keyColor);
            CheckBox checkBox=new CheckBox(getContext());
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.MATCH_PARENT,1.0f));
            checkBox.setChecked(defaultPalette.getKeyColor(i).enabled);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    keyColor.setEnabled(b);
                    paletteView.enableColor(idx, b);
                }
            });
            checkBoxes.add(i, checkBox);
            checkBoxesLayout.addView(checkBox);
        }

        Button acceptButton=(Button)layout.findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultPalette=new PaletteInfo(paletteView.getCurrentPalette());
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

        setTitle("Palette Editor");
    }

    @Override
    protected void onStart() {

        super.onStart();

        paletteView.setPalette(defaultPalette);

        int i=0;
        for(PaletteInfo.KeyColor keyColor :defaultPalette.getKeyColors()) {
            keyColors.get(i).setPreviewColor(keyColor.color);
            checkBoxes.get(i).setChecked(keyColor.enabled);
            i++;
        }
    }

    public Bitmap getPalettePreview(int width,int height) {
        return defaultPalette.buildPaletteBitmap(width, height, true);
    }


}
