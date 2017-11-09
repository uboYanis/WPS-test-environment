package controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

import boiteModale.BoiteModale;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import processing.Processing;
import utils.GeoJsonFileUtils;
import utils.Utils;
import utils.VerifyAnnotation;

public class AppController implements Initializable {
	GeoJsonFileUtils geoJsonFileUtils = new GeoJsonFileUtils();
	Processing processing = new Processing();
	Class c = null;
	@FXML
	private Button chooseJsonFile;

	@FXML
	private TextField displayJsonPath;

	@FXML
	private Button chooseJavaFile;

	@FXML
	private TextField displayJavaPath;

	@FXML
	private Button chooseProject;

	@FXML
	private TextField displayProjectPath;

	@FXML
	private TextField displayPackageFile;

	@FXML
	private Button choosePackage;

	@FXML
	private ChoiceBox<Method> listMethode;

	@FXML
	private Button displayResult;

	@FXML
	private Button displayMap;

	@FXML
	private TextField outputJsonFile;

	@FXML
	private Button clearJsonField;

	@FXML
	private Button clearProjectField;

	@FXML
	private Button clearPackageField;

	@FXML
	private Button clearJavaField;

	private File jsonFile;
	private File projectFile;
	private File packageFile;
	private File javaFile;

	private File outputFile;

	private String packageName;
	private String nameClass;

	private boolean isGeometry, isFeatureCollection;

	@FXML
	void chooseJsonFile(ActionEvent event) {
		try {
			jsonFile = Utils.fileChooser("Open Resource Json File", null, "Json file",
					"*.json*");
			if (jsonFile != null) {
				isGeometry = GeoJsonFileUtils.isGeometryData(jsonFile);
				isFeatureCollection = GeoJsonFileUtils.isFeatureCollectionData(jsonFile);
				outputJsonFile.setText("output_" + jsonFile.getName().replace(".json", ""));
				displayJsonPath.setText(jsonFile.getAbsolutePath());
			}
		} catch (Exception e) {
			BoiteModale.erreur("Error", e.getMessage());
		}
	}

	@FXML
	void chooseProjectFolder(ActionEvent event) {
		try {
			projectFile = Utils.folderChooser("choose project", null);
			if (projectFile != null) {
				displayProjectPath.setText(projectFile.getAbsolutePath());
			}
		} catch (Exception e) {
			BoiteModale.erreur("Error", e.getMessage());
		}
	}

	@FXML
	void choosePackageFolder(ActionEvent event) {
		try {
			packageFile = Utils.folderChooser("choose package", projectFile.getAbsolutePath());
			if (packageFile != null) {
				String str = Utils.subStr(projectFile.getAbsolutePath(), packageFile.getAbsolutePath());
				packageName = str.substring(1, str.length());
				displayPackageFile.setText(packageName);
			}
		} catch (Exception e) {
			BoiteModale.erreur("Error", " error project / not found package ");
		}
	}

	@FXML
	void chooseJavaFile(ActionEvent event) {
		try {
			javaFile = Utils.fileChooser("Open Resource Java File", packageFile.getAbsolutePath(), "Java file",
					"*.java*");
			if (javaFile != null) {
				nameClass = javaFile.getName().replaceAll(".java", "");
				displayJavaPath.setText(javaFile.getName());

			}
		} catch (Exception e) {
			BoiteModale.erreur("Error", " error package / not found Java Class " + nameClass);
		}
	}

	@FXML
	void dislpayMapsGoeJson(ActionEvent event) {
		outputFile = new File(OutputJsonResultFile());
		try {
			LeafletController.setJson(jsonFile, outputFile);
			Stage stage = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("/view/map/displayMap.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ======================================================================
	 * Clear Field
	 * ======================================================================
	 * 
	 */

	@FXML
	void cleanAll(ActionEvent event) {
		displayJsonPath.clear();
		displayProjectPath.clear();
	}

	@FXML
	void clearJavaField(ActionEvent event) {
		displayJavaPath.clear();
	}

	@FXML
	void clearJsonField(ActionEvent event) {
		displayJsonPath.clear();
	}

	@FXML
	void clearPackageField(ActionEvent event) {
		displayPackageFile.clear();
	}

	@FXML
	void clearProjectField(ActionEvent event) {
		displayProjectPath.clear();
	}

	/*
	 * ======================================================================
	 * get & display Function
	 * ======================================================================
	 * 
	 */

	// display list of function
	public void fieldJavaFileListener() {
		try {
			displayJavaPath.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != oldValue) {
					listMethode.getItems().clear();
				}
				if (newValue != null) {
					c = processing.getClassInst(packageName, nameClass);
					ObservableList<Method> obsListeAnnee = FXCollections.observableArrayList();
					try {
						if (VerifyAnnotation.isWpsClass(c)) {
							Method[] listFunction = processing.getListMethode(c);
							for (int i = 0; i < listFunction.length; i++) {
								if (VerifyAnnotation.isWpsFunction(listFunction[i])) {
									if (isGeometry) {
										if (listFunction[i].getGenericParameterTypes().length != 0
												&& listFunction[i].getGenericParameterTypes()[0] == Geometry.class) {
											obsListeAnnee.add(listFunction[i]);
										}
									} else if (isFeatureCollection) {
										if (listFunction[i].getGenericParameterTypes().length != 0
												&& listFunction[i].getGenericParameterTypes()[0] != Geometry.class) {
											obsListeAnnee.add(listFunction[i]);
										}
									}
								}
							}
							listMethode.setItems(obsListeAnnee);
						} else {
							BoiteModale.information("Information", "this class does not contain WPS");
						}
					} catch (Exception e) {
						BoiteModale.erreur("Error", " error java Class / not found Java Class");
					}
				}
			});
		} catch (Exception e) {
			BoiteModale.erreur("Error", " error java Class / not found Java Class");
		}
	}

	private String OutputJsonResultFile() {
		if (!outputJsonFile.getText().trim().isEmpty()) {
			return jsonFile.getParentFile().getAbsolutePath() + File.separator + outputJsonFile.getText() + ".json";
		} else {
			return jsonFile.getParentFile().getAbsolutePath() + File.separator + "output_" + jsonFile.getName();
		}
	}

	// button result
	@FXML
	void displayResult(ActionEvent event) {
		String functionWps = listMethode.getSelectionModel().getSelectedItem().getName();
		try {
			if (isFeatureCollection) {
				FeatureCollection<SimpleFeatureType, SimpleFeature> feature = GeoJsonFileUtils
						.GeoJsonToFeatureCollection(jsonFile);
				FeatureCollection<SimpleFeatureType, SimpleFeature> featureResult = processing
						.useMethodeParamFeature(c.newInstance(), functionWps, feature);
				GeoJsonFileUtils.FeatureCollectionToGeoJsonFile(featureResult, OutputJsonResultFile());
			} else if (isGeometry) {
				Geometry geo = GeoJsonFileUtils.GeoJsonToGeometry(jsonFile);
				Geometry result = processing.useMethodeParamGeo(c.newInstance(), functionWps, geo);
				GeoJsonFileUtils.GeometryToGeoJsonFile(result, OutputJsonResultFile());
			}
			BoiteModale.information("output GeoJson", "The output GeoJson was created");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ======================================================================
	 * Binding & listner
	 * ======================================================================
	 * 
	 */
	private void fieldJsonListner() {
		displayJsonPath.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null && !displayJsonPath.getText().isEmpty()) {
					try {
						if (!GeoJsonFileUtils.isFeatureCollectionData(jsonFile)
								&& !GeoJsonFileUtils.isGeometryData(jsonFile)) {
							displayJsonPath.clear();
							outputJsonFile.clear();
							BoiteModale.erreur("GeoJson error", "This file is not a GeoJson");
						}
					} catch (IOException e) {
						BoiteModale.erreur("GeoJson error", e.getMessage());
					}
				}

			}
		});
	}

	private void fieldProjectListner() {
		displayProjectPath.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != oldValue) {
					displayPackageFile.clear();
					displayJavaPath.clear();
					listMethode.getItems().clear();
				}
			}
		});
	}

	private void fieldPackageListner() {
		displayPackageFile.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				displayJavaPath.clear();
				listMethode.getItems().clear();
			}
		});
	}

	private void desableBntDisplayResult() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayJsonPath.textProperty(), displayProjectPath.textProperty(),
						displayPackageFile.textProperty(), displayJavaPath.textProperty(), listMethode.valueProperty());
			}

			@Override
			protected boolean computeValue() {
				if (displayJsonPath.getText()!=null && displayProjectPath.getText()!=null
						&& displayPackageFile.getText()!=null && displayJavaPath.getText()!=null
						&& listMethode.getSelectionModel().getSelectedItem() != null) {
					return false;
				}
				return true;
			}
		};
		displayResult.disableProperty().bind(bindbtn);
	}

	private void desableBntChooseProject() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayJsonPath.textProperty());
			}

			@Override
			protected boolean computeValue() {
				if (!displayJsonPath.getText().trim().isEmpty()) {
					return false;
				}
				return true;
			}
		};
		chooseProject.disableProperty().bind(bindbtn);
	}

	private void desableBntChoosePackage() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayJsonPath.textProperty(), displayProjectPath.textProperty());
			}

			@Override
			protected boolean computeValue() {
				if (!displayJsonPath.getText().trim().isEmpty() && !displayProjectPath.getText().trim().isEmpty()) {
					return false;
				}
				return true;
			}
		};
		choosePackage.disableProperty().bind(bindbtn);
	}

	private void desableBntChooseJava() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayJsonPath.textProperty(), displayProjectPath.textProperty(),
						displayPackageFile.textProperty());
			}

			@Override
			protected boolean computeValue() {
				if (!displayJsonPath.getText().trim().isEmpty() && !displayProjectPath.getText().trim().isEmpty()
						&& !displayPackageFile.getText().trim().isEmpty()) {
					return false;
				}
				return true;
			}
		};
		chooseJavaFile.disableProperty().bind(bindbtn);
	}

	private void desableChoiceFunction() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayJsonPath.textProperty(), displayProjectPath.textProperty(),
						displayPackageFile.textProperty(), displayJavaPath.textProperty());
			}

			@Override
			protected boolean computeValue() {
				if (!displayJsonPath.getText().trim().isEmpty() && !displayProjectPath.getText().trim().isEmpty()
						&& !displayPackageFile.getText().trim().isEmpty()
						&& !displayJavaPath.getText().trim().isEmpty()) {
					return false;
				}
				return true;
			}
		};
		listMethode.disableProperty().bind(bindbtn);
	}

	/*
	 * ======================================================================
	 * Binding & listner clear button
	 * ======================================================================
	 * 
	 */
	private void desableClearJson() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayJsonPath.textProperty());
			}

			@Override
			protected boolean computeValue() {
				if (!displayJsonPath.getText().trim().isEmpty()) {
					return false;
				}
				return true;
			}
		};
		clearJsonField.disableProperty().bind(bindbtn);
	}

	private void desableClearProject() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayProjectPath.textProperty());
			}

			@Override
			protected boolean computeValue() {
				if (!displayProjectPath.getText().trim().isEmpty()) {
					return false;
				}
				return true;
			}
		};
		clearProjectField.disableProperty().bind(bindbtn);
	}

	private void desableClearPackage() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayPackageFile.textProperty());
			}

			@Override
			protected boolean computeValue() {
				if (!displayPackageFile.getText().trim().isEmpty()) {
					return false;
				}
				return true;
			}
		};
		clearPackageField.disableProperty().bind(bindbtn);
	}

	private void desableClearJava() {
		BooleanBinding bindbtn = new BooleanBinding() {
			{
				super.bind(displayJavaPath.textProperty());
			}

			@Override
			protected boolean computeValue() {
				if (!displayJavaPath.getText().trim().isEmpty()) {
					return false;
				}
				return true;
			}
		};
		clearJavaField.disableProperty().bind(bindbtn);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fieldJsonListner();
		fieldJavaFileListener();
		fieldProjectListner();
		fieldPackageListner();
		//
		desableBntDisplayResult();
		desableBntChooseProject();
		desableBntChoosePackage();
		desableBntChooseJava();
		desableChoiceFunction();
		// listner clear field
		desableClearJson();
		desableClearProject();
		desableClearPackage();
		desableClearJava();
		// set editable field
		displayJsonPath.setEditable(false);
		displayProjectPath.setEditable(false);
		displayPackageFile.setEditable(false);
		displayJavaPath.setEditable(false);
	}

}
