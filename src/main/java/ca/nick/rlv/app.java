package ca.nick.rlv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
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
import com.angryelectron.gphoto2.GPhoto2Config;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraFile;
import com.google.gson.Gson;

public class app {
	private static GPhoto2 camera = new GPhoto2();
	private static GPhoto2Config config;
	
	static List<String> isoList;

	public static void main(String[] args) throws Exception {

		// Open a camera connection
		try {
			camera.open();
		} catch (IOException ex) {
			Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
			System.exit(-1);
		}

		// Make sure the image destination is set to the SD card
		config = new GPhoto2Config(camera);
		config.readConfig();
		if (!config.getParameter("capturetarget").equals("Memory card")) {
			camera.setConfig(config, "capturetarget", "Memory card");
		}

		// TODO Get current camera params for webpage load
		isoList = camera.getChoiceList(config.getParameterWidget("iso"));
		List<String> fstopList = camera.getChoiceList(config.getParameterWidget("aperture"));
		List<String> shutterList = camera.getChoiceList(config.getParameterWidget("shutterspeed"));
		List<String> modeList = camera.getChoiceList(config.getParameterWidget("drivemode"));
		
		// Resource handler
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(".");
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });

		// Context handler
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(StreamMJPG.class, "/stream.mjpg");
		context.addServlet(cameraControl.class, "/");
		context.addServlet(GetParams.class, "/test");

		// Register handlers with the server
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, context, new DefaultHandler() });
		Server server = new Server(8080);
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
	public static class cameraControl extends HttpServlet {
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			switch (req.getParameterNames().nextElement()) {
			case "iso":
				camera.setConfig(config, "iso", req.getParameter("iso"));
				break;
			case "aperture":
				camera.setConfig(config, "aperture", req.getParameter("aperture"));
				break;
			case "shutter":
				camera.setConfig(config, "shutterspeed", req.getParameter("shutter"));
				break;
			case "drivemode":
				camera.setConfig(config, "drivemode", req.getParameter("drivemode"));
				break;
			case "snap":
				camera.capture();
				break;
			}
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}

	@SuppressWarnings("serial")
	public static class StreamMJPG extends HttpServlet {
		private static final byte[] PREFIX = ("--boundary\r\nContent-type: image/jpg\r\nContent-Length: ").getBytes();
		private static final byte[] SEPARATOR = "\r\n\r\n".getBytes();
		private static final long FRAME_INTERVAL = 33;

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			final OutputStream out = resp.getOutputStream();
			CameraFile fileRef = camera.createCameraFile();
			byte[] frame;
			boolean loop = true;
			long prevFrame = 0;

			// Multipart response allows frames to be replaced in subsequent messages
			resp.setContentType("multipart/x-mixed-replace; boundary=--boundary");
			resp.setStatus(HttpServletResponse.SC_OK);

			// Send messages with image data in a while loop.
			while (loop) {
				if (System.currentTimeMillis() - prevFrame > FRAME_INTERVAL) {

					prevFrame = System.currentTimeMillis();
					try {
						frame = camera.capturePreview(fileRef);

						out.write(PREFIX);
						out.write(String.valueOf(frame.length).getBytes());
						out.write(SEPARATOR);
						out.write(frame);
						out.write(SEPARATOR);
						out.flush();

						frame = null;
						Thread.yield();
						System.gc();
					} catch (IOException ex) {
						loop = false;
					}
				}
			}
		}
	}	
	
	@SuppressWarnings("serial")
	public static class GetParams extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			PrintWriter out = resp.getWriter();
			
			String json = new Gson().toJson(isoList);
			out.print(json);	
			
			resp.setContentType("application/json");
			resp.setStatus(HttpServletResponse.SC_OK);
		}		
	}
}
