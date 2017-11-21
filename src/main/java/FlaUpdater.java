import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 * Update wrapper for the Fabrication Lab Anayltics tool.
 *
 */
public class FlaUpdater {
    /*
     * Currently, this file must contain the file version on the first line and a direct download link on the second.
     * Ex:
     * 1.3.2
     * https://github.com/baileyholl/fla-updater/raw/master/fla-jars/Sign%20In%201.3.3.jar
     */
    public static String versionCheckFile = "https://raw.githubusercontent.com/baileyholl/fla-updater/master/version-file";

    public static void main(String[] args){
        try {
            FileManager.setupFolders();
            FlaUpdater flaUpdater = new FlaUpdater();
            flaUpdater.loadVersion();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadVersion(){
        FileManager.setupFolders();
        Version version = null;
        if(Constants.jarPath != null) {
            System.out.println("Path to class load: " + Constants.jarPath);
            try {
                URLClassLoader  urlClassLoader = URLClassLoader.newInstance(
                        new URL[]{new File(Constants.jarPath.toPath().toString()).toURI().toURL()},
                        getClass().getClassLoader()
                );
                //ClassLoader loader = urlClassLoader;
                //Load the Main class from the default package
                Class<?> clazz = Class.forName("Main", true, urlClassLoader);
                //Get and call the public static method getVersion
                Method getVersion = clazz.getMethod("getVersion");
                version = new Version((String) getVersion.invoke(null));
                System.out.println(version);
                urlClassLoader.close();
            }catch(Exception e){
                e.printStackTrace();
                version = null;
            }
        }
        ThreadVersionChecker threadVersionChecker = new ThreadVersionChecker(versionCheckFile, version);
        threadVersionChecker.run();
    }
    public static void launchJar(String jarPath){
        try {
            Process proc = Runtime.getRuntime().exec("java -jar " + jarPath.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
