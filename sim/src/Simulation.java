import java.util.*;

class Simulation {
    List<Host> hosts;
    DestinationNode destination;
    ProtocolType protocol;
    int currentTimeSlot;
    int totalTimeSlots;
    Random random;
    int minFrameSize;
    int maxFrameSize;

    public Simulation(int numHosts, ProtocolType protocol, int minFrameSize, int maxFrameSize) {
        this.hosts = new ArrayList<>();
        for (int i = 0; i < numHosts; i++) {
            Host host = new Host(i, minFrameSize, maxFrameSize);
            host.generateFrames(1000);
            hosts.add(host);
        }
        this.destination = new DestinationNode();
        this.protocol = protocol;
        this.currentTimeSlot = 0;
        this.totalTimeSlots = 0;
        this.random = new Random();
        this.minFrameSize = minFrameSize;
        this.maxFrameSize = maxFrameSize;
    }

    public void runSimulation() {
        while (hostsHaveFrames()) {
            currentTimeSlot++;
            totalTimeSlots++;

            switch (protocol) {
                case SLOTTED_ALOHA:
                    runSlottedALOHA();
                    break;
                case CSMA_CD:
                    runCSMA_CD();
                    break;
                case CSMA_CA:
                    runCSMA_CA();
                    break;
            }
        }
    }

    private boolean hostsHaveFrames() {
        for (Host host : hosts) {
            if (host.hasFramesToSend()) {
                return true;
            }
        }
        return false;
    }

    private void runSlottedALOHA() {
        List<Host> transmittingHosts = new ArrayList<>();

        for (Host host : hosts) {
            if (host.hasFramesToSend() && random.nextDouble() < 0.5) { // 50% chance to transmit
                transmittingHosts.add(host);
                host.transmitting = true;
            }
        }

        if (transmittingHosts.size() == 1) {
            Host transmittingHost = transmittingHosts.get(0);
            Frame frame = transmittingHost.getCurrentFrame();
            if (destination.receiveFrame(frame, ProtocolType.SLOTTED_ALOHA)) {
                transmittingHost.frameSuccessfullySent();
            } else {
                transmittingHost.transmitting = false;
            }
        } else if (transmittingHosts.size() > 1) {
            for (Host host : transmittingHosts) {
                host.transmitting = false;
            }
        }

        destination.resetMediumState();
    }

    private void runCSMA_CD() {
        List<Host> transmittingHosts = new ArrayList<>();

        for (Host host : hosts) {
            if (host.hasFramesToSend() && host.canTransmit()) {
                if (!destination.isMediumBusy()) {
                    transmittingHosts.add(host);
                    host.transmitting = true;
                }
            }
        }

        if (transmittingHosts.size() == 1) {
            Host transmittingHost = transmittingHosts.get(0);
            Frame frame = transmittingHost.getCurrentFrame();
            if (destination.receiveFrame(frame, ProtocolType.CSMA_CD)) {
                transmittingHost.frameSuccessfullySent();
            } else {
                transmittingHost.handleCollision();
            }
        } else if (transmittingHosts.size() > 1) {
            for (Host host : transmittingHosts) {
                host.handleCollision();
            }
        }

        destination.resetMediumState();
    }

    private void runCSMA_CA() {
        for (Host host : hosts) {
            if (host.hasFramesToSend() && host.canTransmit()) {
                if (!destination.isMediumBusy()) {
                    if (destination.sendCTS()) {
                        host.transmitting = true;
                        Frame frame = host.getCurrentFrame();
                        if (destination.receiveFrame(frame, ProtocolType.CSMA_CA)) {
                            destination.sendACK();
                            host.frameSuccessfullySent();
                        } else {
                            host.handleCollision();
                        }
                    } else {
                        host.handleCollision();
                    }
                }
            }
        }

        destination.resetMediumState();
    }

    public int getTotalTimeSlots() {
        return totalTimeSlots;
    }
}