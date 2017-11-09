package boiteModale;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * cette class permet de cr�e des alert de type INFORMATION ou ERROR
 * 
 * @author REMILA Yanis
 * @version 1.1
 */
public class BoiteModale {
	/**
	 * permet de cr�e une alert INFORMATION
	 * 
	 * @param titre
	 *            : titre de l'alerte
	 * @param message
	 *            : le message de l'alerte
	 */
	public static void information(String titre, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titre);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * permet de cr�e une alert ERROR
	 * 
	 * @param titre
	 *            : titre de l'alerte
	 * @param message
	 *            : le message de l'alerte
	 */
	public static void erreur(String titre, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(titre);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

}
