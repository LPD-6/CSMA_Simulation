import java.util.Scanner;

enum ProtocolType {
    SLOTTED_ALOHA, CSMA_CD, CSMA_CA
}

public class NetworkSimulation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int[] hostCounts = getHostCounts(scanner);
        int minFrameSize = getFrameSize(scanner, "minimum", 1);
        int maxFrameSize = getFrameSize(scanner, "maximum", minFrameSize);

        ProtocolType[] protocols = {ProtocolType.SLOTTED_ALOHA, ProtocolType.CSMA_CD, ProtocolType.CSMA_CA};

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
