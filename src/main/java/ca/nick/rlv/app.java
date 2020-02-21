package ca.nick.rlv;

import java.awt.image.BufferedImage;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import com.angryelectron.gphoto2.GPhoto2;

public class app {

	private static GPhoto2 camera = new GPhoto2();

	public static void main(String[] args) throws Exception {
		
		try {
			camera.open();
		} catch (IOException ex) {
			Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
			System.exit(-1);
		}

		Server server = new Server(8080); // Instanciate server
		ServletHandler handler = new ServletHandler(); // Instanciate handler
		server.setHandler(handler); // Register handler with server object
		handler.addServletWithMapping(Snap.class, "/snap"); // Mount the servlet class at some context path
		server.start(); // Starts the server
		server.join(); // current thread joins and waits for server thread to finish

	}

	@SuppressWarnings("serial")
	public static class Snap extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
						
			try {				
				BufferedImage img = camera.capturePreview();
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setContentType("image/jpg");
				ImageIO.write(img, "jpg", resp.getOutputStream());
			} catch (IOException ex) {
				Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
				// camera.setConfig("output", "TFT");
				camera.close();
				System.exit(-1);
			}
		}
	}

}
