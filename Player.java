public class Player {
    private String username;
    private int rating;
    private PlayerState state;
    private final int WIN_INCREASE = 75;

    public Player(String username){
        this.username = username;
    }
    public Player(String username, int rating){
        this.username = username;
        this.rating = rating;

    }
    public int getRating(){
        return this.rating;
    }
    public void increaseRating(){
        this.rating += WIN_INCREASE;
    }
    public void increaseRating(int increment){
        this.rating += increment;
    }
    public void updatePlayerState(PlayerState state){
        this.state = state;
    }
    public void createParty(GameMode mode, boolean custom){

    }
    public void joinParty(Party partyID){

    }
}
