package application;

/*
 * This class is used because of the limitation 
 * when using the MediaPlayer.
 * 
 * The MediaPlayer can only provide us with extra
 * info, such as duration only when the state
 * of the media player is toggled to ready,
 * which requires a runnable that we needed to
 * extend, in order to pass specific data
 * 
 * 
 */

public class MediaRunnable implements Runnable{
	
	private SongModel model;
	
	public MediaRunnable(SongModel model) {
       // store parameter for later user
		this.model = model;
	}

   public void run() {
	   
   }
   
   public SongModel getSongModel() {
	   return this.model;
   }
}
