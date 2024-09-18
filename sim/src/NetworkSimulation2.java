/**
 * Enum representing the different types of network protocols that can be simulated.
 */
enum ProtocolType {
    SLOTTED_ALOHA, CSMA_CD, CSMA_CA
}

/**
 * Main class for running network protocol simulations.
 */
public class NetworkSimulation2 {
    public static void main(String[] args) {
        // Fixed number of hosts
        int hostCount = 16;

        // Define protocols to be simulated
        ProtocolType[] protocols = {ProtocolType.SLOTTED_ALOHA, ProtocolType.CSMA_CD, ProtocolType.CSMA_CA};

        // Define frame size ranges
        int[][] frameSizeRanges = {{5, 10}, {10, 15}, {15, 20}, {20, 25}};

        // Run simulations for each protocol and frame size range
        for (ProtocolType protocol : protocols) {
            System.out.println("Protocol: " + protocol);
            for (int[] range : frameSizeRanges) {
                int minFrameSize = range[0];
                int maxFrameSize = range[1];
                Simulation sim = new Simulation(hostCount, protocol, minFrameSize, maxFrameSize);
                sim.runSimulation();
                System.out.println("Frame size range: [" + minFrameSize + ", " + maxFrameSize + 
                                   "], Total time slots: " + sim.getTotalTimeSlots());
            }
            System.out.println();
        }
    }
}