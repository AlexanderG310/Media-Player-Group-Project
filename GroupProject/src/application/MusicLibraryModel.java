package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
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
	
	public SongModel next() {
		this.currentIndex++;
		if(this.library.size()-1 < this.currentIndex) {
			this.currentIndex = 0;
		}
		return this.library.get(this.currentIndex);
	}
	
	public SongModel prev() {
		this.currentIndex--;
		if(this.currentIndex < 0) {
			this.currentIndex = this.library.size();
		}
		return this.library.get(this.currentIndex);
	}
	
	public SongModel shuffle() {
		Random rand = new Random();
		// Obtain a number between [0 - 49].
		int randN = rand.nextInt(this.library.size());
		// Make sure to avoid getting same number
		if(randN == this.currentIndex) {
			this.shuffle();
		}
		
		this.currentIndex = randN;
		return this.library.get(this.currentIndex);
	}
	
	public void setIndex(int index) {
		this.currentIndex = index;
	}
	
	public void addSong(String fileName, String songFilePath) {		
		SongModel song = new SongModel(fileName, "", "", "");
		song.setExternalPath(songFilePath);
		// Save to our directory
		//song.saveData();
		song.saveMedia();
		
		System.out.print(song.getDuration());
		
		// Add to our current library
		this.library.add(song);
	}
	
	public ArrayList<SongModel> getLibrary() {
		return this.library;
	}
	
	public String readData(Scanner scanner) {
		if(scanner.hasNextLine()) {
			return scanner.nextLine();
		}
		return "";
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
						title = URLDecoder.decode(this.readData(myReader), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Read music path
					String musicPath = this.readData(myReader);
					
					// Read album art path
					String artPath = this.readData(myReader);
					
					// Read description
					String description = "";
					try {
						description = URLDecoder.decode(this.readData(myReader), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Ready Duration
					String duration = this.readData(myReader);
					
					SongModel song = new SongModel(title, description, artPath, musicPath);
					song.setDuration(duration);
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
