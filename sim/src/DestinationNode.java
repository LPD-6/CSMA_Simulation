import java.util.Random;

public class DestinationNode {
    private boolean mediumBusy;
    private boolean collision;
    private Random random;

    public DestinationNode() {
        this.mediumBusy = false;
        this.collision = false;
        this.random = new Random();
    }

    public boolean isMediumBusy() {
        return mediumBusy;
    }

    public boolean isCollisionDetected() {
        return collision;
    }

    public boolean receiveFrame(Frame frame, ProtocolType protocol) {
        switch (protocol) {
            case SLOTTED_ALOHA:
                return receiveSlottedALOHA(frame);
            case CSMA_CD:
                return receiveCSMA_CD(frame);
            case CSMA_CA:
                return receiveCSMA_CA(frame);
            default:
                return false;
        }
    }

    private boolean receiveSlottedALOHA(Frame frame) {
        if (mediumBusy) {
            collision = true;
            return false;
        }
        mediumBusy = true;
        boolean success = random.nextDouble() < 0.95; // 95% chance of successful transmission
        mediumBusy = false;
        return success;
    }

    private boolean receiveCSMA_CD(Frame frame) {
        if (mediumBusy) {
            collision = true;
            return false;
        }
        mediumBusy = true;
        for (int i = 0; i < frame.size; i++) {
            if (random.nextDouble() < 0.05) { // 5% chance of collision during transmission
                collision = true;
                mediumBusy = false;
                return false;
            }
        }
        boolean success = random.nextDouble() < 0.99; // 99% chance of successful transmission
        mediumBusy = false;
        collision = false;
        return success;
    }

    private boolean receiveCSMA_CA(Frame frame) {
        if (mediumBusy) {
            return false; // No collision detection, just fails to receive
        }
        mediumBusy = true;
        boolean success = random.nextDouble() < 0.98; // 98% chance of successful transmission
        mediumBusy = false;
        return success;
    }

    public void sendACK() {
        // Simulate sending an ACK
        try {
            Thread.sleep(2); // ACK takes 2 time slots
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean sendCTS() {
        // Simulate sending CTS for CSMA/CA
        if (mediumBusy) {
            return false;
        }
        try {
            Thread.sleep(2); // CTS takes 2 time slots
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void resetMediumState() {
        mediumBusy = false;
        collision = false;
    }
}