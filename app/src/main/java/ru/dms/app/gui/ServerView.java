package ru.dms.app.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ru.dms.app.ModuleManager;
import ru.dms.app.R;
import ru.dms.app.data.ServerInfo;

/**
 * Created by RinesThaix on 19.12.16.
 */

@SuppressLint("ViewConstructor")
public class ServerView extends RelativeLayout {

    public ServerView(Context context, final ServerInfo info) {
        super(context);

        RelativeLayout top = new RelativeLayout(context);
        TextView tname = new TextView(context);
        tname.setText(info.getName());
        tname.setTextColor(info.getOnline() > 0 ? Color.DKGRAY : Color.LTGRAY);
        top.addView(tname, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        TextView tonline = new TextView(context);
        if(info.getOnline() > 0) {
            tonline.setText(info.getOnline() + " " + getPlayers(info.getOnline()));
            tonline.setTextColor(Color.DKGRAY);
        }else {
            tonline.setText((info.isCluster() ? "кластер" : "сервер") + " пуст");
            tonline.setTextColor(Color.LTGRAY);
        }
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        top.addView(tonline, params);

        final ProgressBar bottom = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);

        bottom.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dms_gold_lighten), PorterDuff.Mode.SRC_IN);
        bottom.setMax(info.getMaxOnline() == 0 ? info.getOnline() : info.getMaxOnline());
        bottom.setProgress(info.getOnline());

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        this.addView(top, params);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.topMargin = 54;
        this.addView(bottom, params);

        if(info.getName().equals("Общий онлайн"))
            return;
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    bottom.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dms_blue_lighten), PorterDuff.Mode.SRC_IN);
                else if(event.getAction() == MotionEvent.ACTION_CANCEL)
                    bottom.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dms_gold_lighten), PorterDuff.Mode.SRC_IN);
                return false;
            }
        });
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(info.isCluster())
                    ModuleManager.setClusterOnline(info.getName());
                else
                    ModuleManager.setServerInfo(info.getName());
            }
        });
    }

    private String getPlayers(int amount) {
        int o1 = amount % 10, o2 = amount % 100;
        if(o1 == 1 && o2 != 11)
            return "игрок";
        if(o1 >= 1 && o1 <= 4 && (o2 < 10 || o2 > 20))
            return "игрока";
        return "игроков";
    }

}
