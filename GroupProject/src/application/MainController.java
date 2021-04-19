package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {

    @FXML
    private Button libraryBtn;
    
    private MusicLibraryModel ml;
    
    public MainController() {
    	// Load our library at the start
    	this.ml = new MusicLibraryModel();
    	ml.load();
    }

	/*@FXML
	public void libraryBtnEvent(ActionEvent event) throws IOException {

		FXMLLoader fl = new FXMLLoader(getClass().getResource("NeedGive.fxml"));

		mainPanel = fl.load();

		Scene scene = new Scene(mainPanel);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		NeedGiveController controller = fl.getController();
		controller.setNeed(true);
		controller.setModel(this.model);

		window.setScene(scene);
		window.show();
	}*/
    
    
}
