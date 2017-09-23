package kr.yohan.cambo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.markjmind.propose.Propose;
import com.markjmind.propose.listener.JwAnimatorListener;

public class CardObj {
    private ImageView cardImgView;
    private Context context;
    private Propose propose;
    private boolean mch;
    private int index;
    private int resID;
    private int px;
    private int mchResId;
    private Resources resources;
    private PlayGame playGame;
    private boolean wMatch = false;
    private boolean isPlayTable;
    long downTime = SystemClock.uptimeMillis();
    long eventTime = SystemClock.uptimeMillis();
    final MotionEvent downEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
    final MotionEvent upEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, 0, 0, 0);

    public CardObj(ImageView cardImgView, Context context, int index, boolean mch, int resID, PlayGame playGame, int px, Resources resources, boolean isPlayTable, int mchResId){
        this.cardImgView = cardImgView;
        this.context = context;
        this.index = index;
        this.mch = mch;
        this.resID = resID;
        this.playGame = playGame;
        this.px = px;
        this.resources = resources;
        this.isPlayTable = isPlayTable;
        this.mchResId = mchResId;
        propose = new Propose(this.context);
        addAnimListenerToImg();
    }

    public void addAnimListenerToImg(){
        ObjectAnimator front = ObjectAnimator.ofFloat(this.cardImgView, View.ROTATION_Y, 0, 90);
        ObjectAnimator back = ObjectAnimator.ofFloat(this.cardImgView, View.ROTATION_Y, -90, 0);
        front.setDuration(100);
        back.setDuration(100);
        if(!isPlayTable){
            front.addListener(new JwAnimatorListener() {
                @Override
                public void onStart(Animator animator) {
                    Bitmap image = BitmapFactory.decodeResource(resources, resID);
                    Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                    cardImgView.setImageBitmap(resized);
                    cardImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }

                @Override
                public void onEnd(Animator animator) {
                    Bitmap image = BitmapFactory.decodeResource(resources, R.drawable.c0);
                    Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                    cardImgView.setImageBitmap(resized);
                    cardImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }

                @Override
                public void onReverseStart(Animator animator) {
                    Bitmap image = BitmapFactory.decodeResource(resources, resID);
                    Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                    cardImgView.setImageBitmap(resized);
                    cardImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    cardImgView.setEnabled(false);
                }
                @Override
                public void onReverseEnd(Animator animator) {  }
            });
        }
        else {
            front.addListener(new JwAnimatorListener() {
                @Override
                public void onStart(Animator animator) {
                    Bitmap image = BitmapFactory.decodeResource(resources, resID);
                    Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                    cardImgView.setImageBitmap(resized);
                    cardImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }

                @Override
                public void onEnd(Animator animator) {
                    int imgResId = 0;
                    if(mch)
                        imgResId = mchResId;
                    else
                        imgResId = resID;
                    Bitmap image = BitmapFactory.decodeResource(resources, imgResId);
                    Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                    cardImgView.setImageBitmap(resized);
                    cardImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    if(playGame.isPlayTimeFlag()){
                        playGame.matching(index);
                    }
                }

                @Override
                public void onReverseStart(Animator animator) {
                    Bitmap image = BitmapFactory.decodeResource(resources, resID);
                    Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                    cardImgView.setImageBitmap(resized);
                    cardImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                    cardImgView.setEnabled(false);
                }
                @Override
                public void onReverseEnd(Animator animator) {
                    int imgResId = 0;
                    if(mch)
                        imgResId = mchResId;
                    else
                        imgResId = resID;
                    Bitmap image = BitmapFactory.decodeResource(resources, imgResId);
                    Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                    cardImgView.setImageBitmap(resized);
                    cardImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    if(playGame.isPlayTimeFlag()){
                        playGame.matching(index);
                    }
                }
            });
        }
        propose.motionRight.play(front).next(back);
        //propose.motionRight.setMotionDistance(500* Propose.getDensity(context));
        cardImgView.setOnTouchListener(propose);
    }//addAnimListenerToImg

    public int getIndex() {
        return index;
    }

    public boolean isMatched() { return wMatch; }
    public void setwMatch(boolean wMatch) { this.wMatch = wMatch; }
    public boolean getMch() { return mch; }
    public void setMch(boolean mch) { this.mch = mch; }

    public void touchEventGeneration(){
        cardImgView.setEnabled(true);
        cardImgView.dispatchTouchEvent(downEvent);
        cardImgView.dispatchTouchEvent(upEvent);
    }

    public void notMatchedEventGeneration(){
        cardImgView.setEnabled(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                touchEventGeneration();
            }
        }, 400);
    }

    // 카드 이미지 뷰 사용 가능
    public void enable(){
        this.cardImgView.setEnabled(true);
    }
    // 카드 이미지 뷰 사용 불가
    public void disable(){
        this.cardImgView.setEnabled(false);
    }
}
