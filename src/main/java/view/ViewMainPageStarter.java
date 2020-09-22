package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewMainPageStarter extends Application {
    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("./layouts/main_page.fxml"));
            Stage stage = new Stage();
            primaryStage.setTitle("Welcome Page");
            primaryStage.setScene(new Scene(root, 830, 880));
            primaryStage.show();
            this.primaryStage = primaryStage;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
