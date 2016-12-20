package ru.dms.app.updater;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import ru.dms.app.MainView;

/**
 * Created by RinesThaix on 19.12.16.
 */

public class UpdateService extends IntentService {

    private static UpdateRequest request;

    public static void perform(UpdateRequest request) {
        UpdateService.request = request;
        MainView.lastInstance.startUpdate();
    }

    public UpdateService() {
        super("Update Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized(this) {
            if (!isInternetAvaliable()) {
                sendErrorBroadcast("Подключение отсутствует");
                return;
            }
            try {
                request.perform();
            }catch(Exception ex) {
                sendErrorBroadcast(ex.getMessage());
                return;
            }
            sendBroadcast(new Intent(MainView.UPDATE_UI));
        }
    }

    private void sendErrorBroadcast(String message) {
        Intent intent = new Intent(MainView.UPDATE_UI);
        intent.putExtra("isError", true);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    private boolean isInternetAvaliable() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo network = mgr.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

}
