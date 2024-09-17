import java.util.Random;

class Frame {
    int size;
    int senderID;
    boolean sent;

    public Frame(int senderID, int minSize, int maxSize) {
        this.senderID = senderID;
        this.size = new Random().nextInt(maxSize - minSize + 1) + minSize;
        this.sent = false;
    }
}