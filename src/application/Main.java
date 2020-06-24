package application;

import controller.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//Parent root=FXMLLoader.load(getClass().getResource("/view/view.fxml"));
		FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/view/view.fxml"));
		Parent root=fxmlLoader.load();
		RootController rootController=fxmlLoader.getController();
		rootController.stage=primaryStage;
		
		Scene scene=new Scene(root);
		scene.getStylesheets().add("/application/main.css");
		primaryStage.setTitle("檬殿切积 己利包府");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

}
