package kr.yohan.cambo;

import android.content.Context;

/**
 * Created by 3330MT on 2017-01-12.
 */

public class UserItem {
    public final static int ADD_TIME = 8;
    private Context context;
    private boolean reqChecker;
    private boolean isPlayTime;
    private int gItem;

    public UserItem(Context context) {
        this.context = context;
        reqChecker = false;
    }

    // 시간 연장하는 아이템
    public int extendTimeItem(Play1Activity.TimeCheckTask timeCheckTask, int playTime){
        if(reqChecker){
            playTime+=ADD_TIME;
            timeCheckTask.onProgressUpdate(playTime);
        }
        return playTime;
    }


    // 게임 시간인지, 아이템이 있는지 확인
    public void reqCheck(){
        if(this.isPlayTime &&(this.gItem >0)){
            this.reqChecker = true;
            this.gItem--;
        }
        else
            this.reqChecker = false;
    }

    public void getCheckValue(int gItem, boolean isPlayTime){
        this.gItem = gItem;
        this.isPlayTime = isPlayTime;
        reqCheck();
    }

    public int useItem(){
        return  gItem;
    }
}
