package application;

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
