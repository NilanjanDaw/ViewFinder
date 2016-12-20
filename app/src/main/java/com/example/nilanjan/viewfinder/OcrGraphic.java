package com.example.nilanjan.viewfinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.example.nilanjan.viewfinder.ui.GraphicOverlay;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

/**
 * Created by nilan on 30-Oct-16.
 * Project ViewFinder
 */

class OcrGraphic extends GraphicOverlay.Graphic {

    private static final String TAG = "OrcGraphic";
    private static final int TEXT_COLOR = Color.WHITE;
    static String searchString = "";
    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final TextBlock mText;
    private int mId;
    private GraphicOverlay mGraphicOverlay;
    private Context context;
    private String legacyURL = "";

    OcrGraphic(GraphicOverlay overlay, TextBlock text, Context context) {
        super(overlay);
        mGraphicOverlay = overlay;
        mText = text;
        this.context = context;
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
            for (final Text element : textElements) {
                if (!searchString.equalsIgnoreCase("") &&
                        element.getValue().toLowerCase().contains(searchString.toLowerCase())) {
                    RectF rect = new RectF(element.getBoundingBox());
                    rect.left = translateX(rect.left);
                    rect.top = translateY(rect.top);
                    rect.right = translateX(rect.right);
                    rect.bottom = translateY(rect.bottom);
                    canvas.drawRect(rect, sRectPaint);
                }
                if (Patterns.WEB_URL.matcher(element.getValue()).matches()) {
                    Log.d(TAG, "draw: URL: " + element.getValue());
                    if (!legacyURL.equals(element.getValue()))
                        Snackbar.make(mGraphicOverlay,
                                element.getValue(),
                                Snackbar.LENGTH_LONG)
                                .setAction("Goto", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startWebView(element.getValue());
                                    }
                                })
                                .show();
                    legacyURL = element.getValue();
                }
            }
        }

    }

    private void startWebView(String url) {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        if (!url.toLowerCase().startsWith("http"))
            url = "http://" + url;
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}