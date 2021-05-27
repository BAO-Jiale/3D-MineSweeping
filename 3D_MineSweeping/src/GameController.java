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
        if (onTurn != null){
            System.out.println("Now it is " + onTurn.getUserName() + "'s turn.");
        }
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

}
