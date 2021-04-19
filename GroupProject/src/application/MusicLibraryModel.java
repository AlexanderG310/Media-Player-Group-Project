package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicLibraryModel {
	private ArrayList<SongModel> library;
	 // Used to identify current song playing on the library
	private int currentIndex;
	private SongModel currentSong;
	private MediaPlayer mp;
	
	public MusicLibraryModel() {
		this.library = new ArrayList<SongModel>();
		this.currentIndex = 0;
	}
	
	public void play() {
		// Get current song from library
		this.currentSong = this.library.get(currentIndex);
		Path songPath = this.currentSong.getSongPath();
		// Feed the path for the song into the media player
		this.mp = new MediaPlayer(new Media(songPath.toString()));
		this.mp.play();
	}
	
	public void addSong(String fileName, String songFilePath) {		
		SongModel song = new SongModel(fileName, "", "", songFilePath);
		// Save to our directory
		song.saveData();
		// Add to our current library
		this.library.add(song);
	}
	
	public void load() {
		
		File folder = new File("resources/metadata/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	try {
					File music = file;
					Scanner myReader = new Scanner(music);
					
			        // Read title
					String title = "";
					try {
						title = URLDecoder.decode(myReader.nextLine(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Read music path
					String musicPath = myReader.nextLine();
					
					// Read album art path
					String artPath = myReader.nextLine();
					
					// Read description
					String description = "";
					try {
						description = URLDecoder.decode(myReader.nextLine(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					SongModel song = new SongModel(title, description, artPath, musicPath);
					this.library.add(song);
			      
					myReader.close();
			    } catch (FileNotFoundException e) {
			    	System.out.println("An error occurred.");
			    	e.printStackTrace();
			    }
		    }
		}
		
	}
}
