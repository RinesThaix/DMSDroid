package ru.dms.app.gui;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.dms.app.util.RDate;
import ru.dms.app.data.PlayerInfo;

/**
 * Created by RinesThaix on 19.02.16.
 */

public class PlayerView {

    public static void show(LinearLayout ll, PlayerInfo player) {
        addLine(ll, "Ник игрока: %s", player.getName());
        addLine(ll, "");
        addLine(ll, "Основная группа: %s", player.getMainGroup());
        StringBuilder sb = new StringBuilder();
        for(String group : player.getAllGroups())
            sb.append(group).append(", ");
        String groups = sb.toString();
        groups = groups.substring(0, groups.length() - 2) + ".";
        addLine(ll, "Все группы: %s", groups);
        addLine(ll, "");
        addLine(ll, "В сети: %s", player.isOnline() ? "да" : "нет");
        addLine(ll, "%s: %s", player.isOnline() ? "Сервер" : "Последний сервер", player.getLastServer());
        if(!player.isOnline())
            addLine(ll, "Последний раз был в сети: %s", new RDate(player.getLastOnlineTime()).toString(false, true));
        addLine(ll, "");
        addLine(ll, "Уровень: %d", player.getLevel());
        addLine(ll, "Опыт: %d", player.getExperience());
        addLine(ll, "");
        PlayerInfo.InfractionData data = player.getMuteData();
        if(data.isActive()) {
            addLine(ll, "Чат заблокирован: да");
            addLine(ll, "Кто заблокировал: %s", data.getEnforcer());
            addLine(ll, "Причина: %s", data.getReason());
            addLine(ll, "Блокировка спадет: %s", new RDate(data.getEndTime()).toString(false, true));
        }else
            addLine(ll, "Чат заблокирован: нет");
        addLine(ll, "Всего блокировок чата: %d", data.getTotal());
        addLine(ll, "");
        data = player.getBanData();
        if(data.isActive()) {
            addLine(ll, "Доступ заблокирован: да");
            addLine(ll, "Кто заблокировал: %s", data.getEnforcer());
            addLine(ll, "Причина: %s", data.getReason());
            addLine(ll, "Блокировка спадет: %s", new RDate(data.getEndTime()).toString(false, true));
        }else
            addLine(ll, "Доступ заблокирован: нет");
        addLine(ll, "Всего блокировок доступа: %d", data.getTotal());
        View divider = new View(ll.getContext());
        divider.setBackgroundColor(Color.WHITE);
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
