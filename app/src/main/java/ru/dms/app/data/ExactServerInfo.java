package ru.dms.app.data;

/**
 * Created by RinesThaix on 20.12.16.
 */

public class ExactServerInfo {

    private final String cluster;
    private final String name;
    private final String address;
    private final int port;
    private final String machineName;
    private final int online;

    public ExactServerInfo(String cluster, String name, String address, int port, String machineName, int online) {
        this.cluster = cluster;
        this.name = name;
        this.address = address;
        this.port = port;
        this.machineName = machineName;
        this.online = online;
    }

    public String getCluster() {
        return cluster;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getMachineName() {
        return machineName;
    }

    public int getOnline() {
        return online;
    }

}
