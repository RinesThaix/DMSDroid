package ru.dms.app.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RinesThaix on 20.12.16.
 */

public class PlayerInfo {

    private final String name;

    private final int level, experience;

    private final String mainGroup;

    private final List<String> allGroups;

    private final String lastServer;

    private final boolean online;

    private final long lastOnlineTime;

    private final InfractionData muteData, banData;

    public PlayerInfo(String name, JSONObject data) throws Exception {
        this.name = name;
        JSONObject json = data.getJSONObject("network_leveling");
        level = json.getInt("level");
        experience = json.getInt("experience");
        json = data.getJSONObject("permissions");
        mainGroup = json.getString("main_group_localized");
        allGroups = new ArrayList<>();
        JSONArray array = json.getJSONArray("all_groups");
        for(int i = 0; i < array.length(); ++i)
            allGroups.add(array.getString(i));
        json = data.getJSONObject("online_information");
        lastServer = json.getString("server");
        online = json.getBoolean("online");
        lastOnlineTime = online ? 0l : json.getLong("last_exit_time");
        json = data.getJSONObject("infractions");
        long endTime = 0l;
        String enforcer = null, reason = null;
        if(json.getBoolean("muted")) {
            endTime = json.getLong("mute_end");
            enforcer = json.getString("mute_enforcer");
            reason = json.getString("mute_reason");
        }
        muteData = new InfractionData(json.getInt("total_mutes"), endTime, enforcer, reason);
        endTime = 0l; enforcer = null; reason = null;
        if(json.getBoolean("banned")) {
            endTime = json.getLong("ban_end");
            enforcer = json.getString("ban_enforcer");
            reason = json.getString("ban_reason");
        }
        banData = new InfractionData(json.getInt("total_bans"), endTime, enforcer, reason);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public String getMainGroup() {
        return mainGroup;
    }

    public List<String> getAllGroups() {
        return allGroups;
    }

    public String getLastServer() {
        return lastServer;
    }

    public boolean isOnline() {
        return online;
    }

    public long getLastOnlineTime() {
        return lastOnlineTime;
    }

    public InfractionData getMuteData() {
        return muteData;
    }

    public InfractionData getBanData() {
        return banData;
    }

    public static class InfractionData {

        private final boolean active;

        private final long endTime;

        private final String enforcer;

        private final String reason;

        private final int total;

        public InfractionData(int total, long endTime, String enforcer, String reason) {
            this.total = total;
            this.endTime = endTime;
            this.active = endTime > 0;
            this.enforcer = enforcer;
            this.reason = reason;
        }

        public boolean isActive() {
            return active;
        }

        public long getEndTime() {
            return endTime;
        }

        public String getEnforcer() {
            return enforcer;
        }

        public String getReason() {
            return reason;
        }

        public int getTotal() {
            return total;
        }

    }

}
