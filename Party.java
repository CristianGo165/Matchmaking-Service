import java.util.ArrayList;

public class Party {
    private ArrayList<Player> players;
    private GameMode mode;
    private boolean open;

    public Party(GameMode mode){
        this.mode = mode;
        this.players = new ArrayList<>();
        open = true;
    }
    public Party(GameMode mode, boolean customMatch){
        this.mode = mode;
        this.players = new ArrayList<>();
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
    public void addPlayer(Player player){
        if(open){
            players.add(player);
        }
    }
    public void readyParty(){
        open = !open;
        MatchMaker.getInstance().joinQueue(new QueueTicket(this));
    }
    public int getPartySize(){
        return players.size();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Players: ");
        for(Player player : players){
            sb.append(", ").append(player);
        }

        return sb.append("\n").toString();
    }
}
