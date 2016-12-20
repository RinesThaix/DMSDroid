package ru.dms.app.data;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by RinesThaix on 19.12.16.
 */

public class ServerInfo {

    private final boolean cluster;

    @NonNull
    private final String name;

    private final int online;

    private int maxOnline = 0;

    public ServerInfo(boolean cluster, String name, int online) {
        this.cluster = cluster;
        this.name = name;
        this.online = online;
    }

    public ServerInfo maxOnline(int maxOnline) {
        this.maxOnline = maxOnline;
        return this;
    }

    public boolean isCluster() {
        return cluster;
    }

    public String getName() {
        return name;
    }

    public int getOnline() {
        return online;
    }

    public int getMaxOnline() {
        return maxOnline;
    }

}
