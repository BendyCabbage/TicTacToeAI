public class MoveEval {
    private Move move;
    private Integer eval;
    public MoveEval(Move move, Integer eval) {
        this.move = move;
        this.eval = eval;
    }
    public Move getMove() {
        return move;
    }
    public Integer getEval() {
        return eval;
    }
}
