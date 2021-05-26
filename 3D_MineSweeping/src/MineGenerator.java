import java.util.Random;

public class MineGenerator {
    private int row=9;
    private int col=9;
    private int number=10;
    private int [][]Mine;
    private Status[][]MineField;
    private final Random random = new Random();

    public void resetMine(){Mine=mineGenerator(row,col,number);setMineField();}

    int[][]chessboard= new int[row][col];;
    public void putMine(){
        chessboard=new int[row][col];
        for (int i = 0; i < number; ){
            int r = random.nextInt(row);
            int c = random.nextInt(col);
            if (chessboard[r][c] != -1){
            chessboard[r][c] = -1;
            i++;
            }
        }
    }
    public boolean checkDensity(){
        for (int i=1; i<row-1;i++){
            for (int j=1;j<col-1;j++){
                if (chessboard[i-1][j-1]==-1&&chessboard[i-1][j]==-1&&chessboard[i-1][j+1]==-1&&
                    chessboard[i][j-1]==-1&&chessboard[i][j]==-1&&chessboard[i][j+1]==-1&&
                    chessboard[i+1][j-1]==-1&&chessboard[i+1][j]==-1&&chessboard[i+1][j+1]==-1
                ){return false;}
            }
        }
        return true;
    }
    public int[][]mineGenerator(int row,int col,int number){
        if (row>24|col>30|number>(row*col*0.5)){return null;}
        //todo: generate chessboard by your own algorithm
        //生成雷区
        putMine();
        //检测雷区密度（如果某九个格子内全是雷，那么重新生成雷区）
        while (!checkDensity()){putMine();}
        //遍历所有格子，如果该格子不是雷，那么需要计算该格子周围的八个格子中雷的数目
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                // suppose -1 represents mine
                int tempCount = 0;
                if (chessboard[i][j] != -1){
                    if (i>0 && j>0 && chessboard[i-1][j-1]==-1) tempCount++;
                    if (i>0 && chessboard[i-1][j]==-1) tempCount++;
                    if (i>0 && j<col-1 && chessboard[i-1][j+1]==-1) tempCount++;
                    if (j>0 && chessboard[i][j-1]==-1) tempCount++;
                    if (j<col-1 && chessboard[i][j+1]==-1) tempCount++;
                    if (i<row-1 && j>0 && chessboard[i+1][j-1]==-1) tempCount++;
                    if (i<row-1 && chessboard[i+1][j]==-1) tempCount++;
                    if (i<row-1 && j<col-1 && chessboard[i+1][j+1]==-1) tempCount++;
                    chessboard[i][j] = tempCount;
                }
            }
        }
        return chessboard;
    }


    public MineGenerator(int row,int col,int number){
        this.row=row;
        this.col=col;
        this.number=number;
        this.Mine=mineGenerator(row,col,number);
        setMineField();
    }

    public int[][] getMine() {
        return Mine;
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public int getNumber() {
        return number;
    }

    public void setMineField() {
        this.MineField=new Status[row][col];
        for (int i=0;i<row;i++){
            for (int j=0;j<col;j++){
                if (this.Mine[i][j]==-1){this.MineField[i][j]=Status.Covered_with_Mine;}
                else {this.MineField[i][j]=Status.Covered_without_Mine;}
            }
        }
    }

    public Status[][] getMineField() {
        return MineField;
    }

}