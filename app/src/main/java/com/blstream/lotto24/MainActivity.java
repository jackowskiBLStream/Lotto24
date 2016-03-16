package com.blstream.lotto24;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
       /* PixelGridView pixelGrid = null;
        try {
            pixelGrid = new PixelGridView(this, 7, 7);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pixelGrid.setSaveEnabled(true);
        pixelGrid.setId(R.id.chessBoardRelativeLayout);*/
        setContentView(R.layout.custom_layout);

        PixelGridView pixel = (PixelGridView) findViewById(R.id.pixel);


        pixel.setOnSwipeListener(new PixelGridView.OnSwipeListener() {
            @Override
            public void onSwipeRight() {
                Toast.makeText(getApplicationContext(), "Right swipe detected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeLeft() {
                Toast.makeText(getApplicationContext(), "Left swipe detected", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
