public class GameController {
    public Player p1;
    public Player p2;
    public Player onTurn;
    boolean isPaused;

    public GameController(Player p1, Player p2) {
        this.init(p1, p2);
        this.onTurn = p1;
    }

    private void init(Player p1, Player p2) {
    }

    public void nextTurn() {
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
}
