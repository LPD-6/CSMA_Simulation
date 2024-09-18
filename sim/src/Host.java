import java.util.*;

/**
 * Class representing a network host in the simulation.
 */
class Host {
    private int id;
    private Queue<Frame> frames;
    boolean transmitting;
    private int collisionCount;
    private int backoffTime;
    private Random random;
    private int minFrameSize;
    private int maxFrameSize;
    private static final int MAX_BACKOFF = 10;

    /**
     * Constructor for the Host class.
     * @param id Unique identifier for the host
     * @param minFrameSize Minimum frame size
     * @param maxFrameSize Maximum frame size
     */
    public Host(int id, int minFrameSize, int maxFrameSize) {
        this.id = id;
        this.frames = new LinkedList<>();
        this.transmitting = false;
        this.collisionCount = 0;
        this.backoffTime = 0;
        this.random = new Random();
        this.minFrameSize = minFrameSize;
        this.maxFrameSize = maxFrameSize;
    }

    /**
     * Generates a specified number of frames for the host to send.
     * @param count Number of frames to generate
     */
    public void generateFrames(int count) {
        for (int i = 0; i < count; i++) {
            frames.add(new Frame(id, minFrameSize, maxFrameSize));
        }
    }

    /**
     * Checks if the host has any frames left to send.
     * @return true if there are frames to send, false otherwise
     */
    public boolean hasFramesToSend() {
        return !frames.isEmpty();
    }

    /**
     * Gets the current frame to be sent.
     * @return The current frame
     */
    public Frame getCurrentFrame() {
        return frames.peek();
    }

    /**
     * Marks the current frame as successfully sent and removes it from the queue.
     */
    public void frameSuccessfullySent() {
        frames.poll();
        transmitting = false;
        collisionCount = 0;
        backoffTime = 0;
    }

    /**
     * Handles a collision by incrementing the collision count and calculating backoff.
     */
    public void handleCollision() {
        collisionCount++;
        if (collisionCount > 16) {
            frames.poll(); // Discard frame after too many attempts
            collisionCount = 0;
        } else {
            calculateBackoff();
        }
        transmitting = false;
    }

    /**
     * Calculates the backoff time using binary exponential backoff algorithm.
     */
    private void calculateBackoff() {
        int maxBackoff = Math.min(MAX_BACKOFF, collisionCount);
        backoffTime = random.nextInt((1 << maxBackoff)) + 1;
    }

    /**
     * Checks if the host can transmit in the current time slot.
     * @return true if the host can transmit, false otherwise
     */
    public boolean canTransmit() {
        if (backoffTime > 0) {
            backoffTime--;
            return false;
        }
        return true;
    }
}