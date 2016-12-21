package ru.dms.app.updater;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;

import java.util.List;

import ru.dms.app.DataProvider;
import ru.dms.app.MainView;
import ru.dms.app.data.MachineInfo;

/**
 * Created by RinesThaix on 20.12.16.
 */

public class Notificator {

    private final static Handler handler = new Handler();
    private final static Runnable run = new Runnable() {
        @Override
        public void run() {
            try {
                look();
            }catch(Exception ex) {
                ex.printStackTrace();
            }finally {
                handler.postDelayed(run, 10000l);
            }
        }
    };
    private static long lastMachinesUpdate = 0l;

    private static boolean lookForOverloadedMachines = false;
    private static boolean initialized = false;

    public static void init() {
        if(initialized)
            return;
        initialized = true;
        handler.post(run);
    }

    private static void look() throws Exception {
        if(!lookForOverloadedMachines)
            return;
        long current = System.currentTimeMillis();
        if(current - lastMachinesUpdate > 60000l) {
            lastMachinesUpdate = current;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        DataProvider.downloadMachinesLoad();
                    }catch(Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        List<MachineInfo> machines = DataProvider.machines;
        for(MachineInfo machine : machines)
            if(machine.getLoadPercentage() >= 90) {
                Vibrator v = (Vibrator) MainView.lastInstance.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(5000l);
                MainView.lastInstance.showHint("Одна из серверных машин перегружена!");
                return;
            }
    }

    public static void setLookForOverloadedMachines(boolean value) {
        lookForOverloadedMachines = value;
    }

}
