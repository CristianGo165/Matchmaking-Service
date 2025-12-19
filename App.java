import java.util.ArrayList;
import java.util.PriorityQueue;

public class App {
    public static void main(String[] args) {
        testComponentLevel();
        testServiceLogic();
        testTickLogic(100);
    }

    private static void testComponentLevel() {
        System.out.println("=== 1. COMPONENT TESTS (UNIT TESTS) ===");
        PlayerDatabase db = PlayerDatabase.getInstance();
        MatchMaker.getInstance();

        // 1.1 PlayerDatabase: Register and Get
        System.out.println("\n[TEST] PlayerDatabase Registration:");
        db.registerPlayer("Test_Unit_1", 1500);
        Player p = db.getPlayer("Test_Unit_1");
        System.out.println("Found registered player: " + (p != null && p.getUsername().equals("Test_Unit_1")));
        System.out.println("Getting non-existent player (expect null): " + db.getPlayer("Ghost_User"));
        //Try to register player with duplicate username
        System.out.print("Register Player With Duplicate Username: ");
        try {
            db.registerPlayer("Test_Unit_1", 67);
        } catch (Exception e) {
            System.out.println("CAUGHT EXCEPTION: " + e);
        }

        // 1.2 Party: Average MMR Logic
        System.out.println("\n[TEST] Party Average MMR Logic:");
        Party testParty = new Party(GameMode.THREE_V_THREE);
        testParty.addPlayer(new Player("P1", 1000));
        testParty.addPlayer(new Player("P2", 1100));
        // Avg: (1000 + 1100) / 2 = 1050
        System.out.println("Avg of [1000, 1100]: " + testParty.calculateAverageRating());

        // 1.3 QueueTicket: Priority Logic (FIFO/Wait Time)
        System.out.println("\n[TEST] QueueTicket Priority (Earlier time comes first):");
        PriorityQueue<QueueTicket> testHeap = new PriorityQueue<>(new TicketComparator());

        long now = System.currentTimeMillis();
        // ticketA is older (should have higher priority as wait time increases)
        QueueTicket ticketA = new QueueTicket(new Party(GameMode.ONE_V_ONE), now - 10000);
        QueueTicket ticketB = new QueueTicket(new Party(GameMode.ONE_V_ONE), now);

        testHeap.add(ticketB);
        testHeap.add(ticketA);

        QueueTicket firstOut = testHeap.poll();
        System.out.println("First ticket out (should be older ticket): " +
                (firstOut == ticketA ? "PASS (Older ticket prioritized)" : "FAIL"));
    }

    private static void testServiceLogic() {
        System.out.println("\n=== 2. SERVICE LOGIC TESTS ===");
        MatchMaker mmr = MatchMaker.getInstance();
        PlayerDatabase db = PlayerDatabase.getInstance();

        // 2.1 Queueing Validations
        System.out.println("\n[TEST] Validation: Party too large for mode:");
        try {
            Party bigParty = new Party(GameMode.ONE_V_ONE); // 1v1 Mode
            bigParty.addPlayer(db.registerPlayer("BigP1", 1000));
            bigParty.addPlayer(db.registerPlayer("BigP2", 1000)); // Should throw error or set open to false
            System.out.println("Result: Party allowed overflow? " + (bigParty.getPartySize() > 1));
        } catch (Exception e) {
            System.out.println("Caught expected size restriction: " + e.getMessage());
        }

        // 2.2 Happy Path Match
        System.out.println("\n[TEST] Happy Path: Two 5v5 parties (~1500 MMR):");
        Party team1 = createFullParty(GameMode.FIVE_V_FIVE, 1500, "T1_");
        Party team2 = createFullParty(GameMode.FIVE_V_FIVE, 1510, "T2_");

        team1.readyParty();
        team2.readyParty();

        Match match = mmr.buildMatch(GameMode.FIVE_V_FIVE);
        if (match != null) {
            System.out.println("Match Formed: " + match);
            System.out.println("\nPlayer 1 State: " + ((Player)team1.getPlayers().get(0)).getState());
        } else {
            System.out.println("FAILED: Match not formed.");
        }

        // 2.3 Team Assembly (Challenge #3)
        System.out.println("\n[TEST] Team Assembly: 3+2 vs 1+4 (All ~1500 MMR):");
        Party p3 = createPartyOfSize(GameMode.FIVE_V_FIVE, 3, 1500, "Size3_");
        Party p2 = createPartyOfSize(GameMode.FIVE_V_FIVE, 2, 1500, "Size2_");
        Party p1 = createPartyOfSize(GameMode.FIVE_V_FIVE, 1, 1500, "Size1_");
        Party p4 = createPartyOfSize(GameMode.FIVE_V_FIVE, 4, 1500, "Size4_");

        p3.readyParty(); p2.readyParty(); p1.readyParty(); p4.readyParty();

        Match complexMatch = mmr.buildMatch(GameMode.FIVE_V_FIVE);
        if (complexMatch != null) {
            System.out.println("Complex Match Formed successfully via recursive assembly.");
        } else {
            System.out.println("FAILED: Could not assemble mixed party sizes.");
        }

        // 2.4 Unbalanced Match (No Dynamic Tolerance)
        System.out.println("\n[TEST] Unbalanced Match (500 vs 3000):");
        Party lowSkill = createFullParty(GameMode.FIVE_V_FIVE, 500, "Low_");
        Party highSkill = createFullParty(GameMode.FIVE_V_FIVE, 3000, "High_");

        lowSkill.readyParty();
        highSkill.readyParty();

        Match unbalancedMatch = mmr.buildMatch(GameMode.FIVE_V_FIVE);
        System.out.println("Unbalanced Match Result: " + (unbalancedMatch != null ? "Matched (Logic permits wide gaps)" : "No Match (Skill gap too high)"));
    }

    public static void testTickLogic(int n) {
        System.out.println("\n=== STARTING TICK LOGIC SIMULATION ===");
        System.out.println("Populating database with " + n + " players...");

        PlayerDatabase db = PlayerDatabase.getInstance();

        // Clear or populate the database with n players
        db.populatePlayerDatabase(n);

        // Retrieve the batch of n players
        ArrayList<Player> batch = db.getRandomBatch(n);

        System.out.println("Batch retrieved. Size: " + batch.size());
        System.out.println("Starting simulation loop (TICK = " + MatchMaker.getInstance().getTick() + "ms)...");

        // Hand off to MatchMaker's simulation loop
        // This will group players into random parties and start the clock
        MatchMaker.getInstance().initializeSimulation(batch);
    }

    // Helper methods to generate test data
    private static Party createFullParty(GameMode mode, int rating, String prefix) {
        return createPartyOfSize(mode, mode.getMaxTeamSize(), rating, prefix);
    }

    private static Party createPartyOfSize(GameMode mode, int size, int rating, String prefix) {
        Party p = new Party(mode);
        for (int i = 0; i < size; i++) {
            p.addPlayer(new Player(prefix + i + "_" + Math.random(), rating, PlayerState.OFFLINE));
        }
        return p;
    }
}
