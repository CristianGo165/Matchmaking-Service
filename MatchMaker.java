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

    private final double TICK = 5000; //Milliseconds
    private Map<GameMode, PriorityQueue<QueueTicket>> ticketQueues;

    private MatchMaker(){
        this.ticketQueues = new HashMap<>();
        for(GameMode mode : GameMode.values()){
            ticketQueues.put(mode, new PriorityQueue<>());
        }
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
    public synchronized void joinQueue(QueueTicket ticket){
        PriorityQueue<QueueTicket> modeQueue = ticketQueues.get(ticket.getGameMode());
        modeQueue.add(ticket);
    }

    private void tick(){
        System.out.println("Pulse");
    }
}
