import java.net.URLDecoder;
import java.sql.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GeoLocationProgram extends Application {
	
	class GeoLocationGui extends BorderPane {
		private TextField octetA, octetB, octetC, octetD;
		private Button btLocate;
		private TextArea outputBox;
		private final String URL = "";
		private final String USER = "readonly";
		private final String PASS = "readonly";
		
		public GeoLocationGui() {
			buildGui();
			generateResults();
		}

		private void generateResults() {
			btLocate.setOnAction(e -> process());
		}

		// Process the action of button "Locate"
		private void process() {
			try {
				if (checkInput(octetA) && checkInput(octetB) && checkInput(octetC) && checkInput(octetD)) {
					outputBox.setText(Integer.parseInt(octetA.getText()) + "." + Integer.parseInt(octetB.getText())
							+ "." + Integer.parseInt(octetC.getText()) + "." + Integer.parseInt(octetD.getText()));
					findLocation();
				} else
					outputBox.setText("INPUT IS OUT OF RANGE.");
			} catch (Exception e) {
				outputBox.setText("ERROR");
			}
		}

		// Find Geo-location of IP Address
		private void findLocation() {
			Statement stmt = null;
			Connection connection = null;
			int strA = Integer.parseInt(octetA.getText());
			String strB = octetB.getText();
			String strC = octetC.getText();
			try {
				Class.forName("com.mysql.jdbc.Driver");
				message("Driver loaded.");
				message("Attempting to connect to database...");
				connection = DriverManager.getConnection(URL, USER, PASS);
				message("Connection is successful.");
				stmt = connection.createStatement();
				ResultSet set = stmt.executeQuery("select b, c, IP.country, IP.city, id, CITY.city, CITY.name, C.name, lat, lng"
												+ " from ip4_" + strA + " IP, "
												+ "cityByCountry CITY, countries C where b = '" + strB + "' and c = '" + strC + "' "
												+ "and IP.country = id and IP.city = CITY.city");
				while (set.next()) {
					String result = URLDecoder.decode("\n" + set.getString("CITY.name") + "  " + set.getString("C.name")
												+ "  LAT: " + set.getString("lat") + "  " + "LNG: " + set.getString("lng"), "UTF-8");
					outputBox.appendText(result);
				}
				
				if(!set.last())
					outputBox.appendText("\nLocation of IP Address not found.");
				
				connection.close();
				message("Connection closed.\n");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
		}

		private boolean checkInput(TextField txt) {
			return Integer.parseInt(txt.getText()) >= 0 && Integer.parseInt(txt.getText()) <= 255;
		}

		private void message(String string) {
			System.out.println(string);
		}

		// Build the GUI for GeoLocation Program
		private void buildGui() {
			HBox hB = new HBox();
			hB.setStyle("-fx-background-color: rgb(102, 204, 255, 0.7);");
			hB.setPadding(new Insets(20, 200, 10, 50));
			hB.setSpacing(5);
			octetA = new TextField();
			octetA.setMinWidth(40);
			octetB = new TextField();
			octetB.setMinWidth(40);
			octetC = new TextField();
			octetC.setMinWidth(40);
			octetD = new TextField();
			octetD.setMinWidth(40);
			btLocate = new Button("Locate");
			btLocate.setMinWidth(60);
			Label lblPrompt = new Label("Enter IP Address");
			lblPrompt.setFont(Font.font("Calibri", FontWeight.BOLD, 15));
			lblPrompt.setMinWidth(110);
			Label lblDot1 = new Label(".");
			Label lblDot2 = new Label(".");
			Label lblDot3 = new Label(".");
			lblDot1.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
			lblDot1.setMinHeight(30);
			lblDot2.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
			lblDot2.setMinHeight(30);
			lblDot3.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
			lblDot3.setMinHeight(30);
			outputBox = new TextArea();
			outputBox.setEditable(false);
			outputBox.setPadding(new Insets(0, 127, 0, 0));
			outputBox.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
			hB.getChildren().addAll(lblPrompt, octetA, lblDot1, octetB, lblDot2, octetC, lblDot3, octetD, btLocate);
			setTop(hB);
			setCenter(outputBox);
		}

	}

	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(new GeoLocationGui(), 500, 200);
		scene.setFill(Color.BLUE);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Geo-Location Program v.TU");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String args[]) {
		launch(args);
	}

}