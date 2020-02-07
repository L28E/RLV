package ca.nick.rlv;

import x.mvmn.gphoto2.jna.Camera;
import x.mvmn.gphoto2.jna.Camera.ByReference;
import x.mvmn.gphoto2.jna.Gphoto2Library;

public class app {

	public static void main(String[] args) {
		final Camera camera = newCamera();

	}
	
	public static ByReference newCamera() {
		Camera.ByReference[] p2CamByRef = new Camera.ByReference[] { new Camera.ByReference() };
		check(Gphoto2Library.INSTANCE.gp_camera_new(p2CamByRef));
		return p2CamByRef[0];
	}
	
	public static int check(int retVal) {
		if (retVal < 0) {
			System.err.println("Error " + retVal);
			new Exception().printStackTrace();
		}

		return retVal;
	}

}
