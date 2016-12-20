package ru.dms.app.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ru.dms.app.R;
import ru.dms.app.data.MachineInfo;

/**
 * Created by RinesThaix on 19.02.16.
 */

@SuppressLint("ViewConstructor")
public class MachineView extends RelativeLayout {

    public MachineView(Context context, MachineInfo info) {
        super(context);

        RelativeLayout text = new RelativeLayout(context);

        TextView machineName = new TextView(context);
        machineName.setText(info.getName());
        machineName.setTextColor(info.getLoadPercentage() > 80 ? Color.RED : Color.DKGRAY);
        text.addView(machineName, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        TextView load = new TextView(context);
        load.setText(info.getLoad() + "/" + info.getMaxLoad() + " load average");
        load.setTextColor(info.getLoadPercentage() > 80 ? Color.RED : Color.DKGRAY);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        text.addView(load, params);

        ProgressBar bar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        bar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dms_gold_lighten), PorterDuff.Mode.SRC_IN);
        bar.setMax(info.getMaxLoad());
        bar.setProgress(info.getLoad());

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        this.addView(text, params);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.topMargin = 54;
        this.addView(bar, params);
    }

}
