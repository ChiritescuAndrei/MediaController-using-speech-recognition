package application;
	

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Video.fxml"));
		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		primaryStage.setTitle("Change Tile");
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setWidth(screenWidth/1.1);
		primaryStage.setHeight(screenHeight/1.2);
		primaryStage.show();
		
	}
		public static void main(String[] args) {
		launch(args);
	}
}
