package application;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class LibraryController {

    @FXML
    private TableView<SongModel> libraryDisplay;

    @FXML
    private TableColumn<SongModel, String> nameDisplay;

    @FXML
    private TableColumn<SongModel, String> timeDisplay;

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
    
    private MediaPlayer mp;
    
    private SongModel currentSong;
    
    @FXML
    public void playBtn(ActionEvent event){
    	SongModel newSong = libraryDisplay.getFocusModel().getFocusedItem();
    	
    	if(this.mp != null) {
    		boolean sameSong = newSong.equals(this.currentSong);
    		if(this.mp.getStatus() == MediaPlayer.Status.PAUSED && sameSong) {
    			this.mp.play();
    		}
    		else {
    			mp.dispose();
    		}
    	}

		this.currentSong = newSong;
    	File f = new File(newSong.getSongPath().toString());
    	this.mp = new MediaPlayer(new Media(f.toURI().toString()));
    	this.mp.play();
    	
    }
    
    @FXML
    public void pauseBtn(ActionEvent event){
    	if(this.mp != null) {
    		this.mp.pause();
    	}
    }

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
    	this.displayLibrary();
    }
	
    public MusicLibraryModel getMusicLibrary() {
		return this.ml;
	}

	public void setMusicLibrary(MusicLibraryModel ml) {
		this.ml = ml;
	}
	
	public TableView<SongModel> getTableView() {
		return this.libraryDisplay;
	}
	
	@FXML
	public void displayLibrary() {
		/*final ObservableList<SongModel> data = FXCollections.observableArrayList(
		     new SongModel("Testing", "", "", "")
			);*/
		
		final ObservableList<SongModel> data = FXCollections.observableList(this.ml.getLibrary());
		nameDisplay.setCellValueFactory(new PropertyValueFactory<>("title"));
		timeDisplay.setCellValueFactory(new PropertyValueFactory<>("duration"));
		//ObservableList<SongModel> list = FXCollections.observableArrayList();
		libraryDisplay.setItems(data);
		
		// Append double-clicking functionality, since display is called
		// after transitioning into the scene
	   	libraryDisplay.setRowFactory( tv -> {
            TableRow<SongModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    if(this.mp != null) {
                		mp.dispose();
                	}
                	SongModel song = libraryDisplay.getFocusModel().getFocusedItem();
                	File f = new File(song.getSongPath().toString());
                	this.mp = new MediaPlayer(new Media(f.toURI().toString()));
                	this.mp.play();
                }
            });
            return row ;
        });
	}
}
