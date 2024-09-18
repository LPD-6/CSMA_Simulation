import java.util.Random;

/**
 * Class representing a network frame in the simulation.
 */
class Frame {
    int size;
    int senderID;
    boolean sent;

    /**
     * Constructor for the Frame class.
     * @param senderID ID of the host sending the frame
     * @param minSize Minimum size of the frame
     * @param maxSize Maximum size of the frame
     */
    public Frame(int senderID, int minSize, int maxSize) {
        this.senderID = senderID;
        this.size = new Random().nextInt(maxSize - minSize + 1) + minSize;
        this.sent = false;
    }
}
