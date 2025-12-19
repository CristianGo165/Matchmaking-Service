import java.util.ArrayList;

public class Match {
    private Team teamOne;
    private Team teamTwo;
    private GameMode mode;

    public Match(GameMode mode, Team teamOne, Team teamTwo){
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.mode = mode;
    }

    public void startMatch(){
        for(Team t : getTeams()){
            for(Party p : t.getParties()){
                p.setAllPlayerStates(PlayerState.PLAYING);
            }
        }
    }

    public ArrayList<Team> getTeams(){
        ArrayList<Team> result = new ArrayList<>();
        result.add(teamOne);
        result.add(teamTwo);
        return result;
    }

    public int getMatchDifference(){
        return Math.abs(teamTwo.getAvgTeamRating() - teamOne.getAvgTeamRating());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String separator = "============================================";

        sb.append(separator).append("\n");
        sb.append(" MATCH DETAILS | Mode: ").append(mode.name()).append("\n");
        sb.append(separator).append("\n");

        // Format Team One
        sb.append(" [TEAM 1] (Avg: ").append(teamOne.getAvgTeamRating()).append(")\n");
        for (Party p : teamOne.getParties()) {
            sb.append("   • ").append(p.getPlayers()).append("\n");
        }

        sb.append("\n VS \n\n");

        // Format Team Two
        sb.append(" [TEAM 2] (Avg: ").append(teamTwo.getAvgTeamRating()).append(")\n");
        for (Party p : teamTwo.getParties()) {
            sb.append("   • ").append(p.getPlayers()).append("\n");
        }

        sb.append(separator).append("\n");
        sb.append(" Rating Difference: ").append(getMatchDifference()).append("\n");
        sb.append(separator);

        return sb.toString();
    }
}
