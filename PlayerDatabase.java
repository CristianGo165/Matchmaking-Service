import java.util.ArrayList;
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
        ArrayList<Player> players = new ArrayList<>();
        for(int i = 0 ; i < numPlayers ; i++){
            int randomUser = (int) (Math.random() * database.size());
            while(usernames.get(randomUser) != null &&
                    (database.get(usernames.get(randomUser)).getState() != PlayerState.PLAYING ||
                            database.get(usernames.get(randomUser)).getState() != PlayerState.LOBBY)){
                players.add(database.get(usernames.get(randomUser)));
            }
        }
        return players;
    }

}
