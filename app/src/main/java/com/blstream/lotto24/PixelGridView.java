package com.blstream.lotto24;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class PixelGridView extends View implements GestureDetector.OnGestureListener {

    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private TextPaint textPaint = new TextPaint();
    private Paint borderPaint = new Paint();
    private Bitmap bitmap;
    private int checkedCounter;
    private boolean[][] cellChecked = null;
    private int height, width = height;
    private OnSwipeListener onSwipeListener;
    private GestureDetectorCompat gestureDetectorCompat;


    public PixelGridView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        TypedArray attributeValuesArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.PixelGridView, 0, 0);

        String numColumns = attributeValuesArray.getString(R.styleable.PixelGridView_numColums);
        String numRows = attributeValuesArray.getString(R.styleable.PixelGridView_numRows);
        if (numColumns != null) {
            this.numColumns = Integer.parseInt(numColumns);
        } else {
            this.numColumns = 1;
        }
        if (numRows != null) {
            this.numRows = Integer.parseInt(numRows);
        } else {
            this.numRows = 1;
        }

        onSwipeListener = null;
        //Init only once(Manifest android:configChanges)
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint greenPaint = new Paint();
        greenPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStrokeWidth(10);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5);
        textPaint.setShadowLayer(10, 10, 10, Color.BLACK);


        gestureDetectorCompat = new GestureDetectorCompat(getContext(), this);
        setSaveEnabled(true);

    }


    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
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
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public void decodeCheckMark() throws IOException {
        AssetManager assetManager = getContext().getAssets();
        InputStream inputStream = assetManager.open("check.png");
        bitmap = BitmapFactory.decodeStream(inputStream);
        bitmap = getResizedBitmap(bitmap, cellHeight, cellWidth);
        inputStream.close();
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
        //init only once
        if (cellChecked == null) {
            cellChecked = new boolean[numColumns][numRows];
        }

        decodeCheckMark();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY);

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


        drawChessBoard(borderPaint, canvas);
        Log.d("No. counter: ", String.valueOf(checkedCounter));
        Log.d("POS After: ", String.valueOf(getHeight()) + " " + String.valueOf(getWidth()));

    }

    private void drawCheckMarks(Canvas canvas) {
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
        textPaint.setTextSize(cellHeight * 0.5f);
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

    private Path Star(float midX, float midY, float degrees) {
        Path path = new Path();
        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();

        path.moveTo(midX + cellWidth / 2, midY + (cellHeight * 3 / 4)); // right corner takes 1/4 of height below
        path.lineTo(midX, midY + (cellHeight / 4));
        path.lineTo(midX - (cellWidth / 2), midY + (cellHeight * 3 / 4));
        path.lineTo(midX - (cellWidth / 5), midY);
        path.lineTo(midX - (cellWidth / 2), midY - (cellHeight / 5));
        path.lineTo(midX - (cellWidth / 7), midY - (cellHeight / 5));
        path.lineTo(midX, midY - (cellHeight * 3 / 4));
        path.lineTo(midX + (cellWidth / 5), midY - (cellHeight / 5));
        path.lineTo(midX + (cellWidth / 2), midY - (cellHeight / 5));
        path.lineTo(midX + (cellHeight * 2 / 7), midY);
        path.lineTo(midX + (cellWidth / 2), midY + (cellHeight * 3 / 4));
        path.computeBounds(bounds, true);
        mMatrix.postRotate(degrees, bounds.centerX(), bounds.centerY());
        path.transform(mMatrix);
        return path;

    }


    public void drawChessBoard(Paint paint, Canvas canvas) {
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                canvas.drawPath(Star(cellWidth / 2 + (j * cellWidth),
                        cellHeight / 2 + (i * cellHeight), i * numColumns + j * 15), paint);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ///FIXME: what does 5 do?
        if (event.getX() < width - 5 && event.getY() < height - 5) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int column = (int) (event.getX() / cellWidth);
                int row = (int) (event.getY() / cellHeight);
                //FIXME: WTF?
                if (checkedCounter == 6 && !cellChecked[column][row]) {

                } else if (cellChecked[column][row]) {
                    cellChecked[column][row] = !cellChecked[column][row];
                    checkedCounter--;
                } else {
                    cellChecked[column][row] = !cellChecked[column][row];
                    checkedCounter++;
                }
                invalidate();
            }
            gestureDetectorCompat.onTouchEvent(event);
        }
        return true;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mArray = cellChecked;
        ss.mCheckCounter = checkedCounter;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        cellChecked = ss.mArray;
        checkedCounter = ss.mCheckCounter;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i("TAK: ", "onFling has been called!");
        final int SWIPE_MIN_DISTANCE = 120;
        final int SWIPE_MAX_OFF_PATH = 250;
        final int SWIPE_THRESHOLD_VELOCITY = 200;
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.i("TAK: ", "Right to Left");
                onSwipeListener.onSwipeLeft();
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.i("TAK: ", "Left to Right");
                onSwipeListener.onSwipeRight();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public interface OnSwipeListener {
        void onSwipeRight();

        void onSwipeLeft();

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
            int N = in.readInt();
            array = new boolean[N][N];
            for (int i = 0; i < N; i++) {
                array[i] = in.createBooleanArray();
            }
            mArray = array;
            mCheckCounter = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            //out.writeInt(array);
            final int N = mArray.length;
            out.writeInt(N);
            for (boolean[] aMArray : mArray) {
                out.writeBooleanArray(aMArray);
            }
            out.writeInt(mCheckCounter);
        }
    }
}
