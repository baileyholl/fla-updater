import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Update wrapper for the Fabrication Lab Anayltics tool.
 * NOTE: THIS CLASS MUST HAVE A DIFFERENT NAME THAN THE REFLECTED CLASS BELOW.
 *
 */
public class FLAUpdater extends Application{

    /*
     * Currently, this file must contain the file version on the first line and a direct download link on the second.
     * Ex:
     * 1.3.2
     * https://github.com/baileyholl/fla-updater/raw/master/fla-jars/Sign%20In%201.3.3.jar
     */
    public static String versionCheckFile = "https://raw.githubusercontent.com/baileyholl/fla-updater/master/version-file";

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
                //Load the FLAUpdater class from the default package
                Class<?> clazz = Class.forName("Main", true, urlClassLoader);
                //Get and call the public static method getVersion
                Method getVersion = clazz.getMethod("getVersion");
                version = new Version((String) getVersion.invoke(null));
                System.out.println("Local jar version: " + version);
                urlClassLoader.close();
            }catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Reflection failure. Contact developer! \n" + e.getMessage(), ButtonType.OK);
                alert.showAndWait();
                version = null;
            }
        }
        ThreadVersionChecker threadVersionChecker = new ThreadVersionChecker(versionCheckFile, version);
        threadVersionChecker.run();
    }

    /**
     * Attempts to launch the jar at the given path
     * @param jarPath Path of jar file
     */
    public static void launchJar(String jarPath){
        try {
            Runtime.getRuntime().exec("java -jar " + jarPath.trim());
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failure launching jar file! Contact developer! \n" + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
        System.exit(1);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FileManager.setupFolders();
            FLAUpdater FLAUpdater = new FLAUpdater();
            FLAUpdater.loadVersion();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        launch(args);
    }
}
