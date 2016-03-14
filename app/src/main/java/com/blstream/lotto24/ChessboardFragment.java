package com.blstream.lotto24;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;

import java.io.IOException;

/**
 *
 */
public class ChessboardFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chessboard_fragment_layout, container, false);
       /* RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.chessBoardRelativeLayout);
        PixelGridView pixelGrid = null;
        try {
            pixelGrid = new PixelGridView(getActivity(), 7, 7);
        } catch (IOException e) {
            e.printStackTrace();
        }

       *//* try {
            pixelGrid.setNumRows(7);
            pixelGrid.setNumColumns(7);
        } catch (IOException e) {
            e.printStackTrace();
        }*//*
        relativeLayout.addView(pixelGrid);*/

        return view;
    }

}
