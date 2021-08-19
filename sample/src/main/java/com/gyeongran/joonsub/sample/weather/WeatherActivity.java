package com.gyeongran.joonsub.sample.weather;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.gyeongran.joonsub.DiscreteScrollView;
import com.yarolegovich.discretescrollview.sample.R;
import com.gyeongran.joonsub.sample.DiscreteScrollViewOptions;
import com.gyeongran.joonsub.transform.ScaleTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class WeatherActivity extends AppCompatActivity implements
        DiscreteScrollView.ScrollStateChangeListener<ForecastAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ForecastAdapter.ViewHolder>,
        View.OnClickListener {

    //UI 변수
    private List<Forecast> forecasts;

    private ForecastView forecastView;
    private DiscreteScrollView cityPicker;

    private Forecast Light;
    private Forecast Temperature;
    private Forecast Humidity;
    private ForecastAdapter forecastAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    //블루투스 변수
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치
    // UUID 생성
    private UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    private String bluetooth_name = "";
    private String plant_type_name = "";
    private int humidity_standard_1 = 400;//물에들어가잇을때
    private int humidity_standard_2 = 600;
    private int humidity_standard_3 = 900;
    private double temperature_standard_1 = 12.00;
    private double temperature_standard_2 = 24.00;
    private double temperature_standard_3 = 30.00;
    private int light_standard_1 = 400;
    private int light_standard_2 = 800;

    private AlertDialog alertDialog;


    private boolean flag = false;

    //축축할때의 플레그
    private boolean humidity_1_danger = false;
    private boolean pre_humidity_1_flag = false;
    private boolean cur_humidity_1_flag = false;

    //건조할때의 플레그
    private boolean humidity_4_danger = false;
    private boolean pre_humidity_4_flag = false;
    private boolean cur_humidity_4_flag = false;

    //추울때의 플레그
    private boolean temperature_1_danger = false;
    private boolean pre_temperature_1_flag = false;
    private boolean cur_temperature_1_flag = false;

    //더울때의 플레그
    private boolean temperature_4_danger = false;
    private boolean pre_temperature_4_flag = false;
    private boolean cur_temperature_4_flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        /*swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //swipeRefreshLayout.setRefreshing(true);
                //새로고침
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //refresh(getBluetoothData());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },1000);//뱅글이 딜레이시간
            }
        });*/

        //화면 UI 및 변수 초기화
        Light = new Forecast("Light", R.drawable.light, "18", Weather.LIGHT_1);
        Temperature = new Forecast("Temperature", R.drawable.temperature, "6", Weather.TEMPERATURE_3);
        Humidity = new Forecast("Humidity", R.drawable.humidity, "20", Weather.HUMIDITY_2);

        forecasts = Arrays.asList(Light,Temperature,Humidity);
        forecastAdapter = new ForecastAdapter(forecasts);

        forecastView = (ForecastView) findViewById(R.id.forecast_view);
        cityPicker = (DiscreteScrollView) findViewById(R.id.forecast_city_picker);
        cityPicker.setSlideOnFling(true);
        cityPicker.setAdapter(forecastAdapter);
        cityPicker.addOnItemChangedListener(this);
        cityPicker.addScrollStateChangeListener(this);
        cityPicker.scrollToPosition(2);
        cityPicker.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime());
        cityPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        forecastView.setForecast(forecasts.get(0));

        findViewById(R.id.home).setOnClickListener(this);
        findViewById(R.id.btn_transition_time).setOnClickListener(this);
        findViewById(R.id.btn_smooth_scroll).setOnClickListener(this);


        //블루투스 관련
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if (bluetoothAdapter == null) { /* 디바이스가 블루투스를 지원하지 않을 때*/}

        Intent intent = getIntent(); /*데이터 수신*/
        bluetooth_name = intent.getExtras().getString("bluetooth_name"); /*String형*/
        plant_type_name = intent.getExtras().getString("plant_type_name");

        initPlant(plant_type_name);

        //123 50****************************************************************
        //String plant_name
        //initPlant();
        //humidify_standard
        //li
        //tem


        connectDevice(bluetooth_name);


        //임시소스
        try {
            // 1초마다 받아옴
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        String[] receivedData = getBluetoothData().split(",");
        if(receivedData.length == 3) {
            Humidity.setNumber((receivedData[0]));
            Temperature.setNumber(receivedData[1]);
            Light.setNumber(receivedData[2]);
        }

        //블루투스 쓰레드
        BluetoothThread thread = new BluetoothThread();
        thread.start();

    }
//다이얼로그..?*********************************************************************************************
    public void DialogClick(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("앱 정보");
        String message =
                "앱 이 름 : 이 조명 온도 습도\n" +
                "제 작 자 : 강효인\n" +
                "버전정보 : 1.0.1\n";
        builder.setMessage(message);
        alertDialog = builder.create();
        //alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        connectDevice(bluetooth_name);

        //임시소스
        try {
            // 1초마다 받아옴
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] receivedData = getBluetoothData().split(",");
        if(receivedData.length == 3) {
            Humidity.setNumber((receivedData[0]));
            Temperature.setNumber(receivedData[1]);
            Light.setNumber(receivedData[2]);
        }

    }

    private void refresh(String bluetooth_data){

        if(bluetooth_data.equals("")) {

            connectDevice(bluetooth_name);

            //임시소스
            try {
                // 1초마다 받아옴
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return;
        }

        String[] receivedData = bluetooth_data.split(",");//getBluetoothData().split(",");

        if(receivedData.length == 3)
        {
            int humidity = Integer.parseInt(receivedData[0]);
            double temperature = Double.parseDouble(receivedData[1]);
            int light = Integer.parseInt(receivedData[2]);

            // 0 - 습도 / 1 - 온도 / 2 - 조도*********************************************************************************
            Humidity.setNumber(""+humidity);
            Temperature.setNumber(""+temperature);
            Light.setNumber(""+light);

            if(humidity < humidity_standard_1) {//500
                Humidity.setWeather(Weather.HUMIDITY_4);
                findViewById(R.id.btn_smooth_scroll).setEnabled(false);
                humidity_1_danger = cur_humidity_1_flag = true;
                humidity_4_danger = pre_humidity_4_flag = cur_humidity_4_flag = false;
            }else if(humidity > humidity_standard_1 && humidity < humidity_standard_2) { // 1000
                Humidity.setWeather(Weather.HUMIDITY_3);
                findViewById(R.id.btn_smooth_scroll).setEnabled(true);
                humidity_1_danger = pre_humidity_1_flag = cur_humidity_1_flag = false;
                humidity_4_danger = pre_humidity_4_flag = cur_humidity_4_flag = false;
            }else if(humidity>humidity_standard_2 && humidity<humidity_standard_3) {
                Humidity.setWeather(Weather.HUMIDITY_2);
                findViewById(R.id.btn_smooth_scroll).setEnabled(true);
                humidity_1_danger = pre_humidity_1_flag = cur_humidity_1_flag = false;
                humidity_4_danger = pre_humidity_4_flag = cur_humidity_4_flag = false;
            }else if(humidity>humidity_standard_3){
                Humidity.setWeather(Weather.HUMIDITY_1);
                findViewById(R.id.btn_smooth_scroll).setEnabled(true);
                humidity_1_danger = pre_humidity_1_flag = cur_humidity_1_flag = false;
                humidity_4_danger = cur_humidity_4_flag = true;
        }

            //Tem

            if(temperature <= temperature_standard_1){
                Temperature.setWeather(Weather.TEMPERATURE_1);
                temperature_1_danger = cur_temperature_1_flag = true;
                temperature_4_danger = pre_temperature_4_flag = cur_temperature_4_flag = false;
            }
            else if(temperature > temperature_standard_1 && temperature <= temperature_standard_2){
                Temperature.setWeather(Weather.TEMPERATURE_2);
                temperature_1_danger = pre_temperature_1_flag = cur_temperature_1_flag = false;
                temperature_4_danger = pre_temperature_4_flag = cur_temperature_4_flag = false;
            }
            else if(temperature > temperature_standard_2 && temperature <= temperature_standard_3) {
                Temperature.setWeather(Weather.TEMPERATURE_3);
                temperature_1_danger = pre_temperature_1_flag = cur_temperature_1_flag = false;
                temperature_4_danger = pre_temperature_4_flag = cur_temperature_4_flag = false;
            }
            else if(temperature > temperature_standard_3) {
                Temperature.setWeather(Weather.TEMPERATURE_4);
                temperature_1_danger = pre_temperature_1_flag = cur_temperature_1_flag = false;
                temperature_4_danger = cur_temperature_4_flag = true;
            }

            //light
            if(light<light_standard_1)
                Light.setWeather(Weather.LIGHT_1);
            else if(light>light_standard_1 && light<light_standard_2)
                Light.setWeather(Weather.LIGHT_2);
            else if(light>light_standard_2)
                Light.setWeather(Weather.LIGHT_3);

            //값이 바뀌었을때 화면 다시 그려주는 함수
            forecastAdapter.notifyDataSetChanged();
        }

        checkNotification();
    }

    private void checkNotification(){
        //Log.v("123", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ h_danger - " + h_danger);
        //Log.v("123", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ prev_h_flag - " + prev_h_flag);
        //Log.v("123", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ cur_h_flag - " + cur_h_flag);
        if(humidity_1_danger && (pre_humidity_1_flag != cur_humidity_1_flag)){
            pre_humidity_1_flag = cur_humidity_1_flag;

            createNotification(1, "습도", "뿌리가 썩을 것 같아요. 더이상 물을 주지 마세요.");
        }
        if(humidity_4_danger &&(pre_humidity_4_flag != cur_humidity_4_flag)){
            pre_humidity_4_flag=cur_humidity_4_flag;

            createNotification(1, "습도","흙이 너무 말랐어요. 어서 물을 주세요.");
        }

        if (temperature_1_danger && (pre_temperature_1_flag != cur_temperature_1_flag)) {
            pre_temperature_1_flag = cur_temperature_1_flag;

            createNotification(2, "온도","날이 점점 추워져요. 얼기 전에 실내로 옮겨주세요");
        } //온도

        if (temperature_4_danger && (pre_temperature_4_flag != cur_temperature_4_flag)) {
            pre_temperature_4_flag = cur_temperature_4_flag;

            createNotification(2, "온도","너무 덥네요. 흙이 마르는지 자주 살펴주세요");
        } //온도
    }

    private void initPlant(String p){//*******************
        if(p.equals("채소")){//비교적 물 많이 먹음 흙 촉촉해야함
        //센서에서 넘겨주는 값 기준
       // humidity_standard_1= 300;
        //humidity_standard_2=500;

        }
        else if(p.equals("허브")){//채소보다는 물을 적게먹긴하는데 그래도 좀 촉촉해야함


        }
        else if(p.equals("관엽식물")){//겉흙이 마르면 물을 줘야함/ 그늘진데서 잘자람

        }
        else if(p.equals("다육식물")){//수분이 많으면 뿌리가 썩는 대표적 식물

        }
        else if(p.equals("수경재배")){//뿌리가 물 밖으로 나오면 안됨

        }

    }

    //if (받아온데이터 > 50) 알람띄우기
    //if (받아온데이터 > plant) 알람띄우기


    public String getBluetoothData(){
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        String text = "";

        try {
            // 데이터를 수신했는지 확인합니다.
            int byteAvailable = inputStream.available();

            // 데이터가 수신 된 경우
            if(byteAvailable > 0) {
                // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                byte[] bytes = new byte[byteAvailable];
                inputStream.read(bytes);
                // 입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
                for(int i = 0; i < byteAvailable; i++) {

                    byte tempByte = bytes[i];
                    // 개행문자를 기준으로 받음(한줄)
                    if(tempByte == '\n') {
                        // readBuffer 배열을 encodedBytes로 복사
                        byte[] encodedBytes = new byte[readBufferPosition];
                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                        // 인코딩 된 바이트 배열을 문자열로 변환
                        text = new String(encodedBytes, "US-ASCII");
                        readBufferPosition = 0;
                    } // 개행 문자가 아닐 경우
                    else {
                        readBuffer[readBufferPosition++] = tempByte;
                        Log.v("bluetooth Test", "tempByte : " + tempByte);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            // 1초마다 받아옴
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return text;
    }

    @Override
    public void onCurrentItemChanged(@Nullable ForecastAdapter.ViewHolder holder, int position) {
        //viewHolder will never be null, because we never remove items from adapter's list
        if (holder != null) {
            forecastView.setForecast(forecasts.get(position));
            holder.showText();
        }
    }

    @Override
    public void onScrollStart(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        holder.hideText();
    }

    @Override
    public void onScroll(
            float position,
            int currentIndex, int newIndex,
            @Nullable ForecastAdapter.ViewHolder currentHolder,
            @Nullable ForecastAdapter.ViewHolder newHolder) {
        Forecast current = forecasts.get(currentIndex);
        if (newIndex >= 0 && newIndex < cityPicker.getAdapter().getItemCount()) {
            Forecast next = forecasts.get(newIndex);
            forecastView.onScroll(1f - Math.abs(position), current, next);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                finish();
                break;
            case R.id.btn_transition_time:
                DialogClick(v);
                alertDialog.show();
                break;
            case R.id.btn_smooth_scroll: //
                sendData("1");
                break;
        }
    }

    @Override
    public void onScrollEnd(@NonNull ForecastAdapter.ViewHolder holder, int position) {   }



    public void connectDevice(String deviceName) {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 되어있는 장치가 없는 경우
        if (devices.size() == 0) {
            // 페어링을 하기위한 함수 호출
            return;
        }

        // 페어링 된 디바이스들을 모두 탐색
        for(BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        if(bluetoothDevice == null) return;
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            // 데이터 송,수신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String Waterpump) {
        try{
            // 데이터 송신
            outputStream.write(Waterpump.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        flag = false;
    }


    final Handler handler = new Handler()    {
        public void handleMessage(Message msg)
        {
            // 원래 하고싶었던 일들 (UI변경작업 등...)
            refresh(msg.obj.toString());
        }
    };

    private class BluetoothThread extends Thread {
        private static final String TAG = "BluetoothThread";

        public BluetoothThread() {
            // 초기화 작업
            connectDevice(bluetooth_name);
            flag = true;
        }

        public void run() {
            while(flag){
                String bluetooth_data = getBluetoothData();
                Message message = handler.obtainMessage();
                message.obj = (String)bluetooth_data;
                handler.sendMessage(message) ;

                Log.v("Thread", "bluetooth_data - " + bluetooth_data);

                try {
                    Thread.sleep(5000); //화면갱신을 좀더 빨리 하게 해주려면 이 부분을 수정.**********************************************************************************************
                }catch (Exception e){}
            }


        }
    }





    private void createNotification(int id, String title, String content) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(content);

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(id, builder.build());
    }

    private void removeNotification(int id) {
        // Notification 제거
        NotificationManagerCompat.from(this).cancel(id);
    }



}
