import java.util.*;

class Simulation {
    List<Host> hosts;
    DestinationNode destination;
    ProtocolType protocol;
    int currentTimeSlot;
    int totalTimeSlots;
    Random random;

    public Simulation(int numHosts, ProtocolType protocol) {
        this.hosts = new ArrayList<>();
        for (int i = 0; i < numHosts; i++) {
            Host host = new Host(i);
            host.generateFrames(1000);
            hosts.add(host);
        }
        this.destination = new DestinationNode();
        this.protocol = protocol;
        this.currentTimeSlot = 0;
        this.totalTimeSlots = 0;
        this.random = new Random();
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
            }
        } else if (transmittingHosts.size() > 1) {
            // Collision occurred, hosts will retry in future slots
            for (Host host : transmittingHosts) {
                host.transmitting = false;
            }
        }
    }

    private void runCSMA_CD() {
        // Simplified CSMA/CD implementation
        for (Host host : hosts) {
            if (host.hasFramesToSend() && !host.transmitting) {
                if (random.nextDouble() < 0.8) { // 80% chance medium is free
                    host.transmitting = true;
                    Frame frame = host.getCurrentFrame();
                    if (destination.receiveFrame(frame, ProtocolType.CSMA_CD)) {
                        host.frameSuccessfullySent();
                    }
                }
            }
        }
    }

    private void runCSMA_CA() {
        // Simplified CSMA/CA implementation
        for (Host host : hosts) {
            if (host.hasFramesToSend() && !host.transmitting) {
                if (random.nextDouble() < 0.8) { // 80% chance medium is free
                    // Send RTS
                    if (random.nextDouble() < 0.9) { // 90% chance RTS is successful
                        // Received CTS
                        host.transmitting = true;
                        Frame frame = host.getCurrentFrame();
                        if (destination.receiveFrame(frame, ProtocolType.CSMA_CA)) {
                            host.frameSuccessfullySent();
                        }
                    }
                }
            }
        }
    }

    public int getTotalTimeSlots() {
        return totalTimeSlots;
    }
}
