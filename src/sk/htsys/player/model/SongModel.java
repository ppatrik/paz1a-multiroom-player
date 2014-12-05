package sk.htsys.player.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

/**
 * Uchovava informacie o piesni (Subor, Autor, Nazov, ...)
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
public class SongModel implements Serializable {
	private static final long serialVersionUID = 103633848461797734L;

	private final File file;
	private String author;
	private String album;
	private String title;
	private long duration = 0;

	/**
	 * Vytvara novy Model, a nastavuje konštantu suboru
	 * 
	 * @param file
	 */
	private SongModel(File file) {
		super();
		this.file = file;
	}

	/**
	 * Vytvara SongModel zo suboru, Nacitava ID3 tagy
	 * 
	 * @param file
	 * @return
	 */
	public static SongModel fromFile(File file) {
		SongModel song = new SongModel(file);

		try {
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();

			AudioFileFormat format = AudioSystem.getAudioFileFormat(file);

			song.setDuration((Long) format.properties().get("duration"));
			song.setTitle(tag.getFirst(FieldKey.TITLE));
			song.setAuthor(tag.getFirst(FieldKey.ARTIST));
			song.setAlbum(tag.getFirst(FieldKey.ALBUM));

		} catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException | InvalidAudioFrameException
				| UnsupportedAudioFileException e1) {
			System.err.println("Nemozno nacitat " + file);
		}
		return song;
	}

	/**
	 * Debug Method: vypisuje info o modeli
	 */
	@Override
	public String toString() {
		return "Title: " + title + ", Album: " + album + ", Author: " + author;
	}

	// Setters & Gettters
	public File getFile() {
		return file;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getDuration() {
		return duration;
	}

	private void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Uklada zmeny ID3 tagu späť do suboru
	 * 
	 * @return
	 */
	public boolean save() {
		try {
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();

			tag.setField(FieldKey.TITLE, getTitle());
			tag.setField(FieldKey.ARTIST, getAuthor());
			tag.setField(FieldKey.ALBUM, getAlbum());
			f.commit();
			return true;
		} catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException | InvalidAudioFrameException
				| CannotWriteException e) {
			System.err.println("Nepodarilo sa ulozit zmeny do suboru");
			return false;
		}
	}

}
