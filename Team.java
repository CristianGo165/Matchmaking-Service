import java.util.ArrayList;

public class Team{
    private ArrayList<Party> parties;
    private int rating;

    public Team(){
        this.parties = new ArrayList<>();
        rating = getAvgTeamRating();
    }
    public Team(Team team){
        this.parties = new ArrayList<>(team.parties);
        rating = getAvgTeamRating();
    }
    public void addParty(Party party){
        this.parties.add(party);
    }
    public ArrayList<Party> getParties(){
        return this.parties;
    }
    private int calcAvgTeamRating(){
        int totalRating = 0;
        int totalPlayers = 0;
        for(Party party : parties){
            totalRating += party.calculateAverageRating() * party.getPartySize();
            totalPlayers += party.getPartySize();
        }
        return totalRating / totalPlayers;
    }
    public int getAvgTeamRating(){
        return this.rating;
    }

    @Override
    public String toString(){
        String result = "-----Team-----\n";
        int i = 1;
        for(Party p : parties){
            result += "\t Party #" + i + ": " + p + "\n";
            i++;    
        }
        result += "\t Average Team Rating: " + getAvgTeamRating() + "\n";

        return result;
    }
}