package utility;

import utility.*;

public class KillAllProcess {
    public KillAllProcess() {
        try {
            Runtime.getRuntime().exec(Constants.KILL_PROCESS);
        } catch (Exception e) {
            System.out.println("Failure in killing process: " + e.getMessage());
        }
    }
}
