public class App {
    public static void main(String[] args){
        PlayerDatabase.getInstance().populatePlayerDatabase(2000);
        //System.out.println(PlayerDatabase.getInstance().getRandomBatch(30));
        MatchMaker.getInstance().initializeSimulation(PlayerDatabase.getInstance().getRandomBatch(200), false );

    }
}
