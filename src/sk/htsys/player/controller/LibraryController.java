package sk.htsys.player.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import sk.htsys.player.model.LibraryModel;
import sk.htsys.player.model.SongModel;
import sk.htsys.player.view.LibraryView;
import sk.htsys.player.view.SongView;


/**
 * Metoda poskytujuca kontrolu nad kniznicou, jej zobrazenim a udalostami v nej
 * 
 * @author Patrik Pekarčík <patrik@htsys.sk>
 * @version 0.1
 * @since 2014-01-01
 */
public class LibraryController {
	private LibraryModel m_model;
	private LibraryView m_view;
	private LibraryToolMenu libraryToolMenu;
	private List<PlayerController> players = new ArrayList<>();

	/**
	 * Priradzuje zakladne listenery pre vstupne objekty
	 * 
	 * @param model
	 *            LibraryModel
	 * @param view
	 *            LibraryView
	 */
	public LibraryController(LibraryModel model, LibraryView view) {
		m_model = model;
		m_view = view;

		m_view.setTableClickListener(new TableClickListener());
		m_model.setListener(new LibraryListener());
		libraryToolMenu = new LibraryToolMenu();
	}

	/**
	 * Pridanie prehravacov do modelu
	 * 
	 * @param PlayerController
	 *            p_controller
	 */
	public void addPlayer(PlayerController p_controller) {
		players.add(p_controller);
	}

	/**
	 * Listener pre model kniznice, pre zmenu statusu v dolnej casti okna
	 * 
	 * @author patrik
	 */
	public class LibraryListener {
		public void statusChanges(String string) {
			m_view.setStatusString(string);
		}
	}

	/**
	 * Menu po kliknuti pravym tlacitkom na riadok
	 * 
	 * @author patrik
	 */
	@SuppressWarnings("serial")
	public class LibraryToolMenu extends JPopupMenu {

		public void generate(SongModel song) {
			removeAll();
			for (PlayerController player : players) {
				player.menuItem(libraryToolMenu, song);
			}
		}

	}

	/**
	 * Listener pre udalosti myši v tabulke kniznice
	 * 
	 * @author patrik
	 */
	class TableClickListener implements MouseListener {
		/**
		 * Podla MouseEvent(informacii o kliknuti uzivatelom) a X, Y suradnice
		 * ziskava SongModel, na ktory sa kliklo
		 * 
		 * @param MouseEvent
		 *            e
		 * @return SongModel
		 */
		private SongModel getInfo(MouseEvent e) {
			JTable source = (JTable) e.getSource();
			int row = source.rowAtPoint(e.getPoint());
			// kedze vyuzivame zoradenie od JTable tak potrebujeme ziskat index
			// riadku v modeli
			int modelRow = source.convertRowIndexToModel(row);
			int column = source.columnAtPoint(e.getPoint());
			TableModel model = source.getModel();
			SongModel song = (SongModel) model.getValueAt(modelRow, -1);

			if (!source.isRowSelected(row))
				source.changeSelection(row, column, false, false);
			return song;

		}

		/**
		 * Udalost praveho klikntia myši
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				SongModel song = getInfo(e);

				libraryToolMenu.generate(song);
				libraryToolMenu.show(e.getComponent(), e.getX(), e.getY());
			}

		}

		/**
		 * Udalost pre dvojklik na tabulku
		 */
		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						SongModel model = getInfo(e);
						SongView view = new SongView(model);
						new SongController(model, view, m_model);
					}
				});
			}
		}

		// ostatne nepotrebujeme ale su v interface :(
		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	}
}
