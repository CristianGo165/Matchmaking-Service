import java.util.ArrayList;

public class Party {
    private final ArrayList<Player> players;
    private final GameMode mode;
    private PartyStatus status;
    private double tempTimeStamp = 0;
    private boolean open;

    public Party(GameMode mode){
        this.mode = mode;
        this.players = new ArrayList<>();
        open = true;
        status = PartyStatus.WAITING;
    }

    public Party(GameMode mode, boolean customMatch){
        this.mode = mode;
        this.players = new ArrayList<>();
        open = !customMatch;
        status = PartyStatus.WAITING;

    }

    public ArrayList<Player> getPlayers(){
        return this.players;
    }

    public GameMode getMode(){
        return this.mode;
    }

    public int getPartySize(){
        return players.size();
    }

    public double calculateAverageRating(){
        double result = 0;
        for(Player player : players){
            result += player.getRating();
        }
        if(result == 0 && players.isEmpty()) return -1;
        return result/players.size();
    }

    public void addPlayer(Player player){
        if(player.getState().equals(PlayerState.WAITING) || player.getState().equals(PlayerState.PLAYING))
            throw new IllegalArgumentException("UNABLE TO ADD [" + player.getUsername() + "] TO PARTY");

        if(open){
            player.setState(PlayerState.WAITING);
            players.add(player);
        } else{
            throw new IllegalStateException("PARTY IS FULL");
        }

        if(players.size() >= mode.getMaxTeamSize()){
            open = false;
        }
    }

    public void setAllPlayerStates(PlayerState state){
        for(Player p : players){
            p.setState(state);
        }
    }

    public void setToWaiting(){
        this.status = PartyStatus.WAITING;
    }

    public void readyParty(){
        if(status.equals(PartyStatus.SEARCHING)) throw new IllegalCallerException("CANNOT READY PARTY");
        status = PartyStatus.SEARCHING;
        open = !open;
        MatchMaker.getInstance().joinQueue(new QueueTicket(this));
    }

    public void reReadyParty(){
        if(status.equals(PartyStatus.SEARCHING)) throw new IllegalCallerException("CANNOT RE-READY PARTY THAT HAS NOT BEEN INITIALLY READIED");
        status = PartyStatus.SEARCHING;
        open = !open;
        MatchMaker.getInstance().joinQueue(new QueueTicket(this, tempTimeStamp));
    }

    public void setTempTimeStamp(double timeStamp){
        this.tempTimeStamp = timeStamp;
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

    private enum PartyStatus{
        SEARCHING, WAITING
    }
}
