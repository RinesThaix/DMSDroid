package ru.dms.app.gui;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.dms.app.data.ExactServerInfo;

/**
 * Created by RinesThaix on 19.02.16.
 */

public class ExactServerView {

    public static void show(LinearLayout ll, ExactServerInfo server) {
        addLine(ll, "Имя сервера: %s", server.getName());
        addLine(ll, "Игроков онлайн: %d", server.getOnline());
        addLine(ll, "Адрес: %s:%d", server.getAddress(), server.getPort());
        addLine(ll, "Серверная машина: %s", server.getMachineName());
        View divider = new View(ll.getContext());
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2);
        ll.addView(divider, dividerParams);
    }

    private static void addLine(LinearLayout ll, String line, Object... args) {
        addLine(ll, String.format(line, args));
    }

    private static void addLine(LinearLayout ll, String line) {
        TextView text = new TextView(ll.getContext());
        text.setText(line);
        ll.addView(text);
    }

}
