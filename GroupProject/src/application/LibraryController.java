package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class LibraryController {

    @FXML
    private TableView<?> libraryDisplay;

    @FXML
    private TableColumn<?, ?> library;

    @FXML
    private ScrollBar libraryScroller;

    @FXML
    private Button play;

    @FXML
    private Button backward;

    @FXML
    private Button pause;

    @FXML
    private Button forward;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button muteUnmute;

    @FXML
    private Slider seek;

    @FXML
    private Button uploadMusic;
    
    private MusicLibraryModel ml;
    

	@FXML
    public void multiFileChooser(ActionEvent event) throws IOException {
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().add(new ExtensionFilter("Media", "*.mp3", "*.mp4", "*.wav"));
    	List<File> f = fc.showOpenMultipleDialog(null);
    	for (File file : f) {
    		String fileName = file.getName();
    		String filePath = file.getAbsolutePath();
    		ml.addSong(fileName, filePath);
    	}
    }
	
    public MusicLibraryModel getMusicLibrary() {
		return ml;
	}

	public void setMusicLibrary(MusicLibraryModel ml) {
		this.ml = ml;
	}


}
