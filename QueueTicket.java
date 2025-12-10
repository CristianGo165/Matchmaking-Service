import java.util.Comparator;

public class QueueTicket implements Comparable<QueueTicket> {
    private Party party;
    private int averageRating;
    private Double waitTime;

    public QueueTicket(Party party){
        this.party = party;
        this.averageRating = party.calculateAverageRating();
        this.waitTime = 0.0;
        MatchMaker.getInstance().joinQueue(this);
    }
    public GameMode getGameMode(){
        return party.getMode();
    }

    @Override
    public int compareTo(QueueTicket o) {
        return 0;
    }
}
