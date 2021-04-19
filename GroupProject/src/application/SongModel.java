package application;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class SongModel {
	private String uid;
	private String title;
	private String description;
	private Path albumArtPath;
	private Path songPath;
	
	public SongModel(String title, String description, String artPath, String songPath) {
		this.title = title;
		this.description = description;
		this.albumArtPath = Paths.get(artPath);
		this.songPath = Paths.get(songPath);
	}
	
	public void saveData() {
		// Create new file with unique identifier
		String uniqueID = UUID.randomUUID().toString();
		String filename = uniqueID + ".meta";
		String metaPath = "/resources/metadata/" + filename;
		
		try {
			FileWriter myWriter = new FileWriter(metaPath);
			// Format/Write title
			myWriter.write(URLEncoder.encode(this.title + "\n", "UTF-8"));
			// Write music path
			myWriter.write(this.albumArtPath.toString() + "\n");
			// Write album art path
			myWriter.write(this.songPath.toString() + "\n");
			// Write description
			myWriter.write(URLEncoder.encode(this.description + "\n", "UTF-8"));
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
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
	
}
