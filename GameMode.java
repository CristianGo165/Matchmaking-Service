public enum GameMode {
    ONE_V_ONE(1),
    THREE_V_THREE(3),
    FIVE_V_FIVE(5);

    private final int maxTeamSize;

    GameMode(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    
}
