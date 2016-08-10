package com.uqi.oyue;

import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    WolfAndSheepView xTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xTitleList = (WolfAndSheepView) findViewById(R.id.chess);
        xTitleList.setOnGameStateChangeListener(new WolfAndSheepView.OnGameStateChangeListener() {
            @Override
            public void onGameOver(WolfAndSheepView.ChessType pType, boolean isWin) {
                String user = pType== WolfAndSheepView.ChessType.SHEEP?"羊":"狼";
                if(isWin){
                    Toast.makeText(MainActivity.this,user+" Win",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,user+" Lost",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGameStart() {
                Toast.makeText(MainActivity.this,"Game Start",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGameReay() {
                Toast.makeText(MainActivity.this,"Game Ready",Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.sheep_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                xTitleList.reset();
            }
        });
        findViewById(R.id.sheep_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                xTitleList.setFirstActionType(WolfAndSheepView.ChessType.SHEEP);
            }
        });
        findViewById(R.id.sheep_giveup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                xTitleList.giveUp(WolfAndSheepView.ChessType.SHEEP);
            }
        });
        findViewById(R.id.wolf_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                xTitleList.reset();
            }
        });
        findViewById(R.id.wolf_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                xTitleList.setFirstActionType(WolfAndSheepView.ChessType.WOLF);
            }
        });
        findViewById(R.id.wolf_giveup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                xTitleList.giveUp(WolfAndSheepView.ChessType.WOLF);
            }
        });
    }
}
