package ru.dms.app;

import ru.dms.app.updater.UpdateRequest;
import ru.dms.app.updater.UpdateService;

/**
 * Created by RinesThaix on 19.12.16.
 */

public class ModuleManager {

    private static Module activeModule = Module.AUTHORIZATION;

    public static Module getActiveModule() {
        return activeModule;
    }

    public static void setActiveModuleUnsafe(Module module) {
        activeModule = module;
    }

    public static void setWholeOnline() {
        setActiveModule(Module.WHOLE_ONLINE, new UpdateRequest() {
            @Override
            public void perform() throws Exception {
                DataProvider.download();
            }
        });
    }

    public static void setClusterOnline(final String cluster) {
        setActiveModule(Module.CLUSTER_ONLINE, new UpdateRequest() {
            @Override
            public void perform() throws Exception {
                DataProvider.downloadClusterData(cluster);
            }
        });
    }

    public static void setMachinesLoad() {
        setActiveModule(Module.MACHINES_LOAD, new UpdateRequest() {
            @Override
            public void perform() throws Exception {
                DataProvider.downloadMachinesLoad();
            }
        });
    }

    public static void setPlayerInfo(final String player) {
        setActiveModule(Module.PLAYER_INFO, new UpdateRequest() {
            @Override
            public void perform() throws Exception {
                DataProvider.downloadPlayerInfo(player);
            }
        });
    }

    public static void setAuthorization(final String username, final String password) {
        setActiveModule(Module.AUTHORIZATION, new UpdateRequest() {
            @Override
            public void perform() throws Exception {
                DataProvider.authorize(username, password);
                DataProvider.saveCache();
                setActiveModuleUnsafe(ModuleManager.Module.WHOLE_ONLINE);
                DataProvider.download();
            }
        });
    }

    public static void setServerInfo(final String server) {
        setActiveModule(Module.SERVER_INFO, new UpdateRequest() {
            @Override
            public void perform() throws Exception {
                DataProvider.downloadServerInfo(DataProvider.lastUsedCluster, server);
            }
        });
    }

    private static void setActiveModule(Module module, UpdateRequest request) {
        activeModule = module;
        UpdateService.perform(request);
    }

    public enum Module {
        WHOLE_ONLINE, CLUSTER_ONLINE, MACHINES_LOAD, PLAYER_INFO, AUTHORIZATION, SERVER_INFO;
    }

}
