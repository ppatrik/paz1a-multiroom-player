package sk.htsys.player.controller;

import sk.htsys.player.model.LibraryModel;
import sk.htsys.player.model.SongListener;
import sk.htsys.player.model.SongModel;
import sk.htsys.player.view.SongView;

/**
 * Controller pre editaciu id3
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
public class SongController {
	private SongModel m_model;
	private SongView m_view;
	private LibraryModel m_library;

	/**
	 * Vytvara Controller a nastavuje data z modelu pre VIEW + pridava listenery
	 * 
	 * @param model
	 * @param view
	 * @param library
	 */
	public SongController(SongModel model, SongView view, LibraryModel library) {
		m_model = model;
		m_view = view;
		m_library = library;

		m_view.setData(m_model.getFile().toString(), m_model.getTitle(),
				m_model.getAlbum(), m_model.getAuthor());
		m_view.setListener(new SongFormListener());
		m_view.setVisible(true);
	}

	/**
	 * Uklada nove ID3 do modelu a zapisuje do suboru
	 * 
	 * @return
	 */
	public boolean saveSong() {
		m_model.setTitle(m_view.getTitle());
		m_model.setAlbum(m_view.getAlbum());
		m_model.setAuthor(m_view.getAuthor());
		boolean change = m_model.save();
		m_library.fireTableDataChanged();
		return change;
	}

	/**
	 * Vytvara Udalost po kliknuti na save button
	 * 
	 * @author patrik
	 */
	private class SongFormListener implements SongListener {

		@Override
		public void save() {
			if(saveSong()) {
				m_view.dispose();
			} else {
				// ENHANCE: should open warming dialog
				System.err.println("Nastala chyba pri ukladani zmien");
			}
			
		}

	}

}
