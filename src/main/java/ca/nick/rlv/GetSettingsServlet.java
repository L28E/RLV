package ca.nick.rlv;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.gphoto2.GPhoto2Config;

@SuppressWarnings("serial")
public class GetSettingsServlet extends HttpServlet {
	
	private GPhoto2 camera;
	private GPhoto2Config config;	
	
	static List<String> isoList;
	static List<String> fstopList;
	static List<String> shutterList;
	static List<String> modeList;
	
	public GetSettingsServlet() {
		this.camera = app.camera;
		this.config = app.config;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// Get a list of values for each parameter
		isoList = camera.getChoiceList(config.getParameterWidget("iso"));
		fstopList = camera.getChoiceList(config.getParameterWidget("aperture"));
		shutterList = camera.getChoiceList(config.getParameterWidget("shutterspeed"));
		modeList = camera.getChoiceList(config.getParameterWidget("drivemode"));
		
		// Build and send a JSON
		JSONObject object = new JSONObject();
		object.put("iso", new JSONArray(isoList))
			  .put("fstop", new JSONArray(fstopList))
			  .put("shutter", new JSONArray(shutterList))
			  .put("mode", new JSONArray(modeList));			

		resp.getWriter().print(object.toString());
		resp.setContentType("application/json");
		resp.setStatus(HttpServletResponse.SC_OK);
	}
}