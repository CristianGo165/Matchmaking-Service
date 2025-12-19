
public class QueueTicket {
    private final Party party;
    private static final double WAIT_TIME_CONST = 5;
    private final double TIME_STAMP;
    private double waitTime;

    public QueueTicket(Party party){
        this.party = party;
        this.TIME_STAMP = System.currentTimeMillis();
        this.waitTime = 0;
        this.party.setTempTimeStamp(TIME_STAMP);
    }

    public QueueTicket(Party party, double timeStamp){
        this.party = party;
        this.TIME_STAMP = timeStamp;
        this.waitTime = (System.currentTimeMillis() - TIME_STAMP)/1000.0;
        this.party.setTempTimeStamp(TIME_STAMP);
    }

    public Party getParty(){
        return this.party;
    }

    public GameMode getGameMode(){
        return party.getMode();
    }

    public void drawn(){
        party.setToWaiting();
    }

    public void update(){
        double currentTime = System.currentTimeMillis();
        this.waitTime = (currentTime - TIME_STAMP)/1000.0;
    }

    public double calculatePriorityScore(){
        return party.calculateAverageRating() + (waitTime * WAIT_TIME_CONST);
    }

    @Override
    public String toString() {
        // Formatting wait time to 1 decimal place (e.g., 12.5s)
        String formattedWait = String.format("%.1fs", waitTime);
        // Formatting priority score for cleaner logs
        String formattedPriority = String.format("%.2f", calculatePriorityScore());
        return String.format(
                "[TICKET] Mode: %-12s | Rating: %4.0f | Wait: %6s | Priority: %8s | Status: %s",
                party.getMode(),
                party.calculateAverageRating(),
                formattedWait,
                formattedPriority,
                "In Queue"
        );
    }
}
