enum ProtocolType {
    SLOTTED_ALOHA, CSMA_CD, CSMA_CA
}

public class NetworkSimulation {
    public static void main(String[] args) {
        int[] hostCounts = {5, 10, 15, 20, 25};
        ProtocolType[] protocols = {ProtocolType.SLOTTED_ALOHA, ProtocolType.CSMA_CD, ProtocolType.CSMA_CA};

        for (ProtocolType protocol : protocols) {
            System.out.println("Protocol: " + protocol);
            for (int hostCount : hostCounts) {
                Simulation sim = new Simulation(hostCount, protocol);
                sim.runSimulation();
                System.out.println("Hosts: " + hostCount + ", Total time slots: " + sim.getTotalTimeSlots());
            }
            System.out.println();
        }
    }
}