package com.stormdzh.minajavaclient;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.stormdzh.minajavaclient.common.ConnectionManager;
import com.stormdzh.minajavaclient.common.MinaConnfig;
import com.stormdzh.minajavaclient.common.MinaService;
import com.stormdzh.minajavaclient.common.SessionManager;

//https://blog.csdn.net/feinifi/article/details/81700891
public class MainActivity extends Activity implements OnClickListener {

    protected TextView dateView;
    protected Button connectBtn;
    protected Button disconnectBtn;
    protected Button sendBtn;
    protected EditText etHost;
    protected EditText etPort;

    MessageBroadcastReceiver receiver = new MessageBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        registerBroadcast();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 200);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MinaService.class));
        unregisterBroadcast();
    }

    //receive message and update ui
    private class MessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            addMessage(false, intent.getStringExtra(ConnectionManager.MESSAGE));
        }

    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter(ConnectionManager.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    public void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public void initView() {
        etHost = findViewById(R.id.etHost);
        etPort = findViewById(R.id.etPort);
        connectBtn = (Button) findViewById(R.id.connectBtn);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
        dateView = (TextView) findViewById(R.id.datetxt);
        connectBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        disconnectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectBtn:
                String port = etPort.getText().toString();
                if (TextUtils.isEmpty(port)) {
                    Toast.makeText(this, "请输入端口号", Toast.LENGTH_SHORT).show();
                    return;
                }
                String host = etHost.getText().toString();
                if (TextUtils.isEmpty(host)) {
                    Toast.makeText(this, "请输入端IP地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                MinaConnfig.port = Integer.parseInt(port);
                MinaConnfig.host = host;
                Intent intent = new Intent(this, MinaService.class);
                startService(intent);
                Log.e("tag", "connect to server");
                break;
            case R.id.disconnectBtn:
                stopService(new Intent(this, MinaService.class));
                Log.e("tag", "disconnect to server");
                break;
            case R.id.sendBtn:
                String txt = "11111";
                SessionManager.getInstance().writeToServer("1111");
                addMessage(true, txt);
                Log.e("tag", "send message to server");
                break;
        }
    }

    private void addMessage(boolean isend, String msg) {
        String txt = dateView.getText().toString();

        if (isend) {
            String sendtxt = "发送了：" + msg;
            txt = txt + sendtxt + "\n";
        } else {
            String rectxt = "收到了：" + msg;
            txt = txt + rectxt + "\n";
        }
        dateView.setText(txt);
    }
}
