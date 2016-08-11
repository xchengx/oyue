package com.uqi.oyue;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shuxin on 2016/8/6.
 */
public class WolfAndSheepView extends View implements View.OnTouchListener {

    private Context xContext;
    private int padding = 40;
    private static final int sheepCount = 20;
    private int width, height, viewWidth, viewHeight;
    private Paint mapPaint;
    private Paint wolfPaint;
    private Paint wolfTextPaint;
    private Paint sheepBorderPaint;
    private Paint sheepPaint;
    private Paint sheepTextPaint;
    private int fontHeight = 0;
    private Chess wolf;
    private Chess sheep[] = new Chess[sheepCount];

    private Coords[][] sCoordses = new Coords[5][5];
    private ChessType actionType = ChessType.WOLF;
    private ChessState xChessState = ChessState.GAME_LOADING;
    private int sheepSelectd = -1;
    private ChessType giveUp = null;
    public interface OnGameStateChangeListener{
        public void onGameOver(ChessType pType,boolean isWin);
        public void onGameStart();
        public void onGameReay();
    }
    private OnGameStateChangeListener xChangeListener;
    public void setOnGameStateChangeListener(OnGameStateChangeListener pListener){
        this.xChangeListener = pListener;
    }
    public void giveUp(ChessType pType){
        giveUp = pType;
        xChessState = ChessState.GAME_OVER;
        if(this.xChangeListener!=null){
            this.xChangeListener.onGameOver(pType,false);
        }
        invalidate();
    }
    public WolfAndSheepView(Context context) {
        super(context);
        xContext = context;
        initPaint();
    }

    public WolfAndSheepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        xContext = context;
        initPaint();
    }

    public WolfAndSheepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        xContext = context;
        initPaint();
    }

    private void initPaint() {
        setOnTouchListener(this);
        mapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mapPaint.setColor(Color.rgb(0x22, 0x22, 0x22));
        mapPaint.setStrokeWidth(2);
        mapPaint.setStyle(Paint.Style.STROKE);

        wolfPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wolfPaint.setColor(Color.GRAY);
        wolfPaint.setStrokeWidth(1);
        wolfPaint.setStyle(Paint.Style.FILL);


        wolfTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wolfTextPaint.setColor(Color.WHITE);
        wolfTextPaint.setTextAlign(Paint.Align.CENTER);
        wolfTextPaint.setTextSize(16 * xContext.getResources().getDisplayMetrics().scaledDensity/padding*40);
        Rect r = new Rect();
        wolfTextPaint.getTextBounds("狼", 0, 1, r);
        fontHeight = r.height();

        sheepBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sheepBorderPaint.setColor(Color.BLACK);
        sheepBorderPaint.setStrokeWidth(2);
        sheepBorderPaint.setStyle(Paint.Style.STROKE);

        sheepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sheepPaint.setColor(Color.WHITE);
        sheepPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        sheepTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sheepTextPaint.setColor(Color.BLACK);
        sheepTextPaint.setTextAlign(Paint.Align.CENTER);
        sheepTextPaint.setTextSize(16 * xContext.getResources().getDisplayMetrics().scaledDensity/padding*40);
    }

    private void initPoints() {
        initSheep();
        int widthGap = width / 4;
        int heightGap = height / 4;

        sCoordses[0][0] = new Coords(new PointF(0 * widthGap + padding, 0 * heightGap + padding), StepDirection.DOWN, StepDirection.RIGHT, StepDirection.DOWN_RIGHT);
        sCoordses[0][1] = new Coords(new PointF(0 * widthGap + padding, 1 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.RIGHT);
        sCoordses[0][2] = new Coords(new PointF(0 * widthGap + padding, 2 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.RIGHT, StepDirection.UP_RIGHT, StepDirection.DOWN_RIGHT);
        sCoordses[0][3] = new Coords(new PointF(0 * widthGap + padding, 3 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.RIGHT);
        sCoordses[0][4] = new Coords(new PointF(0 * widthGap + padding, 4 * heightGap + padding), StepDirection.UP, StepDirection.RIGHT, StepDirection.UP_RIGHT);

        sCoordses[1][0] = new Coords(new PointF(1 * widthGap + padding, 0 * heightGap + padding), StepDirection.DOWN, StepDirection.RIGHT, StepDirection.LEFT);
        sCoordses[1][1] = new Coords(new PointF(1 * widthGap + padding, 1 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.LEFT, StepDirection.RIGHT, StepDirection.UP_LEFT, StepDirection.UP_RIGHT, StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT);
        sCoordses[1][2] = new Coords(new PointF(1 * widthGap + padding, 2 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.RIGHT, StepDirection.LEFT);
        sCoordses[1][3] = new Coords(new PointF(1 * widthGap + padding, 3 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.LEFT, StepDirection.RIGHT, StepDirection.UP_LEFT, StepDirection.UP_RIGHT, StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT);
        sCoordses[1][4] = new Coords(new PointF(1 * widthGap + padding, 4 * heightGap + padding), StepDirection.UP, StepDirection.RIGHT, StepDirection.LEFT);

        sCoordses[2][0] = new Coords(new PointF(2 * widthGap + padding, 0 * heightGap + padding), StepDirection.DOWN, StepDirection.RIGHT, StepDirection.LEFT, StepDirection.DOWN_RIGHT, StepDirection.DOWN_LEFT);
        sCoordses[2][1] = new Coords(new PointF(2 * widthGap + padding, 1 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.RIGHT, StepDirection.LEFT);
        sCoordses[2][2] = new Coords(new PointF(2 * widthGap + padding, 2 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.LEFT, StepDirection.RIGHT, StepDirection.UP_LEFT, StepDirection.UP_RIGHT, StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT);
        sCoordses[2][3] = new Coords(new PointF(2 * widthGap + padding, 3 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.RIGHT, StepDirection.LEFT);
        sCoordses[2][4] = new Coords(new PointF(2 * widthGap + padding, 4 * heightGap + padding), StepDirection.UP, StepDirection.LEFT, StepDirection.RIGHT, StepDirection.UP_RIGHT, StepDirection.UP_LEFT);

        sCoordses[3][0] = new Coords(new PointF(3 * widthGap + padding, 0 * heightGap + padding), StepDirection.DOWN, StepDirection.RIGHT, StepDirection.LEFT);
        sCoordses[3][1] = new Coords(new PointF(3 * widthGap + padding, 1 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.LEFT, StepDirection.RIGHT, StepDirection.UP_LEFT, StepDirection.UP_RIGHT, StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT);
        sCoordses[3][2] = new Coords(new PointF(3 * widthGap + padding, 2 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.RIGHT, StepDirection.LEFT);
        sCoordses[3][3] = new Coords(new PointF(3 * widthGap + padding, 3 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.LEFT, StepDirection.RIGHT, StepDirection.UP_LEFT, StepDirection.UP_RIGHT, StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT);
        sCoordses[3][4] = new Coords(new PointF(3 * widthGap + padding, 4 * heightGap + padding), StepDirection.UP, StepDirection.RIGHT, StepDirection.LEFT);


        sCoordses[4][0] = new Coords(new PointF(4 * widthGap + padding, 0 * heightGap + padding), StepDirection.DOWN, StepDirection.LEFT, StepDirection.DOWN_LEFT);
        sCoordses[4][1] = new Coords(new PointF(4 * widthGap + padding, 1 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.LEFT);
        sCoordses[4][2] = new Coords(new PointF(4 * widthGap + padding, 2 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.LEFT, StepDirection.UP_LEFT, StepDirection.DOWN_LEFT);
        sCoordses[4][3] = new Coords(new PointF(4 * widthGap + padding, 3 * heightGap + padding), StepDirection.UP, StepDirection.DOWN, StepDirection.LEFT);
        sCoordses[4][4] = new Coords(new PointF(4 * widthGap + padding, 4 * heightGap + padding), StepDirection.UP, StepDirection.LEFT, StepDirection.UP_LEFT);
        {
            Coords lCoords = sCoordses[1][1];
            lCoords.addChess(sheep[0]);
            lCoords.addChess(sheep[1]);
            lCoords.addChess(sheep[2]);
            lCoords.addChess(sheep[3]);
            lCoords.addChess(sheep[4]);
        }
        {
            Coords lCoords = sCoordses[3][1];
            lCoords.addChess(sheep[5]);
            lCoords.addChess(sheep[6]);
            lCoords.addChess(sheep[7]);
            lCoords.addChess(sheep[8]);
            lCoords.addChess(sheep[9]);
        }
        {
            Coords lCoords = sCoordses[1][3];
            lCoords.addChess(sheep[10]);
            lCoords.addChess(sheep[11]);
            lCoords.addChess(sheep[12]);
            lCoords.addChess(sheep[13]);
            lCoords.addChess(sheep[14]);
        }
        {
            Coords lCoords = sCoordses[3][3];
            lCoords.addChess(sheep[15]);
            lCoords.addChess(sheep[16]);
            lCoords.addChess(sheep[17]);
            lCoords.addChess(sheep[18]);
            lCoords.addChess(sheep[19]);
        }
        {
            Coords lCoords = sCoordses[2][2];
            lCoords.addChess(wolf);
        }
        xChessState = ChessState.GAME_READY;
        if(this.xChangeListener!=null){
            this.xChangeListener.onGameReay();
        }
    }

    private void initSheep() {
        int widthGap = width / 8;
        int heightGap = height / 8;
        sheep[0]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding, 2 * heightGap + padding));
        sheep[1]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding, 2 * heightGap + padding+15));
        sheep[2]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding+15, 2 * heightGap + padding));
        sheep[3]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding, 2 * heightGap + padding-15));
        sheep[4]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding-15, 2 * heightGap + padding));

        sheep[5]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding, 2 * heightGap + padding));
        sheep[6]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding, 2 * heightGap + padding+15));
        sheep[7]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding+15, 2 * heightGap + padding));
        sheep[8]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding, 2 * heightGap + padding-15));
        sheep[9]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding-15, 2 * heightGap + padding));

        sheep[10]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding, 6 * heightGap + padding));
        sheep[11]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding, 6 * heightGap + padding+15));
        sheep[12]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding+15, 6 * heightGap + padding));
        sheep[13]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding, 6 * heightGap + padding-15));
        sheep[14]=new Chess(ChessType.SHEEP, new PointF(2 * widthGap + padding-15, 6 * heightGap + padding));

        sheep[15]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding, 6 * heightGap + padding));
        sheep[16]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding, 6 * heightGap + padding+15));
        sheep[17]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding+15, 6 * heightGap + padding));
        sheep[18]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding, 6 * heightGap + padding-15));
        sheep[19]=new Chess(ChessType.SHEEP, new PointF(6 * widthGap + padding-15, 6 * heightGap + padding));
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, size);
        padding = viewWidth/14;
        viewWidth = size;
        viewHeight = size;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - 2 * padding;
        height = h - 2 * padding;
        PointF wolfPoint = new PointF(width / 2 + padding, height / 2 + padding);
        wolf = new Chess(ChessType.WOLF, wolfPoint);
        initPoints();
    }
    public void setFirstActionType(ChessType pActionType){
        if(xChessState!=ChessState.GAME_PLAYING && xChessState !=ChessState.GAME_OVER){
            actionType = pActionType;
            postInvalidate();
        }
    }
    public void reset(){
        PointF wolfPoint = new PointF(width / 2 + padding, height / 2 + padding);
        wolf = new Chess(ChessType.WOLF, wolfPoint);
        actionType = ChessType.WOLF;
        sheepSelectd = -1;
        initPoints();
        postInvalidate();
    }


    private void drawMap(Canvas pCanvas) {
        Path lPath = new Path();
        lPath.moveTo(padding, padding);
        lPath.lineTo(viewWidth - padding, padding);
        lPath.lineTo(viewWidth - padding, viewHeight - padding);
        lPath.lineTo(padding, viewHeight - padding);
        lPath.close();
        mapPaint.setColor(Color.rgb(99,73,48));
        mapPaint.setStrokeWidth(5);
        pCanvas.drawPath(lPath, mapPaint);
        mapPaint.setStrokeWidth(2);
        mapPaint.setColor(Color.BLACK);
        //斜线
        pCanvas.drawLine(padding, padding, viewWidth - padding, viewHeight - padding, mapPaint);
        pCanvas.drawLine(viewWidth - padding, padding, padding, viewHeight - padding, mapPaint);
        int gap = width / 4;
//        //三条垂直线
        pCanvas.drawLine(gap + padding, padding, gap + padding, viewHeight - padding, mapPaint);
        mapPaint.setStrokeWidth(5);
        mapPaint.setColor(Color.rgb(99,73,48));
        pCanvas.drawLine(2 * gap + padding, padding, 2 * gap + padding, viewHeight - padding, mapPaint);
        mapPaint.setStrokeWidth(2);
        mapPaint.setColor(Color.BLACK);
        pCanvas.drawLine(3 * gap + padding, padding, 3 * gap + padding, viewHeight - padding, mapPaint);
//        //三条横线
        int heightGap = width / 4;
        pCanvas.drawLine(padding, heightGap + padding, viewWidth - padding, heightGap + padding, mapPaint);
        mapPaint.setStrokeWidth(5);
        mapPaint.setColor(Color.rgb(99,73,48));
        pCanvas.drawLine(padding, 2 * heightGap + padding, viewWidth - padding, 2 * heightGap + padding, mapPaint);
        mapPaint.setStrokeWidth(2);
        mapPaint.setColor(Color.BLACK);
        pCanvas.drawLine(padding, 3 * heightGap + padding, viewWidth - padding, 3 * heightGap + padding, mapPaint);
//        //内四角线
        Path mpath = new Path();
        mpath.moveTo(2 * gap + padding, padding);
        mpath.lineTo(viewWidth - padding, 2 * heightGap + padding);
        mpath.lineTo(2 * gap + padding, viewHeight - padding);
        mpath.lineTo(padding, 2 * heightGap + padding);
        mpath.close();
        pCanvas.drawPath(mpath, mapPaint);
    }

    private void drawWolf(Canvas pCanvas) {
        PointF wolfPoint = wolf.getPointF();
        if(actionType.equals(ChessType.WOLF)) {
            wolfPaint.setColor(Color.RED);
        }else {
            wolfPaint.setColor(Color.GRAY);
        }

        pCanvas.drawCircle(wolfPoint.x, wolfPoint.y, padding, wolfPaint);
        pCanvas.drawText("狼", wolfPoint.x, wolfPoint.y + fontHeight / 3, wolfTextPaint);
    }

    private void drawSheep(Canvas pCanvas) {
        for (int i = 19; i >=0; i--) {//倒序绘制
            Chess shep = sheep[i];
            if (sheepSelectd == i) {
                sheepPaint.setColor(Color.RED);
                sheepTextPaint.setColor(Color.WHITE);
            } else {
                sheepPaint.setColor(Color.WHITE);
                sheepTextPaint.setColor(Color.BLACK);
            }
            if(shep.isLive()){
                pCanvas.drawCircle(shep.xPointF.x, shep.xPointF.y, padding-13, sheepBorderPaint);
                pCanvas.drawCircle(shep.xPointF.x, shep.xPointF.y, padding-15, sheepPaint);
                pCanvas.drawText("羊", shep.xPointF.x, shep.xPointF.y + fontHeight / 3, sheepTextPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(202, 147, 93));
        drawMap(canvas);
        drawSheep(canvas);
        drawWolf(canvas);
    }



    @Override
    public boolean onTouch(View pView, MotionEvent pMotionEvent) {
        if(xChessState == ChessState.GAME_LOADING || xChessState == ChessState.GAME_OVER){
            return true;
        }
        if(xChessState ==ChessState.GAME_READY) {
            xChessState = ChessState.GAME_PLAYING;
            if(xChangeListener!=null)
                xChangeListener.onGameStart();
        }
        switch (pMotionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
                if (actionType.equals(ChessType.WOLF)) {
                    Point lPoint = getNearestPoint(pMotionEvent.getX(), pMotionEvent.getY());
                    Coords lCoords = sCoordses[lPoint.x][lPoint.y];
                    if (lCoords.isHasChess()) {
                        break;
                    }
                    Point wolfPosition = getChessPosition(wolf);
                    StepDirection lDirection = getDirection(wolfPosition, lPoint);
                    Coords curCoords = sCoordses[wolfPosition.x][wolfPosition.y];
                    if (!curCoords.hasDirection(lDirection)) {
                        break;
                    }
                    if(lPoint.x-wolfPosition.x == 0 && Math.abs(lPoint.y-wolfPosition.y)==1
                            || Math.abs(lPoint.x-wolfPosition.x) == 1 && lPoint.y-wolfPosition.y==0
                            || Math.abs(lPoint.x-wolfPosition.x) == 1 && Math.abs(lPoint.y-wolfPosition.y)==1){
                        //单步行动
                        for (int i = 0; i < sCoordses.length; i++) {
                            for (int j = 0; j < sCoordses[i].length; j++) {
                                Coords cCoords = sCoordses[i][j];
                                if (cCoords.isChessIn(wolf)) {
                                    cCoords.removeChess(wolf);
                                }
                            }
                        }
                        PointF mp = lCoords.getPointF();
                        wolf.setPointF(mp);
                        lCoords.addChess(wolf);
                        actionType = ChessType.SHEEP;
                        postInvalidate();
                    }else if(lPoint.x-wolfPosition.x == 0 && Math.abs(lPoint.y-wolfPosition.y)==2
                            || Math.abs(lPoint.x-wolfPosition.x) == 2 && lPoint.y-wolfPosition.y==0
                            || Math.abs(lPoint.x-wolfPosition.x) == 2 && Math.abs(lPoint.y-wolfPosition.y)==2){
                        //处理跳跃吃羊逻辑
                        boolean invalidate = false;
                        switch (lDirection){
                            case UP:
                                invalidate = isWolfEatASheep(wolfPosition.x,wolfPosition.y-1);
                                break;
                            case DOWN:
                                invalidate = isWolfEatASheep(wolfPosition.x,wolfPosition.y+1);
                                break;
                            case LEFT:
                                invalidate = isWolfEatASheep(wolfPosition.x-1,wolfPosition.y);
                                break;
                            case RIGHT:
                                invalidate = isWolfEatASheep(wolfPosition.x+1,wolfPosition.y);
                                break;
                            case UP_LEFT:
                                invalidate = isWolfEatASheep(wolfPosition.x-1,wolfPosition.y-1);
                                break;
                            case UP_RIGHT:
                                invalidate = isWolfEatASheep(wolfPosition.x+1,wolfPosition.y-1);
                                break;
                            case DOWN_LEFT:
                                invalidate = isWolfEatASheep(wolfPosition.x-1,wolfPosition.y+1);
                                break;
                            case DOWN_RIGHT:
                                invalidate = isWolfEatASheep(wolfPosition.x+1,wolfPosition.y+1);
                                break;
                        }

                        if(invalidate){
                            for (int i = 0; i < sCoordses.length; i++) {
                                for (int j = 0; j < sCoordses[i].length; j++) {
                                    Coords cCoords = sCoordses[i][j];
                                    if (cCoords.isChessIn(wolf)) {
                                        cCoords.removeChess(wolf);
                                    }
                                }
                            }
                            PointF mp = lCoords.getPointF();
                            wolf.setPointF(mp);
                            lCoords.addChess(wolf);
                            actionType = ChessType.SHEEP;
                            postInvalidate();
                        }
                    }
                } else {
                    if (sheepSelectd == -1) {
                        sheepSelectd = getSheepSelectd(pMotionEvent.getX(), pMotionEvent.getY());
                        if (sheepSelectd != -1) {
                            postInvalidate();
                        }
                    } else {
                        int selctid = getSheepSelectd(pMotionEvent.getX(), pMotionEvent.getY());
                        if (selctid != -1 && selctid != sheepSelectd) {
                            sheepSelectd = selctid;
                            postInvalidate();
                            break;
                        }
                        Point lPoint = getNearestPoint(pMotionEvent.getX(), pMotionEvent.getY());
                        Coords lCoords = sCoordses[lPoint.x][lPoint.y];
                        if (lCoords.isHasChess()) {
                            break;
                        } else {
                            Chess lChess = sheep[sheepSelectd];
                            Point curPoint =  getChessPosition(lChess);
                            StepDirection lDirection = getDirection(curPoint, lPoint);

                            Coords curCoords = sCoordses[curPoint.x][curPoint.y];
                            if (!curCoords.hasDirection(lDirection)) {
                                break;
                            }
                            if(lPoint.x-curPoint.x == 0 && Math.abs(lPoint.y-curPoint.y)==1
                                    || Math.abs(lPoint.x-curPoint.x) == 1 && lPoint.y-curPoint.y==0
                                    || Math.abs(lPoint.x-curPoint.x) == 1 && Math.abs(lPoint.y-curPoint.y)==1) {
                                for (int i = 0; i < sCoordses.length; i++) {
                                    for (int j = 0; j < sCoordses[i].length; j++) {
                                        Coords cCoords = sCoordses[i][j];
                                        if (cCoords.isChessIn(lChess)) {
                                            cCoords.removeChess(lChess);
                                        }
                                    }
                                }
                                PointF mp = lCoords.getPointF();
                                lCoords.addChess(lChess);
                                lChess.setPointF(mp);
                                sheepSelectd = -1;
                                actionType = ChessType.WOLF;
                                postInvalidate();
                            }
                        }
                    }

                }
                break;
        }
        return true;
    }
    /**坐标点上是否有羊可以吃*/
    private boolean isWolfEatASheep(int x,int y){
        Coords mCoords = sCoordses[x][y];
        if (!mCoords.isHasChess()) {
            return false;
        }
        Chess mChess = mCoords.getChess();
        if(mChess!=null){
            mChess.setLive(false);
        }
        mCoords.removeChess(mChess);
        return true;
    }
    /**判断点击坐标是否在棋子（羊，狼只有一个，不用选中）上面*/
    private int getSheepSelectd(float x, float y) {
        int select = -1;
        for (int i = 0; i < sheepCount; i++) {
            Chess lChess = sheep[i];
            if(!lChess.isLive){
                continue;
            }
            PointF lPointF = lChess.getPointF();
            //圆心坐标
            float vCenterX = lPointF.x;
            float vCenterY = lPointF.y;

            //点击位置x坐标与圆心的x坐标的距离
            float distanceX = Math.abs(vCenterX - x);
            //点击位置y坐标与圆心的y坐标的距离
            float distanceY = Math.abs(vCenterY - y);
            //点击位置与圆心的直线距离
            float distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

            //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
            if (distanceZ < (padding-15)) {
                select = i;
                break;
            }
        }
        return select;
    }
    /**获取棋子在坐标点上的位置*/
    private Point getChessPosition(Chess pChess) {
        Point lPoint = new Point();
        for (int i = 0; i < sCoordses.length; i++) {
            for (int j = 0; j < sCoordses[i].length; j++) {
                Coords lCoords = sCoordses[i][j];
                if(lCoords.isChessIn(pChess)){
                    lPoint.set(i,j);
                    break;
                }
            }
        }
        return lPoint;
    }

    /**获取点击坐标最近的坐标点,
     * @return sCoordses 数组的坐标
     * */
    private Point getNearestPoint(float x, float y) {
        int lst = -1;
        Point lPoint = new Point();
        for (int i = 0; i < sCoordses.length; i++) {
            for (int j = 0; j < sCoordses[i].length; j++) {
                Coords lCoords = sCoordses[i][j];
                PointF p = lCoords.getPointF();
                RectF rf = new RectF(p.x, p.y, x, y);
                int xc = (int) Math.abs(Math.sqrt(rf.width() * rf.width() + rf.height() * rf.height()));
                if (lst == -1) {
                    lst = xc;
                    lPoint.set(i, j);
                } else {
                    if (xc < lst) {
                        lst = xc;
                        lPoint.set(i, j);
                    }
                }
            }
        }
        return lPoint;
    }
    /**通过两个坐标来判断棋子移动方向*/
    private StepDirection getDirection(Point src, Point dst) {
        if (dst.x - src.x == 0 && dst.y - src.y >= 1) {
            return StepDirection.DOWN;
        } else if (dst.x - src.x == 0 && dst.y - src.y <= -1) {
            return StepDirection.UP;
        } else if (dst.y - src.y == 0 && dst.x - src.x >= 1) {
            return StepDirection.RIGHT;
        } else if (dst.y - src.y == 0 && dst.x - src.x <= -1) {
            return StepDirection.LEFT;
        } else if (dst.x - src.x >= 1 && dst.y - src.y >= 1) {
            return StepDirection.DOWN_RIGHT;
        } else if (dst.x - src.x >= 1 && dst.y - src.y <= -1) {
            return StepDirection.UP_RIGHT;
        } else if (dst.x - src.x <= -1 && dst.y - src.y >= 1) {
            return StepDirection.DOWN_LEFT;
        } else /*if(dst.x -src.x<=-1 && dst.y-src.y<=-1)*/ {
            return StepDirection.UP_LEFT;
        }
    }

    public enum ChessType {
        SHEEP, WOLF
    }

    private class Chess {
        /**在棋盘上的坐标位置*/
        private PointF xPointF;
        private ChessType type;
        /**是否还活着*/
        private boolean isLive = true;
        public Chess(ChessType pType, PointF pPointF) {
            xPointF = pPointF;
            type = pType;
        }
        public PointF getPointF() {
            return xPointF;
        }
        public void setPointF(PointF pPointF) {
            xPointF = pPointF;
        }

        public boolean isLive() {
            return isLive;
        }

        public void setLive(boolean pLive) {
            isLive = pLive;
        }
    }
    /**坐标点类*/
    private class Coords {
        /**坐标点的实际坐标**/
        private PointF xPointF;
        private boolean hasChess = false;
        private List<Chess> xChesses = new ArrayList<>();
        private StepDirection[] xDirections;

        public Coords(PointF pPointF, StepDirection... pDirections) {
            xPointF = pPointF;
            xDirections = pDirections;
        }

        private boolean hasDirection(StepDirection pDirection) {
            if (xDirections != null) {
                for (StepDirection lDirection : xDirections) {
                    if (lDirection.equals(pDirection)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public PointF getPointF() {
            return xPointF;
        }

        public void setPointF(PointF pPointF) {
            xPointF = pPointF;
        }

        public int getChessNum() {
            return xChesses.size();
        }
        /**是否有棋子在坐标上*/
        public boolean isChessIn(Chess pChess) {
            for (Chess lChess : xChesses) {
                if (lChess.equals(pChess)) {
                    return true;
                }
            }
            return false;
        }
        /**坐标点上是否已经有棋子存在*/
        public boolean isHasChess() {
            return xChesses.size() != 0;
        }
        /**初始化坐标的时候，有4个点 包含5个棋子*/
        public void addChess(Chess pChess) {
            xChesses.add(pChess);
        }
        /**移除棋子*/
        public void removeChess(Chess pChess) {
            xChesses.remove(pChess);
        }
        public Chess getChess(){
            if(xChesses==null){
                return null;
            }
            return xChesses.get(0);
        }
        public List<Chess> getChesses(){
            if(xChesses==null){
                return null;
            }
            return xChesses;
        }
        public void clearChess() {
            xChesses.clear();
        }
    }
    /**棋子走动方向*/
    private enum StepDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UP_LEFT,
        UP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT;
    }
    public enum ChessState{
        GAME_LOADING,GAME_READY,GAME_PLAYING,GAME_OVER
    }
}
