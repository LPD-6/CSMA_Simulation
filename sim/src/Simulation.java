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
            } else {
                transmittingHost.transmitting = false;
                // For Slotted ALOHA, we don't use the backoff mechanism
                // Instead, the host will try again in a future slot
            }
        } else if (transmittingHosts.size() > 1) {
            // Collision occurred
            for (Host host : transmittingHosts) {
                host.transmitting = false;
                // Again, for Slotted ALOHA, we don't use the backoff mechanism
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
            // Collision occurred
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
                    // Send RTS
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
                        // CTS not received, treat as collision
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
