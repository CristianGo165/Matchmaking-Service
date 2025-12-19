public class Player {
    private final int WIN_INCREASE = 75;
    private PlayerState state;
    private String username;
    private int rating;

    public Player(String username){
        this.username = username;
        this.state = PlayerState.OFFLINE;

    }

    public Player(String username, int rating){
        this.username = username;
        this.rating = rating;
        this.state = PlayerState.OFFLINE;
    }

    public Player(String username, int rating, PlayerState state){
        this.username = username;
        this.rating = rating;
        this.state = state;
    }

    public int getRating(){
        return this.rating;
    }

    public String getUsername() {
        return this.username;
    }

    public PlayerState getState() {
        return this.state;
    }

    public void setState(PlayerState state){
        this.state = state;
    }

    public void increaseRating(){
        this.rating += WIN_INCREASE;
    }

    public void increaseRating(int increment){
        this.rating += increment;
    }

    @Override
    public String toString(){
        return "Player{" + "username=" + username + ", rating=" + rating + ", state=" + state + "}";
    }
}
