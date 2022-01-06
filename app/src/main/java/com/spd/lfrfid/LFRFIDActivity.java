package com.spd.lfrfid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.DeviceControlSpd;
import android.serialport.SerialPortSpd;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author xuyan  低频LFdemo
 */
public class LFRFIDActivity extends Activity implements OnCheckedChangeListener, OnClickListener {

    private static final int BUFSIZE = 64;

    private ToggleButton powerBtn;
    private Button clearBtn;
    private Button closeBtn;
    private TextView contView;

    private Handler handler;
    private ReadThread reader;

    private int size = 0;

    private SerialPortSpd serialPortSpd;
    private DeviceControlSpd deviceControlSpd;
    private int fd;

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        powerBtn = findViewById(R.id.toggleButton_power);
        powerBtn.setOnCheckedChangeListener(this);

        clearBtn = findViewById(R.id.button_clear);
        clearBtn.setOnClickListener(this);

        closeBtn = findViewById(R.id.button_close);
        closeBtn.setOnClickListener(this);

        contView = findViewById(R.id.tv_content);

        serialPortSpd = new SerialPortSpd();
        try {

            serialPortSpd.OpenSerial(SerialPortSpd.SERIAL_TTYMT2, 9600);

        } catch (IOException e) {
            e.printStackTrace();
            contView.setText(R.string.Status_OpenSerialFail);
            powerBtn.setEnabled(false);
            clearBtn.setEnabled(false);
            return;
        }

        fd = serialPortSpd.getFd();

        try {
            //MTK(6737)平台安卓6.0及以下版本 主板上电路径（例如：kt55、kt50、kt80、kt40、sk100）
            //DevCtrl = new DeviceControl("/sys/class/misc/mtgpio/pin");
            //MTK(6763)平台安卓8.1版本  主板上电路径(例如：SD55、SD60)
            //DevCtrl = new DeviceControl("/sys/bus/platform/drivers/mediatek-pinctrl/10005000.pinctrl/mt_gpio");
            deviceControlSpd = new DeviceControlSpd(DeviceControlSpd.PowerType.MAIN, 94);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            contView.setText(R.string.Status_OpenDevFileFail);
            powerBtn.setEnabled(false);
            clearBtn.setEnabled(false);
            new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            }).show();
            return;
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    byte[] buf = (byte[]) msg.obj;
                    if (buf.length == 30 && buf[0] == (byte) 0x02 && (buf[29] == 0x03 || (buf[29] == 0x07))) {
                        if (encrypt(buf) == buf[27]) {
                            //校验结果是否为27
                            Log.d("lfrfid", "xor done");
                        }
                        String cnt = new String(buf);
                        String[] serial_number = new String[30];
                        serial_number[9] = cnt.substring(1, 2);
                        serial_number[8] = cnt.substring(2, 3);
                        serial_number[7] = cnt.substring(3, 4);
                        serial_number[6] = cnt.substring(4, 5);
                        serial_number[5] = cnt.substring(5, 6);
                        serial_number[4] = cnt.substring(6, 7);
                        serial_number[3] = cnt.substring(7, 8);
                        serial_number[2] = cnt.substring(8, 9);
                        serial_number[1] = cnt.substring(9, 10);
                        serial_number[0] = cnt.substring(10, 11);
                        String reverse = serial_number[0] + serial_number[1] + serial_number[2] + serial_number[3] + serial_number[4] + serial_number[5] + serial_number[6] + serial_number[7] + serial_number[8] + serial_number[9];
                        long dec_first = Long.parseLong(reverse, 16);
                        String string = Long.toString(dec_first);
                        size = string.length();
                        switch (size) {
                            case 1:// if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //   break;
                            }
                            break;
                            case 2: // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //   break;
                            }
                            break;
                            case 3: // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                // break;
                            }
                            break;
                            case 4:   //if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + "0" + "0" + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //  break;
                            }
                            break;
                            case 5:  // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + "0" + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //  break;
                            }
                            break;
                            case 6:  // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //  break;
                            }
                            break;
                            case 7:   // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                // break;
                            }
                            break;
                            case 8:   // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //  break;
                            }
                            break;
                            case 9:   // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //  break;
                            }
                            break;
                            case 10:  // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //	  break;
                            }
                            break;
                            case 11:   //if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + "0" + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //	  break;
                            }
                            break;
                            case 12:   // if (xor_result==buf[27])
                            {
                                serial_number[10] = cnt.substring(14, 15);
                                serial_number[11] = cnt.substring(13, 14);
                                serial_number[12] = cnt.substring(12, 13);
                                serial_number[13] = cnt.substring(11, 12);
                                String country_code = serial_number[11] + serial_number[12] + serial_number[13];
                                long dec_result = Long.parseLong(country_code, 16);
                                String second_dec = Long.toString(dec_result);
                                StringBuilder combine = new StringBuilder(second_dec + string);
                                int len = combine.length();
                                for (int i = len; i < 15; i++) {
                                    combine.insert(0, "0");
                                }
                                contView.setTextSize(30);
                                contView.append(combine.toString());
                                contView.append("\n");
                                //  break;
                            }
                            break;
                            default:
                                break;
                        }
                    } else if (buf.length == 13 && buf[0] == (byte) 0x02 && ((buf[12] == 0x03) || (buf[12] == 0x07))) {
                        String cnt = new String(buf);
                        int count0 = Integer.parseInt(cnt.substring(1, 3), 16);
                        int count1 = Integer.parseInt(cnt.substring(3, 5), 16);
                        int count2 = Integer.parseInt(cnt.substring(5, 7), 16);
                        int count3 = Integer.parseInt(cnt.substring(7, 9), 16);
                        int count4 = Integer.parseInt(cnt.substring(9, 11), 16);
                        int count5 = count0 ^ count1 ^ count2 ^ count3 ^ count4;
                        byte[] b = new byte[4];
                        b[0] = (byte) (count5 & 0xff);
                        if (b[0] == buf[11]) {
                            contView.setTextSize(30);
                            contView.append(cnt.substring(1, cnt.length() - 2));
                            contView.append("\n");
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        if (powerBtn.isChecked()) {
            try {
                reader.interrupt();
                deviceControlSpd.PowerOffDevice();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serialPortSpd.CloseSerial(fd);
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

        if (arg1) {
            try {

                deviceControlSpd.PowerOnDevice();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                serialPortSpd.clearPortBuf(fd);
                reader = new ReadThread();
                reader.start();

            } catch (IOException e) {
                contView.setText(R.string.Status_ManipulateFail);
            }
        } else {
            try {
                reader.interrupt();
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                deviceControlSpd.PowerOffDevice();

            } catch (IOException e) {
                contView.setText(R.string.Status_ManipulateFail);
            }
        }
    }

    @Override
    public void onClick(View arg0) {

        if (arg0 == clearBtn) {
            contView.setText("");
        } else if (arg0 == closeBtn) {
            finish();
        }
    }

    class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            Log.d("lfrfid", "thread start");
            while (!isInterrupted()) {

                byte[] buf = new byte[BUFSIZE];
                try {
                    serialPortSpd.clearPortBuf(fd);
                    buf = serialPortSpd.ReadSerial(fd, BUFSIZE, 300);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (buf != null) {
                    Message msg = new Message();

                    if (buf.length >= 2) {
                        size = 0;
                        msg.what = 1;
                        msg.obj = buf;
                        handler.sendMessage(msg);
                    }
                }
            }
            Log.d("lfrfid", "thread stop");
        }
    }


    public byte encrypt(byte[] bytes) {

        byte result = bytes[1];

        for (int i = 2; i < 27; i++) {
            result = (byte) (result ^ bytes[i]);

        }
        return result;
    }


}