package ca.nick.rvf;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.gphoto2.GPhoto2Config;

public class Main {
	static GPhoto2 camera = new GPhoto2();
	static GPhoto2Config config;

	public static void main(String[] args) throws Exception {
		{
			try {
				new Main().run();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public void run() throws Exception {

		// Open a camera connection
		try {
			camera.open();
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage());
			System.exit(-1);
		}

		// Get URI of static web content
		URL resourceLocation = this.getClass().getResource("/index.html");
		if (resourceLocation == null) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Could not find index.html");
			System.exit(-1);
		}
		URI resourceUri = URI.create(resourceLocation.toURI().toASCIIString().replaceFirst("/index.html$", "/"));
		
		// Make sure the image destination is set to the SD card
		config = new GPhoto2Config(camera);
		config.readConfig();
		if (!config.getParameter("capturetarget").equals("Memory card")) {
			camera.setConfig(config, "capturetarget", "Memory card");
		}		

		// Context handler
		ServletContextHandler contextHandler = new ServletContextHandler();
		contextHandler.setContextPath("/");
		contextHandler.setBaseResource(Resource.newResource(resourceUri));
		contextHandler.setWelcomeFiles(new String[] { "index.html" });
		contextHandler.addServlet(StreamMJPG.class, "/stream.mjpg");
		contextHandler.addServlet(CameraControlServlet.class, "/cameraControl");
		contextHandler.addServlet(GetSettingsServlet.class, "/getValues");
		contextHandler.addServlet(DefaultServlet.class, "/");

		// Start server
		Server server = new Server(8080);
		server.setHandler(contextHandler);
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
