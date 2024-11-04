package xyz.sunqian.common.net;

/**
 * Represents server states.
 *
 * @author fredsuvn
 */
public class GekServerStates {

    /**
     * Created state: the channel or endpoint never open or close. This is the initialized state.
     */
    public static final int CREATED = 0;
    /**
     * Opened state: the channel or endpoint has been opened.
     */
    public static final int OPENED = 1;
    /**
     * Closed state: the channel or endpoint has been closed.
     */
    public static final int CLOSED = 2;

    private int state = CREATED;

    /**
     * Returns whether current state is {@link #CREATED}.
     *
     * @return whether current state is {@link #CREATED}
     */
    public boolean isCreated() {
        return state == CREATED;
    }

    /**
     * Returns whether current state is {@link #OPENED}.
     *
     * @return whether current state is {@link #OPENED}
     */
    public boolean isOpened() {
        return state == OPENED;
    }

    /**
     * Returns whether current state is {@link #CLOSED}.
     *
     * @return whether current state is {@link #CLOSED}
     */
    public boolean isClosed() {
        return state == CLOSED;
    }

    /**
     * Returns current state: {@link #CREATED}, {@link #OPENED} or {@link #CLOSED}.
     *
     * @return current state
     */
    public int getState() {
        return state;
    }

    /**
     * Sets state to {@link #OPENED}.
     */
    public void open() {
        state = OPENED;
    }

    /**
     * Sets state to {@link #CLOSED}.
     */
    public void close() {
        state = CLOSED;
    }
}
