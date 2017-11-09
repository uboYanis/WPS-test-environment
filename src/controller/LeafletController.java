package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.simple.parser.ParseException;

import boiteModale.BoiteModale;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import utils.GeoJsonFileUtils;

public class LeafletController implements Initializable {
	private static File inputGeoJson;
	private static File outputGeoJson;

	public static void setJson(File json1, File json2) {
		inputGeoJson = json1;
		outputGeoJson = json2;
	}

	final boolean SHOW_INPUT_GEOJSON = true;
	final boolean HIDDEN_INPUT_GEOJSON = false;
	final boolean SHOW_OUTPUT_GEOJSON = true;
	final boolean HIDDEN_OUTPUT_GEOJSON = false;
	@FXML
	private AnchorPane inputJson;

	@FXML
	private Button input;

	@FXML
	private Button output;

	@FXML
	private TitledPane titlePane;

	@FXML
	private TextField latitude;

	@FXML
	private TextField longitude;

	@FXML
	private TextField zoom;

	@FXML
	private Button find;

	@FXML
	private WebView webView;
	WebEngine webEngine;
	private boolean stateInputGeoJson = SHOW_INPUT_GEOJSON;
	private boolean stateOutputGeoJson = SHOW_OUTPUT_GEOJSON;

	private void showInputGeoJson(final File geoJson) {
		try {
			this.webEngine.executeScript("" + "window.inputJson =" + GeoJsonFileUtils.GeoJsonToString(geoJson) + ";");
			this.webEngine.executeScript("initLayer1(window.inputJson);");
			this.webEngine.executeScript("addLayer1();");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void shwoOutputGeoJson(final File geoJson) {
		try {
			this.webEngine.executeScript("" + "window.outputJson =" + GeoJsonFileUtils.GeoJsonToString(geoJson) + ";");
			this.webEngine.executeScript("initLayer2(window.outputJson);");
			this.webEngine.executeScript("addLayer2();");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void hiddenInputGeoJson() {
		this.webEngine.executeScript("removeLayer1();");
	}

	private void hiddenOutputGeoJson() {
		this.webEngine.executeScript("removeLayer2();");
	}

	private void updateCentre(String latitude, String longitude, String zoom) {
		webEngine.executeScript("Window.lat=" + this.latitude.getText() + ";");
		webEngine.executeScript("Window.lon=" + this.longitude.getText() + ";");
		webEngine.executeScript("Window.zoom=" + this.zoom.getText() + ";");
		webEngine.executeScript("updateCenter(Window.lat,Window.lon,Window.zoom)");
	}

	@FXML
	private void inputGeoJson(ActionEvent e) {
		if (stateInputGeoJson == SHOW_INPUT_GEOJSON) {
			showInputGeoJson(inputGeoJson);
			input.setText("Hidden input GeoJson");
			stateInputGeoJson = HIDDEN_INPUT_GEOJSON;
		} else if (stateInputGeoJson == HIDDEN_INPUT_GEOJSON) {
			hiddenInputGeoJson();
			input.setText("Show input GeoJson");
			stateInputGeoJson = SHOW_INPUT_GEOJSON;
		}
	}

	@FXML
	private void outputGeoJson(ActionEvent e) {
		if (stateOutputGeoJson == SHOW_OUTPUT_GEOJSON) {
			shwoOutputGeoJson(outputGeoJson);
			output.setText("Hidden output GeoJson");
			stateOutputGeoJson = HIDDEN_OUTPUT_GEOJSON;
		} else if (stateOutputGeoJson == HIDDEN_OUTPUT_GEOJSON) {
			hiddenOutputGeoJson();
			output.setText("Show output GeoJson");
			stateOutputGeoJson = SHOW_OUTPUT_GEOJSON;
		}
	}

	@FXML
	private void goTo() {
		String latitude = this.latitude.getText();
		String longitude = this.longitude.getText();
		String zoom = this.zoom.getText();
		if (latitude != null && longitude != null) {
			if (zoom == null) {
				zoom = "6";
			}
			webEngine.executeScript("Window.lat=" + latitude + ";");
			webEngine.executeScript("Window.lon=" + longitude + ";");
			webEngine.executeScript("updateCenter(Window.lat,Window.lon);");
		}else if(latitude==null){
			BoiteModale.erreur("error", "Veuillez saisir la latitude");
		}else{
			BoiteModale.erreur("error", "Veuillez saisir la longitude");
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		webEngine = webView.getEngine();
		final URL urlLeafletMaps = getClass().getResource("newIndex.html");
		webEngine.load(urlLeafletMaps.toExternalForm());
	}

}
