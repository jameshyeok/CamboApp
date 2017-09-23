package kr.yohan.cambo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayGame {
    private Context context;
    private CardObj[] playCardObj;
    private TextView scoreText;
    private ImageView [][] solCardImgView;
    private Resources resources;
    private int resID;
    private  int eraseResId;
    private int px;
    private int pan;
    protected int mchCnt = 0;
    protected int stageScore = 0;
    protected int matchingPoint;
    protected int gameScore;
    protected int panMchCnt = 0;
    protected int [] smch;
    protected boolean scoreStageClearFlag = false;
    protected boolean stageFlag = false;
    protected boolean MarkCards = false;
    protected boolean EraseCards = false;
    private boolean isShowTimeFlag = true;
    private boolean isPlayTimeFlag = false;
    UserGameInfo userGameInfo;

    public PlayGame(Context context, TextView scoreText) {
        this.context = context;
        setInit(scoreText);
    }

    public void matching(int playIndx){
        // 매칭 성공
        if (playCardObj[playIndx].getMch()){
            playCardObj[playIndx].disable();
            mchCnt--;
            scoreText.setText(Integer.toString(gameScore += matchingPoint));
            if(MarkCards){
                int solIndx = smch[playIndx];
                int tr = solIndx / pan;
                int td = solIndx % pan;
                Bitmap image = BitmapFactory.decodeResource(resources, resID);
                Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                solCardImgView[tr][td].setImageBitmap(resized);
                solCardImgView[tr][td].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            else if(EraseCards){
                int solIndx = smch[playIndx];
                int tr = solIndx / pan;
                int td = solIndx % pan;
                Bitmap image = BitmapFactory.decodeResource(resources, eraseResId);
                Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                solCardImgView[tr][td].setImageBitmap(resized);
                solCardImgView[tr][td].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        }
    }

    public void setConfig(CardObj[] playCardObj, ImageView[][] solCardImgView, int[] smch, int mchCnt, Resources resources, int resID, int px, int pan, int eraseResId){
        this.playCardObj = playCardObj;
        this.solCardImgView = solCardImgView;
        this.smch = smch;
        this.mchCnt = mchCnt;
        this.resources = resources;
        this.resID = resID;
        this.px = px;
        this.pan = pan;
        this.eraseResId = eraseResId;
    }

    public void setInit(TextView scoreText) {
        this.stageScore = 0;
        this.panMchCnt = 0;
        this.stageFlag = false;
        this.scoreStageClearFlag = false;
        this.scoreText = scoreText;
        this.userGameInfo = UserGameInfo.getInstance();
        this.gameScore = userGameInfo.getGameScore();
        final int L1_POINT = 4;
        final int L2_POINT = 5;
        final int L3_POINT = 6;
        switch(userGameInfo.getGameLevel()) {
            case 1: matchingPoint = L1_POINT; break;
            case 2: matchingPoint = L2_POINT; break;
            case 3: matchingPoint = L3_POINT; break;
        }
    }

    public int getPanMchCnt() {
        return panMchCnt;
    }

    public void setPanMchCnt(int panMchCnt) {
        this.panMchCnt = panMchCnt;
    }

    public int getGameScore() {
        return gameScore;
    }

    public int getMchCnt() {
        return mchCnt;
    }

    public void setShowTimeFlag(boolean showTimeFlag) { this.isShowTimeFlag = showTimeFlag; }

    public boolean isPlayTimeFlag() {
        return isPlayTimeFlag;
    }

    public void setPlayTimeFlag(boolean playTimeFlag) {
        isPlayTimeFlag = playTimeFlag;
    }

    public void setMarkCards(boolean markCards) {
        MarkCards = markCards;
    }

    public void setEraseCards(boolean eraseCards) {
        EraseCards = eraseCards;
    }
}
