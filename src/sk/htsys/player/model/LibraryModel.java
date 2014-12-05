package sk.htsys.player.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import sk.htsys.player.controller.LibraryController.LibraryListener;

/**
 * Model na uchovavanie SongModel-ov
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
@SuppressWarnings("serial")
public class LibraryModel extends AbstractTableModel {
	private LibraryListener listener;
	private List<SongModel> songs = new ArrayList<>();

	/**
	 * Pridanie piesne do kniznice
	 * 
	 * @param SongModel
	 *            song
	 */
	private void add(SongModel song) {
		songs.add(song);
	}

	/**
	 * Zmazanie prveho vyskytu piesne z kniznice
	 * 
	 * @param SongModel
	 *            song
	 */
	public void remove(SongModel song) {
		songs.remove(song);
	}

	/**
	 * Ziskanie piesne z indexu
	 * 
	 * @param index
	 * @return
	 */
	public SongModel get(int index) {
		return songs.get(index);
	}

	// Folder scanner
	private Thread scannerThread;

	/**
	 * Nacitanie vsetkych *.mp3 suborov do kniznice zo zadaneho priecinka,
	 * pracuje vo vlakne aby na dlhu dobu nezastavilo kniznicu, nakoniec posle
	 * informaciu aby sa obnovila tabulka vo View
	 * 
	 * @param dir
	 */
	public void scanFolder(final File dir) {
		scannerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean interrupted = false;
				try {
					scanFolderDo(dir);
				} catch (InterruptedException e) {
					interrupted = true;
				}
				if (interrupted) {
					listener.statusChanges("!INTERRUPTED! Scan finished (songs "
							+ songs.size() + ")");
				} else {
					listener.statusChanges("Scan finished (songs "
							+ songs.size() + ")");
				}
				fireTableDataChanged();
			}
		});
		scannerThread.start();
	}

	/**
	 * Samotne skenovanie priecinka (Rekurzivna metoda!)
	 * 
	 * @param dir
	 * @throws InterruptedException
	 */
	private void scanFolderDo(File dir) throws InterruptedException {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".mp3")) {
				listener.statusChanges(file.getName());
				add(SongModel.fromFile(file));
			}
			if (file.isDirectory())
				scanFolderDo(file);
			Thread.sleep(0);
		}
	}

	/**
	 * Debug metoda na vypisanie vsetkych priesni
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (SongModel song : songs) {
			sb.append(song + "\n");
		}
		return sb.toString();
	}

	/**
	 * Ulozenie kniznice do binarneho suboru
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void save(File file) throws IOException {
		FileOutputStream ostream = null;
		ObjectOutputStream output = null;
		try {
			ostream = new FileOutputStream(file);
			output = new ObjectOutputStream(ostream);
			for (SongModel song : songs) {
				output.writeObject(song);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ostream != null)
				ostream.close();
			if (output != null)
				output.close();
		}

	}

	/**
	 * Nacitanie kniznice z binarneho suboru
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static LibraryModel load(File file) throws IOException {
		LibraryModel l = new LibraryModel();
		FileInputStream istream = null;
		ObjectInputStream input = null;
		try {
			istream = new FileInputStream(file);
			input = new ObjectInputStream(istream);
			while (istream.available() > 0) {
				Object found = input.readObject();
				if (found == null)
					break;
				l.add((SongModel) found);
			}
			input.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (istream != null)
				istream.close();
			if (input != null)
				input.close();
		}
		return l;
	}
	

	public void setListener(LibraryListener libraryListener) {
		listener = libraryListener;
	}
	
	/**
	 * TableModel Method: Pocet stlpcov
	 */
	@Override
	public int getColumnCount() {
		return 3;
	}

	/**
	 * TableModel Method: Pocet riadkov
	 */
	@Override
	public int getRowCount() {
		return songs.size();
	}

	/**
	 * TableModel Method: Ziskanie hodnoty pre bunku v tabulke, pri col=-1 vracia samotny SongModel
	 */
	@Override
	public Object getValueAt(int row, int col) {
		SongModel song = songs.get(row);
		switch (col) {
		case -1:
			return song;
		case 0:
			return song.getTitle();
		case 1:
			return song.getAlbum();
		case 2:
			return song.getAuthor();
		}
		return null;
	}

	/**
	 * TableModel Method: Stlpce tabulky
	 */
	@Override
	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Title";
		case 1:
			return "Album";
		case 2:
			return "Author";
		}
		return super.getColumnName(col);
	}

}
