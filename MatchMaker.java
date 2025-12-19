import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class MatchMaker {
    private static MatchMaker instance = null;
    public static synchronized MatchMaker getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new MatchMaker();
    }
    private MatchMaker(){
        this.ticketQueues = new HashMap<>();
        for(GameMode mode : GameMode.values()){
            ticketQueues.put(mode, new PriorityQueue<>(new TicketComparator()));
        }
        running = true;
    }

    private static final double TICK = 5000; //Milliseconds
    //Number of players taken from the priority queue when building teams
    private static final int MATCH_POOL_SIZE = 10;

    //Match will not be formed if rating difference is too high
    public static final int MAX_MATCH_DISPARITY = 500;
    private Map<GameMode, PriorityQueue<QueueTicket>> ticketQueues;
    private boolean running;

    private void tick(){
        //updates ticket wait time
        System.out.println("\n----------TICK----------");

        //Updates wait times
        updateTickets();
        rebuildQueues();


        int nullMatches = 0;

        //DEBUG LOG MATCHES
        for(GameMode mode : GameMode.values()){
            Match m = buildMatch(mode);
            if(m == null){
                System.out.println("\n\tNO " + mode + " MATCH FOUND (NOT ENOUGH PLAYERS)");
                nullMatches++;
            } else{
                System.out.println("\n" + m.toString().indent(4));
            }
        }
        //DEBUG LOG MATCHES

        System.out.println("\n----------END OF TICK----------\n");

        System.out.println("\n-----Updated Player States-----");
        for(Player p : PlayerDatabase.getInstance().getRegisteredPlayers()){
            System.out.println("\t" + p);
        }

        System.out.println("\n-----Updated Tickets-----");
        debugLogTickets();

        //When no possible matches are buildable, simulation ends.
        if(nullMatches == GameMode.values().length) endSimulation();
    }

    public void initializeSimulation(ArrayList<Player> batch){

        //Clears priority queues
        for(PriorityQueue<QueueTicket> pq : ticketQueues.values()){
            pq.clear();
        }

        //Gets the number of players that should be in every mode given the batch size
        int numPlayersPerMode = batch.size()/GameMode.values().length;

        //Makes random sized parties for each mode
        for(GameMode mode : GameMode.values()){
            ArrayList<Player> players = new ArrayList<>();

            for(int i = 0 ; i < numPlayersPerMode && !batch.isEmpty(); i++){
                players.add(batch.remove(0));
            }
            makeRandomSizedParties(players, mode);
        }

        //Starts Loop
        double startTime = System.currentTimeMillis();
        double currentTime;
        while(running){
            currentTime = System.currentTimeMillis();
            if(currentTime - startTime >= TICK){
                tick();
                startTime = System.currentTimeMillis();
            }
        }
    }

    private void endSimulation(){
        running = false;
        System.out.println("=== ENDING TICK LOGIC SIMULATION ===");
    }

    public void initialize(ArrayList<Party> parties){

        //"Readies" each of the parties in the list if they already aren't
        for(Party p : parties){
            p.readyParty();
        }

        System.out.println("\n-----Open Tickets-----");
        debugLogTickets();


        //Starts Loop
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
    public synchronized void joinQueue(QueueTicket ticket){
        PriorityQueue<QueueTicket> modeQueue = ticketQueues.get(ticket.getGameMode());
        modeQueue.add(ticket);
    }

    //Gets the top MATCH_POOL_SIZE number of elements from the priority queue
    public ArrayList<Party> buildMatchPool(GameMode mode){
        PriorityQueue<QueueTicket> queue = ticketQueues.get(mode);

        ArrayList<Party> partyPool = new ArrayList<>();
        for(int i = 0 ; i < MATCH_POOL_SIZE && !queue.isEmpty() ; i++){
            QueueTicket t = queue.poll();
            if(t == null) break;
            t.drawn();
            partyPool.add(t.getParty());
        }
        return partyPool;
    }

    //Builds a match for a given game mode
    public Match buildMatch(GameMode mode){

        //Gets the top elements from the priority queue

        ArrayList<Party> partyPool = buildMatchPool(mode);

        //Builds a list of valid teams
        ArrayList<Team> validTeams = new ArrayList<>();
        buildValidTeams(mode.getMaxTeamSize(), partyPool, 0, new Team(), validTeams);
        validTeams.sort(Comparator.comparing(Team::getAvgTeamRating).reversed());

        //Finds the team with the smallest difference in skill level
        Double minDiff = Double.MAX_VALUE;
        Match result = null;

        for(int i = 0 ; i < validTeams.size() - 1 && validTeams.size() >= 2; i++){
            Team teamA = validTeams.get(i);
            Team teamB = validTeams.get(i + 1);

            if(!Collections.disjoint(teamA.getParties(), teamB.getParties())) continue;

            double currentDiff = Math.abs(teamA.getAvgTeamRating() - teamB.getAvgTeamRating());
            if(currentDiff < minDiff && currentDiff <= MAX_MATCH_DISPARITY){
                minDiff = currentDiff;
                result = new Match(mode, teamA, teamB);
            }
        }

        if (result != null) {
            result.startMatch();
            for (Team t : result.getTeams()) {
                partyPool.removeAll(t.getParties());
            }
        }

        //puts unchosen parties back in the queue
        for(Party p : partyPool){
            p.reReadyParty();
        }

        //Returns the Best Team
        return result;
    }

    //recursive loop to build an arraylist of all possible teams able to be made from the match pool
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

            buildValidTeams(requiredPlayers - currentParty.getPartySize(), parties, i + 1, team, validTeams);
        }
    }

    //Rebuilds queues. Used to update ticket wait times and positions in the queue
    private void rebuildQueues(){
        Map<GameMode, PriorityQueue<QueueTicket>> newQueues = new HashMap<>();
        for(GameMode mode : GameMode.values()){
            newQueues.put(mode, new PriorityQueue<>(new TicketComparator()));
        }

        for(GameMode mode : GameMode.values()){
            PriorityQueue<QueueTicket> oldQueue = ticketQueues.get(mode);
            PriorityQueue<QueueTicket> newQueue = newQueues.get(mode);

            while(!oldQueue.isEmpty()){
                newQueue.add(oldQueue.poll());
            }
        }

        ticketQueues = newQueues;
    }

    //Updates ticket wait times
    private void updateTickets(){
        for(PriorityQueue<QueueTicket> pq : ticketQueues.values()){
            for(QueueTicket t : pq){
                t.update();
            }
        }
    }

    //DEBUGGING METHODS
    public void debugLogTickets(){
         for(GameMode mode : GameMode.values()){
             System.out.println("Queue for mode: " + mode);
             PriorityQueue<QueueTicket> queue = ticketQueues.get(mode);
             int i = 0;
             for(QueueTicket t : queue){
                 System.out.println("" + i + t + "\n");
                i++;
             }
         }
    }

    //Makes random sized parties when a batch of players is given to the matchmaker
    private void makeRandomSizedParties(ArrayList<Player> players, GameMode mode){
        while(!players.isEmpty()){

            //Makes randomly sized parties within the size for the game mode until the given list of players is empty
            Party party = new Party(mode);
            int partySize = (int)(Math.random() * ((mode.getMaxTeamSize()) - 1)) + 1;
            for(int i = 0 ; i < partySize && !players.isEmpty() ; i++){
//              party.addPlayer(players.removeFirst());
                party.addPlayer(players.remove(0));
            }

            //Puts Parties in a ticket
            party.readyParty();
        }
    }

    public double getTick(){
        return TICK;
    }
    //DEBUGGING METHODS
}
