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
                    party.addPlayer(players.removeFirst());
                }
                party.readyParty();
            }
            
            System.out.println("Mode " + mode.name() + " Match: " + buildMatch(mode));
        }

        // for(GameMode mode : GameMode.values()){
        //     System.out.println("Queue for mode: " + mode);
        //     PriorityQueue<QueueTicket> queue = ticketQueues.get(mode);
        //     int i = 0;
        //     while(!queue.isEmpty()){
        //         System.out.println("" + i + queue.poll() + "\n");
        //         i++;
        //     }
        // }

        ArrayList<Team> validTeams = new ArrayList<>();
    }

    public ArrayList<Team> buildMatch(GameMode mode){
        
        ArrayList<Team> validTeams = new ArrayList<>();
        PriorityQueue<QueueTicket> queue = ticketQueues.get(mode);

        ArrayList<Party> partyPool = new ArrayList<>();
        for(int i = 0 ; i < 20 && !queue.isEmpty() ; i++){
            partyPool.add(queue.poll().getParty());
        }

        buildTeam(mode.getMaxTeamSize(), partyPool, 0, new Team(), validTeams);
        validTeams.sort(Comparator.comparing(Team::getAvgTeamRating).reversed());

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
        return result;
    }
    public synchronized void joinQueue(QueueTicket ticket){
        PriorityQueue<QueueTicket> modeQueue = ticketQueues.get(ticket.getGameMode());
        modeQueue.add(ticket);
    }

    private void tick(){
        System.out.println("Pulse");
    }

    private void buildTeam(int requiredPlayers, ArrayList<Party> parties, int index, Team currentTeam, ArrayList<Team> validTeams){
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

            buildTeam(requiredPlayers - currentParty.getPartySize(), parties, index + 1, team, validTeams);
        }
    }
}
