package ca.nick.rlv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraFile;

public class app {
	private static GPhoto2 camera = new GPhoto2();
	private static final byte[] prefix = ("--boundary\r\nContent-type: image/jpg\r\nContent-Length: ").getBytes();
	private static final byte[] separator = "\r\n\r\n".getBytes();

	public static void main(String[] args) throws Exception {

		// Open a camera connection
		try {
			camera.open();
		} catch (IOException ex) {
			Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
			System.exit(-1);
		}

		Server server = new Server(8080);
		HandlerList handlers = new HandlerList();

		// Resource handler
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(".");
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });

		// Image handler
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(StreamMJPG.class, "/stream.mjpg");

		// Register handlers with the server
		handlers.setHandlers(new Handler[] { resourceHandler, context, new DefaultHandler() });
		server.setHandler(handlers);

		// Start server
		server.start();
		server.join();

		// Close the camera connection when the program ends
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				camera.close();
			}
		});
	}

	@SuppressWarnings("serial")
	public static class StreamMJPG extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			final OutputStream out = resp.getOutputStream();
			CameraFile fileRef = camera.createCameraFile();
			byte[] frame;			

			// Multipart response allows frames to be replaced in subsequent messages   
			resp.setContentType("multipart/x-mixed-replace; boundary=--boundary");
			resp.setStatus(HttpServletResponse.SC_OK);

			// Send messages with frame data in a while loop.
			try {
				while (true) {
					frame = camera.capturePreview(fileRef);

					// Write the message.
					out.write(prefix);
					out.write(String.valueOf(frame.length).getBytes());
					out.write(separator);

					// If the response is interrupted, exit doGet before an error occurs
					try {
						out.write(frame);
						out.write(separator);
						out.flush();
					} catch (Exception ex) {
						break;
					}					

					// TODO: I dont like this, try de/un-reffing stuff here and in the api.
					Thread.yield();
					System.gc();
				}
			} catch (IOException ex) {
				Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
				camera.close();
				ex.printStackTrace();
				System.exit(-1);
			}
		}

	}
}
