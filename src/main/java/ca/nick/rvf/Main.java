package ca.nick.rvf;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.gphoto2.GPhoto2Config;

public class Main {
	static GPhoto2 camera = new GPhoto2();
	static GPhoto2Config config;

	public static void main(String[] args) throws Exception {

		// Open a camera connection
		try {
			camera.open();
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage());
			System.exit(-1);
		}

		// Make sure the image destination is set to the SD card
		config = new GPhoto2Config(camera);
		config.readConfig();
		if (!config.getParameter("capturetarget").equals("Memory card")) {
			camera.setConfig(config, "capturetarget", "Memory card");
		}			
		
		// Resource handler
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(".");
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });

		// Context handler
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(StreamMJPG.class, "/stream.mjpg");
		context.addServlet(CameraControlServlet.class, "/cameraControl");
		context.addServlet(GetSettingsServlet.class, "/getValues");

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
			@Override
			public void run() {
				camera.close();
			}
		});
	}
}
