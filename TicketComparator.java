import java.util.Comparator;

public class TicketComparator implements Comparator<QueueTicket> {
	@Override
    public int compare(QueueTicket t1, QueueTicket t2) {
        return Double.compare(t2.calculatePriorityScore(), t1.calculatePriorityScore());
    }
}
