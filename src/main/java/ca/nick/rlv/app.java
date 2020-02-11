package ca.nick.rlv;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.angryelectron.gphoto2.GPhoto2;

public class app {

	public static void main(String[] args) {

		// Connect to camera
		GPhoto2 camera = new GPhoto2();
		try {
			camera.open();
		} catch (IOException ex) {
			Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
			System.exit(-1);
		}

		// Diddle around with the camera
		try {
//            String iso = camera.getConfig("iso"); // Where params are found by entering command "gphoto2 --list-config" in a terminal"
//            System.out.println(iso);
			BufferedImage image = camera.capturePreview();

			JFrame frame = new JFrame();
			frame.getContentPane().setLayout(new FlowLayout());
			frame.getContentPane().add(new JLabel(new ImageIcon(image)));
			frame.pack();
			frame.setVisible(true);

		} catch (IOException ex) {
			Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
			camera.close();
			System.exit(-1);
		}

		// Cleanup
		camera.close();

	}

}
