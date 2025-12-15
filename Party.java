import java.util.ArrayList;

public class Party {
    private ArrayList<Player> players;
    private GameMode mode;
    private boolean customMatch;
    private boolean open;

    public Party(GameMode mode){
        this.mode = mode;
        this.players = new ArrayList<>();
        open = true;
    }
    public Party(GameMode mode, boolean customMatch){
        this.mode = mode;
        this.customMatch = customMatch;
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
        MatchMaker.getInstance().joinQueue(new QueueTicket(this));
    }
    
    public int getPartySize(){
        return players.size();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Party Mode: ").append(mode).append("\n");
        sb.append("Players:\n");
        for(Player player : players){
            sb.append(" - ").append(player).append("\n");
        }
        return sb.toString();
    }
}
