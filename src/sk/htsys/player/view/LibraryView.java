package sk.htsys.player.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JPanel;

import sk.htsys.player.model.LibraryModel;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;

/**
 * Zobrazenie kniznice
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
@SuppressWarnings("serial")
public class LibraryView extends JFrame {
	private LibraryModel m_model;
	private JTable table;
	private JLabel libraryStatus;

	/**
	 * Vytvara Swing okno Kniznice a nastavuje DataModel tabulky
	 * 
	 * @param model
	 */
	public LibraryView(LibraryModel model) {
		m_model = model;
		setTitle("Hudobná knižnica");
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		table = new JTable(m_model);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		table.setDragEnabled(false);
		table.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(table);

		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);

		libraryStatus = new JLabel("Ready");
		panel.add(libraryStatus);
	}

	/**
	 * Nastavuje tabulke listener pre myš
	 * 
	 * @param tableClickListener
	 */
	public void setTableClickListener(MouseListener tableClickListener) {
		table.addMouseListener(tableClickListener);
	}

	/**
	 * Nastavuje status text
	 * 
	 * @param string
	 */
	public void setStatusString(String string) {
		libraryStatus.setText(string);
	}
}
