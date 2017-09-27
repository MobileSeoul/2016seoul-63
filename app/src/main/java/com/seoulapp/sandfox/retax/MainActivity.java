package com.seoulapp.sandfox.retax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * 맵 / 계산기 / 긴급통화 메뉴를 선택할 수 있는 메인 화면 클래스.
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ReTax.IS_FIRST_TIME){
            findViewById(R.id.fragment).setVisibility(View.VISIBLE);
            ReTax.IS_FIRST_TIME = false;
        }else{
            findViewById(R.id.fragment).setVisibility(View.GONE);
        }

    }
    public void onClickMenuButton(View view){
        buttonClickEffect(view);

        switch (view.getId()){
            case R.id.linkToMap:
                Intent toMap = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(toMap);
                break;
            case R.id.linkToCalc:
                Intent toCalc = new Intent(MainActivity.this, CalculatorActivity.class);
                startActivity(toCalc);
                break;
            case R.id.linkToCall:
                Intent toContact = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(toContact);
                break;
        }
    }

    public static void buttonClickEffect(View view){
        Animation anim = new AlphaAnimation(1.0F, 0.7F);
        view.startAnimation(anim);
    }

}
