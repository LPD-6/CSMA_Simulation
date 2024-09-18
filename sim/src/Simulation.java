import java.util.*;

/**
 * Class representing the network simulation.
 */
class Simulation {
    private List<Host> hosts;
    private DestinationNode destination;
    private ProtocolType protocol;
    private int currentTimeSlot;
    private int totalTimeSlots;
    private Random random;
    private int minFrameSize;
    private int maxFrameSize;
    private static final double TRANSMISSION_PROBABILITY = 0.3;
    private static final int ACK_SIZE = 1; // Size of ACK in time slots

    /**
     * Constructor for the Simulation class.
     * @param numHosts Number of hosts in the simulation
     * @param protocol Protocol type to be simulated
     * @param minFrameSize Minimum frame size
     * @param maxFrameSize Maximum frame size
     */
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

    /**
     * Runs the simulation until all hosts have sent all their frames.
     */
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

    /**
     * Checks if any hosts still have frames to send.
     * @return true if any host has frames, false otherwise
     */
    private boolean hostsHaveFrames() {
        for (Host host : hosts) {
            if (host.hasFramesToSend()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Runs one time slot of the Slotted ALOHA protocol.
     */
    private void runSlottedALOHA() {
        List<Host> transmittingHosts = new ArrayList<>();

        // Determine which hosts will transmit in this slot
        for (Host host : hosts) {
            if (host.hasFramesToSend() && host.canTransmit()) {
                if (random.nextDouble() < TRANSMISSION_PROBABILITY) {
                    transmittingHosts.add(host);
                    host.transmitting = true;
                }
            }
        }

        if (transmittingHosts.size() == 1) {
            // Successful transmission
            Host transmittingHost = transmittingHosts.get(0);
            Frame frame = transmittingHost.getCurrentFrame();
            boolean success = destination.receiveFrame(frame, ProtocolType.SLOTTED_ALOHA);
            
            // Simulate frame transmission time
            totalTimeSlots += frame.size;
            
            if (success) {
                transmittingHost.frameSuccessfullySent();
            } else {
                transmittingHost.handleCollision();
            }
        } else if (transmittingHosts.size() > 1) {
            // Collision occurred
            for (Host host : transmittingHosts) {
                host.handleCollision();
            }
            // Collision detection time
            totalTimeSlots++;
        }

        destination.resetMediumState();
        totalTimeSlots++; // Count this slot
    }

    /**
     * Runs one time slot of the CSMA/CD protocol.
     */
    private void runCSMA_CD() {
        List<Host> transmittingHosts = new ArrayList<>();

        // Determine which hosts will transmit in this slot
        for (Host host : hosts) {
            if (host.hasFramesToSend() && host.canTransmit()) {
                if (!destination.isMediumBusy()) {
                    transmittingHosts.add(host);
                    host.transmitting = true;
                }
            }
        }

        if (transmittingHosts.size() == 1) {
            // Successful transmission
            Host transmittingHost = transmittingHosts.get(0);
            Frame frame = transmittingHost.getCurrentFrame();
            boolean success = destination.receiveFrame(frame, ProtocolType.CSMA_CD);
            
            totalTimeSlots += frame.size; // Frame transmission time

            if (success) {
                boolean ackReceived = sendAndWaitForAck(transmittingHost);
                if (ackReceived) {
                    transmittingHost.frameSuccessfullySent();
                } else {
                    transmittingHost.handleCollision();
                }
            } else {
                transmittingHost.handleCollision();
            }
        } else if (transmittingHosts.size() > 1) {
            // Collision occurred
            for (Host host : transmittingHosts) {
                host.handleCollision();
            }
            totalTimeSlots++; // Collision detection time
        }

        destination.resetMediumState();
        totalTimeSlots++; // Count this slot
    }

    /**
     * Runs one time slot of the CSMA/CA protocol.
     */
    private void runCSMA_CA() {
        for (Host host : hosts) {
            if (host.hasFramesToSend() && host.canTransmit()) {
                if (!destination.isMediumBusy()) {
                    host.transmitting = true;
                    Frame frame = host.getCurrentFrame();
                    boolean success = destination.receiveFrame(frame, ProtocolType.CSMA_CA);
                    
                    totalTimeSlots += frame.size; // Frame transmission time

                    if (success) {
                        boolean ackReceived = sendAndWaitForAck(host);
                        if (ackReceived) {
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
        totalTimeSlots++; // Count this slot
    }

    /**
     * Simulates sending an ACK and waiting for it to be received.
     * @param host The host that sent the original frame
     * @return true if ACK was received successfully, false otherwise
     */
    private boolean sendAndWaitForAck(Host host) {
        totalTimeSlots += ACK_SIZE; // Time for ACK transmission
        return destination.sendACK();
    }
    /**
     * Gets the total number of time slots used in the simulation.
     * @return Total number of time slots
     */
    public int getTotalTimeSlots() {
        return totalTimeSlots;
    }
}