import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Host {
    int id;
    Queue<Frame> frames;
    boolean transmitting;
    int collisionCount;
    int backoffTime;
    Random random;

    public Host(int id) {
        this.id = id;
        this.frames = new LinkedList<>();
        this.transmitting = false;
        this.collisionCount = 0;
        this.backoffTime = 0;
        this.random = new Random();
    }

    public void generateFrames(int count) {
        for (int i = 0; i < count; i++) {
            frames.add(new Frame(id));
        }
    }

    public boolean hasFramesToSend() {
        return !frames.isEmpty();
    }

    public Frame getCurrentFrame() {
        return frames.peek();
    }

    public void frameSuccessfullySent() {
        frames.poll();
        transmitting = false;
        collisionCount = 0;
        backoffTime = 0;
    }

    public void handleCollision() {
        collisionCount++;
        if (collisionCount > 16) {
            // Too many collisions, discard the frame
            frames.poll();
            collisionCount = 0;
        } else {
            calculateBackoff();
        }
        transmitting = false;
    }

    private void calculateBackoff() {
        int maxBackoff = Math.min(10, collisionCount); // Cap at 2^10
        backoffTime = random.nextInt((1 << maxBackoff)) + 1;
    }

    public boolean canTransmit() {
        if (backoffTime > 0) {
            backoffTime--;
            return false;
        }
        return true;
    }
}