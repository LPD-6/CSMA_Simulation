import java.util.LinkedList;
import java.util.Queue;

class Host {
    int id;
    Queue<Frame> frames;
    boolean transmitting;

    public Host(int id) {
        this.id = id;
        this.frames = new LinkedList<>();
        this.transmitting = false;
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
    }
}
