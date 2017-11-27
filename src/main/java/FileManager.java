import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    private static String OSProgramPath;

    public static void setupFolders() {
        Alert folderAlert = new Alert(Alert.AlertType.ERROR, "Error setting up folders! Contact the developer :(", ButtonType.OK);
        System.out.println("Searching for resource folder");
        Constants.mainFolder = new File(getFilePath());
        Constants.programFolder = new File(Constants.mainFolder.toString() + OSProgramPath);

        if(!Constants.mainFolder.exists()) {
            System.out.println("Making main folder");
            if(!Constants.mainFolder.mkdir()){
                System.out.println("Failed creating main folder");
                folderAlert.showAndWait();
                System.exit(1);
            }
        }
        if(!Constants.programFolder.exists()) {
            System.out.println("Making program folder");
            if(!Constants.programFolder.mkdir()){
                System.out.println("Failed making program folder");
                folderAlert.showAndWait();
                System.exit(1);
            }
        }

        List<File> list = Arrays.asList((Constants.programFolder.listFiles()));
        for (File f : list) {
            if (f.toPath().toString().contains(".jar")) {
                Constants.jarPath = f;
                break;
            }
        }

    }

    private static String getFilePath(){
        String FileFolder = System.getenv("APPDATA") + "\\" + "FabLabAnalytics";
        System.out.println("Searching for system");
        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) {
            FileFolder = System.getenv("APPDATA") + "\\" + "FabLabAnalytics";
            OSProgramPath = ("\\program");
            System.out.println("Found windows");
        }
        if (os.contains("MAC")) {
            FileFolder = System.getProperty("user.home") + "/Library/Application Support" + "/FabLabAnalytics";
            OSProgramPath = ("/program");
            System.out.println("Found mac");
        }
        if (os.contains("NUX")) {
            FileFolder = System.getProperty("user.dir") + ".FabLabAnalytics";
            OSProgramPath = (".program");
            System.out.println("Found linux");
        }
        System.out.println(FileFolder);
        return FileFolder;
    }
}
