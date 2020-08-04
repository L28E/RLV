package ca.nick.rvf;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.gphoto2.GPhoto2Config;

@SuppressWarnings("serial")
public class CameraControlServlet extends HttpServlet {

	private final GPhoto2 camera;
	private final GPhoto2Config config;

	public CameraControlServlet() {
		this.camera = Main.camera;
		this.config = Main.config;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
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
			default:
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				break;
			}
		} catch (IOException ex) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			Logger.getLogger(CameraControlServlet.class.getName()).log(Level.SEVERE, ex.getMessage());
			return;
		}
		resp.setStatus(HttpServletResponse.SC_OK);
	}
}
