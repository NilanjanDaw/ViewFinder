package com.example.nilanjan.viewfinder;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.example.nilanjan.viewfinder.ui.GraphicOverlay;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

/**
 * Created by nilan on 30-Oct-16.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private static final String TAG = "OrcGraphic";
    private int mId;
    public static String searchString = "";
    private static final int TEXT_COLOR = Color.WHITE;

    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final TextBlock mText;

    OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        mText = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(54.0f);
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public TextBlock getTextBlock() {
        return mText;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     *
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        // TODO: Check if this graphic's text contains this point.

        return false;
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {

        if (mText == null)
            return;
        // Getting Text Elements from the total text
        Log.d(TAG, "draw: " + searchString);
        List<? extends Text> textComponents = mText.getComponents();
        for(Text currentText : textComponents) {
            List<? extends Text> textElements =  currentText.getComponents();
            for (Text element: textElements) {
                if (!searchString.equalsIgnoreCase("") && element.getValue().equalsIgnoreCase(searchString)) {
                    RectF rect = new RectF(element.getBoundingBox());
                    rect.left = translateX(rect.left);
                    rect.top = translateY(rect.top);
                    rect.right = translateX(rect.right);
                    rect.bottom = translateY(rect.bottom);
                    Log.d(TAG, "draw: " + element.getValue());
                    canvas.drawRect(rect, sRectPaint);
                }
            }
        }

    }
}