import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;

public class PlayerDatabase {
    private static PlayerDatabase instance = null;
    public static synchronized PlayerDatabase getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new PlayerDatabase();
    }

    private PlayerDatabase(){
        database = new HashMap<>();
        usernames = new HashMap<>();
    }

    HashMap<String, Player> database;
    HashMap<Integer, String> usernames;
    private static int playerIdentifier = 0;

    public void populatePlayerDatabase(int numPlayers){
        ArrayList<Player> players = new ArrayList<>();
        for(int i = 0 ; i < numPlayers ; i++){
            Player player = new Player("Player_" + playerIdentifier, 1000 + (int)(Math.random() * 3000));
            database.put(player.getUsername(), player);
            usernames.put(playerIdentifier, (player.getUsername()));
            playerIdentifier++;
        }
//        System.out.println(database);
//        System.out.println(usernames);
    }
    public ArrayList<Player> getRandomBatch(int numPlayers){
        ArrayList<Player> databaseList = new ArrayList<>(database.values());

        Collections.shuffle(databaseList);

        return new ArrayList<>(databaseList.subList(0, Math.min(numPlayers, databaseList.size())));
    }

    public Player getPlayer(String username){
        return database.get(username);
    }

    public Player registerPlayer(String username){
        if(database.get(username) != null) throw new IllegalArgumentException("PLAYER IS ALREADY REGISTERED");
        return database.put(username, new Player(username));
    }
}
