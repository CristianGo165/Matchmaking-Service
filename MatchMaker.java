import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import observer_pattern.*;

public class MatchMaker {
    private static MatchMaker instance = null;
    public static synchronized MatchMaker getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new MatchMaker();
    }
    private static final double TICK = 5000; //Milliseconds
    private static final int MATCH_POOL_SIZE = 20;

    private Map<GameMode, PriorityQueue<QueueTicket>> ticketQueues;
    public static Event tickEvent;

    private MatchMaker(){
        this.ticketQueues = new HashMap<>();
        for(GameMode mode : GameMode.values()){
            ticketQueues.put(mode, new PriorityQueue<>(new TicketComparator()));
        }
        tickEvent = new Event();
    }

    public void start(){
        double startTime = System.currentTimeMillis();
        double currentTime;
        while(true){
            currentTime = System.currentTimeMillis();
            if(currentTime - startTime >= TICK){
                tick();
                startTime = System.currentTimeMillis();
            }
        }
    }
    private void tick(){
        System.out.println("Pulse");
    }

    public void initializeSimulation(int numPlayersPerMode){

        for(GameMode mode : GameMode.values()){
            ArrayList<Player> players = new ArrayList<>();
            for(int i = 0 ; i < numPlayersPerMode ; i++){
                Player player = new Player("Player_" + mode.name() + "_" + i, 1000 + (int)(Math.random() * 1000));
                players.add(player);
            }
            while(!players.isEmpty()){
                Party party = new Party(mode);
                int partySize = (int)(Math.random() * ((mode.getMaxTeamSize()) - 1)) + 1;
                for(int i = 0 ; i < partySize && !players.isEmpty() ; i++){
//                    party.addPlayer(players.removeFirst());
                    party.addPlayer(players.remove(0));
                }
                party.readyParty();
            }

            //DEBUG LOG MATCHES
            System.out.println(mode.name() + " Match: \n\t" + buildMatch(mode));
            //DEBUG LOG MATCHES
        }
    }
    public synchronized void joinQueue(QueueTicket ticket){
        PriorityQueue<QueueTicket> modeQueue = ticketQueues.get(ticket.getGameMode());
        modeQueue.add(ticket);
    }

    //Gets the top MATCH_POOL_SIZE number of elements from the priority queue
    public ArrayList<Party> buildMatchPool(GameMode mode){
        PriorityQueue<QueueTicket> queue = ticketQueues.get(mode);

        ArrayList<Party> partyPool = new ArrayList<>();
        for(int i = 0 ; i < MATCH_POOL_SIZE && !queue.isEmpty() ; i++){
            partyPool.add(queue.poll().getParty());
        }
        return partyPool;
    }
    public ArrayList<Team> buildMatch(GameMode mode){

        //Gets the top elements from the priority queue
        ArrayList<Party> partyPool = buildMatchPool(mode);

        //Builds a list of valid teams
        ArrayList<Team> validTeams = new ArrayList<>();
        buildValidTeams(mode.getMaxTeamSize(), partyPool, 0, new Team(), validTeams);
        validTeams.sort(Comparator.comparing(Team::getAvgTeamRating).reversed());

        //Finds the team with the smallest difference in skill level
        Double minDiff = Double.MAX_VALUE;
        ArrayList<Team> result = new ArrayList<>();

        for(int i = 0 ; i < validTeams.size() - 1; i++){
            Team teamA = validTeams.get(i);
            Team teamB = validTeams.get(i + 1);

            if(!Collections.disjoint(teamA.getParties(), teamB.getParties())) continue;

            double currentDiff = Math.abs(teamA.getAvgTeamRating() - teamB.getAvgTeamRating());
            if(currentDiff < minDiff){
                minDiff = currentDiff;
                result.clear();
                result.add(teamA);
                result.add(teamB);
            }
        }

        if (result.isEmpty()) {
            return null;
        }
        validTeams.remove(result.get(0));
        validTeams.remove(result.get(1));

        //Returns the Best Team
        return result;
    }
    private void buildValidTeams(int requiredPlayers, ArrayList<Party> parties, int index, Team currentTeam, ArrayList<Team> validTeams){
        //Base Case 1: Team size met
        if(requiredPlayers == 0){
            validTeams.add(currentTeam);
            return;
        }
        //Base Case 2: Exceeded team size/no more parties
        if(requiredPlayers < 0 || index >= parties.size()){
            return;
        }

        //Recursive Case
        for(int i = index ; i < parties.size() ; i++){
            Party currentParty = parties.get(i);
            Team team = new Team(currentTeam);
            team.addParty(currentParty);

            buildValidTeams(requiredPlayers - currentParty.getPartySize(), parties, index + 1, team, validTeams);
        }
    }


    //DEBUGGING METHODS
    public void debugLogTickets(){
         for(GameMode mode : GameMode.values()){
             System.out.println("Queue for mode: " + mode);
             PriorityQueue<QueueTicket> queue = ticketQueues.get(mode);
             int i = 0;
             while(!queue.isEmpty()){
                 System.out.println("" + i + queue.poll() + "\n");
                 i++;
             }
         }
    }
    //DEBUGGING METHODS
}
