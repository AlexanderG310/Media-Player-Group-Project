package application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

/*
 * This is the library controller which contains all the buttons and what will happen when they are pressed.
 * Also, changes around some labels and images to display the the songs information
 */

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
	private Slider seek;

	@FXML
	private Button uploadMusic;
	
    @FXML
    private ImageView albumCover;

    @FXML
    private Label title;

    @FXML
    private Label artist;

    @FXML
    private Label album;

    @FXML
	private Label playTime;

    @FXML
    private ToggleButton shuffleToggle;

    @FXML
    private ToggleButton volToggle;

	private MusicLibraryModel ml;

	private MediaPlayer mp;

	private SongModel currentSong;

	private boolean userControlTime = false;

	private boolean shuffleSongs = false;

	// Add specific behaviors at initialization
	public void initialize() {

		// Default album cover to default image
		File imageFile = new File("resources/art/No-album-art.png");
		albumCover.setImage(new Image(imageFile.toURI().toString()));
		
		// initialize volume to max
		volumeSlider.setValue(volumeSlider.getMax());

		// Handle setting the volume on the media player
		// when the user drags the node on the volume slider
		volumeSlider.setOnMouseDragged(event -> {
			if (this.mp != null) {
				double volume = this.volumeSlider.getValue();
				this.mp.setVolume(volume * 0.01);
			}
		});

		// Track when seek slider is pressed by the user, 
		// set flag to temporarily stop the seek streaming
		seek.setOnMousePressed(event -> {
			if (this.mp != null) {
				userControlTime = true;
			}
		});

		// Track when seek slider is clicked and let go by
		// the user, this will the new spot on the seek
		// to begin playing the song from that position
		// it will then resume the seek to continue to
		// stream
		seek.setOnMouseReleased(event -> {
			if (this.mp != null) {
				Duration duration = this.mp.getTotalDuration();
				this.mp.seek(duration.multiply(this.seek.getValue() / 100.0));
				userControlTime = false;
			}
		});

		// Allow the user to play the song by double-clicking
		// a row in the table
		libraryDisplay.setRowFactory(tv -> {
			TableRow<SongModel> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					this.play();
				}
			});
			return row;
		});
		
	}

	@FXML
	public void playBtn(ActionEvent event) {
		this.play();
	}

	/* Main Play functionality that handles different scenarios
	 * 
	 * The Song played will depend on the selected/focused row
	 * on the table.
	 * 
	 * If the media player doesn't exist yet it will create it
	 * and it will also attach a listener to the seek slider
	 * so it streams and shows the duration decrementing as
	 * the song plays.
	 * 
	 * If the media player is already initialized, if the
	 * current song is paused, it will resume playing
	 * else if there is a new song selected, it will dispose
	 * of the previous one and play the new one
	 * 
	 * Lastly, if the song finishes playing it will either
	 * grab a random song if shuffle is enabled or continue
	 * to play the next song
	 */
	public void play() {
		SongModel newSong = libraryDisplay.getFocusModel().getFocusedItem();

		if (this.mp != null) {
			boolean sameSong = newSong.equals(this.currentSong);
			if (this.mp.getStatus() == MediaPlayer.Status.PAUSED && sameSong) {
				this.mp.play();
			} else {
				mp.dispose();
				this.currentSong = newSong;
				File f = new File(newSong.getSongPath().toString());
				this.mp = new MediaPlayer(new Media(f.toURI().toString()));
				displayMetadata();
				
				double volume = this.volumeSlider.getValue();
				this.mp.setVolume(volume * 0.01);
				this.mp.play();
				mp.setOnEndOfMedia(() -> {
					if (shuffleSongs) {
						shuffle();
					} else {
						next();
					}
				});
				// The time slider gets incremented as media plays
				mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
					if (!userControlTime) {
						Duration currentTime = mp.getCurrentTime();
						Duration duration = mp.getTotalDuration();
						seek.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
					}
				});
			}

		} else {
			this.currentSong = newSong;
			File f = new File(newSong.getSongPath().toString());
			this.mp = new MediaPlayer(new Media(f.toURI().toString()));
			displayMetadata();
			
			double volume = this.volumeSlider.getValue();
			this.mp.setVolume(volume * 0.01);
			this.mp.play();
			mp.setOnEndOfMedia(() -> {
				if (shuffleSongs) {
					shuffle();
				} else {
					next();
				}
			});
			mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
				if (!userControlTime) {
					Duration currentTime = mp.getCurrentTime();
					Duration duration = mp.getTotalDuration();
					seek.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
				}
			});
		}
	}

	@FXML
	public void nextSongBtn(ActionEvent event) {
		this.next();
	}

	/*
	 * Next function that is used for selecting the next song
	 * on the table and playing it.
	 */
	public void next() {
		// get next song on the list
		int index = libraryDisplay.getSelectionModel().getSelectedIndex();
		int max = libraryDisplay.getItems().size();
		index++;
		System.out.println("Next: " + index);
		if (index > max - 1) {
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

	/*
	 * Prev function that is used for selecting the previous
	 * song on the table and playing it
	 */
	public void prev() {
		// get prev song on the list
		int index = libraryDisplay.getSelectionModel().getSelectedIndex();
		int min = 0;
		index--;
		if (index < min) {
			index = libraryDisplay.getItems().size() - 1;
		}
		libraryDisplay.getSelectionModel().select(index);
		libraryDisplay.getFocusModel().focus(index);
		play();
	}

	@FXML
	public void shuffleBtn(ActionEvent event) {
		if (shuffleSongs) {
			shuffleSongs = false;
		} else {
			shuffleSongs = true;
		}
	}

	/*
	 * Toggles the state of the controller, which the play functionality
	 * uses to determine if it should select a random song next, after
	 * the current song finishes playing
	 */
	public void shuffle() {
		int index = libraryDisplay.getSelectionModel().getSelectedIndex();
		int newIndex = index;
		while (index == newIndex) {
			newIndex = randomIndex();
		}
		libraryDisplay.getSelectionModel().select(newIndex);
		libraryDisplay.getFocusModel().focus(newIndex);
		play();
	}

	/*
	 * Used to support the shuffle functionality for generating a
	 * random index within the range using the size of the table
	 */
	public int randomIndex() {
		Random rand = new Random();
		int randN = rand.nextInt(libraryDisplay.getItems().size());
		return randN;
	}

	
	/*
	 * Mute Button event that can be toggled to lower the volume
	 * completely, or put it to max.
	 */
	@FXML
	public void muteBtn(ActionEvent event) {
		if (this.mp != null) {
			if (mp.getVolume() == 0) {
				mp.setVolume(1.0);
			} else {
				mp.setVolume(0);

			}
		}

		if (volumeSlider.getValue() == volumeSlider.getMin()) {
			volumeSlider.setValue(volumeSlider.getMax());
		} else {
			volumeSlider.setValue(volumeSlider.getMin());
		}

	}

	/*
	 * Pause Button event that handles setting the media
	 * player to the pause state when selected
	 */
	@FXML
	public void pauseBtn(ActionEvent event) {
		if (this.mp != null) {
			this.mp.pause();
		}
	}

	/*
	 * Event that allows the user to select multiple files to add to
	 * the music library and refreshes the library.
	 * This only supports mp3, mp4, & wav type of file extensions
	 */
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

	/*
	 * Sets the index on the table to focus/select
	 */
	public void setIndex(int index) {
		libraryDisplay.getSelectionModel().select(0);
		libraryDisplay.getFocusModel().focus(0);
	}
	
	/*
	 * Displays Metadata on the media player
	 * if the song file has any tied to it
	 */
	public void displayMetadata() {
		if(this.mp != null) {
			this.clearMetadata();
			mp.getMedia().getMetadata().addListener((Change<? extends String, ? extends Object> ch) -> {
				if (ch.wasAdded()) {
					handleMetadata(ch.getKey(), ch.getValueAdded());
				}
		    });
		}
	}

	/*
	 * Used to clear out the previous data displayed
	 * in the case that the new song may be missing some
	 * metadata
	 */
	private void clearMetadata() {
		album.setText("");
		artist.setText("");
		title.setText("");
		File imageFile = new File("resources/art/No-album-art.png");
		albumCover.setImage(new Image(imageFile.toURI().toString()));
	}
	
	/*
	 * Handles parsing the metadata from a media object
	 * and displaying it on the text fields on our UI
	 */
	private void handleMetadata(String key, Object value) {
		if (key.equals("album")) {
			album.setText(value.toString());
		}
		if(key.equals("artist")) {
			artist.setText(value.toString());
		}
		if (key.equals("title")) {
			title.setText(value.toString());
		}
		if (key.equals("image")) {
			albumCover.setImage((Image) value);
		}
	}

	/*
	 * Displays the library based on the music library mode
	 * where we store a collection of Songs
	 */
	@FXML
	public void displayLibrary() {

		final ObservableList<SongModel> data = FXCollections.observableList(this.ml.getLibrary());
		nameDisplay.setCellValueFactory(new PropertyValueFactory<>("title"));
		timeDisplay.setCellValueFactory(new PropertyValueFactory<>("duration"));
		// ObservableList<SongModel> list = FXCollections.observableArrayList();
		libraryDisplay.setItems(data);

	}

}
