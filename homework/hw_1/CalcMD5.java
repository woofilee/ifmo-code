import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

/**
 * Created by woofi on 12.02.2016.
 */
public class CalcMD5 {
    public static void main(String[] args) throws Exception {
        for (String testFile : Files.readAllLines(Paths.get(args[0]))) {
            for (byte md5byte : MessageDigest.getInstance("MD5").digest(Files.readAllBytes(Paths.get(testFile)))) {
                System.out.printf("%02X", md5byte);
            }
            System.out.println();
        }
    }
}
