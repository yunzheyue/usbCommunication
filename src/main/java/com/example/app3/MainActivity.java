package com.example.app3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(){
            @Override
            public void run() {
                super.run();
                handleData();
            }
        }.start();


    }

    private void handleData() {
        try {
            // adb 指令
            Runtime.getRuntime().exec("adb forward tcp:12580 tcp:10086"); // 端口转换
            Thread.sleep(3000);
            Runtime.getRuntime().exec("adb shell am broadcast -a NotifyServiceStart");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Socket socket = null;
        try {
            InetAddress serveraddr = null;
            serveraddr = InetAddress.getByName("127.0.0.1");
            Log.e("TAG","TCP1" + "C: Connecting...");
            socket = new Socket(serveraddr, 10086);
            Log.e("TAG","TCP2" + "C: Receive");
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            boolean flag = true;
            while (flag) {
                Log.e("TAG","输入数0进行文件传输,退出输入-1\n");
                String strWord = br.readLine();// 从控制台输入
                if (strWord.equals("0")) {
                    /* 准备接收文件数据 */
                    out.write("4".getBytes());
                    out.flush();
                    Thread.sleep(300);//等待服务端回复
                    String strFormsocket = readFromSocket(in);
                    Log.e("TAG","安卓传来的数据" + strFormsocket);
                } else if (strWord.equalsIgnoreCase("EXIT")) {
                    out.write("EXIT".getBytes());
                    out.flush();
                    Log.e("TAG","EXIT!");
                    String strFormsocket = readFromSocket(in);
                    Log.e("TAG","the data sent by server is:/r/n" + strFormsocket);
                    flag = false;
                }
            }

        } catch (UnknownHostException e1) {
            Log.e("TAG","TCP3" + "ERROR:" + e1.toString());
        } catch (Exception e2) {
            Log.e("TAG","TCP4" + "ERROR:" + e2.toString());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                    Log.e("TAG","socket.close()");
                }
            } catch (IOException e) {
                Log.e("TAG","TCP5" + "ERROR:" + e.toString());
            }
        }
    }


    /* 从InputStream流中读数据 */
    public static String readFromSocket(InputStream in) {
        int MAX_BUFFER_BYTES = 4000;
        String msg = "";
        byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
        try {
            int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
            msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
            tempbuffer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
}
