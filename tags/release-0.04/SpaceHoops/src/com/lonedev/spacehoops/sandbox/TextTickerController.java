package com.lonedev.spacehoops.sandbox;

import com.jme.scene.Controller;
import com.jmex.angelfont.BitmapText;

public class TextTickerController extends Controller {
    private float stringIndexFloat = 1f;
    private int stringIndex = 1;
    private BitmapText bitmapText;
    private String text;

    public TextTickerController(BitmapText bitmapText, String text) {
        this.bitmapText = bitmapText;
        this.text = text;
        setSpeed(10);
    }

    @Override
    public void update(float tpf) {
        stringIndexFloat += tpf*getSpeed();

        if (stringIndexFloat > stringIndex) {
            stringIndex++;

            if (stringIndex > (text.length())) {
//                bitmapText.removeController(this);

                // This is the way we pause before re-displaying.
                if (stringIndex > text.length()+20) {
                    stringIndex = 1;
                    stringIndexFloat = 1f;
                }
            } else {
                bitmapText.setText(text.substring(0, stringIndex));
                bitmapText.update();
            }
        }
    }
}
