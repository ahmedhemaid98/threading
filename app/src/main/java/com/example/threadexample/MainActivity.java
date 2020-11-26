package com.example.threadexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static final int SET_PROGRESS_CODE = 100;
    public static final int SHOW_IMAGE_CODE = 101;
    private ImageView image;
    private ProgressBar progressBar;
    private MyHandler myHandler;

    static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mainActivityWeakReference;
        public MyHandler(WeakReference<MainActivity> mainActivityWeakReference){
            super();
            this.mainActivityWeakReference=mainActivityWeakReference;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity mainActivity=mainActivityWeakReference.get();
            if (mainActivity!=null){
                switch (msg.what) {
                    case SET_PROGRESS_CODE:
                            mainActivity.progressBar.setProgress(msg.arg1);
                        break;
                    case SHOW_IMAGE_CODE:
                            mainActivity.image.setImageBitmap((Bitmap) msg.obj);
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.imageView2);
        progressBar = findViewById(R.id.ProgressBar);
        myHandler=new MyHandler(new WeakReference<>(this));

    }

    public void onClickLoadImage(View view) {
        Executor executor= Executors.newFixedThreadPool(3);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message progress_message = myHandler.obtainMessage(SET_PROGRESS_CODE, i, -1);
                    myHandler.sendMessage(progress_message);
                }
                Message show_message=myHandler.obtainMessage(SHOW_IMAGE_CODE, bitmap);
                myHandler.sendMessage(show_message);
                //another way
//                image.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        image.setImageBitmap(bitmap);
//                    }
//                });

            }});

    }


    public void onClickShowMessage(View view) {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

}