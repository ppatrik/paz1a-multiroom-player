package sk.htsys.player;

import java.io.File;
import java.util.List;

import javax.sound.sampled.Mixer;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import sk.htsys.player.controller.LibraryController;
import sk.htsys.player.controller.PlayerController;
import sk.htsys.player.model.LibraryModel;
import sk.htsys.player.model.PlayerModel;
import sk.htsys.player.view.LibraryView;
import sk.htsys.player.view.PlayerView;

/**
 * Multi-Roam Music Player
 * 
 * @author      Patrik Pekarčík <patrik@htsys.sk>
 * @version     0.1
 * @since       2014-01-01
 */
public class Main {
	/**
	 * Startup method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}
	
	/**
	 * Constructor of Player Application
	 */
	public Main() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LibraryModel model = new LibraryModel();
		LibraryView view = new LibraryView(model);
		LibraryController controller = new LibraryController(model, view);

		JFileChooser j = new JFileChooser(new File("c:\\_data\\music\\"));
		j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		j.setDialogTitle("Vyberte priečinok s hudbou");
		j.showOpenDialog(new JFrame());
		if (j.getSelectedFile() == null) {
			System.exit(0);
			return;
		}
		model.scanFolder(j.getSelectedFile());
		
		
		view.setVisible(true);

		List<Mixer.Info> devices = PlayerModel.getDevices();
		for (final Mixer.Info device : devices) {

			PlayerModel p_model = new PlayerModel(device);
			PlayerView p_view = new PlayerView(p_model);
			p_view.setTitle(p_view.getTitle() + " (" + device + ")");
			PlayerController p_controller = new PlayerController(p_model,
					p_view);

			controller.addPlayer(p_controller);

		}
	}

}
