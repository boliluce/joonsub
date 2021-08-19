package com.gyeongran.joonsub.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gyeongran.joonsub.sample.weather.WeatherActivity;
import com.yarolegovich.discretescrollview.sample.R;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private View root;


    //블루투스 변수
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private ArrayList<String> bluetooth_names;

    //식물타입 변수
    private  ArrayList<String> plant_type;

    //생장타입 변수
   // private  ArrayList<String> growth;

    //스피너 변수
    private Spinner bluetooth_spinner;
    private Spinner plant_type_spinner;
    //private Spinner growth_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.preview_weather).setOnClickListener(this);


        //블루투스 관련
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if (bluetoothAdapter == null) { /* 디바이스가 블루투스를 지원하지 않을 때*/}
        else {
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                devices = bluetoothAdapter.getBondedDevices();
                if(devices.size() == 0) return;



                bluetooth_names = new ArrayList<>();
                Log.v("bluetooth Test", "*****************************************************************devices.size() : " + devices.size());
                for (BluetoothDevice bluetoothDevice : devices) {
                    bluetooth_names.add(bluetoothDevice.getName());
                    Log.v("bluetooth Test", "*****************************************************************bluetoothDevice.getName() : " + bluetoothDevice.getName());
                }

                bluetooth_spinner = (Spinner)findViewById(R.id.bluetooth_spinner);
                ArrayAdapter bluetoothSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bluetooth_names);
                bluetooth_spinner.setAdapter(bluetoothSpinnerAdapter);
                bluetooth_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            } else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }


        //종류관련
        plant_type = new ArrayList<>();
        plant_type.add("채소");
        plant_type.add("허브");
        plant_type.add("관엽식물");
        plant_type.add("다육식물");
        plant_type.add("수경재배");

        plant_type_spinner = (Spinner)findViewById(R.id.PlantType_spinner);
        ArrayAdapter PlantTypeSpinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,plant_type);
        plant_type_spinner.setAdapter(PlantTypeSpinnerAdapter);



        //생장관련
        /*
        growth = new ArrayList<>();
        growth.add("씨앗");
        growth.add("새싹");
        growth.add("꽃, 성목");
        growth.add("열매");

        growth_spinner = (Spinner)findViewById(R.id.Growth_spinner);
        ArrayAdapter GrowthSpinnerAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,growth);
        growth_spinner.setAdapter(GrowthSpinnerAdapter);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    //selectBluetoothDevice();
                } else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_weather:
                start(WeatherActivity.class);
                //연결할 블루투스 장비
                //식물 종류(꽃, 다육, 선인장, 나무, 채소)
                //성장단계(씨앗, 새싹, 꽃, 열매)
                break;
        }
    }

    private void start(Class<? extends Activity> token) {
        Intent intent = new Intent(this, token);
        intent.putExtra("bluetooth_name", bluetooth_spinner.getSelectedItem().toString());
        intent.putExtra("plant_type_name", plant_type_spinner.getSelectedItem().toString());
       // intent.putExtra("growth_name",growth_spinner.getSelectedItem().toString());
        startActivity(intent);
    }


}
