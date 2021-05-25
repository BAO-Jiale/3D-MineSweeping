public class GameController {
    public Player p1;
    public Player p2;
    public Player onTurn;
    boolean isPaused;
    private int mine_Left;
    private int openCount=1;

    public GameController(Player p1, Player p2) {
        this.init(p1, p2);
        this.onTurn = p1;
    }

    private void init(Player p1, Player p2) {
        this.p1=p1;
        this.p2=p2;

    }

    public void nextTurn() {
        if (onTurn == p1) {
            onTurn = p2;
        } else if (onTurn == p2) {
            onTurn = p1;
        }
        System.out.println("Now it is " + onTurn.getUserName() + "'s turn.");

        //scoreBoard.update();
        //TODO: 在每个回合结束的时候，还需要做什么 (例如...检查游戏是否结束？)


    }

    public Player getOnTurnPlayer() {
        return onTurn;
    }
    public Player getP1() {
        return p1;
    }
    public Player getP2() {
        return p2;
    }

    public void find_mine(){this.mine_Left--;}

    public void setMine_Left(int mine_Left) {
        this.mine_Left = mine_Left;
    }

    public int getMine_Left() {
        return mine_Left;
    }

    public int getOpenCount() {
        return openCount;
    }
    public void addOpenCount(){this.openCount++;}


    public void readFileData(String fileName) {
        //todo: read date from file

    }

    public void writeDataToFile(String fileName){
        //todo: write data into file
    }
}
