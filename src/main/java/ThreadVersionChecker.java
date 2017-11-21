import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Arrays;

public class ThreadVersionChecker extends Thread{
    private String infoURL = "";
    private Version localVersion = null;
    public ThreadVersionChecker(String url, Version version) {
        setName("FLA Version Checker");
        this.infoURL = url;
        localVersion = version;
        setDaemon(false); //Don't close the program until this thread finishes
    }

    @Override
    public void run() {
        try {
            URL url = new URL(infoURL);
            BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
            Version latestVersion = new Version(r.readLine().trim());
            URL website = new URL(r.readLine());
            System.out.println("Latest version: " + latestVersion);
            if(localVersion == null || latestVersion.compareTo(localVersion) > 0 && !latestVersion.equals(localVersion)) {
                System.out.println("Version out of date or jar missing. Beginning Download");
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                String jarPath = "\\FLA-" + latestVersion.get() + ".jar";
                FileOutputStream fos = new FileOutputStream(Constants.programFolder + jarPath);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                r.close();
                fos.close();
                rbc.close();
                if(Constants.jarPath != null){
                    System.out.println("attempting delete of file at path: " + Constants.jarPath.toPath().toString());
                    if(Constants.programFolder.listFiles() != null && Arrays.asList((Constants.programFolder.listFiles())).size() > 1) {
                        if (Files.deleteIfExists(Constants.jarPath.toPath())) {
                            System.out.println("Old version deleted");
                        } else {
                            System.out.println("Failed to delete.");
                        }
                    }else {
                        System.out.println("Improper versioning detected!");
                    }
                }
                Constants.jarPath = new File(Constants.programFolder.toString() + jarPath);
            }
            FlaUpdater.launchJar(Constants.jarPath.toPath().toString());
        } catch(Exception e) {
            e.printStackTrace();

        }
    }
}