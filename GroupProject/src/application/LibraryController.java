package application;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    
    // Add specific behaviors at initialization
    public void initialize() {
        /*this.seek.valueProperty().addListener((obs, oldValue, newValue) -> {
        	if(this.mp != null) {
        		Duration duration = this.mp.getTotalDuration();
        		this.mp.seek(duration.multiply(this.seek.getValue() / 100.0));
        	} 
        });*/
    	
    	seek.setOnMousePressed(event -> {
    		if(this.mp != null) {
    			userControlTime = true;
    		}
    	});
    	
    	seek.setOnMouseReleased(event -> {
    		if(this.mp != null) {
        		Duration duration = this.mp.getTotalDuration();
        		this.mp.seek(duration.multiply(this.seek.getValue() / 100.0));
        		userControlTime = false;
        	}
        });
        
        libraryDisplay.setRowFactory( tv -> {
            TableRow<SongModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    this.play();
                }
            });
            return row ;
        });
        
        /*mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
		     System.out.println("Player:" + observableValue + " | Changed from playing at: " + oldDuration + " to play at " + newDuration);
		});*/
        
        /*mp.currentTimeProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });*/
 
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
    	    	this.mp.play();
    	    	mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
    	    		if(!userControlTime) {
	    	    		Duration currentTime = mp.getCurrentTime();
	    	    		Duration duration = mp.getTotalDuration();
	    	    		seek.setValue(currentTime.divide(duration.toMillis()).toMillis()
	  		                  * 100.0);
	    	    		//updateValues();
    	    		}
    			});
    		}
    		
    	}
    	else {
			this.currentSong = newSong;
	    	File f = new File(newSong.getSongPath().toString());
	    	this.mp = new MediaPlayer(new Media(f.toURI().toString()));
	    	this.mp.play();
	    	mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
	    		if(!userControlTime) {
		    		Duration currentTime = mp.getCurrentTime();
		    		Duration duration = mp.getTotalDuration();
		    		seek.setValue(currentTime.divide(duration.toMillis()).toMillis()
			                  * 100.0);
		    		//updateValues();
	    		}
			});
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
	
	protected void updateValues() {
		  //if (playTime != null && seek != null && volumeSlider != null) {
			if (seek != null) {
		     Platform.runLater(new Runnable() {
		        public void run() {
		          Duration duration = mp.getTotalDuration();
		          Duration currentTime = mp.getCurrentTime();
		          //playTime.setText(formatTime(currentTime, duration));
		          seek.setDisable(duration.isUnknown());
		          if (!seek.isDisabled() 
		            && duration.greaterThan(Duration.ZERO) 
		            && !seek.isValueChanging()) {
		        	  seek.setValue(currentTime.divide(duration.toMillis()).toMillis()
		                  * 100.0);
		          }
		          /*if (!volumeSlider.isValueChanging()) {
		            volumeSlider.setValue((int)Math.round(mp.getVolume() 
		                  * 100));
		          }*/
		        }
		     });
		  }
		}
	
	private static String formatTime(Duration elapsed, Duration duration) {
		   int intElapsed = (int)Math.floor(elapsed.toSeconds());
		   int elapsedHours = intElapsed / (60 * 60);
		   if (elapsedHours > 0) {
		       intElapsed -= elapsedHours * 60 * 60;
		   }
		   int elapsedMinutes = intElapsed / 60;
		   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
		                           - elapsedMinutes * 60;
		 
		   if (duration.greaterThan(Duration.ZERO)) {
		      int intDuration = (int)Math.floor(duration.toSeconds());
		      int durationHours = intDuration / (60 * 60);
		      if (durationHours > 0) {
		         intDuration -= durationHours * 60 * 60;
		      }
		      int durationMinutes = intDuration / 60;
		      int durationSeconds = intDuration - durationHours * 60 * 60 - 
		          durationMinutes * 60;
		      if (durationHours > 0) {
		         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
		            elapsedHours, elapsedMinutes, elapsedSeconds,
		            durationHours, durationMinutes, durationSeconds);
		      } else {
		          return String.format("%02d:%02d/%02d:%02d",
		            elapsedMinutes, elapsedSeconds,durationMinutes, 
		                durationSeconds);
		      }
		      } else {
		          if (elapsedHours > 0) {
		             return String.format("%d:%02d:%02d", elapsedHours, 
		                    elapsedMinutes, elapsedSeconds);
		            } else {
		                return String.format("%02d:%02d",elapsedMinutes, 
		                    elapsedSeconds);
		            }
		        }
		    }
	
}
