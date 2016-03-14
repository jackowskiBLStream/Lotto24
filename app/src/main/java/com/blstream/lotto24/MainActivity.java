package com.blstream.lotto24;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        PixelGridView pixelGrid = null;
        try {
            pixelGrid = new PixelGridView(this, 7, 7);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pixelGrid.setSaveEnabled(true);
        pixelGrid.setId(R.id.chessBoardRelativeLayout);
        setContentView(pixelGrid);



    }
}
