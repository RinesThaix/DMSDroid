package ru.dms.app;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.dms.app.api.Params;
import ru.dms.app.data.MachineInfo;
import ru.dms.app.data.ServerInfo;
import ru.dms.app.gui.ExactServerView;
import ru.dms.app.gui.MachineView;
import ru.dms.app.gui.PlayerView;
import ru.dms.app.gui.ServerView;
import ru.dms.app.updater.UpdateService;

/**
 * Created by RinesThaix on 19.12.16.
 */

public class MainView extends AppCompatActivity {
    public static final String TAG = "DmsDroid";
    public static final String ALARM_RECEIVE = "ru.dms.app.ALARM_RECEIVE";
    public static final String UPDATE_UI = "ru.dms.app.UPDATE_UI";

    public static MainView lastInstance;

    private final BroadcastReceiver UPDATE_UI_RECEIVER = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("isError", false)) {
                new AlertDialog.Builder(MainView.this, AlertDialog.THEME_HOLO_DARK)
                        .setTitle("Ошибка!")
                        .setMessage(intent.getStringExtra("message"))
                        .setIcon(R.drawable.white_logo)
                        .setNeutralButton("Закрыть", null)
                        .create()
                        .show();
                findViewById(R.id.loading).setVisibility(View.GONE);
                findViewById(R.id.scroll).setVisibility(View.GONE);
            }
            show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastInstance = this;
        DataProvider.setupCacheDir(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dms_gold));
            window.setNavigationBarColor(getResources().getColor(R.color.dms_gold));

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.white_logo);
            setTaskDescription(new ActivityManager.TaskDescription("DMS Droid", bm, getResources().getColor(R.color.dms_gold)));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dms_gold_lighten)));

        setContentView(R.layout.activity_main);

        preloadAuthorizationPage();
        if(DataProvider.loadCache()) {
            if(ModuleManager.getActiveModule() == ModuleManager.Module.AUTHORIZATION)
                ModuleManager.setWholeOnline();
            else
                show();
            return;
        }
        showAuthorizationPage();
    }

    @Override
    protected void onResume() {
        registerReceiver(UPDATE_UI_RECEIVER, new IntentFilter(UPDATE_UI));
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(UPDATE_UI_RECEIVER);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        switch(ModuleManager.getActiveModule()) {
            case MACHINES_LOAD:
            case CLUSTER_ONLINE:
            case PLAYER_INFO:
                ModuleManager.setWholeOnline();
                break;
            case SERVER_INFO:
                if(DataProvider.server == null)
                    return;
                ModuleManager.setClusterOnline(DataProvider.server.getCluster());
                break;
            default:
                super.onBackPressed();
        }
    }

    private boolean checkWhetherAuthorized() {
        if(ModuleManager.getActiveModule() == ModuleManager.Module.AUTHORIZATION) {
            showHint("Пожалуйста, авторизуйтесь.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!checkWhetherAuthorized())
            return true;
        switch(item.getItemId()) {
            case R.id.update:
                startUpdate();
                return true;
            case R.id.onlinep:
                ModuleManager.setWholeOnline();
                return true;
            case R.id.loadp:
                ModuleManager.setMachinesLoad();
                return true;
            case R.id.playerinfo:
                final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.playerinfo_dialogue, null);
                final AlertDialog dialogue = new AlertDialog.Builder(this)
                        .setTitle("Ник игрока")
                        .setView(view)
                        .setNegativeButton("Отмена", null)
                        .setPositiveButton("Найти", null)
                        .create();
                dialogue.show();
                dialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v0) {
                        String text = ((EditText) view.findViewById(R.id.delay)).getText().toString();
                        if(text.isEmpty()) {
                            showHint("Вы не ввели ник игрока.");
                            return;
                        }
                        ModuleManager.setPlayerInfo(text);
                        dialogue.dismiss();
                    }
                });
                return true;
            case R.id.change_account:
                Params.token = null;
                ModuleManager.setActiveModuleUnsafe(ModuleManager.Module.AUTHORIZATION);
                showAuthorizationPage();
                DataProvider.saveCache();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startUpdate() {
        if(isServiceRunning(UpdateService.class))
            return;

        findViewById(R.id.loginscroll).setVisibility(View.INVISIBLE);
        findViewById(R.id.scroll).setVisibility(View.INVISIBLE);
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.loadingText)).setText("Загрузка..");
        startService(new Intent(getApplicationContext(), UpdateService.class));
    }

    private void showHint(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void attemptLogin() {
        Log.d(TAG, "Attempting login..");
        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();
        if(username.isEmpty()) {
            showHint("Вы не ввели логин.");
            return;
        }
        if(username.length() < 3 || username.length() > 16) {
            showHint("Вы ввели некорректный логин.");
            return;
        }
        if(password.isEmpty()) {
            showHint("Вы не ввели пароль.");
            return;
        }
        ModuleManager.setAuthorization(username, password);
    }

    private void preloadAuthorizationPage() {
        EditText field = (EditText) findViewById(R.id.login_password);
        field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        field = (EditText) findViewById(R.id.login_username);
        field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void showAuthorizationPage() {
        this.setTitle("DMS Droid | Авторизация");
        findViewById(R.id.loginscroll).setVisibility(View.VISIBLE);
        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.scroll).setVisibility(View.GONE);
    }

    private void show() {
        if(DataProvider.servers == null)
            return;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 60);
        params.bottomMargin = 20;

        findViewById(R.id.loginscroll).setVisibility(View.GONE);
        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.scroll).setVisibility(View.VISIBLE);

        LinearLayout ll = (LinearLayout) findViewById(R.id.servers);
        ll.removeAllViewsInLayout();

        switch(ModuleManager.getActiveModule()) {
            case WHOLE_ONLINE:
            case CLUSTER_ONLINE: {
                this.setTitle("DMS Droid | Онлайн");
                int _online = 0, _max = 0;
                for(ServerInfo si : DataProvider.servers) {
                    ll.addView(new ServerView(this, si), params);
                    _online += si.getOnline();
                }
                if(ModuleManager.getActiveModule() == ModuleManager.Module.WHOLE_ONLINE) {
                    _max = DataProvider.maxOnline;
                    _online = DataProvider.summaryOnline;
                }

                View divider = new View(getApplicationContext());
                divider.setBackgroundColor(Color.LTGRAY);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2);
                dividerParams.bottomMargin = 10;
                ll.addView(divider, dividerParams);
                ll.addView(new ServerView(getApplicationContext(),
                        new ServerInfo(true, "Общий онлайн", _online).maxOnline(_max)
                ), params);
                break;
            }case MACHINES_LOAD: {
                this.setTitle("DMS Droid | Нагрузка");
                for(MachineInfo mi : DataProvider.machines)
                    ll.addView(new MachineView(this, mi), params);
                break;
            }case PLAYER_INFO: {
                this.setTitle("DMS Droid | Игрок");
                if(DataProvider.player != null)
                    PlayerView.show(ll, DataProvider.player);
                return;
            }case AUTHORIZATION:
                findViewById(R.id.loginscroll).setVisibility(View.VISIBLE);
                findViewById(R.id.scroll).setVisibility(View.GONE);
                return;
            case SERVER_INFO: {
                this.setTitle("DMS Droid | Сервер");
                if(DataProvider.server != null)
                    ExactServerView.show(ll, DataProvider.server);
                return;
            }
            default:
                return;
        }

        TextView lastUpdated = new TextView(getApplicationContext());
        lastUpdated.setText("Обновлено: " + new SimpleDateFormat("dd.MM.yyyy H:mm:ss", Locale.ENGLISH).format(DataProvider.lastUpdate));
        lastUpdated.setTextColor(Color.GRAY);
        lastUpdated.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.addView(lastUpdated);
    }

    public boolean isServiceRunning(Class<?> clazz) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if(service.service.getClassName().equals(clazz.getName()))
                return true;
        return false;
    }

    public boolean isAlarmActive() {
        return getAlarmPending(PendingIntent.FLAG_NO_CREATE) != null;
    }

    public PendingIntent getAlarmPending(int flag) {
        return PendingIntent.getBroadcast(getApplicationContext(), 1001, new Intent(ALARM_RECEIVE), flag);
    }
}
