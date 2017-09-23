package kr.yohan.cambo;

import com.google.gson.Gson;

/**
 * Created by 3330MT on 2017-01-17.
 */

public class UserGameInfo {
    private static UserGameInfo userGameInfo;
    private UserGameInfo() {}

    public static UserGameInfo getInstance() {
        if (userGameInfo == null) {
            synchronized (UserGameInfo.class) {
                if (userGameInfo == null) { userGameInfo = new UserGameInfo(); }
            }
        }
        return userGameInfo;
    }

    //init
    public static void init(UserGameInfo info) {
        userGameInfo = info;
    }

    public void add() {
        new RestfulPostAPI(new OnCompletionListener() {
            @Override
            public void onComplete(String result) {
                try {
                    //System.out.println("userGameInfo is added ");
                } catch (Exception e) {
                }
            }
        }).execute("http://121.168.204.177:3000/user2/addGameInfo",new Gson().toJson(userGameInfo));
    }

    public void save() {
        new RestfulPostAPI(new OnCompletionListener() {
            @Override
            public void onComplete(String result) {
                try {
                    //System.out.println("userGameInfo is saved");
                } catch (Exception e) {
                }
            }
        }).execute("http://121.168.204.177:3000/user2/updateGameInfo",new Gson().toJson(userGameInfo));
    }

    private int uid=0;
    private String phoneNo;
    private String nickName;
    private String gameDate;
    private int gameCount=0;
    private int gameScore=0;
    private int gameMoney=0;
    private int gameMode=1;
    private int gameLevel=1;
    private int gameItem0=0, gameItem1=0, gameItem2=0;  // gameItems[3]: 시간연장, 2장 힌트, 실패삭제
    private int gameChar=1, avatarChar=0, scoreChar=0, moneyChar=0; // gameChars[4], 12 Korean zodiac

    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid=uid; }

    public void setPhoneNo(String phoneNo) { this.phoneNo=phoneNo; }
    public void setNickName(String nickName) { this.nickName=nickName; }

    public String getGameDate() { return gameDate.substring(0, 10); }
    public void setGameDate(String gameDate) { this.gameDate=gameDate.substring(0, 10); }

    public int getGameCount() { return gameCount; }
    public void setGameCount(int gameCount) { this.gameCount=gameCount; }

    public int getGameScore() { return gameScore; }
    public void setGameScore(int gameScore) { this.gameScore=gameScore; }

    public int getGameMoney() { return gameMoney; }
    public void setGameMoney(int gameMoney) { this.gameMoney=gameMoney; }

    public int getGameMode() { return gameMode; }
    public void setGameMode(int gameMode) { this.gameMode=gameMode; }

    public int getGameLevel() { return gameLevel; }
    public void setGameLevel(int gameLevel) { this.gameLevel=gameLevel; }

    public int[] getGameItems() { return new int[] {gameItem0, gameItem1, gameItem2}; }
    public void setGameItems(int[] gameItems) {this.gameItem0=gameItems[0]; this.gameItem1=gameItems[1]; this.gameItem2=gameItems[2]; }

    public int[] getGameChars() { return new int[] {gameChar, avatarChar, scoreChar, moneyChar}; }
    public void setGameChars(int[] gameChars) {this.gameChar=gameChars[0]; this.avatarChar=gameChars[1]; this.scoreChar=gameChars[2]; this.moneyChar=gameChars[3]; }

    public void setAvatarChar(int avatarChar) {this.avatarChar=avatarChar; }
}
