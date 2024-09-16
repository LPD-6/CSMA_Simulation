import java.util.Random;

class Frame {
    int size;
    int senderID;
    boolean sent;

    public Frame(int senderID) {
        this.senderID = senderID;
        this.size = new Random().nextInt(9) + 8; // Random size between 8 and 16
        this.sent = false;
    }
}