package sk.htsys.player.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Samotny prehravac hudby
 * 
 * @author Patrik Pekarčík <patrik@htsys.sk>
 * @version 0.1
 * @since 2014-01-01
 */
public class PlayerModel extends Thread {
	/**
	 * Uzamykaci objekt pre prehravanie jednej piesne (Pauza, pokracovanie)
	 */
	private final Object lock = new Object();
	/**
	 * Uzamykaci objekt pre zivotny cyklus prehravaca (Caka na prehratie dalsej
	 * pesniky)
	 */
	private final Object lock2 = new Object();
	/**
	 * zariadenie na ktorom sa bude hudba pustat
	 */
	private Mixer.Info device;

	/**
	 * Velkost bufferu (Default: 4K), ine velkosti som netestoval
	 */
	private int bufferSize = 4096;

	/**
	 * Premenna pomocou ktorej vieme pozastavit pesnicku
	 */
	private boolean paused = false;

	/**
	 * Ukazovatel zastavenej piesne, sluzi na indentifikovanie ci piesen
	 * skoncila alebo bola prerusena
	 */
	private boolean stopped = false;

	/**
	 * Uchovava otvotenu zvukovu kartu na prehravanie
	 */
	private SourceDataLine line;

	/**
	 * Prepojenie na controller pomocou listenera
	 */
	private PlayerListener playerListener;

	/**
	 * Vytvara model prehravania hudby
	 * 
	 * @param device
	 */
	public PlayerModel(Mixer.Info device) {
		this.device = device;
	}

	/**
	 * Zastavuje piesen
	 */
	public void stopSong() {
		stopSongLocal();
		playerListener.playStopped(song);
	}

	/**
	 * Lokakne zastavi piesen sposobom, ze ju pauzne a zapise do stopped ze bola
	 * stopnuta aby sa neodoslal event ukoncenej piesne
	 */
	private void stopSongLocal() {
		if (!isPaused()) {
			paused = true;
		}
		stopped = true;
	}

	/**
	 * @return True if song is paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Pozastavi piesen
	 */
	public void pauseSong() {
		if (!isPaused()) {
			paused = true;
			playerListener.playPaused();
		}
	}

	/**
	 * Piesen pokracuje v hrani dalej
	 */
	public void resumeSong() {
		if (isPaused()) {
			// vojde do synchronizovaneho prehravania, pomocou notify ukonci
			// wait
			synchronized (lock) {
				lock.notifyAll();
				paused = false;
			}
			playerListener.playResumed();
		}
	}

	/**
	 * Podla aktualneho stavu prehravania urobi zmenu Prehrava sa -> Pauzne,
	 * Pauznuta -> Spusti prehravanie od pauznutej pozicie Zastavena -> Spusti
	 * odznova
	 */
	public void toggleSong() {
		if (isPlaying()) {
			if (isPaused()) {
				resumeSong();
			} else {
				pauseSong();
			}
		} else {
			play(song);
		}

	}

	/**
	 * Uchovava piesen vo formate mp3
	 */
	private AudioInputStream encoded;

	/**
	 * Uchovava piesen vo formate ktory dokaze prehrat v Jave vstavany
	 * AudioSystem
	 */

	private AudioInputStream currentDecoded;
	/**
	 * Vytvara buffer
	 */
	private byte[] b = new byte[this.bufferSize];

	/**
	 * Umoznuje zablokovat cakanie na dalsie spustenie prikazu play, Vyuziva to
	 * prehratie dalsej piesne z queue ak skoncila aktualna
	 */
	private boolean disabledLock2 = false;
	
	/**
	 * Uchovava velkovu dlzku piesne
	 */
	private Long duration;
	
	/**
	 * Uchovava model aktualne prehravanej prisne
	 */
	private SongModel song;

	/**
	 * V pripade ze skoncila predchadzajuca piesen a chceme vykonat spustenie dalsej pouzijeme tuto verziu play
	 * @param song
	 */
	public void playNext(SongModel song) {
		play(song);
		disabledLock2 = true;
	}

	/**
	 * Prehratie zadanej piesne, Metoda pripravuje dekoder z mp3 do AudioSystem
	 * @param song
	 */
	public void play(SongModel song) {
		stopSongLocal();

		this.song = song;

		synchronized (lock2) {

			try {
				encoded = AudioSystem.getAudioInputStream(song.getFile());
				AudioFormat encodedFormat = encoded.getFormat();
				AudioFormat decodedFormat = getDecodedFormat(encodedFormat);
				currentDecoded = AudioSystem.getAudioInputStream(decodedFormat,
						encoded);
				line = AudioSystem.getSourceDataLine(decodedFormat, device);
				line.open(decodedFormat);

				duration = song.getDuration();
				playerListener.setSongName(song.getTitle() + " - "
						+ song.getAuthor());

				duration = duration < 0 ? 0 : duration;
			} catch (UnsupportedAudioFileException | IOException
					| LineUnavailableException e) {
				e.printStackTrace();
			}
			lock2.notify();
		}
	}

	/**
	 * Metoda tvori samotny zivotny cyklus prehravania jednej piesne
	 */
	private void playerRunSong() {
		paused = false;
		stopped = false;
		try {
			line.start();
			playerListener.playStarted();
			synchronized (lock) {
				int i = 0;
				while (true) {
					playerListener.setPosition(line.getMicrosecondPosition(),
							duration);
					if (paused == true) {
						line.stop();
						if (stopped == true) {
							paused = false;
							break;
						}
						lock.wait();
						line.start();
					}

					i = currentDecoded.read(b, 0, b.length);
					if (i == -1) {
						break;
					}

					line.write(b, 0, i);
				}
			}

			line.drain();
			line.stop();
			line.close();
			currentDecoded.close();
			encoded.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (!stopped)
				playerListener.playFinished(song);
			stopped = false;
		}
	}

	/**
	 * Thread method, beziace vlákno
	 */
	public void run() {
		try {
			synchronized (lock2) {
				while (true) {
					if (!disabledLock2)
						lock2.wait();
					disabledLock2 = false;
					playerRunSong();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Nastavenie hlasitosti
	 * @param value
	 */
	public void setVolume(double value) {
		if (!line.isOpen())
			return;
		// value is between 0 and 1
		value = (value <= 0.0) ? 0.0001 : ((value > 1.0) ? 1.0 : value);
		try {
			float dB = (float) (Math.log(value) / Math.log(10.0) * 20.0);
			((FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN))
					.setValue(dB);
		} catch (Exception ex) {

		}
	}

	/*
	 * @return True if it's playing
	 */
	public boolean isPlaying() {
		return (line != null && line.isOpen());
	}

	/**
	 * Vytvyra objekt AudioFormat dekodovanej piesne
	 * @param format
	 * @return
	 */
	private static AudioFormat getDecodedFormat(AudioFormat format) {
		AudioFormat decodedFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED, // Encoding to use
				format.getSampleRate(), // sample rate (same as base format)
				16, // sample size in bits (thx to Javazoom)
				format.getChannels(), // # of Channels
				format.getChannels() * 2, // Frame Size
				format.getSampleRate(), // Frame Rate
				false // Big Endian
		);
		return decodedFormat;
	}

	/**
	 * Getter pre device
	 * @return
	 */
	public Mixer.Info getDevice() {
		return device;
	}

	/**
	 * Method to get all available devices for playing audio
	 * 
	 * @return List<Mixer.Info> devices
	 */
	public static List<Mixer.Info> getDevices() {
		List<Mixer.Info> devices = new ArrayList<>();

		try {
			InputStream io = PlayerModel.class.getResourceAsStream("test.mp3");
			AudioInputStream encoded = AudioSystem.getAudioInputStream(io);
			AudioFormat encodedFormat = encoded.getFormat();
			AudioFormat decodedFormat = getDecodedFormat(encodedFormat);

			Mixer.Info[] mi = AudioSystem.getMixerInfo();
			for (Mixer.Info info : mi) {
				boolean supported = true;
				try {
					SourceDataLine line = AudioSystem.getSourceDataLine(
							decodedFormat, info);
					line.close();
				} catch (LineUnavailableException | IllegalArgumentException ignore) {
					// taky zvukovy vystup nechceme
					supported = false;
				}
				if (supported
						&& !info.getName().startsWith("Primary Sound Driver")) {
					devices.add(info);
				}
			}
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		return devices;
	}

	/**
	 * Nastavuje player listener
	 * @param playerListener
	 */
	public void setPlayerListener(PlayerListener playerListener) {
		this.playerListener = playerListener;

	}

}
