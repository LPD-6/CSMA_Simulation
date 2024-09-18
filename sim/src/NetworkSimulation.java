import java.util.Scanner;

/**
 * Enum representing the different types of network protocols that can be simulated.
 */
enum ProtocolType {
    SLOTTED_ALOHA, CSMA_CD, CSMA_CA
}

/**
 * Main class for running network protocol simulations.
 */
public class NetworkSimulation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get simulation parameters from user
        int[] hostCounts = getHostCounts(scanner);
        int minFrameSize = getFrameSize(scanner, "minimum", 1);
        int maxFrameSize = getFrameSize(scanner, "maximum", minFrameSize);

        // Define protocols to be simulated
        ProtocolType[] protocols = {ProtocolType.SLOTTED_ALOHA, ProtocolType.CSMA_CD, ProtocolType.CSMA_CA};

        // Run simulations for each protocol and host count
        for (ProtocolType protocol : protocols) {
            System.out.println("Protocol: " + protocol);
            for (int hostCount : hostCounts) {
                Simulation sim = new Simulation(hostCount, protocol, minFrameSize, maxFrameSize);
                sim.runSimulation();
                System.out.println("Hosts: " + hostCount + ", Total time slots: " + sim.getTotalTimeSlots());
            }
            System.out.println();
        }

        scanner.close();
    }

    /**
     * Prompts the user to enter the number of hosts to simulate.
     * @param scanner Scanner object for user input
     * @return Array of host counts
     */
    private static int[] getHostCounts(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Enter the number of host counts to simulate (comma-separated):");
                String[] hostCountsStr = scanner.nextLine().split(",");
                int[] hostCounts = new int[hostCountsStr.length];
                for (int i = 0; i < hostCountsStr.length; i++) {
                    hostCounts[i] = Integer.parseInt(hostCountsStr[i].trim());
                    if (hostCounts[i] <= 0) {
                        throw new IllegalArgumentException("Host count must be positive");
                    }
                }
                return hostCounts;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter integers separated by commas.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Prompts the user to enter the frame size.
     * @param scanner Scanner object for user input
     * @param sizeType String indicating whether it's for minimum or maximum frame size
     * @param minValue Minimum allowed value for the frame size
     * @return The frame size entered by the user
     */
    private static int getFrameSize(Scanner scanner, String sizeType, int minValue) {
        while (true) {
            try {
                System.out.println("Enter the " + sizeType + " frame size (in time slots):");
                int size = Integer.parseInt(scanner.nextLine().trim());
                if (size < minValue) {
                    throw new IllegalArgumentException(sizeType + " frame size must be at least " + minValue);
                }
                return size;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}