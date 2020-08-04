package ca.nick.rvf;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.gphoto2.GPhoto2Config;

@SuppressWarnings("serial")
public class GetSettingsServlet extends HttpServlet {

	private final GPhoto2 camera;
	private final GPhoto2Config config;

	public GetSettingsServlet() {
		this.camera = Main.camera;
		this.config = Main.config;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			// Get a list of values for each parameter
			List<String> isoList = camera.getChoiceList(config.getParameterWidget("iso"));
			List<String> fstopList = camera.getChoiceList(config.getParameterWidget("aperture"));
			List<String> shutterList = camera.getChoiceList(config.getParameterWidget("shutterspeed"));
			List<String> modeList = camera.getChoiceList(config.getParameterWidget("drivemode"));

			// Get current settings
			String startIso = config.getParameterValue(config.getParameterWidget("iso"));
			String startFstop = config.getParameterValue(config.getParameterWidget("aperture"));
			String startShutter = config.getParameterValue(config.getParameterWidget("shutterspeed"));
			String startMode = config.getParameterValue(config.getParameterWidget("drivemode"));

			// Build and send a JSON
			JSONObject object = new JSONObject();
			JSONObject settingsList = new JSONObject();
			JSONObject currentSettings = new JSONObject();

			settingsList.put("iso", new JSONArray(isoList)).put("fstop", new JSONArray(fstopList))
					.put("shutter", new JSONArray(shutterList)).put("mode", new JSONArray(modeList));

			currentSettings.put("iso", startIso).put("fstop", startFstop).put("shutter", startShutter).put("mode",
					startMode);
			object.put("settingsList", settingsList).put("currentSettings", currentSettings);

			resp.getWriter().print(object.toString());
		} catch (JSONException | IOException ex) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			Logger.getLogger(GetSettingsServlet.class.getName()).log(Level.SEVERE, ex.getMessage());
			return;
		}

		resp.setContentType("application/json");
		resp.setStatus(HttpServletResponse.SC_OK);
	}
}