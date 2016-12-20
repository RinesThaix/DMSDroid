package ru.dms.app;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.dms.app.api.Params;
import ru.dms.app.api.Requester;
import ru.dms.app.data.ExactServerInfo;
import ru.dms.app.data.MachineInfo;
import ru.dms.app.data.PlayerInfo;
import ru.dms.app.data.ServerInfo;
import ru.dms.app.util.Formatter;

/**
 * Created by RinesThaix on 19.12.16.
 */

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class DataProvider {

    private static File TOKEN_CACHE;

    public static List<ServerInfo> servers = new ArrayList<>();
    public static List<MachineInfo> machines = new ArrayList<>();
    public static PlayerInfo player = null;
    public static ExactServerInfo server = null;
    public static String lastUsedCluster = null;
    public static int summaryOnline = 0;
    public static int maxOnline = 2000;
    public static long lastUpdate = 0;

    public static void setupCacheDir(Context context) {
        TOKEN_CACHE = new File(context.getFilesDir(), "cache.dat");
        if(!TOKEN_CACHE.exists())
            TOKEN_CACHE.getParentFile().mkdirs();
    }

    public static boolean loadCache() {
        if(TOKEN_CACHE.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TOKEN_CACHE));
                Params.token = ois.readUTF();
                ois.close();
                return true;
            }catch (Exception ignored) {
                ignored.printStackTrace();
                TOKEN_CACHE.delete();
            }
        }
        return false;
    }

    public static void saveCache() {
        if(Params.token == null && TOKEN_CACHE.exists()) {
            TOKEN_CACHE.delete();
            return;
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TOKEN_CACHE));
            oos.writeUTF(Params.token);
            oos.close();
        }catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public static void download() throws Exception {
        servers.clear();
        JSONObject json = Requester.get(new Params("project.getOnline"));
        checkError(json);
        JSONArray array = json.getJSONArray("clusters");
        for(int i = 0; i < array.length(); ++i) {
            JSONObject cluster = array.getJSONObject(i);
            String name = cluster.getString("name");
            servers.add(new ServerInfo(true, name, cluster.getInt("online")));
        }
        summaryOnline = json.getInt("total_online");
        lastUpdate = System.currentTimeMillis();
        Log.d(MainView.TAG, "Data has been loaded (" + servers.size() + " clusters in total)");
    }

    public static void downloadClusterData(String cluster) throws Exception {
        servers.clear();
        JSONObject json = Requester.get(new Params("project.getClusterOnline", "cluster", cluster));
        checkError(json);
        Map<String, ServerInfo> data = new LinkedHashMap<>();
        JSONArray array = json.getJSONArray("servers");
        for(int i = 0; i < array.length(); ++i) {
            JSONObject server = array.getJSONObject(i);
            String name = server.getString("name");
            if(Formatter.isServerAFake(name))
                continue;
            servers.add(new ServerInfo(false, name, server.getInt("online")));
        }
        lastUpdate = System.currentTimeMillis();
        lastUsedCluster = cluster;
        Log.d(MainView.TAG, "Cluster data for " + cluster + " has been loaded (" + servers.size() + " servers in total)");
    }

    public static void downloadMachinesLoad() throws Exception {
        machines.clear();
        JSONObject json = Requester.get(new Params("project.getMachinesLoad"));
        checkError(json);
        JSONArray array = json.getJSONArray("machines");
        for(int i = 0; i < array.length(); ++i) {
            JSONObject machine = array.getJSONObject(i);
            String name = Formatter.formatMachineName(machine.getString("name"));
            machines.add(new MachineInfo(name, machine.getInt("load"), machine.getInt("max_possible_load"), machine.getInt("load_percentage")));
        }
        lastUpdate = System.currentTimeMillis();
        Log.d(MainView.TAG, "Machines load data has been loaded (for " + machines.size() + " machines in total)");
    }

    public static void downloadPlayerInfo(String player) throws Exception {
        JSONObject json = Requester.get(new Params("player.getGeneralInfo", "player", player));
        checkError(json);
        DataProvider.player = new PlayerInfo(player, json);
        lastUpdate = System.currentTimeMillis();
        Log.d(MainView.TAG, "Player data for " + player + " has been loaded");
    }

    public static void authorize(String username, String password) throws Exception {
        JSONObject json = Requester.get(new Params("api.authorize", "username", username, "password", password, "secretKey", "checkMyCredentials"));
        checkError(json);
        Params.token = json.getString("token");
        Log.d(MainView.TAG, "Successfully authorized for " + username);
    }

    public static void downloadServerInfo(String cluster, String server) throws Exception {
        JSONObject json = Requester.get(new Params("project.getServerInfo", "server", server));
        checkError(json);
        DataProvider.server = new ExactServerInfo(cluster, json.getString("name"), json.getString("address"),
                json.getInt("port"), Formatter.formatMachineName(json.getString("machine")), json.getInt("online"));
        lastUpdate = System.currentTimeMillis();
        Log.d(MainView.TAG, "Server data for " + server + " has been loaded");
    }

    private static void checkError(JSONObject json) throws Exception {
        if(json.has("error"))
            throw new Exception(json.getString("text"));
    }

}
