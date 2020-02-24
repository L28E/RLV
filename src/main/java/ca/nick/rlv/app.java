package ca.nick.rlv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import com.angryelectron.gphoto2.GPhoto2;

public class app {
	private static GPhoto2 camera = new GPhoto2();
	private static final byte[] prefix = ("--boundary\r\nContent-type: image/jpg\r\nContent-Length: ").getBytes();
	private static final byte[] separator = "\r\n\r\n".getBytes();

	public static void main(String[] args) throws Exception {

		// Establish a connection to the camera
		try {
			camera.open();
		} catch (IOException ex) {
			Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
			System.exit(-1);
		}

		// Start the server
		Server server = new Server(8080);
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		handler.addServletWithMapping(StreamMJPG.class, "/stream.mjpg");
		server.start();
		server.join();
	}

	@SuppressWarnings("serial")
	public static class StreamMJPG extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			final OutputStream out = resp.getOutputStream();
			byte[] frame;	
			
			/*
			 * The MIME content type "multipart/x-mixed-replace" lets me replace a component
			 * of the page in subsequent HTTP responses.
			 */
			resp.setContentType("multipart/x-mixed-replace; boundary=--boundary");
			resp.setStatus(HttpServletResponse.SC_OK);

			// Send HTTP responses with frame data in a while loop.
			while (true) {
				try {
					// Get a frame.
					frame = camera.capturePreview();

					// Write the HTTP response.
					out.write(prefix);
					out.write(String.valueOf(frame.length).getBytes());
					out.write(separator);
					out.write(frame);
					out.write(separator);
					out.flush();
				} catch (IOException ex) {
					Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
					// camera.setConfig("output", "TFT");
					camera.close();
					System.exit(-1);
				}
			}
		}
	}
}
