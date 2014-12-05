package sk.htsys.player.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Uchovava aktualny queue(playlist) prehravaca
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
@SuppressWarnings("serial")
public class PlayerQueueModel extends AbstractTableModel {
	private List<SongModel> queue = new ArrayList<>();

	/**
	 * Pridava piesen do queue
	 * 
	 * @param song
	 */
	public void add(SongModel song) {
		queue.add(song);
		fireTableDataChanged();
	}

	/**
	 * Pridava priesen do queue na zadany index, ostatne posunie o jedno vyzsie
	 * 
	 * @param index
	 * @param song
	 */
	public void add(int index, SongModel song) {
		queue.add(index, song);
		fireTableDataChanged();
	}

	/**
	 * Vrati objekt na indexe
	 * 
	 * @param index
	 * @return
	 */
	public SongModel get(int index) {
		return queue.get(index);
	}

	/**
	 * Zmaze prvy vyskyt piesne
	 * 
	 * @param song
	 */
	public void remove(SongModel song) {
		queue.remove(song);
		fireTableDataChanged();
	}

	/**
	 * Zmaze objekt na indexe
	 * 
	 * @param index
	 */
	public void remove(int index) {
		if (index < queue.size())
			queue.remove(index);
		fireTableDataChanged();
	}

	/**
	 * Presuva objekt v liste
	 *  
	 * @param fromIndex
	 * @param toIndex
	 */
	public void moveTo(int fromIndex, int toIndex) {
		if(toIndex==0) {
			toIndex = 1;
		}
		SongModel obj = queue.get(fromIndex);
		queue.remove(fromIndex);
		if(toIndex>fromIndex) {
			toIndex--;
		}
		queue.add(toIndex, obj);
		fireTableDataChanged();
		
	}

	
	/**
	 * Vrati velkost zoznamu
	 * 
	 * @return
	 */
	public int size() {
		return queue.size();
	}

	/**
	 * TableModel Method: pocet riadkov
	 */
	@Override
	public int getRowCount() {
		return queue.size();
	}

	/**
	 * TableModel Method: pocet stlpcov
	 */
	@Override
	public int getColumnCount() {
		return 3;
	}

	/**
	 * TableModel Method: vrati hodnotu v bunke
	 */
	@Override
	public Object getValueAt(int row, int col) {
		SongModel song = queue.get(row);
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
	 * TableModel Method: nastavuje hlavicku
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
