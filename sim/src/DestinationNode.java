import java.util.Random;

/**
 * Class representing the destination node in the network simulation.
 */
class DestinationNode {
    private boolean mediumBusy;
    private Random random;
    private static final double ACK_SUCCESS_RATE = 0.99;
    private static final double SLOTTED_ALOHA_SUCCESS_RATE = 0.95;
    private static final double CSMA_SUCCESS_RATE = 0.98;

    /**
     * Constructor for the DestinationNode class.
     */
    public DestinationNode() {
        this.mediumBusy = false;
        this.random = new Random();
    }

    /**
     * Checks if the medium is currently busy.
     * @return true if the medium is busy, false otherwise
     */
    public boolean isMediumBusy() {
        return mediumBusy;
    }

    /**
     * Simulates receiving a frame using the specified protocol.
     * @param frame The frame being received
     * @param protocol The protocol being used
     * @return true if the frame was successfully received, false otherwise
     */
    public boolean receiveFrame(Frame frame, ProtocolType protocol) {
        switch (protocol) {
            case SLOTTED_ALOHA:
                return receiveSlottedALOHA(frame);
            case CSMA_CD:
            case CSMA_CA:
                return receiveCSMA(frame);
            default:
                return false;
        }
    }

    /**
     * Simulates receiving a frame using the Slotted ALOHA protocol.
     * @param frame The frame being received
     * @return true if the frame was successfully received, false otherwise
     */
    private boolean receiveSlottedALOHA(Frame frame) {
        if (mediumBusy) {
            return false;
        }
        mediumBusy = true;
        boolean success = random.nextDouble() < SLOTTED_ALOHA_SUCCESS_RATE;
        mediumBusy = false;
        return success;
    }

    /**
     * Simulates receiving a frame using CSMA protocols (CD or CA).
     * @param frame The frame being received
     * @return true if the frame was successfully received, false otherwise
     */
    private boolean receiveCSMA(Frame frame) {
        if (mediumBusy) {
            return false;
        }
        mediumBusy = true;
        boolean success = random.nextDouble() < CSMA_SUCCESS_RATE;
        mediumBusy = false;
        return success;
    }

    /**
     * Simulates sending an acknowledgment (ACK).
     * @return true if the ACK was successfully sent, false otherwise
     */
    public boolean sendACK() {
        return random.nextDouble() < ACK_SUCCESS_RATE;
    }

    /**
     * Resets the medium state to not busy.
     */
    public void resetMediumState() {
        mediumBusy = false;
    }
}