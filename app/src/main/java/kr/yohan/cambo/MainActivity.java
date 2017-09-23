package kr.yohan.cambo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_SELECT = 1000;
    UserGameInfo userGameInfo;
    String phoneNo = "821234567890";
    String nickName = "Yohan";
    final int GAME_MONEY = 10;
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    final int AVATAR_LEVEL_SCORE = 150000;              // = 3,000*50

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태 바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 타이틀 바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        userGameInfo = UserGameInfo.getInstance();
        if (userGameInfo.getUid() == 0) {       // COZ API lower version (eg. kitkat)
            /*new CountDownTimer(1000, 1000) {  // 1초 delay
                @Override
                public void onTick(long millisUntilFinished) {
                }    // do nothing

                @Override
                public void onFinish() {
                }  // do nothing
            }.start();

            ImageView touchImg = (ImageView) findViewById(R.id.logo);
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(700);
            animation.setStartOffset(20);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setRepeatCount(Animation.INFINITE);
            touchImg.startAnimation(animation);*/

            // 네트워크 연결 여부 확인
            ConnectivityManager nm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = nm.getActiveNetworkInfo();
            if (ni != null && ni.isConnectedOrConnecting()) {
                checkPermission();
            } else {
                Toast.makeText(getApplicationContext(), " NETWORK error! ", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    } // onCreate

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                //userGameInfo = UserGameInfo.getInstance();
                int uid = userGameInfo.getUid();
                if (uid == 0) { finish(); break; }
                intent = new Intent(getApplicationContext(), SelectActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onTouchEvent(event);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                //폰 상태 정보 사용권한에 대한 콜백을 받음
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //권한 동의 버튼 선택
                    init();
                }
                else{
                    Toast.makeText(MainActivity.this, " 'ALLOW' to use! ", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }//onRequestPermissionsResult

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            //권한 없을 경우
            //최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
                //사용자가 임의로 권한을 취소시킨 경우
                //권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
            else{
                //최초로 권한을 요청하는 경우
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        }
        else {
            //사용 권한 있음 확인
            init();
        }
    }//checkPermission

    private void init() {
        //회원가입시 game 정보 자동생성
        //서버로부터 게임정보 로드 // String text = URLEncoder.encode( "요한기술", "UTF8");
        //get phone number (ex: +8210xxxxyyyy=010-xxxx-yyyy)
        TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNo = tm.getLine1Number();
        if(phoneNo == null || phoneNo.trim().equals("")) {
            phoneNo = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        new RestfulGetAPI(new OnCompletionListener() {
            @Override
            public void onComplete(String result) {
                try {
                    userGameInfo = new Gson().fromJson(result, UserGameInfo.class);
                    UserGameInfo.init(userGameInfo);
                    userGameInfo = UserGameInfo.getInstance();
                    int uid = userGameInfo.getUid();

                    int score = userGameInfo.getGameScore();
                    int avatarLevel = -1;
                    while (score >= 0) {
                        score -= AVATAR_LEVEL_SCORE;
                        avatarLevel++;
                    }
                    if (avatarLevel > 12) {
                        avatarLevel = 12;
                    } else if (avatarLevel < 0) {
                        avatarLevel = 0;
                    }
                    userGameInfo.setAvatarChar(avatarLevel);

                    if (uid != 0) {
                        // singleton: 점수, 게임머니, 캐릭터·아이템 배열, 최근 출석일 수신
                        SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String today = toFormat.format(new Date());
                        String date = userGameInfo.getGameDate();
                        if (!today.equals(date)) {
                            int gameMoney = userGameInfo.getGameMoney();
                            gameMoney += GAME_MONEY;
                            userGameInfo.setGameMoney(gameMoney);
                            userGameInfo.setGameDate(today);
                            //userGameInfo.save();                // DB에 저장
                        }
                        Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                    } else { inputNickname(); }     // record not found or network not connected
                } catch (Exception e) { finish(); }
            }
        }).execute("http://121.168.204.177:3000/user2/getGameInfoByPhoneNo?phoneNo=" + phoneNo);
    }

    public void inputNickname() {
        // DialogBox로부터 nickname을 받음
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_nickname, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Button btnComplete = (Button)dialogView.findViewById(R.id.btnComplete);
        final EditText etInputNickname = (EditText)dialogView.findViewById(R.id.etInputNickname);

        // 영어, 숫자만 입력 받음
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-z0-9]+$");
                if (!ps.matcher(source).matches()) { return ""; }
                return null;
            }
        };
        etInputNickname.setFilters(new InputFilter[] {filter});

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickName = etInputNickname.getText().toString();
                userGameInfo.setPhoneNo(phoneNo);
                userGameInfo.setNickName(nickName);
                userGameInfo.setGameDate("2016-09-08");
                dialog.dismiss();
                userGameInfo.add();                 // 신규 사용자 등록 후 종료!!!
                Toast.makeText(getApplicationContext(), " RESTART again! ", Toast.LENGTH_LONG).show();
                //finish();
            }
        });
        dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT:   // do nothing
                    break;
            }
        }
        // 올바르게 종료: 점수, 게임머니, 아이템·캐릭터 배열 저장
        // 서버로 송신, 정상종료 T
        userGameInfo.save();                // DB에 최종 저장
        userGameInfo.setUid(0);
        finish();
    }

}