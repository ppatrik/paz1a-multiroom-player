package sk.htsys.player.model;


/**
 * Interface listeneru pre prehravac hudby
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
public interface PlayerListener {
	public void playStarted();

	public void playFinished(SongModel song);

	public void playStopped(SongModel song);

	public void playPaused();

	public void playResumed();

	public void setSongName(String string);

	public void setPosition(long pos, long dur);
}
