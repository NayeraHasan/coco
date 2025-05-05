import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

public class Logger {
    private static final String FILE_NAME = "bst_log.csv";
    private static final Object lock = new Object();
    private static boolean initialized = false;

    public static void log(String thread, String lockType, String method, String action, String value) {
        synchronized (lock) {
            try (FileWriter fw = new FileWriter(FILE_NAME, true);
                 PrintWriter out = new PrintWriter(fw)) {

                // Write header once
                if (!initialized) {
                    out.println("Timestamp,Thread,LockType,Method,Action,Value");
                    initialized = true;
                }

                String timestamp = Instant.now().toString();
                out.printf("%s,%s,%s,%s,%s,%s%n", timestamp, thread, lockType, method, action, value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
