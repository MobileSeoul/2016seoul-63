package com.seoulapp.sandfox.retax;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * 가장 먼저 띄워지는 화면으로써,
 * raw data를 데이터베이스에 담는 역할을 한다.
 *
 */
public class SplashActivity extends AppCompatActivity {
    private InitAsyncTask init;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ReTax.isInitialized()){
            proceed();
        }else{
            init = new InitAsyncTask(this);
            init.execute();
        }


    }

    private void proceed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(initStatusReceiver, new IntentFilter(Constants.ACTION_INIT));
    }
    @Override
    protected void onPause() {
        unregisterReceiver(initStatusReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver initStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && Constants.ACTION_INIT.equals(intent.getAction())){
                switch (intent.getIntExtra(Constants.EXTRA_STATUS, 100)){
                    case Constants.STATUS_SUCCESS:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                proceed();
                            }
                        });
                        break;
                    case Constants.STATUS_FAILED:
                        Toast.makeText(context, "Initialization Failed", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
            }
        }
    };
}
