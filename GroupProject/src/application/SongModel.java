package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SongModel {
	private String uid;
	private String title;
	private String description;
	private String duration;
	private Path albumArtPath;
	private Path songPath;
	private Media media;
	private Path externalPath;

	public SongModel(String title, String description, String artPath, String songPath) {
		this.title = title;
		this.description = description;

		if(artPath != "") {
			this.albumArtPath = Paths.get(artPath);
		}
		
		if(songPath != "") {
			this.songPath = Paths.get(songPath);
			
		
		}

	}
	
	/*
	 * Workaround function that temporarily initializes a 
	 * media player in order to fetch information like
	 * duration, etc. Anything that can only be provided
	 * when a media player is set to ready
	 */
	public void saveMedia() {
		File f = new File(this.songPath.toString());
		MediaPlayer mp = new MediaPlayer(new Media(f.toURI().toString()));
		mp.setOnReady(new MediaRunnable(this) {
			
			Media m = mp.getMedia();
			SongModel sm = this.getSongModel();
			
			@Override
	        public void run() {
				String duration = String.format("%.2f", m.getDuration().toMinutes());
				sm.duration = duration;
				sm.saveData();
			}
		});
		
	}

	/*
	 * Handles saving the song into the proper resources folder
	 * and writing it in a specific format to support loading
	 * it later
	 */
	public void saveData() {
		
		// Create new file with unique identifier
		String uniqueID = UUID.randomUUID().toString();
		String filename = uniqueID + ".meta";
		String metaPath = "resources/metadata/" + filename;

		if(this.externalPath != null) {
			File song = new File(this.externalPath.toString());
			Path musicDest = Paths.get("resources/music/" + song.getName());
			
			// Check if song already exists within our own folder
				try {
					FileWriter myWriter = new FileWriter(metaPath);
					// Format/Write title

					myWriter.write(this.title + "\n");

					//we just continue to support the existing path without changing it
					if(this.songPath != null) {
						myWriter.write(this.songPath.toString()+"\n");
					}
					else {
						myWriter.write("null\n");
					}
					
					// Write album art path
					if (this.albumArtPath != null) {
						myWriter.write(this.albumArtPath.toString()+"\n");
					} else {
						myWriter.write("null\n");
					}
					// Write description
					if (this.description.toString() != "") {
						myWriter.write(URLEncoder.encode(this.description, "UTF-8").toString() + "\n");
					} else {
						myWriter.write("null\n");
					}
					
					// Write duration
					if (this.duration != null) {
					myWriter.write(this.duration + "\n");
					} else {
						myWriter.write("null\n");
					}
					
					myWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Path getAlbumArtPath() {
		return albumArtPath;
	}

	public void setAlbumArtPath(Path albumArtPath) {
		this.albumArtPath = albumArtPath;
	}

	public Path getSongPath() {
		return songPath;
	}

	public void setSongPath(Path songPath) {
		this.songPath = songPath;
	}

	/*
	 * We use this function to copy the music when it's first added
	 * and then later invoke the saveData function for saving
	 * all other information
	 */
	public void setExternalPath(String songFilePath) {
		this.externalPath = Paths.get(songFilePath);
		
		File song = new File(this.externalPath.toString());
		Path musicDest = Paths.get("resources/music/" + song.getName());
		this.songPath = musicDest;
		
		try {
			Files.copy(this.externalPath, musicDest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDuration(String durationInMinutes) {
		this.duration = durationInMinutes;
	}

	public String getDuration() {
		return this.duration;
	}

}
