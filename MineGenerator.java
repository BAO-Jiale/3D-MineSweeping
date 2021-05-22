public class MineGenerator {
    private int row;
    private int col;
    private int number;
    private int [][]Mine;
    private Status[][]MineField;

    public int[][]mineGenerator(){return mineGenerator(9,9,10);}
    public int[][]mineGenerator(String mode){
        if (mode.equals("Junior")){return mineGenerator(9,9,10);}
        if (mode.equals("Senior")){return mineGenerator(16,16,40);}
        if (mode.equals("Professional")){return mineGenerator(16,30,99);}
        else return mineGenerator(9,9,10);
    }

    public int[][]mineGenerator(int row,int col,int number){
        if (row>24|col>30|number>(row*col*0.5)){return null;}
        else return new int[row][col];
    }

    public MineGenerator(String mode){
        if (mode.equals("Junior")){this.row=9;this.col=9;this.number=10;}
        if (mode.equals("Senior")){this.row=16;this.col=16;this.number=40;}
        if (mode.equals("Professional")){this.row=16;this.col=30;this.number=99;}
        this.Mine= mineGenerator(mode);
        setMineField();
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