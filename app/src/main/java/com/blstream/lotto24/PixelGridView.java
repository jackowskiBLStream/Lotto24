package com.blstream.lotto24;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by blstream on 3/14/2016.
 */
public class PixelGridView extends View {


    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private TextPaint textPaint = new TextPaint();
    private Paint greenPaint = new Paint();
    private Paint borderPaint = new Paint();
    private Bitmap bitmap;
    private int checkedCounter;
    private boolean[][] cellChecked = null;
    private int height, width;

    private boolean isSaved = false;


    public PixelGridView(Context context, int numColumns, int numRows) throws IOException {
        this(context, null, numColumns, numRows);

    }

    public PixelGridView(Context context, AttributeSet attrs, int numColumns, int numRows) throws IOException {
        super(context, attrs);
        //Init only once(Manifest android:configChanges)
        this.numColumns = numColumns;
        this.numRows = numRows;
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        greenPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStrokeWidth(10);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(20);
        setSaveEnabled(true);



    }
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public void decodeCheckMark() throws IOException {
        AssetManager assetManager = getContext().getAssets();
        InputStream inputStream = assetManager.open("check.png");
        // bitmap = decodeSampledBitmapFromStream(inputStream, 100, 100 );
        bitmap = BitmapFactory.decodeStream(inputStream);
        bitmap = getResizedBitmap(bitmap, cellWidth, cellHeight);
        inputStream.close();
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) throws IOException {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) throws IOException {
        this.numRows = numRows;
        calculateDimensions();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        try {
            calculateDimensions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calculateDimensions() throws IOException {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        if (getWidth() > getHeight()) {
            cellHeight = getHeight() / numRows;
            cellWidth = cellHeight;

        } else {
            cellWidth = getWidth() / numColumns;
            cellHeight = cellWidth;
        }
//TODO: init only once!
        if (cellChecked == null) {
            cellChecked = new boolean[numColumns][numRows];
        }

        decodeCheckMark();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        if (numColumns == 0 || numRows == 0) {
            return;
        }
        setColors();

        if (getWidth() > getHeight()) {
            height = getHeight();
            width = height;
        } else {
            width = getWidth();
            height = width;
        }
        drawNumbers(canvas);
        drawCheckMarks(canvas);

        drawChessBoard(borderPaint, canvas, height, width);
        Log.d("No. counter: ", String.valueOf(checkedCounter));

        //drawBorder(borderPaint, canvas, height, width);


    }

    private void drawCheckMarks(Canvas canvas) {
        //Drawing checkMark
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (cellChecked[i][j]) {
                    canvas.drawBitmap(bitmap, i * cellWidth, j * cellHeight,
                            textPaint);

                }
            }
        }
    }

    public void drawNumbers(Canvas canvas) {
        //drawNumbers
        textPaint.setTextSize(cellHeight * 0.5f);
        //TODO: text should takes 50% of height
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                    canvas.drawText(String.valueOf((i * numRows + j + 1)), (j + 1)
                            * cellWidth - cellWidth / 2 - textPaint.measureText
                            (String.valueOf((i * numRows + j + 1))) / 2, (i + 1) * cellHeight -
                            cellHeight / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
            }
        }

    }

    public void setColors() {
        if (checkedCounter == 6) {
            borderPaint.setColor(Color.GREEN);
            textPaint.setColor(Color.GREEN);
        } else {
            borderPaint.setARGB(255, 255, 160, 0);
            textPaint.setARGB(255, 255, 160, 0);
        }
    }

    //TODO: height = width
    public void drawChessBoard(Paint paint, Canvas canvas, int height, int width) {

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, paint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, paint);
        }

        canvas.drawRect(0, 0, width, height, paint);
    }

    public void drawBorder(Paint paint, Canvas canvas, int height, int width) {
/*        if (checkedCounter == 6) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.YELLOW);
        }*/
        canvas.drawRect(0, 0, width, height, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() < width - 5 && event.getY() < height - 5) {
            int column = (int) (event.getX() / cellWidth);
            int row = (int) (event.getY() / cellHeight);


            if (checkedCounter == 6 && !cellChecked[column][row]) {

            } else if (cellChecked[column][row]) {
                cellChecked[column][row] = !cellChecked[column][row];
                checkedCounter--;
            } else {
                cellChecked[column][row] = !cellChecked[column][row];
                checkedCounter++;
            }


            // Log.d("No. counter: ", String.valueOf(checkedCounter));
            invalidate();
        }

        return true;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        //ss.value = currentValue;
        ss.mArray = cellChecked;
        ss.mCheckCounter = checkedCounter;
        isSaved = true;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        // currentValue = ss.value;
        cellChecked = ss.mArray;
        checkedCounter = ss.mCheckCounter;
    }

    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean[][] mArray; //this will store the current value
        int mCheckCounter;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            boolean[][] array;
            //array = in.readArray(ClassLoader.getSystemClassLoader())
            final int N = in.readInt();
            array = new boolean[N][N];
            for (int i = 0; i < N; i++) {
                array[i] = in.createBooleanArray();
            }
            mArray = array;
            mCheckCounter = in.readInt();
            // array = in.readBooleanArray();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            //out.writeInt(array);
            final int N = mArray.length;
            out.writeInt(N);
            for (int i = 0; i < N; i++) {
                out.writeBooleanArray(mArray[i]);
            }
            out.writeInt(mCheckCounter);

            // out.writeTypedArray(array);
        }
    }
}
