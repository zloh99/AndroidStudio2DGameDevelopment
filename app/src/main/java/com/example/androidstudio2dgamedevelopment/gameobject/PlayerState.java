package com.example.androidstudio2dgamedevelopment.gameobject;

public class PlayerState {

    //enumeration - array of different variables that can be accessed
    public enum State {
        NOT_M0VING,
        STARTED_MOVING,
        IS_MOVING
    }

    private Player player;
    private State state;

    public PlayerState(Player player) {
        this.player = player;
        this.state = State.NOT_M0VING;
    }

    public State getState() {
        return state;
    }

    public void update() {
        switch (state) {
            case NOT_M0VING:
                if (player.velocityX != 0 || player.velocityY != 0)
                    state = State.STARTED_MOVING;
                break;
            case STARTED_MOVING:
                if (player.velocityX != 0 || player.velocityY != 0)
                    state = State.IS_MOVING;
                break;
            case IS_MOVING:
                if (player.velocityX == 0 && player.velocityY == 0)
                    state = State.NOT_M0VING;
                break;
            default:
                break;
        }
    }
}
