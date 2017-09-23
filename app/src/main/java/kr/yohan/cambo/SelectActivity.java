package kr.yohan.cambo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectActivity extends Activity {
    public static final int REQUEST_CODE_PLAY = 1001;
    public static final int REQUEST_CODE_CART = 1002;
    public static final float TROPHY_SCORE = (float) 31.0;
    public static final float GOLD_SCORE = (float) 27.0;
    public static final float SILVER_SCORE = (float) 25.0;
    public static final float BRONZE_SCORE = (float) 21.0;

    UserGameInfo userGameInfo = UserGameInfo.getInstance();
    int gameScore, gameMoney;
    int[] gameItems = new int[3];   // [0]:gameItem0(시간연장), [1]:gameItem1(2장 보기), [2]:gameItem2(실패삭제)
    int[] gameChars = new int[4];   // [0]:gameChar, [1]:avatarChar, [2]:scoreChar, [3]:moneyChar
    int gameMode, gameLevel;

    ImageView avatarImgView, levelImgView;
    TextView moneyText, scoreText;
    ImageView cmView;
    Commercial cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태 바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 타이틀 바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select);

        avatarImgView = (ImageView) findViewById(R.id.avatarImgView);
        moneyText = (TextView) findViewById(R.id.moneyTextView);
        scoreText = (TextView) findViewById(R.id.scoreTextView);
        levelImgView = (ImageView) findViewById(R.id.levelImgView);

        cmView = (ImageView) findViewById(R.id.cmView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cm = new Commercial();
        //cm thread 시작
        cm.onStart(new CmListener() {
            @Override
            public void setCommercial(final int CurrentCmNo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int resID = getResources().getIdentifier("ycm"+CurrentCmNo, "drawable", getPackageName());
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

        int avatarResID = getResources().getIdentifier("a"+gameChars[1], "drawable", getPackageName());
        avatarImgView.setImageResource(avatarResID);
        moneyText.setText(String.valueOf(gameMoney));
        scoreText.setText(String.valueOf(gameScore));
        int levelResID = getResources().getIdentifier("level"+gameLevel, "drawable", getPackageName());
        levelImgView.setImageResource(levelResID);
    }

    /* 레벨선택 버튼 */
    public void onButton1Clicked(View v) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View levelView = layoutInflater.inflate(R.layout.dialog_level, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        final ImageView level1 = (ImageView) levelView.findViewById(R.id.level1);
        final ImageView level2 = (ImageView) levelView.findViewById(R.id.level2);
        final ImageView level3 = (ImageView) levelView.findViewById(R.id.level3);

        level1.setTag(new Integer(1));
        level2.setTag(new Integer(2));
        level3.setTag(new Integer(3));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cm.onStop();
                gameLevel = (int)v.getTag();
                userGameInfo.setGameLevel(gameLevel);
                Intent intent = new Intent(getApplicationContext(), Play1Activity.class);
                startActivityForResult(intent, REQUEST_CODE_PLAY);
                alertDialog.dismiss();
            }
        };
        level1.setOnClickListener(listener);
        level2.setOnClickListener(listener);
        level3.setOnClickListener(listener);

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(levelView);
        alertDialog.show();
    }

    /* 종료 버튼 */
    public void onButtonExitClicked(View v) {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);         // resultCode = -1
        cm.onStop();
        finish();
    }

    /* 설정 버튼 */
    public void onButtonConfigClicked(View v) {
        cm.onStop();
        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CART);
    }

    /* 결과보기 버튼 */
    public void onButtonRecordClicked(View v) {
        // 대화상자로 구현
        LayoutInflater inflater = LayoutInflater.from(SelectActivity.this);
        final View recordView = inflater.inflate(R.layout.dialog_record, null);
        final AlertDialog dialog = new AlertDialog.Builder(SelectActivity.this).create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(recordView);
        final ImageView trophy = (ImageView) recordView.findViewById(R.id.trophy); trophy.setVisibility(View.INVISIBLE);
        final ImageView gold = (ImageView) recordView.findViewById(R.id.gold); gold.setVisibility(View.INVISIBLE);
        final ImageView silver = (ImageView) recordView.findViewById(R.id.silver); silver.setVisibility(View.INVISIBLE);
        final ImageView bronze = (ImageView) recordView.findViewById(R.id.bronze); bronze.setVisibility(View.INVISIBLE);
        final ImageView laurel = (ImageView) recordView.findViewById(R.id.laurel); laurel.setVisibility(View.INVISIBLE);

        int gameCount = userGameInfo.getGameCount();
        if (gameCount == 0) { gameCount = 1; }
        float record = userGameInfo.getGameScore()/gameCount;
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
    }

    /* play 버튼 */
    public void onButtonPlayClicked(View v) {
        cm.onStop();
        Intent intent = new Intent(getApplicationContext(), Play1Activity.class);
        startActivityForResult(intent, REQUEST_CODE_PLAY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);         // resultCode = 0
        cm.onStop();
        finish();
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode==RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_PLAY:
                    break;
                case REQUEST_CODE_CART:
                    break;
            }
    }*/

}