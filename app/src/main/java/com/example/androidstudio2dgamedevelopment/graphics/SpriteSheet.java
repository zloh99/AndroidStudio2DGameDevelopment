package com.example.androidstudio2dgamedevelopment.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.androidstudio2dgamedevelopment.R;

/**
 * Class responsible for returning specific sprite we are interested in, which we then add to the Sprite Object
 * The sprite object is then returned to the Player object, which then draws it to the screen
 */
public class SpriteSheet {
    //bitmap = 2d array of pixels, with each pixel having 3 values: R, G, and B
    private Bitmap bitmap;

    public SpriteSheet(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sprite_sheet, bitmapOptions);
    }

    public Sprite getPlayerSprite() {
        return new Sprite(this, new Rect(0, 0, 64, 64));
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
