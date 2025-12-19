import java.util.ArrayList;

public class Team{
    private ArrayList<Party> parties;
    private int rating;

    public Team(){
        this.parties = new ArrayList<>();
        rating = calcAvgTeamRating();
    }

    public Team(Team team){
        this.parties = new ArrayList<>(team.parties);
        rating = calcAvgTeamRating();
    }

    public void addParty(Party party){
        this.parties.add(party);
        this.rating = calcAvgTeamRating();

    }

    public ArrayList<Party> getParties(){
        return this.parties;
    }

    public int getAvgTeamRating(){
        return this.rating;
    }

    private int calcAvgTeamRating(){
        int totalRating = 0;
        int totalPlayers = 0;
        for(Party party : parties){
            totalRating += party.calculateAverageRating() * party.getPartySize();
            totalPlayers += party.getPartySize();
        }
        if(totalRating == 0 && totalPlayers == 0) return 0;
        return totalRating / totalPlayers;
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