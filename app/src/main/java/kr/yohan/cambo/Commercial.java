package kr.yohan.cambo;

import java.util.Calendar;

/**
 * Created by 3330MT on 2017-01-03.
 */

public class Commercial {
    int PaddingMin, RealMin, RealSec, SleepTime, CurrentCmNo;
    final int CMCOUNT = 6; //CM 광고 갯수
    CmListener sl;
    boolean flag;

    public void onStart(CmListener listener) {
        this.sl = listener;
        flag = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
            Calendar cal = Calendar.getInstance();
            RealMin = cal.get(Calendar.MINUTE);
            RealSec = cal.get(Calendar.SECOND);
            // 처음 재생시간.
            SleepTime = ((60 - RealSec) % 30) * 1000;
            if (0 <= RealSec && RealSec <= 29) {
                PaddingMin = 0;
            } else {
                PaddingMin = 1;
            }
            //절대시간으로 광고선택
            CurrentCmNo = (2 * (RealMin)) % CMCOUNT + PaddingMin;
            while (flag) {
                //ImageView에 광고 삽입
                sl.setCommercial(CurrentCmNo);
                //다음 30초후 CmNO 1증가
                CurrentCmNo = (CurrentCmNo+1) % CMCOUNT;
                try {
                    Thread.sleep(SleepTime);
                    // 30초로 초기화.
                    SleepTime = 30000;
                } catch (Exception ex) {}
            }
            }
        }).start();
    }

    public void onStop() {
        flag = false;
    }

}

interface CmListener {
    void setCommercial(final int CurrentCmNo);
}

