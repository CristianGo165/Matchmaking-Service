import java.util.ArrayList;

public class Party {
    private ArrayList<Player> players;
    private GameMode mode;
    private boolean customMatch;
    private boolean open;

    public Party(GameMode mode){
        this.mode = mode;

        open = true;
    }
    public Party(GameMode mode, boolean customMatch){
        this.mode = mode;
        this.customMatch = customMatch;

        open = !customMatch;
    }
    public GameMode getMode(){
        return this.mode;
    }
    public int calculateAverageRating(){
        int result = 0;
        for(Player player : players){
            result += player.getRating();
        }
        return result/players.size();
    }
    public void readyParty(){
        QueueTicket ticket = new QueueTicket(this);
    }

}
