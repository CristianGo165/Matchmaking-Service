import observer_pattern.Observer;

public class QueueTicket implements Comparable<QueueTicket>, Observer {
    private static final double MAX_WAIT_TIME = 350.0; //Seconds
    private double waitTime;
    private Party party;
    private double averageRating;
    private final double TIME_STAMP;

    public QueueTicket(Party party){
        this.party = party;
        this.averageRating = party.calculateAverageRating();
        this.TIME_STAMP = System.currentTimeMillis();
        this.waitTime = (int) (Math.random() * 350);
        subscribeToTickEvent();
    }
    public GameMode getGameMode(){
        return party.getMode();
    }
    public double averageRating(){
        return this.averageRating;
    }

    public void drawn(){
        unsubscribeFromTickEvent();
    }
    
    private void subscribeToTickEvent(){
        MatchMaker.tickEvent.add(this);
    }
    //TODO Remember to unsubscribe when removed from queue
    private void unsubscribeFromTickEvent(){
        MatchMaker.tickEvent.remove(this);
    }
    @Override
    public void update(){
        double currentTime = System.currentTimeMillis();
        this.waitTime = (currentTime - TIME_STAMP)/1000.0;
    }
    public double calculateWaitFactor(){
        return waitTime / MAX_WAIT_TIME;
    }
    public double calculatePriorityScore(){
        double waitFactor = calculateWaitFactor();
        return (1 - waitFactor) * averageRating;
    }
    public Party getParty(){
        return this.party;
    }

    @Override
    public int compareTo(QueueTicket o) {
        if(this.averageRating - o.averageRating >= 0){
            return 1;
        } else if (this.averageRating - o.averageRating < 0) {
            return -1;
        } else {
            return 0;
        }
    }
    
    @Override
    public String toString(){
        return "QueueTicket{" +
                "party=" + party +
                ", averageRating=" + averageRating +
                ", priorityScore=" + calculatePriorityScore() +
                '}';
    }
}
