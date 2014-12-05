package sk.htsys.player.view;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;

import sk.htsys.player.model.PlayerModel;

/**
 * Zobrazenie prehravaca
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
@SuppressWarnings("serial")
public class PlayerView extends JFrame {
	private PlayerModel m_model;

	private JTextField status;
	private JTextField songName;
	private JButton toggleButton;
	private JButton stopButton;
	private JButton nextButton;
	private JScrollPane scrollPane;
	private JTable table;
	
	/**
	 * Vytvara Swing okno Kniznice a nastavuje DataModel queue (playlistu)
	 * 
	 * @param model
	 */
	public PlayerView(PlayerModel model) {
		m_model = model;
		setTitle("Prehrávač");
		setSize(495, 265);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		status = new JTextField(20);
		songName = new JTextField(20);
		toggleButton = new JButton("Play");
		stopButton = new JButton("Stop");
		nextButton = new JButton("Next");

		JPanel content = new JPanel();

		setContentPane(content);

		scrollPane = new JScrollPane();
		GroupLayout gl_content = new GroupLayout(content);
		gl_content
				.setHorizontalGroup(gl_content
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								Alignment.TRAILING,
								gl_content
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_content
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																scrollPane,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																458,
																Short.MAX_VALUE)
														.addGroup(
																gl_content
																		.createSequentialGroup()
																		.addComponent(
																				toggleButton)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				stopButton)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addGroup(
																				gl_content
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								songName,
																								GroupLayout.PREFERRED_SIZE,
																								277,
																								GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								status,
																								GroupLayout.DEFAULT_SIZE,
																								277,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				nextButton)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)))
										.addGap(43)));
		gl_content
				.setVerticalGroup(gl_content
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_content
										.createSequentialGroup()
										.addGroup(
												gl_content
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_content
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				songName,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				status,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																gl_content
																		.createSequentialGroup()
																		.addGap(23)
																		.addGroup(
																				gl_content
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								toggleButton)
																						.addComponent(
																								stopButton)))
														.addGroup(
																gl_content
																		.createSequentialGroup()
																		.addGap(23)
																		.addComponent(
																				nextButton)))
										.addGap(15)
										.addComponent(scrollPane,
												GroupLayout.DEFAULT_SIZE, 140,
												Short.MAX_VALUE)
										.addContainerGap()));

		table = new JTable();
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		content.setLayout(gl_content);
		
		// pri zatvoreni okna, chceme aj zastavit prehravanu hudbu 
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				m_model.stopSong();
			}
		});

	}

	/**
	 * Nastavuje nazov aktualne prehravanej priesne
	 * 
	 * @param string
	 */
	public void setSongName(String string) {
		songName.setText(string);
	}

	/**
	 * Metoda na konvertovanie microsekund na string formatu HH:MM:SS
	 * 
	 * @param microseconds
	 * @return
	 */
	private String MicrosecondsToString(long microseconds) {
		long millis = microseconds;
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	/**
	 * Nastavuje aktualnu poziciu prehravanej piesne
	 * 
	 * @param microsecondPosition
	 * @param duration
	 */
	public void setPosition(long microsecondPosition, long duration) {
		status.setText(MicrosecondsToString(microsecondPosition / 1000) + " / "
				+ MicrosecondsToString(duration / 1000));
	}

	/**
	 * Nastavuje stav prehravania hudby
	 * 
	 * @param string (playing|paused|resume|stopped)
	 */
	public void playingState(String string) {
		if (string.equals("playing")) {
			toggleButton.setText("Pause");
		} else if (string.equals("paused")) {
			toggleButton.setText("Resume");
		} else if (string.equals("stopped")) {
			toggleButton.setText("Play");
		}

	}

	/**
	 * Udalost na stlacenie Toggle tlacidla (Play|Pause|Resume)
	 * 
	 * @param toggleActionListener
	 */
	public void addToggleActionListener(ActionListener toggleActionListener) {
		toggleButton.addActionListener(toggleActionListener);
	}

	/**
	 * Udalost na stlacenie Stop tlacidla
	 * 
	 * @param stopActionListener
	 */
	public void addStopActionListener(ActionListener stopActionListener) {
		stopButton.addActionListener(stopActionListener);
	}

	/**
	 * Udalost na stlacenie NextTlacidla
	 * 
	 * @param nextActionListener
	 */
	public void addNextActionListener(ActionListener nextActionListener) {
		nextButton.addActionListener(nextActionListener);
	}

	/**
	 * Nastavuje queue model
	 * @param model
	 */
	public void setTableModel(TableModel model) {
		table.setModel(model);
	}

	/**
	 * Udalost na stlacenie myši v tabulke
	 * 
	 * @param tableClickListener
	 */
	public void setTableClickListener(MouseListener tableClickListener) {
		table.addMouseListener(tableClickListener);
	}

	/**
	 * Nastvuje metodu ktora bude analyzovat Drag'n Drop s riadkami v tabulke queue
	 * 
	 * @param handler
	 */
	public void setTableDragNDrop(TransferHandler handler) {
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setTransferHandler(handler);
	}
	
	/**
	 * @return Queue table object
	 */
	public JTable getTable() {
		return table;
	}
}
