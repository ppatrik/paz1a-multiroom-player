package sk.htsys.player.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

import sk.htsys.player.model.SongListener;
import sk.htsys.player.model.SongModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Zobrazenie formularu na upravu ID3 tagu
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
@SuppressWarnings("serial")
public class SongView extends JFrame {
	@SuppressWarnings("unused")
	private SongModel m_model;

	private JPanel contentPane;
	private JTextField fileText;
	private JTextField titleText;
	private JTextField authorText;
	private JTextField albumText;

	private SongListener listener;

	/**
	 * Vytvara Swing okno Kniznice a nastavuje DataModel tabulky
	 * 
	 * @param model
	 */
	public SongView(SongModel model) {
		m_model = model;
		
		setName("Song info");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 193);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblFile = new JLabel("File:");

		fileText = new JTextField();
		fileText.setEditable(false);
		fileText.setEnabled(false);
		fileText.setColumns(10);

		titleText = new JTextField();
		titleText.setColumns(10);

		JLabel lblTitle = new JLabel("Title:");

		JLabel lblAuthor = new JLabel("Author:");

		authorText = new JTextField();
		authorText.setColumns(10);

		albumText = new JTextField();
		albumText.setColumns(10);

		JLabel lblAlbum = new JLabel("Album:");

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listener.save();
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.TRAILING)
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addGroup(
																				gl_contentPane
																						.createParallelGroup(
																								Alignment.LEADING,
																								false)
																						.addComponent(
																								lblFile,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								lblTitle,
																								GroupLayout.DEFAULT_SIZE,
																								50,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addGroup(
																				gl_contentPane
																						.createParallelGroup(
																								Alignment.TRAILING)
																						.addComponent(
																								titleText,
																								Alignment.LEADING,
																								GroupLayout.DEFAULT_SIZE,
																								350,
																								Short.MAX_VALUE)
																						.addComponent(
																								fileText,
																								Alignment.LEADING,
																								GroupLayout.DEFAULT_SIZE,
																								350,
																								Short.MAX_VALUE)))
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addComponent(
																				lblAuthor,
																				GroupLayout.PREFERRED_SIZE,
																				50,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(4)
																		.addComponent(
																				authorText,
																				GroupLayout.PREFERRED_SIZE,
																				350,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addComponent(
																				lblAlbum,
																				GroupLayout.PREFERRED_SIZE,
																				50,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(4)
																		.addComponent(
																				albumText,
																				GroupLayout.PREFERRED_SIZE,
																				350,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addComponent(
																				cancelButton)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				saveButton)))
										.addContainerGap()));
		gl_contentPane
				.setVerticalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(lblFile)
														.addComponent(
																fileText,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																titleText,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblTitle))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addGap(3)
																		.addComponent(
																				lblAuthor))
														.addComponent(
																authorText,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addGap(3)
																		.addComponent(
																				lblAlbum))
														.addComponent(
																albumText,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																saveButton)
														.addComponent(
																cancelButton))
										.addContainerGap(113, Short.MAX_VALUE)));
		contentPane.setLayout(gl_contentPane);
	}

	/**
	 * Sets text in all inputs
	 * 
	 * @param file
	 * @param title
	 * @param album
	 * @param author
	 */
	public void setData(String file, String title, String album, String author) {
		fileText.setText(file);
		titleText.setText(title);
		albumText.setText(album);
		authorText.setText(author);
	}

	/**
	 * @return song title
	 */
	public String getTitle() {
		return titleText.getText();
	}

	/**
	 * @return song album
	 */
	public String getAlbum() {
		return albumText.getText();
	}

	/**
	 * @return song author
	 */
	public String getAuthor() {
		return authorText.getText();
	}


	/**
	 * Nastavuje listener pre ulozenie
	 * 
	 * @param songFormListener
	 */
	public void setListener(SongListener songFormListener) {
		listener = songFormListener;
	}
}
