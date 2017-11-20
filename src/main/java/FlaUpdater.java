import java.io.File;
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
        try {
            FileManager.setupFolders();
            if(Constants.jarPath == null) {
                System.out.println("No jar existing");

            }


            if(Constants.jarPath != null) {
                System.out.println("Path to class load: " + Constants.jarPath);
                ClassLoader loader = URLClassLoader.newInstance(
                        new URL[]{new File(Constants.jarPath).toURI().toURL()},
                        getClass().getClassLoader()
                );
                //Load the Main class from the default package
                Class<?> clazz = Class.forName("Main", true, loader);
                //Get and call the public static method getVersion
                Method getVersion = clazz.getMethod("getVersion");
                String version = (String) getVersion.invoke(null);
                System.out.println(version);
                //Process proc = Runtime.getRuntime().exec("java -jar " + Constants.jarPath);
            }else{
                System.out.println("Path is empty!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
