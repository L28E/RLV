package ca.nick.rlv;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.gphoto2.GPhoto2Config;

@SuppressWarnings("serial")
public class CameraControlServlet extends HttpServlet {
	
	private GPhoto2 camera;
	private GPhoto2Config config;	
	
	public CameraControlServlet() {
		this.camera = app.camera;
		this.config = app.config;
	}	

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
