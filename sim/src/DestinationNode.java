import java.util.Random;

class DestinationNode {
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
        boolean success = random.nextDouble() < 0.95;
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
            if (random.nextDouble() < 0.05) {
                collision = true;
                mediumBusy = false;
                return false;
            }
        }
        boolean success = random.nextDouble() < 0.99;
        mediumBusy = false;
        collision = false;
        return success;
    }

    private boolean receiveCSMA_CA(Frame frame) {
        if (mediumBusy) {
            return false;
        }
        mediumBusy = true;
        boolean success = random.nextDouble() < 0.98;
        mediumBusy = false;
        return success;
    }

    public void sendACK() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean sendCTS() {
        if (mediumBusy) {
            return false;
        }
        try {
            Thread.sleep(2);
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