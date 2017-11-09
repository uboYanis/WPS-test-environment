package utils;

import java.io.File;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Utils {

	public static File fileChooser(String title, String absolutePath, String nameEx, String extension) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		if (absolutePath != null) {
			File userDirectory = new File(absolutePath);
			fileChooser.setInitialDirectory(userDirectory);
		}
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(nameEx, extension));
		return fileChooser.showOpenDialog(new Stage());
	}

	public static File folderChooser(String title, String absolutePath) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle(title);
		if (absolutePath != null) {
			File userDirectory = new File(absolutePath);
			directoryChooser.setInitialDirectory(userDirectory);
		}
		return directoryChooser.showDialog(new Stage());
	}
	
	public static String subStr(String project, String pack) {
		return pack.substring(project.length(), pack.length()).replace('\\', '.');
	}
}
