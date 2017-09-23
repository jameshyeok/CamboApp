package kr.yohan.cambo;

import java.util.Random;

public class RandomMatrix extends Play1Activity {

    int[] types = new int[2];   // types[0]: 타입(1~7) - 0 제외, types[1]: # of marks
    int numMark, numBean, mOffset, numType, tOffset;

    final int NO_OF_TYPE1=2;    // 레벨 1 타입: 1(좌우대칭), 2(상하대칭)
    final int NO_OF_TYPE2=3;    // 레벨 2 타입: 3(90˚ CCW 회전), 4(180˚ 회전), 5(270˚ 회전)
    final int NO_OF_TYPE3=5;    // 레벨 3 타입: 3(90˚ CCW 회전), 4(180˚ 회전), 5(270˚ 회전), 6(좌하\대칭), 7(우하/대칭)
    final int TYPE_OFFSET1=1;
    final int TYPE_OFFSET2=3;
    final int TYPE_OFFSET3=3;
    final int NO_OF_MARK1=4;    // 레벨 1 표지 수: 3~6개 (4종류)
    final int NO_OF_MARK2=5;    // 레벨 2 표지 수: 6~10개 (5종류)
    final int NO_OF_MARK3=6;    // 레벨 3 표지 수: 10~15개 (6종류)
    final int MARK_OFFSET1=3;
    final int MARK_OFFSET2=6;
    final int MARK_OFFSET3=10;
    final int NO_OF_BEAN1=9;    // 레벨 1 그리드 수
    final int NO_OF_BEAN2=16;   // 레벨 2 그리드 수
    final int NO_OF_BEAN3=25;   // 레벨 3 그리드 수
    final int MAX_INDEX_OF_BEAN2=NO_OF_BEAN2-1; // 레벨 2 MaxIndex of grid
    final int MAX_INDEX_OF_BEAN3=NO_OF_BEAN3-1; // 레벨 3 MaxIndex of grid

    public int[] randomMatrix(int level, String img[], boolean mch[], int smch[]) {
        switch(level) {
            case 1:
                numMark=NO_OF_MARK1; mOffset=MARK_OFFSET1; numBean=NO_OF_BEAN1;
                numType=NO_OF_TYPE1; tOffset=TYPE_OFFSET1; randomGrid(level, img, mch, smch);
                break;
            case 2:
                numMark=NO_OF_MARK2; mOffset=MARK_OFFSET2; numBean=NO_OF_BEAN2;
                numType=NO_OF_TYPE2; tOffset=TYPE_OFFSET2; randomGrid(level, img, mch, smch);
                break;
            case 3:
                numMark=NO_OF_MARK3; mOffset=MARK_OFFSET3; numBean=NO_OF_BEAN3;
                numType=NO_OF_TYPE3; tOffset=TYPE_OFFSET3; randomGrid(level, img, mch, smch);
                break;
        }
        return types;   // types: 1(좌우대칭), 2(상하대칭), 3(90˚CCW회전), 4(180˚회전), 5(270˚회전), 6(좌하\대칭), 7(우하/대칭)
    }

    private void randomGrid(int level, String img[], boolean mch[], int smch[]) {
        boolean[] tmch = new boolean[numBean];
        //int r0=level*10;
        for (int i=0; i<numBean; i++) { img[i]="c0"; tmch[i]=false; }   // initialization
        //for (int i=0; i<numBean; i++) { img[i]="c"+String.valueOf(r0)+"0"; tmch[i]=false; }
        Random random=new Random();
        int r1=random.nextInt(numMark); r1+=mOffset; types[1]=r1;   // mark count
        int r2;                                                     // mark position
        for (int i=0; i<r1; i++) {
            r2=random.nextInt(numBean);
            while (tmch[r2]) r2=random.nextInt(numBean);
            img[r2]="c1"; tmch[r2]=true;
            //img[r2]="c"+String.valueOf(r0)+"1"; tmch[r2]=true;
        }
        types[0]=random.nextInt(numType); types[0]+=tOffset;
        // 타입에 따라 tmch[]로부터 mch[] 구성, mch[]에 대한 original index of tmch[]인 smch[] 구성
        switch (types[0]) {
            case 1:         // 타입 1(좌우대칭) - 레벨 1
                mch[0]=tmch[2]; mch[1]=tmch[1]; mch[2]=tmch[0];
                mch[3]=tmch[5]; mch[4]=tmch[4]; mch[5]=tmch[3];
                mch[6]=tmch[8]; mch[7]=tmch[7]; mch[8]=tmch[6];
                smch[0]=2;      smch[1]=1;      smch[2]=0;
                smch[3]=5;      smch[4]=4;      smch[5]=3;
                smch[6]=8;      smch[7]=7;      smch[8]=6;
                break;
            case 2:         // 타입 2(상하대칭) - 레벨 1
                mch[0]=tmch[6]; mch[1]=tmch[7]; mch[2]=tmch[8];
                mch[3]=tmch[3]; mch[4]=tmch[4]; mch[5]=tmch[5];
                mch[6]=tmch[0]; mch[7]=tmch[1]; mch[8]=tmch[2];
                smch[0]=6;      smch[1]=7;      smch[2]=8;
                smch[3]=3;      smch[4]=4;      smch[5]=5;
                smch[6]=0;      smch[7]=1;      smch[8]=2;
                break;
            case 3:         // 타입 3(90˚ CCW 회전) - 레벨 2, 3
                if (level==2) {
                    mch[0] =tmch[3];  mch[1] =tmch[7];  mch[2] =tmch[11]; mch[3] =tmch[15];
                    mch[4] =tmch[2];  mch[5] =tmch[6];  mch[6] =tmch[10]; mch[7] =tmch[14];
                    mch[8] =tmch[1];  mch[9] =tmch[5];  mch[10]=tmch[9];  mch[11]=tmch[13];
                    mch[12]=tmch[0];  mch[13]=tmch[4];  mch[14]=tmch[8];  mch[15]=tmch[12];
                    smch[0] =3;       smch[1] =7;       smch[2] =11;      smch[3] =15;
                    smch[4] =2;       smch[5] =6;       smch[6] =10;      smch[7] =14;
                    smch[8] =1;       smch[9] =5;       smch[10]=9;       smch[11]=13;
                    smch[12]=0;       smch[13]=4;       smch[14]=8;       smch[15]=12;
                } else {
                    mch[0] =tmch[4];  mch[1] =tmch[9];  mch[2] =tmch[14]; mch[3] =tmch[19]; mch[4] =tmch[24];
                    mch[5] =tmch[3];  mch[6] =tmch[8];  mch[7] =tmch[13]; mch[8] =tmch[18]; mch[9] =tmch[23];
                    mch[10]=tmch[2];  mch[11]=tmch[7];  mch[12]=tmch[12]; mch[13]=tmch[17]; mch[14]=tmch[22];
                    mch[15]=tmch[1];  mch[16]=tmch[6];  mch[17]=tmch[11]; mch[18]=tmch[16]; mch[19]=tmch[21];
                    mch[20]=tmch[0];  mch[21]=tmch[5];  mch[22]=tmch[10]; mch[23]=tmch[15]; mch[24]=tmch[20];
                    smch[0] =4;       smch[1] =9;       smch[2] =14;      smch[3] =19;      smch[4] =24;
                    smch[5] =3;       smch[6] =8;       smch[7] =13;      smch[8] =18;      smch[9] =23;
                    smch[10]=2;       smch[11]=7;       smch[12]=12;      smch[13]=17;      smch[14]=22;
                    smch[15]=1;       smch[16]=6;       smch[17]=11;      smch[18]=16;      smch[19]=21;
                    smch[20]=0;       smch[21]=5;       smch[22]=10;      smch[23]=15;      smch[24]=20;
                }
                break;
            case 4:         // 타입 4(180˚ 회전) - 레벨 2, 3
                if (level==2) {
                    for (int i=0; i<NO_OF_BEAN2; i++) { mch[i]=tmch[MAX_INDEX_OF_BEAN2-i]; smch[i]=MAX_INDEX_OF_BEAN2-i; }
                } else {
                    for (int i=0; i<NO_OF_BEAN3; i++) { mch[i]=tmch[MAX_INDEX_OF_BEAN3-i]; smch[i]=MAX_INDEX_OF_BEAN3-i; }
                }
                break;
            case 5:         // 타입 5(270˚ 회전) - 레벨 2, 3
                if (level==2) {
                    mch[0] =tmch[12]; mch[1] =tmch[8];  mch[2] =tmch[4];  mch[3] =tmch[0];
                    mch[4] =tmch[13]; mch[5] =tmch[9];  mch[6] =tmch[5];  mch[7] =tmch[1];
                    mch[8] =tmch[14]; mch[9] =tmch[10]; mch[10]=tmch[6];  mch[11]=tmch[2];
                    mch[12]=tmch[15]; mch[13]=tmch[11]; mch[14]=tmch[7];  mch[15]=tmch[3];
                    smch[0] =12;      smch[1] =8;       smch[2] =4;       smch[3] =0;
                    smch[4] =13;      smch[5] =9;       smch[6] =5;       smch[7] =1;
                    smch[8] =14;      smch[9] =10;      smch[10]=6;       smch[11]=2;
                    smch[12]=15;      smch[13]=11;      smch[14]=7;       smch[15]=3;
                } else {
                    mch[0] =tmch[20]; mch[1] =tmch[15]; mch[2] =tmch[10]; mch[3] =tmch[5];  mch[4] =tmch[0];
                    mch[5] =tmch[21]; mch[6] =tmch[16]; mch[7] =tmch[11]; mch[8] =tmch[6];  mch[9] =tmch[1];
                    mch[10]=tmch[22]; mch[11]=tmch[17]; mch[12]=tmch[12]; mch[13]=tmch[7];  mch[14]=tmch[2];
                    mch[15]=tmch[23]; mch[16]=tmch[18]; mch[17]=tmch[13]; mch[18]=tmch[8];  mch[19]=tmch[3];
                    mch[20]=tmch[24]; mch[21]=tmch[19]; mch[22]=tmch[14]; mch[23]=tmch[9];  mch[24]=tmch[4];
                    smch[0] =20;      smch[1] =15;      smch[2] =10;      smch[3] =5;       smch[4] =0;
                    smch[5] =21;      smch[6] =16;      smch[7] =11;      smch[8] =6;       smch[9] =1;
                    smch[10]=22;      smch[11]=17;      smch[12]=12;      smch[13]=7;       smch[14]=2;
                    smch[15]=23;      smch[16]=18;      smch[17]=13;      smch[18]=8;       smch[19]=3;
                    smch[20]=24;      smch[21]=19;      smch[22]=14;      smch[23]=9;       smch[24]=4;
                }
                break;
            case 6:         // 타입 6(좌하\대칭) - 레벨 3
                mch[0] =tmch[0];  mch[1] =tmch[5];  mch[2] =tmch[10]; mch[3] =tmch[15]; mch[4] =tmch[20];
                mch[5] =tmch[1];  mch[6] =tmch[6];  mch[7] =tmch[11]; mch[8] =tmch[16]; mch[9] =tmch[21];
                mch[10]=tmch[2];  mch[11]=tmch[7];  mch[12]=tmch[12]; mch[13]=tmch[17]; mch[14]=tmch[22];
                mch[15]=tmch[3];  mch[16]=tmch[8];  mch[17]=tmch[13]; mch[18]=tmch[18]; mch[19]=tmch[23];
                mch[20]=tmch[4];  mch[21]=tmch[9];  mch[22]=tmch[14]; mch[23]=tmch[19]; mch[24]=tmch[24];
                smch[0] =0;       smch[1] =5;       smch[2] =10;      smch[3] =15;      smch[4] =20;
                smch[5] =1;       smch[6] =6;       smch[7] =11;      smch[8] =16;      smch[9] =21;
                smch[10]=2;       smch[11]=7;       smch[12]=12;      smch[13]=17;      smch[14]=22;
                smch[15]=3;       smch[16]=8;       smch[17]=13;      smch[18]=18;      smch[19]=23;
                smch[20]=4;       smch[21]=9;       smch[22]=14;      smch[23]=19;      smch[24]=24;
                break;
            case 7:         // 타입 7(우하/대칭) - 레벨 3
                mch[0] =tmch[24]; mch[1] =tmch[19]; mch[2] =tmch[14]; mch[3] =tmch[9];  mch[4] =tmch[4];
                mch[5] =tmch[23]; mch[6] =tmch[18]; mch[7] =tmch[13]; mch[8] =tmch[8];  mch[9] =tmch[3];
                mch[10]=tmch[22]; mch[11]=tmch[17]; mch[12]=tmch[12]; mch[13]=tmch[7];  mch[14]=tmch[2];
                mch[15]=tmch[21]; mch[16]=tmch[16]; mch[17]=tmch[11]; mch[18]=tmch[6];  mch[19]=tmch[1];
                mch[20]=tmch[20]; mch[21]=tmch[15]; mch[22]=tmch[10]; mch[23]=tmch[5];  mch[24]=tmch[0];
                smch[0] =24;      smch[1] =19;      smch[2] =14;      smch[3] =9;       smch[4] =4;
                smch[5] =23;      smch[6] =18;      smch[7] =13;      smch[8] =8;       smch[9] =3;
                smch[10]=22;      smch[11]=17;      smch[12]=12;      smch[13]=7;       smch[14]=2;
                smch[15]=21;      smch[16]=16;      smch[17]=11;      smch[18]=6;       smch[19]=1;
                smch[20]=20;      smch[21]=15;      smch[22]=10;      smch[23]=5;       smch[24]=0;
                break;
        }
    }

}
