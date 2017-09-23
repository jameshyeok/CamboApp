package kr.yohan.cambo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

import static kr.yohan.cambo.SelectActivity.BRONZE_SCORE;
import static kr.yohan.cambo.SelectActivity.GOLD_SCORE;
import static kr.yohan.cambo.SelectActivity.REQUEST_CODE_CART;
import static kr.yohan.cambo.SelectActivity.SILVER_SCORE;
import static kr.yohan.cambo.SelectActivity.TROPHY_SCORE;

public class Play1Activity extends Activity {

    final int SHOW_TIME = 4;
    final int PLAY_TIME = 4;

    public static int SINGLE_MODE = 25;
    public static final int MAX_GRID = 25;      // 최대 격자 수 (L1: 9, L2: 16, L3: 25)

    UserGameInfo userGameInfo = UserGameInfo.getInstance();
    int gameScore, gameMoney, gameCount;
    int[] gameItems = new int[3];   // [0]:gameItem0(시간 연장), [1]:gameItem1(매칭 표시), [2]:gameItem2(매칭 지우기)
    int[] gameChars = new int[4];   // [0]:gameChar, [1]:avatarChar, [2]:scoreChar, [3]:moneyChar
    int gameLevel;
    int pan = 0, solDp = 0, playDp = 0;
    String[] img;
    boolean[] mch;
    int[] smch;
    int showTime = SHOW_TIME;
    int playTime = PLAY_TIME;
    int[] types = new int[2];
    int mchResId;
    // types[0]: 타입(1~7) - 0 제외, types[1]: # of marks
    // 타입: 1(좌우대칭), 2(상하대칭), 3(90˚CCW회전), 4(180˚회전), 5(270˚회전), 6(좌하\대칭), 7(우하/대칭)
    TableRow.LayoutParams solRowLayout;
    TableRow.LayoutParams playRowLayout;

    @Bind(R.id.avatarImgView)
    ImageView avatarImgView;
    @Bind(R.id.moneyTextView)
    TextView moneyText;
    @Bind(R.id.scoreTextView)
    TextView scoreText;
    @Bind(R.id.timeTextView)
    TextView timeText;
    @Bind(R.id.levelImgView)
    ImageView levelImgView;

    @Bind(R.id.btnTimeExtension)
    ImageButton btnTimeExtension;
    @Bind(R.id.btnMark)
    ImageButton btnMark;
    @Bind(R.id.btnErase)
    ImageButton btnErase;
    @Bind(R.id.btnStart)
    ImageButton btnStart;
    @Bind(R.id.btnClose)
    ImageButton btnClose;
    @Bind(R.id.btnCart)
    ImageButton btnCart;
    @Bind(R.id.record_button)
    ImageButton btnRecord;
    @Bind(R.id.btnNxtStage)
    ImageButton btnNxtStage;
    @Bind(R.id.typeImg1)
    ImageView typeImg1;
    @Bind(R.id.typeImg2)
    ImageView typeImg2;

    @Bind(R.id.solTable)
    TableLayout solTable;
    @Bind(R.id.playTable)
    TableLayout playTable;
    CardObj[] solCardObj, playCardObj;
    ImageView[][] solCardImgView;
    ImageView[] playCardImgView;
    TimeCheckTask timeCheckTask;
    PlayGame playGame;
    UserItem item;
    @Bind(R.id.cmView)
    ImageView cmView;    // 5/17 shin
    Commercial cm;                          // 5/17 shin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play1);
        ButterKnife.bind(this);
        mchResId = getResources().getIdentifier("c2","drawable",getPackageName());
        cm = new Commercial();              // 5/17 shin
        gameScore = userGameInfo.getGameScore();
        gameMoney = userGameInfo.getGameMoney();
        gameItems = userGameInfo.getGameItems();
        gameChars = userGameInfo.getGameChars();
        gameLevel = userGameInfo.getGameLevel();
        gameCount = userGameInfo.getGameCount();

        timeCheckTask = new TimeCheckTask();
        timeCheckTask.execute();

        item = new UserItem(getApplicationContext());

        // 시간연장 버튼
        btnTimeExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.getCheckValue(gameItems[0], isPlayTimeFlag);
                playTime = item.extendTimeItem(timeCheckTask, playTime);
                gameItems[0] = item.useItem();
                userGameInfo.setGameItems(gameItems);
            }
        });

        // 마크 아이템
        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame.setMarkCards(true);
                item.getCheckValue(gameItems[1], isPlayTimeFlag);
                gameItems[1] = item.useItem();
                userGameInfo.setGameItems(gameItems);
            }
        });

        //주우기 아이템
        btnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame.setEraseCards(true);
                item.getCheckValue(gameItems[2], isPlayTimeFlag);
                gameItems[2] = item.useItem();
                userGameInfo.setGameItems(gameItems);
            }
        });

        // 바로시작
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime = 0;
                timeCheckTask.onProgressUpdate(playTime);
            }
        });

        // 게임 종료
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeCheckTask.cancel(true);
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                cm.onStop();//cm thread 종료  // 5/17 shin
                finish();
            }
        });

        // 카트
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlayTimeFlag) {   // stageFlag &&
                    Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_CART);
                }
            }
        });

        // 다음 스테이지 진행
        btnNxtStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNxtStage.setEnabled(false);
                timeCheckTask = new TimeCheckTask();
                timeCheckTask.execute();
            }
        });

        //cm thread 시작  // 5/17 shin
        cm.onStart(new CmListener() {
            @Override
            public void setCommercial(final int CurrentCmNo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int resID = getResources().getIdentifier("ycm" + CurrentCmNo, "drawable", getPackageName());
                        cmView.setBackgroundResource(resID);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameScore = userGameInfo.getGameScore();
        gameMoney = userGameInfo.getGameMoney();
        gameChars = userGameInfo.getGameChars();
        gameLevel = userGameInfo.getGameLevel();

        int avatarResID = getResources().getIdentifier("a" + gameChars[1], "drawable", getPackageName());
        avatarImgView.setImageResource(avatarResID);
        moneyText.setText(String.valueOf(gameMoney));
        scoreText.setText(String.valueOf(gameScore));
        int levelResID = getResources().getIdentifier("level" + gameLevel, "drawable", getPackageName());
        levelImgView.setImageResource(levelResID);
    }

    private ImageView[][] tableGrid(int pan, int dp, TableLayout table, TableRow.LayoutParams rowLayout, CardObj[] cardObj, boolean isPlayTable) {
        TableRow row[] = new TableRow[pan];
        ImageView cardImg[][] = new ImageView[pan][pan];
        int px = dpToPx(dp);
        int index = 0;
        for (int tr = 0; tr < pan; tr++) {
            row[tr] = new TableRow(getBaseContext());
            for (int td = 0; td < pan; td++, index++) {
                cardImg[tr][td] = new ImageView(getBaseContext());
                String tmp = "";
                int resID;
                if(isPlayTable)
                    tmp = "c0";
                else
                    tmp = img[index];
                resID = getResources().getIdentifier(tmp, "drawable", getPackageName());
                Resources resources = getResources();
                Bitmap image = BitmapFactory.decodeResource(resources, resID);
                Bitmap resized = Bitmap.createScaledBitmap(image, px, px, true);
                cardImg[tr][td].setEnabled(true);
                cardImg[tr][td].setImageBitmap(resized);
                cardImg[tr][td].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if(isPlayTable)
                    cardObj[index] = new CardObj(cardImg[tr][td], getApplicationContext(), index, mch[index], resID, playGame, px, resources, isPlayTable, mchResId);
                row[tr].addView(cardImg[tr][td]);
            }
            table.addView(row[tr], rowLayout);
        }
        return cardImg;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    //     초기설정 함수
    public void init() {
        playGame = new PlayGame(getApplicationContext(), scoreText);
        showTime = SHOW_TIME;
        playTime = PLAY_TIME;
        playGame.setShowTimeFlag(isShowTimeFlag = true);
        playGame.setPlayTimeFlag(isPlayTimeFlag = false);
        // 랜덤으로 이미지 생성
        RandomMatrix rm = new RandomMatrix();
        switch (gameLevel) {
            case 1:
                pan = 3;
                solDp = 40;
                playDp = 66;
                break;
            case 2:
                pan = 4;
                solDp = 30;
                playDp = 50;
                break;
            case 3:
                pan = 5;
                solDp = 24;
                playDp = 40;
        }
        img = new String[pan * pan];
        mch = new boolean[pan * pan];
        smch = new int[pan * pan];
        SINGLE_MODE = pan * pan;
        // 카드 오브젝트, 카드 이미지 객체 생성
        solCardObj = new CardObj[pan * pan];
        playCardObj = new CardObj[pan * pan];
        solCardImgView = new ImageView[pan][pan];
        playCardImgView = new ImageView[pan * pan];
        solRowLayout =  new TableRow.LayoutParams();
        playRowLayout = new TableRow.LayoutParams();
        // 레벨 1: type1, type2
        // 레벨 2: type3, type4, type5
        // 레벨 3: type3, type4, type5, type6, type7
        // types[0]: 1(좌우대칭), 2(상하대칭), 3(90˚CCW회전), 4(180˚회전), 5(270˚회전), 6(좌하\대칭), 7(우하/대칭)
        // types[1]: number of marks
        types = rm.randomMatrix(gameLevel, img, mch, smch); // img: 이미지 파일명(String), mch: 정답(boolean)
        //        타입 이미지 변경
        String typeFileName = "type" + types[0];
        int typeImgResId = getResources().getIdentifier(typeFileName, "drawable", getPackageName());
        typeImg1.setImageResource(typeImgResId);
        typeImg2.setImageResource(typeImgResId);
        solCardImgView = tableGrid(pan, solDp, solTable, solRowLayout, solCardObj, false);
        tableGrid(pan, playDp, playTable, playRowLayout, playCardObj, true);
        playGame.setConfig(playCardObj, solCardImgView, smch, types[1], getResources(), mchResId, dpToPx(solDp), pan, getResources().getIdentifier("c0","drawable",getPackageName()));
        disableAllCardObj(playCardObj);
//        tableGrid(pan,playDp, playTable, playRowLayout);
//        disableAllCardObj(solCardObj);
    }//init()

    //    모든 카드를 뒤집음
    public void hideAllCardObj(CardObj[] cardObj) {
        for (int index = 0; index < SINGLE_MODE; index++) {
            cardObj[index].touchEventGeneration();
        }
    }//hideCardObject()

    //    모든 카드 이미지 사용 disable
    public void disableAllCardObj(CardObj[] cardObj) {
        for (int index = 0; index < SINGLE_MODE; index++) {
            cardObj[index].disable();
        }
    }//disableAllCardObj

    //     모든 카드 이미지 사용 enable
    public void enableAllCardObj(CardObj[] cardObj) {
        for (int index = 0; index < SINGLE_MODE; index++) {
            cardObj[index].enable();
        }
    } //enableAllCardObj

    @Override
    protected void onUserLeaveHint() {
        timeCheckTask.cancel(true);
        super.onUserLeaveHint();
    }

    public void onBackPressed() {
        timeCheckTask.cancel(true);
        super.onBackPressed();
    }


    // 결과보기 버튼  2017/6/29 by Shin
    public void onButtonRecordClicked(View v) {
        // 대화상자로 구현
        LayoutInflater inflater = LayoutInflater.from(Play1Activity.this);
        final View recordView = inflater.inflate(R.layout.dialog_record, null);
        final AlertDialog dialog = new AlertDialog.Builder(Play1Activity.this).create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(recordView);
        final ImageView trophy = (ImageView) recordView.findViewById(R.id.trophy);
        trophy.setVisibility(View.INVISIBLE);
        final ImageView gold = (ImageView) recordView.findViewById(R.id.gold);
        gold.setVisibility(View.INVISIBLE);
        final ImageView silver = (ImageView) recordView.findViewById(R.id.silver);
        silver.setVisibility(View.INVISIBLE);
        final ImageView bronze = (ImageView) recordView.findViewById(R.id.bronze);
        bronze.setVisibility(View.INVISIBLE);
        final ImageView laurel = (ImageView) recordView.findViewById(R.id.laurel);
        laurel.setVisibility(View.INVISIBLE);

        int gameCount = userGameInfo.getGameCount();
        if (gameCount == 0) {
            gameCount = 1;
        }
        float record = userGameInfo.getGameScore() / gameCount;
        if (TROPHY_SCORE <= record) {
            trophy.setVisibility(View.VISIBLE);
        } else if (GOLD_SCORE <= record && record < TROPHY_SCORE) {
            gold.setVisibility(View.VISIBLE);
        } else if (SILVER_SCORE <= record && record < GOLD_SCORE) {
            silver.setVisibility(View.VISIBLE);
        } else if (BRONZE_SCORE <= record && record < SILVER_SCORE) {
            bronze.setVisibility(View.VISIBLE);
        } else {
            laurel.setVisibility(View.VISIBLE);
        }
        dialog.show();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }     // 2017/6/29 by Shin

    int tmpPanMchCnt;
    boolean isShowTimeFlag = true;
    boolean isPlayTimeFlag = false;

    class TimeCheckTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            init();
            btnCart.setEnabled(false);          // 2017-06-30 by Shin
            btnRecord.setEnabled(false);        // 2017-06-30 by Shin
            btnNxtStage.setEnabled(false);
            btnStart.setEnabled(true);
            scoreText.setText(Integer.toString(playGame.getGameScore()));
            timeText.setText(Integer.toString(showTime));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            timeText.setText("END");
            btnCart.setEnabled(true);          // 2017-06-30 by Shin
            btnRecord.setEnabled(true);        // 2017-06-30 by Shin
            btnNxtStage.setEnabled(true);
            btnStart.setEnabled(false);
            disableAllCardObj(playCardObj);
            //disableAllCardObj(solCardObj);
            //playGame.setScoreStageClearFlag();
            //playGame.updateScore();
            userGameInfo.setGameScore(playGame.getGameScore());
            userGameInfo.setGameMoney(gameMoney);
            userGameInfo.setGameItems(gameItems);
            userGameInfo.setGameCount(++gameCount);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            timeText.setText(Integer.toString(values[0]));
        }

        @Override
        protected void onCancelled() {
            timeText.setText("END");
            //btnNxtStage.setEnabled(true);
            //disableAllCardObj();
            //playGame.setScoreStageClearFlag();
            //playGame.updateScore();
            //userGameInfo.save();      // 6/1 shin
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            while (!isCancelled()) {
                if (isShowTimeFlag) {
                    if (showTime <= 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                setListener();
                                //enableAllCardObj(solCardObj);
                                //hideAllCardObj(solCardObj);
                                //disableAllCardObj(solCardObj);
                                enableAllCardObj(playCardObj);
                            }
                        });
                        playGame.setShowTimeFlag(isShowTimeFlag = false);
                        playGame.setPlayTimeFlag(isPlayTimeFlag = true);
                    }
                    publishProgress(showTime--);
                } //if(isShowTimeFlag)
                else if (isPlayTimeFlag) {
                    if (playTime <= 0 || playGame.getMchCnt() <= 0) {
                        playGame.setPanMchCnt(playGame.getPanMchCnt() + 1);
                        playGame.setPlayTimeFlag(isPlayTimeFlag = false);
                        tmpPanMchCnt = playGame.getPanMchCnt();
                        break;
                    }
                    publishProgress(playTime--);
                }//if(isPlayTimeFlag)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            return playTime;
        }
    } //TimeCheckTask

}
