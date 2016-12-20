package ru.dms.app.data;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by RinesThaix on 19.12.16.
 */

public class MachineInfo {

    @NonNull
    private final String name;
    private final int load, maxLoad;
    private final int loadPercentage;

    public MachineInfo(String name, int load, int maxLoad, int loadPercentage) {
        this.name = name;
        this.load = load;
        this.maxLoad = maxLoad;
        this.loadPercentage = loadPercentage;
    }

    public String getName() {
        return name;
    }

    public int getLoad() {
        return load;
    }

    public int getMaxLoad() {
        return maxLoad;
    }

    public int getLoadPercentage() {
        return loadPercentage;
    }

}
