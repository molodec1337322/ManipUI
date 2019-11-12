package com.example.manipui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity{

    public TextView textStatus;
    public TextView textCurrentStatus;

    public Button buttonFirstElbowSpread;
    public Button buttonFirstElbowSlide;
    public Button buttonSecondElbowSpread;
    public Button buttonSecondElbowSlide;
    public Button buttonTurnLeft;
    public Button buttonTurnRight;
    public Button buttonOpen;
    public Button buttonClose;

    //задаем блютуз адаптер по умолчанию
    private BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    //подключение блютуз сокета
    private BluetoothSocket clientSocket;


    // коды для передачи сигналов
    //
    // все коды представленны в виде 4-х цифр
    // 1-я цифра указывает на часть, которя должна двигаться
    // (1 - первый локоть; 2 - второй локоть; 3 - платформа; 4 - ковш )
    //
    // 2-я цифра пока-что никак не использутся
    //
    // 3-я цифра указывает на действие
    // (0 - какое-нибудь действие; 1 - обратное ему действие)
    //
    // 4-я цифра используется для старта/остановки действия

    /*
    СИГНАЛЫ ДЛЯ ПЕРВОГО ЛОКТЯ
     */
    private final int FIRST_ELBOW_SPREAD_START  =  1000;
    private final int FIRST_ELBOW_SPREAD_STOP   =  1001;

    private final int FIRST_ELBOW_SLIDE_START   =  1010;
    private final int FIRST_ELBOW_SLIDE_STOP    =  1011;

    /*
    СИГНАЛЫ ДЛЯ ВТОРОГО ЛОКТЯ
     */
    private final int SECOND_ELBOW_SPREAD_START =  2000;
    private final int SECOND_ELBOW_SPREAD_STOP  =  2001;

    private final int SECOND_ELBOW_SLIDE_START  =  2010;
    private final int SECOND_ELBOW_SLIDE_STOP   =  2011;

    /*
    СИГНАЛЫ ДЛЯ ПОВОРОТА ПЛАТФОРМЫ
     */
    private final int PLATFORM_TURN_LEFT_START  =  3000;
    private final int PLATFORM_TURN_LEFT_STOP   =  3001;

    private final int PLATFORM_TURN_RIGHT_START =  3010;
    private final int PLATFORM_TURN_RIGHT_STOP  =  3011;

    /*
    СИГНАЛЫ ДЛЯ УПРАВЛЕНИЯ КОВШОМ
     */
    private final int CLAWS_OPEN_START          =  4000;
    private final int CLAWS_OPEN_STOP           =  4001;

    private final int CLAWS_CLOSE_START         =  4010;
    private final int CLAWS_CLOSE_STOP          =  4011;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //создание надписей
        textStatus = (TextView) findViewById(R.id.textStatus);
        textCurrentStatus = (TextView) findViewById(R.id.textCurrentStatus);

        //создание кнопок
        buttonFirstElbowSpread = (Button) findViewById(R.id.buttonFirstElbowSpread);
        buttonFirstElbowSlide = (Button) findViewById(R.id.buttonFirstElbowSlide);
        buttonSecondElbowSpread = (Button) findViewById(R.id.buttonSecondElbowSpread);
        buttonSecondElbowSlide = (Button) findViewById(R.id.buttonSecondElbowSlide);
        buttonTurnLeft = (Button) findViewById(R.id.buttonTurnLeft);
        buttonTurnRight = (Button) findViewById(R.id.buttonTurnRight);
        buttonOpen = (Button) findViewById(R.id.buttonOpen);
        buttonClose = (Button) findViewById(R.id.buttonClose);

        checkIsBluetoothEnable();

        if(bluetooth == null){
            textCurrentStatus.setText("На устройстве \nотсутсвует \nbluetooth \nмодуль");
        }else{ //если адаптер есть, то подключаемся
            connectToManipulator();
        }

        /*
        ПРЕВЫЙ ЛОКОТЬ
         */
        //разгибание первого локтя
        buttonFirstElbowSpread.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sendCommandToManipulator(FIRST_ELBOW_SPREAD_START);
                        break;
                    case MotionEvent.ACTION_MOVE: //движение
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        sendCommandToManipulator(FIRST_ELBOW_SPREAD_STOP);
                        break;
                }
                return true;
            }
        });

        //сгибание первого локтя
        buttonFirstElbowSlide.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sendCommandToManipulator(FIRST_ELBOW_SLIDE_START);
                        break;
                    case MotionEvent.ACTION_MOVE: //движение
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        sendCommandToManipulator(FIRST_ELBOW_SLIDE_STOP);
                        break;
                }
                return true;
            }
        });


        /*
        ВТОРОЙ ЛОКОТЬ
         */
        //разгибание второго локтя
        buttonSecondElbowSpread.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sendCommandToManipulator(SECOND_ELBOW_SPREAD_START);
                        break;
                    case MotionEvent.ACTION_MOVE: //движение
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        sendCommandToManipulator(SECOND_ELBOW_SPREAD_STOP);
                        break;
                }
                return true;
            }
        });

        //сгибание второго локтя
        buttonSecondElbowSlide.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sendCommandToManipulator(SECOND_ELBOW_SLIDE_START);
                        break;
                    case MotionEvent.ACTION_MOVE: //движение
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        sendCommandToManipulator(SECOND_ELBOW_SLIDE_STOP);
                        break;
                }
                return true;
            }
        });


        /*
        УПРАВЛЕНИЕ ПЛАТФОРМОЙ
         */
        //поворот платформы налево
        buttonTurnLeft.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sendCommandToManipulator(PLATFORM_TURN_LEFT_START);
                        break;
                    case MotionEvent.ACTION_MOVE: //движение
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        sendCommandToManipulator(PLATFORM_TURN_LEFT_STOP);
                        break;
                }
                return true;
            }
        });

        //поворот платформы направо
        buttonTurnRight.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sendCommandToManipulator(PLATFORM_TURN_RIGHT_START);
                        break;
                    case MotionEvent.ACTION_MOVE: //движение
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        sendCommandToManipulator(PLATFORM_TURN_RIGHT_STOP);
                        break;
                }
                return true;
            }
        });


        /*
        УПРАВЛЕНИЕ КЛЕШНЁЙ
         */
        //расжатие ковша
        buttonOpen.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sendCommandToManipulator(CLAWS_OPEN_START);
                        break;
                    case MotionEvent.ACTION_MOVE: //движение
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        sendCommandToManipulator(CLAWS_OPEN_STOP);
                        break;
                }
                return true;
            }
        });

        //сжатие ковша
        buttonClose.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sendCommandToManipulator(CLAWS_CLOSE_START);
                        break;
                    case MotionEvent.ACTION_MOVE: //движение
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        sendCommandToManipulator(CLAWS_CLOSE_STOP);
                        break;
                }
                return true;
            }
        });
    }

    //проверяет, подключен ли блютуз на телефоне
    //есни не подключен, то предлагает поключить
    private void checkIsBluetoothEnable(){
        if(!bluetooth.isEnabled()){
            String enableBlutooth = BluetoothAdapter.ACTION_REQUEST_ENABLE;
            startActivityForResult(new Intent(enableBlutooth), 0);
        }
    }

    //попытка подключения
    private void connectToManipulator(){
        try{
            BluetoothDevice device = bluetooth.getRemoteDevice("тут должен быть " +
                    "адрес девайса к которому нужно будет подключиться, но его пока что нет");
            //иниициируем соединение с устройством
            Method m = device.getClass().getMethod(
                    "createRfcommSocket", new Class[] {int.class});
            clientSocket = (BluetoothSocket) m.invoke(device, 1);
            clientSocket.connect();

            textCurrentStatus.setText("Подключение успешно");
        }catch(Exception e){
            textCurrentStatus.setText("Ошибка подключения \nк манипулятору");
        }
    }

    //метод для передачи данных
    private void sendCommandToManipulator(int signal){
        try{
            //получение выходного потока для передаи данных
            OutputStream outStream = clientSocket.getOutputStream();
            outStream.write(signal);
        }catch(Exception e){
            textCurrentStatus.setText("Ошибка отправки\nданных");
        }
    }

    /*
   УПРАВЛЕНИЕ ПЕРВЫМ КОЛЕНОМ
    */
    //разгибает первое колено
    public void buttonFirstElbowSpreadClick(View view){ }

    //сгибает первое колено
    public void buttonFirstElbowSlideClick(View view){ }


    /*
    УПРАВЛЕНИЕ ВТОРЫМ КОЛЕНОМ
    */
    //разгибает второе колено
    public void buttonSecondElbowSpreadClick(View view){ }

    //сгибает второе колено
    public void buttonSecondElbowSlideClick(View view){ }


    /*
    УПРРАВЛЕНИЕ ПЛАТФОРМОЙ
     */
    //поворот платформы налево
    public void buttonTurnLeftClick(View view){ }

    //поворот платформы направо
    public void buttonTurnRightClick(View view){ }


    /*
    УПРАВЛЕНИЕ КОВШОМ
     */
    //открывает ковш
    public void buttonOpenClick(View view){ }

    //закрывает ковш
    public void buttonCloseClick(View view){ }


    //переход в упрщенный режим управления
    public void buttonEasyModeActivate(View view){
        Intent intent = new Intent(MainActivity.this, EasyMode.class);
        startActivity(intent);
    }

}







