import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.ArrayList;
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
            System.out.println("Latest version found: " + latestVersion + " Local Version: " + localVersion);
            if(localVersion == null || (latestVersion.compareTo(localVersion) > 0 && !latestVersion.get().equals(localVersion.get()))) {
                System.out.println("Version out of date or jar missing. Beginning Download");
                Alert versionUpdate = new Alert(Alert.AlertType.INFORMATION,"Updating to the latest iHub analytics version! :)", ButtonType.OK);
                versionUpdate.show();
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
                        System.out.println(Files.deleteIfExists(Constants.jarPath.toPath()) ? "Old version deleted" : "Failed to delete.");
                    }else {
                        System.out.println("Improper versioning detected!");
                        System.out.println("ListFile: " + Constants.programFolder.listFiles() + "Size: " + Arrays.asList(Constants.programFolder.listFiles()).size());
                        Alert improperVersionAlert = new Alert(Alert.AlertType.ERROR,"Improper versioning detected with online version file. Contact developer!", ButtonType.OK);
                        improperVersionAlert.showAndWait();
                    }
                }
                Constants.jarPath = new File(Constants.programFolder.toString() + jarPath);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Alert urlAlert = new Alert(Alert.AlertType.ERROR,"Unable to resolve host. Internet connection may be down! Attempting to launch old version.", ButtonType.OK);
            urlAlert.showAndWait();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Alert urlAlert = new Alert(Alert.AlertType.ERROR,"Unable to find proper files. Did you modify folders during run time? Contact developer." , ButtonType.OK);
            urlAlert.showAndWait();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Alert urlAlert = new Alert(Alert.AlertType.ERROR,"Malformed URL. Contact developer!", ButtonType.OK);
            urlAlert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert urlAlert = new Alert(Alert.AlertType.ERROR,"IO Exception. Contact developer! Attempting to launch old version.", ButtonType.OK);
            urlAlert.showAndWait();
        }
        FLAUpdater.launchJar(Constants.jarPath.toPath().toString());
    }

}