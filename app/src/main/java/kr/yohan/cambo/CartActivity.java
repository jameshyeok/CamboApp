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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CartActivity extends Activity {
    public static final int COST_TIME = 40;
    public static final int COST_MARK = 20;
    public static final int COST_ERASE = 20;

    UserGameInfo userGameInfo = UserGameInfo.getInstance();
    int gameScore, gameMoney, gameCount;
    int[] gameItems = new int[3];   // [0]:gameItem0(시간 연장), [1]:gameItem1(매칭 표시), [2]:gameItem2(매칭 지우기)
    int[] gameChars = new int[4];   // [0]:gameChar, [1]:avatarChar, [2]:scoreChar, [3]:moneyChar
    int gameMode, gameLevel;

    ImageView avatarImgView, levelImgView;
    TextView GameMoney, scoreText;
    TextView numberI0, numberI1, numberI2;
    ImageView item0, item1, item2;
    ImageButton increaseI0, increaseI1, increaseI2;
    RadioGroup levelSelect;
    RadioButton levelButton1, levelButton2, levelButton3;
    ImageButton ResetButton, OkButton, ExitButton, CancelButton;
    ImageView cmView;
    Commercial cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태 바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 타이틀 바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cart);

        avatarImgView = (ImageView) findViewById(R.id.avatarImgView);
        GameMoney = (TextView) findViewById(R.id.moneyTextView);
        scoreText = (TextView) findViewById(R.id.scoreTextView);
        levelImgView = (ImageView) findViewById(R.id.levelImgView);

        gameCount = userGameInfo.getGameCount();
        gameScore = userGameInfo.getGameScore();
        gameMoney = userGameInfo.getGameMoney();
        gameItems = userGameInfo.getGameItems();
        gameChars = userGameInfo.getGameChars();
        gameLevel = userGameInfo.getGameLevel();

        levelSelect = (RadioGroup) findViewById(R.id.levelSelect);

        levelButton1 = (RadioButton) findViewById(R.id.levelButton1);
        levelButton2 = (RadioButton) findViewById(R.id.levelButton2);
        levelButton3 = (RadioButton) findViewById(R.id.levelButton3);

        // TextView
        item0 = (ImageView) findViewById(R.id.item0);
        numberI0 = (TextView) findViewById(R.id.numberI0);
        item1 = (ImageView) findViewById(R.id.item1);
        numberI1 = (TextView) findViewById(R.id.numberI1);
        item2 = (ImageView) findViewById(R.id.item2);
        numberI2 = (TextView) findViewById(R.id.numberI2);

        // increase button
        increaseI0 = (ImageButton) findViewById(R.id.increaseI0);
        increaseI1 = (ImageButton) findViewById(R.id.increaseI1);
        increaseI2 = (ImageButton) findViewById(R.id.increaseI2);

        numberI0.setText(String.valueOf(gameItems[0]));
        numberI1.setText(String.valueOf(gameItems[1]));
        numberI2.setText(String.valueOf(gameItems[2]));

        cmView = (ImageView) findViewById(R.id.cmView);
        cm = new Commercial();

        levelSelect.clearCheck();
        switch (gameLevel) {
            case 1:
                levelSelect.check(R.id.levelButton1);
                break;
            case 2:
                levelSelect.check(R.id.levelButton2);
                break;
            case 3:
                levelSelect.check(R.id.levelButton3);
                break;
        }

        levelSelect.setOnCheckedChangeListener(lRadioCheck);

        levelButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelSelect.clearCheck();
                levelSelect.check(R.id.levelButton1);
                gameLevel = 1;
            }
        });

        levelButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelSelect.clearCheck();
                levelSelect.check(R.id.levelButton2);
                gameLevel = 2;
            }
        });

        levelButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelSelect.clearCheck();
                levelSelect.check(R.id.levelButton3);
                gameLevel = 3;
            }
        });

        increaseI0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (gameMoney >= COST_TIME) {
                    gameMoney -= COST_TIME;
                    gameItems[0]++;
                    numberI0.setText(String.valueOf(gameItems[0]));
                    GameMoney.setText(String.valueOf(gameMoney));
                }
            }
        });

        increaseI1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (gameMoney >= COST_MARK) {
                    gameMoney -= COST_MARK;
                    gameItems[1]++;
                    numberI1.setText(String.valueOf(gameItems[1]));
                    GameMoney.setText(String.valueOf(gameMoney));
                }
            }
        });

        increaseI2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (gameMoney >= COST_ERASE) {
                    gameMoney -= COST_ERASE;
                    gameItems[2]++;
                    numberI2.setText(String.valueOf(gameItems[2]));
                    GameMoney.setText(String.valueOf(gameMoney));
                }
            }
        });

        // Reset button
        ResetButton = (ImageButton) findViewById(R.id.reset);
        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameCount = gameScore = gameChars[1] = 0;
                int avatarResID = getResources().getIdentifier("a"+gameChars[1], "drawable", getPackageName());
                avatarImgView.setImageResource(avatarResID);
                scoreText.setText(String.valueOf(gameScore));
            }
        });

        // OK button
        OkButton = (ImageButton) findViewById(R.id.ok_button);
        OkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                userGameInfo.setGameCount(gameCount);
                userGameInfo.setGameScore(gameScore);
                userGameInfo.setGameMoney(gameMoney);
                userGameInfo.setGameItems(gameItems);
                userGameInfo.setGameChars(gameChars);
                userGameInfo.setGameLevel(gameLevel);
                cm.onStop();//cm thread 종료
                finish();
            }
        });

        // Exit button
        ExitButton = (ImageButton) findViewById(R.id.exit_button);
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                cm.onStop();//cm thread 종료
                finish();
            }
        });

        //Cancel button
        CancelButton = (ImageButton) findViewById(R.id.cancel_button);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameCount = userGameInfo.getGameCount();
                gameScore = userGameInfo.getGameScore();
                gameMoney = userGameInfo.getGameMoney();
                gameItems = userGameInfo.getGameItems();
                gameChars = userGameInfo.getGameChars();
                gameLevel = userGameInfo.getGameLevel();

                int avatarResID = getResources().getIdentifier("a"+gameChars[1], "drawable", getPackageName());
                avatarImgView.setImageResource(avatarResID);
                scoreText.setText(String.valueOf(gameScore));
                GameMoney.setText(String.valueOf(gameMoney));
                numberI0.setText(String.valueOf(gameItems[0]));
                numberI1.setText(String.valueOf(gameItems[1]));
                numberI2.setText(String.valueOf(gameItems[2]));

                levelSelect.clearCheck();
                switch (gameLevel) {
                    case 1:
                        levelSelect.check(R.id.levelButton1);
                        break;
                    case 2:
                        levelSelect.check(R.id.levelButton2);
                        break;
                    case 3:
                        levelSelect.check(R.id.levelButton3);
                        break;
                }
            }
        });

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

    // 정보보기 버튼  2017/6/30 by Shin
    public void onButtonAboutClicked(View v) {
        // 대화상자로 구현
        LayoutInflater inflater = LayoutInflater.from(CartActivity.this);
        final View aboutView = inflater.inflate(R.layout.dialog_about, null);
        final AlertDialog dialog = new AlertDialog.Builder(CartActivity.this).create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(aboutView);
        dialog.show();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }     // 2017/6/30 by Shin

    @Override
    protected void onResume() {
        super.onResume();
        gameScore = userGameInfo.getGameScore();
        gameMoney = userGameInfo.getGameMoney();
        gameItems = userGameInfo.getGameItems();
        gameChars = userGameInfo.getGameChars();
        gameLevel = userGameInfo.getGameLevel();

        int avatarResID = getResources().getIdentifier("a"+gameChars[1], "drawable", getPackageName());
        avatarImgView.setImageResource(avatarResID);
        GameMoney.setText(String.valueOf(gameMoney));
        scoreText.setText(String.valueOf(gameScore));
        int levelResID = getResources().getIdentifier("level"+gameLevel, "drawable", getPackageName());
        levelImgView.setImageResource(levelResID);
    }

    RadioGroup.OnCheckedChangeListener lRadioCheck = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(group.getId() == R.id.levelSelect) {
                switch(checkedId) {
                    case R.id.levelButton1:
                        gameLevel = 1;
                        break;
                    case R.id.levelButton2:
                        gameLevel = 2;
                        break;
                    case R.id.levelButton3:
                        gameLevel = 3;
                        break;
                }
            }
        }
    };

}
