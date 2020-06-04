package drwdrd.adev.realtimefractal.preferences;

import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;


public class OrbitTrapsSettingsDialogActivity extends OrbitTrapsSettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.0f;
        getWindow().setAttributes(params);

        DisplayMetrics metric=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int w=(int)(0.9f*Math.min(metric.widthPixels,metric.heightPixels));
        getWindow().setLayout(w,w);

        super.onCreate(savedInstanceState);
    }

}
