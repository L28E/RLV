package ca.nick.rlv;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraFile;

@SuppressWarnings("serial")
public class StreamMJPG extends HttpServlet {	
	
	private GPhoto2 camera;	
	
	private static final byte[] PREFIX = ("--boundary\r\nContent-type: image/jpg\r\nContent-Length: ").getBytes();
	private static final byte[] SEPARATOR = "\r\n\r\n".getBytes();
	private static final long FRAME_INTERVAL = 33;
	
	public StreamMJPG() {
		this.camera = app.camera;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean loop = true;
		byte[] frame;
		long prevFrame = 0;
		CameraFile fileRef = camera.createCameraFile();			
		final OutputStream out = resp.getOutputStream();			

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