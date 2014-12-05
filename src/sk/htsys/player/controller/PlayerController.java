package sk.htsys.player.controller;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import sk.htsys.player.controller.LibraryController.LibraryToolMenu;
import sk.htsys.player.model.PlayerListener;
import sk.htsys.player.model.PlayerModel;
import sk.htsys.player.model.PlayerQueueModel;
import sk.htsys.player.model.SongModel;
import sk.htsys.player.view.PlayerView;

/**
 * Metoda poskytujuca kontrolu prehravacom
 *  
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
public class PlayerController {
	private PlayerModel m_model;
	private PlayerView m_view;
	private PlayerQueueModel queue = new PlayerQueueModel();

	/**
	 * Vytvara controller a nastavuje spojenie s VIEW, spusta vlakno prehravaca
	 * 
	 * @param model
	 * @param view
	 */
	public PlayerController(PlayerModel model, PlayerView view) {
		m_model = model;
		m_view = view;

		m_view.addToggleActionListener(new ToggleActionListener());
		m_view.addStopActionListener(new StopActionListener());
		m_view.addNextActionListener(new NextActionListener());
		m_view.setTableModel(queue);
		m_view.setTableClickListener(new TableClickListener());
		m_view.setTableDragNDrop(new TableRowTransferHandler(m_view.getTable()));

		m_model.setPlayerListener(new PlayerModelListener());
		m_model.start();
	}

	/**
	 * Okamzite prehra vybranu piesen, zastavi aktualne hranu piesen a spusti tu
	 * zo vstupu
	 * 
	 * @param song
	 */
	public void play(SongModel song) {
		if (queue.size() >= 1) {
			queue.remove(0);
		}
		queue.add(0, song);
		m_model.play(song);
		m_view.setVisible(true);
	}

	/**
	 * Prida piesen do zoznamu prehravania
	 * 
	 * @param song
	 */
	public void queue(SongModel song) {
		queue.add(song);
		if (queue.size() == 1) {
			next();
			m_view.setVisible(true);
		}
	}

	/**
	 * Prehra nasledujucu piesen v zozname
	 */
	public void next() {
		if (queue.size() > 0) {
			m_model.playNext(queue.get(0));
		}
	}

	/**
	 * Vytvara polozky v menu pre {@link LibraryController.LibraryToolMenu}
	 * 
	 * @param libraryToolMenu
	 * @param song
	 */
	public void menuItem(LibraryToolMenu libraryToolMenu, final SongModel song) {

		JMenuItem playItem = new JMenuItem("Play " + " (" + m_model.getDevice()
				+ ")");
		playItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				play(song);
			}
		});
		libraryToolMenu.add(playItem);

		JMenuItem queueItem = new JMenuItem("Queue " + " ("
				+ m_model.getDevice() + ")");
		queueItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				queue(song);
			}
		});
		libraryToolMenu.add(queueItem);

	}

	/**
	 * Udalost na stlacenie Toggle tlacidla
	 * 
	 * @author patrik
	 */
	private class ToggleActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			m_model.toggleSong();
		}
	}

	/**
	 * Udalost na stlacenie Stop tlacidla
	 * 
	 * @author patrik
	 */
	private class StopActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			m_model.stopSong();
		}
	}

	/**
	 * Udalost na stlacenie Next tlacidla
	 * 
	 * @author patrik
	 */
	private class NextActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			m_model.stopSong();
			queue.remove(0);
			next();
		}
	}

	/**
	 * Udalosti z prehravaca
	 * 
	 * @author patrik
	 */
	private class PlayerModelListener implements PlayerListener {
		/**
		 * Po ukonceni hrania piesne prejdeme na dalsiu v zozname
		 */
		@Override
		public void playFinished(SongModel song) {
			queue.remove(song);
			next();
		}

		/**
		 * pri zacati zmenime pohlad vo VIEW
		 */
		@Override
		public void playStarted() {
			m_view.playingState("playing");
		}

		/**
		 * Pri stopnuti zmenime pohlad vo VIEW
		 */
		@Override
		public void playStopped(SongModel song) {
			m_view.playingState("stopped");
		}

		/**
		 * Pri pauze zmenime pohlad vo VIEW
		 */
		@Override
		public void playPaused() {
			m_view.playingState("paused");
		}

		/**
		 * Po vrateni z pauzy zmenime pohlad vo VIEW
		 */
		@Override
		public void playResumed() {
			m_view.playingState("playing");
		}

		/**
		 * Nastavime nazov piesne
		 */
		@Override
		public void setSongName(String string) {
			m_view.setSongName(string);
		}

		/**
		 * Nastavime aktualnu poziciu
		 */
		@Override
		public void setPosition(long pos, long dur) {
			m_view.setPosition(pos, dur);
		}

	}

	/**
	 * Udalosti myši v tabulke queue
	 * 
	 * @author patrik
	 */
	class TableClickListener implements MouseListener {
		/**
		 * Stlacenim praveho tlacitka sa zmaze piesen z queue
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				JTable source = (JTable) e.getSource();
				int row = source.rowAtPoint(e.getPoint());
				int modelRow = source.convertRowIndexToModel(row);
				PlayerQueueModel model = (PlayerQueueModel) source.getModel();
				if (modelRow != 0)
					model.remove(modelRow);
			}

		}

		/**
		 * Dvojklikom sa okmaznite spusti vybrana piesen
		 */
		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				JTable source = (JTable) e.getSource();
				int row = source.rowAtPoint(e.getPoint());
				int modelRow = source.convertRowIndexToModel(row);
				PlayerQueueModel model = (PlayerQueueModel) source.getModel();
				model.moveTo(modelRow, 1);
				m_model.stopSong();
				queue.remove(0);
				next();
			}
		}

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

	/**
	 * Uprava poradie v queue
	 */
	@SuppressWarnings("serial")
	public class TableRowTransferHandler extends TransferHandler {
		private final DataFlavor localObjectFlavor = new ActivationDataFlavor(
				Integer.class, DataFlavor.javaJVMLocalObjectMimeType,
				"Integer Row Index");
		private JTable table = null;

		/**
		 * Ulozime si s ktorou tabulkou pracujeme
		 * 
		 * @param table
		 */
		public TableRowTransferHandler(JTable table) {
			this.table = table;
		}

		/**
		 * Zistime ci mozeme objekt presuvat (Prva sa prehrava preto sa nemoze
		 * presuvat), Vytvorime objekt presuvania
		 */
		@Override
		protected Transferable createTransferable(JComponent c) {
			if (table.getSelectedRow() == 0)
				return null;
			return new DataHandler(table.getSelectedRow(),
					localObjectFlavor.getMimeType());
		}

		/**
		 * Zistime ci sa dany presuvany objekt moze polozit na aktualnom mieste 
		 */
		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {
			boolean b = info.getComponent() == table && info.isDrop()
					&& info.isDataFlavorSupported(localObjectFlavor);
			table.setCursor(b ? DragSource.DefaultMoveDrop
					: DragSource.DefaultMoveNoDrop);
			return b;
		}

		/**
		 * Nastavenie fungovania
		 */
		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY_OR_MOVE;
		}

		/**
		 * Samotny zapis presunu do queue
		 */
		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			JTable target = (JTable) info.getComponent();
			JTable.DropLocation dl = (JTable.DropLocation) info
					.getDropLocation();
			int index = dl.getRow();
			int max = table.getModel().getRowCount();
			if (index < 0 || index > max)
				index = max;
			target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			try {
				Integer rowFrom = (Integer) info.getTransferable()
						.getTransferData(localObjectFlavor);
				if (rowFrom != -1 && rowFrom != index) {
					PlayerQueueModel pq = (PlayerQueueModel) table.getModel();
					pq.moveTo(rowFrom, index);
					if (index > rowFrom)
						index--;
					target.getSelectionModel().addSelectionInterval(index,
							index);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}


		@Override
		protected void exportDone(JComponent c, Transferable t, int act) {
			if (act == TransferHandler.MOVE) {
				table.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

	}

}
