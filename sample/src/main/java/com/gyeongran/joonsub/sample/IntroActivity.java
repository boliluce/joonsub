package com.gyeongran.joonsub.sample;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gyeongran.joonsub.sample.MainActivity;
import com.yarolegovich.discretescrollview.sample.R;

public class IntroActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태

    Button btn_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btn_retry = (Button) findViewById(R.id.retry);
        bluetoothCheck();
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothCheck();
            }
        });

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                bluetoothCheck();
            }
        }, 600);// 0.6초 정도 딜레이를 준 후 시작


    }

    private void bluetoothCheck(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if (bluetoothAdapter == null) { /* 디바이스가 블루투스를 지원하지 않을 때*/}
        else {
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else { // '취소'를 눌렀을 때
                    btn_retry.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

}