package application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.util.Duration;

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
    
    private Label playTime;
    
    private MusicLibraryModel ml;
    
    private MediaPlayer mp;
    
    private SongModel currentSong;
    
    private boolean userControlTime = false;
    
    private boolean shuffleSongs = false;
        
    // Add specific behaviors at initialization
    public void initialize() {
 
    	// initialize volume to max
    	volumeSlider.setValue(volumeSlider.getMax());
    	
    	// Handle volume set by user
    	volumeSlider.setOnMouseDragged(event -> {
    		if(this.mp != null) {
    			double volume = this.volumeSlider.getValue();
    			this.mp.setVolume(volume*0.01);
    		}
    	});
    	
    	// Slider clicked by user, set flag to avoid interference
    	// when the time slider is auto updating.
    	seek.setOnMousePressed(event -> {
    		if(this.mp != null) {
    			userControlTime = true;
    		}
    	});
    	
    	// Re-enable auto time slider update
    	seek.setOnMouseReleased(event -> {
    		if(this.mp != null) {
        		Duration duration = this.mp.getTotalDuration();
        		this.mp.seek(duration.multiply(this.seek.getValue() / 100.0));
        		userControlTime = false;
        	}
        });
        
    	// Play song when double-clicking a row in the table
        libraryDisplay.setRowFactory( tv -> {
            TableRow<SongModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    this.play();
                }
            });
            return row ;
        });
        
    }
    
    @FXML
    public void playBtn(ActionEvent event){
    	this.play();
    }
    
    public void play() {
    	SongModel newSong = libraryDisplay.getFocusModel().getFocusedItem();
    	
    	if(this.mp != null) {
    		boolean sameSong = newSong.equals(this.currentSong);
    		if(this.mp.getStatus() == MediaPlayer.Status.PAUSED && sameSong) {
    			this.mp.play();
    		}
    		else {
    			mp.dispose();
    			this.currentSong = newSong;
    	    	File f = new File(newSong.getSongPath().toString());
    	    	this.mp = new MediaPlayer(new Media(f.toURI().toString()));
    	    	double volume = this.volumeSlider.getValue();
    			this.mp.setVolume(volume*0.01);
    	    	this.mp.play();
    	        mp.setOnEndOfMedia(() -> {
    	        	if(shuffleSongs) {
    	        		shuffle();
    	        	}
    	        	else {
    	        		next();
    	        	}
    	        });
    	    	// The time slider gets incremented as media plays
    	    	mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
    	    		if(!userControlTime) {
	    	    		Duration currentTime = mp.getCurrentTime();
	    	    		Duration duration = mp.getTotalDuration();
	    	    		seek.setValue(currentTime.divide(duration.toMillis()).toMillis()
	  		                  * 100.0);
    	    		}
    			});
    		}
    		
    	}
    	else {
			this.currentSong = newSong;
	    	File f = new File(newSong.getSongPath().toString());
	    	this.mp = new MediaPlayer(new Media(f.toURI().toString()));
	    	double volume = this.volumeSlider.getValue();
			this.mp.setVolume(volume*0.01);
	    	this.mp.play();
  	        mp.setOnEndOfMedia(() -> {
	        	if(shuffleSongs) {
	        		shuffle();
	        	}
	        	else {
	        		next();
	        	}
	        });
	    	mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
	    		if(!userControlTime) {
		    		Duration currentTime = mp.getCurrentTime();
		    		Duration duration = mp.getTotalDuration();
		    		seek.setValue(currentTime.divide(duration.toMillis()).toMillis()
			                  * 100.0);
	    		}
			});
    	}
    }
    
    @FXML
    public void nextSongBtn(ActionEvent event) {
    	this.next();
    }
    
    public void next() {
    	// get next song on the list
    	int index = libraryDisplay.getSelectionModel().getSelectedIndex();
    	int max = libraryDisplay.getItems().size();
    	index++;
    	System.out.println("Next: " + index);
    	if(index > max-1) {
    		index = 0;
    	}
    	libraryDisplay.getSelectionModel().select(index);
    	libraryDisplay.getFocusModel().focus(index);
    	play();
    }
    
    @FXML
    public void prevSongBtn(ActionEvent event) {
    	this.prev();
    }
    
    public void prev() {
    	// get prev song on the list
    	int index = libraryDisplay.getSelectionModel().getSelectedIndex();
    	int min = 0;
    	index--;
    	if(index < min) {
    		index = libraryDisplay.getItems().size()-1;
    	}
    	libraryDisplay.getSelectionModel().select(index);
    	libraryDisplay.getFocusModel().focus(index);
    	play();
    }
    
    @FXML
    public void shuffleBtn(ActionEvent event) {
    	if(shuffleSongs) {
    		shuffleSongs = false;
    	}
    	else {
    		shuffleSongs = true;
    	}
    }
    
    public void shuffle() {
    	int index = libraryDisplay.getSelectionModel().getSelectedIndex();
    	int newIndex = index;
    	while(index == newIndex) {
    		newIndex = randomIndex();
    	}
    	System.out.println(newIndex);
		libraryDisplay.getSelectionModel().select(newIndex);
    	libraryDisplay.getFocusModel().focus(newIndex);
    	play();
    }
    
    public int randomIndex() {
    	Random rand = new Random();
		int randN = rand.nextInt(libraryDisplay.getItems().size());
		return randN;
    }
    
    @FXML
    public void muteBtn(ActionEvent event) {
    	if(this.mp != null) {
    		if(mp.getVolume() == 0) {
    			mp.setVolume(1.0);
    		}
    		else {
    			mp.setVolume(0);
    		}
    	}

		if(volumeSlider.getValue() == volumeSlider.getMin()) {
			volumeSlider.setValue(volumeSlider.getMax());
		}
		else {
			volumeSlider.setValue(volumeSlider.getMin());
		}
    	
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
	
	public Slider getTimeSlider() {
		return this.seek;
	}
	
	public MediaPlayer getMediaPlayer() {
		return this.mp;
	}
	
	public void setIndex(int index) {
    	libraryDisplay.getSelectionModel().select(0);
    	libraryDisplay.getFocusModel().focus(0);
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
		
	}
	
}
