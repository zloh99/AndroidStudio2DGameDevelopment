package com.example.androidstudio2dgamedevelopment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Main activity is the entry point to our application
 */
public class MainActivity extends Activity {

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set window to fullscreen
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        //Set content view to game, so that only objects in the game class can be rendered to screen
        setContentView(new Game(this));
    }
}