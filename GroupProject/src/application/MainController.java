package application;

import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

    @FXML
    private Button libraryBtn;
    
    @FXML
    private AnchorPane mainPanel;
    
    private MusicLibraryModel ml;
    
    public MainController() {
    	// Load our library at the start
    	this.ml = new MusicLibraryModel();
    	ml.load();
    }

	@FXML
	public void libraryBtnEvent(ActionEvent event) throws IOException {

		FXMLLoader fl = new FXMLLoader(getClass().getResource("LibraryView.fxml"));

		mainPanel = fl.load();

		Scene scene = new Scene(mainPanel);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		LibraryController controller = fl.getController();
		controller.setMusicLibrary(ml);
		controller.displayLibrary();
		
		window.setScene(scene);
		window.show();
	}
    
    
	
}
