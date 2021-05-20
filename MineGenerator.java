public class MineGenerator {
    private int row;
    private int col;
    private int [][]Mine=new int[9][9];
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
        this.Mine= mineGenerator(mode);
        setMineField();
    }

    public MineGenerator(int row,int col,int number){
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

    public void setMineField() {
        for (int i=0;i<row;i++){
            for (int j=0;j<col;j++){
                if (this.Mine[i][j]==0){this.MineField[i][j]=Status.Covered_with_Mine;}
                else {this.MineField[i][j]=Status.Covered_without_Mine;}
            }
        }
    }

    public Status[][] getMineField() {
        this.setMineField();
        return MineField;
    }

}
