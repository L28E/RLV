package ca.nick.rlv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.angryelectron.gphoto2.GPhoto2;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraFile;

@SuppressWarnings("serial")
public class StreamMJPG extends HttpServlet {

	private final GPhoto2 camera;

	private static final byte[] PREFIX = ("--boundary\r\nContent-type: image/jpg\r\nContent-Length: ").getBytes();
	private static final byte[] SEPARATOR = "\r\n\r\n".getBytes();
	private static final long FRAME_INTERVAL = 33;

	public StreamMJPG() {
		this.camera = Main.camera;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean loop = true;
		byte[] frame;
		long prevFrame = 0;
		CameraFile fileRef;
		final OutputStream out;

		try {
			fileRef = camera.createCameraFile();
			out = resp.getOutputStream();
		} catch (IOException ex) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			Logger.getLogger(StreamMJPG.class.getName()).log(Level.SEVERE, ex.getMessage());
			return;
		}

		/*
		 * Multipart response allows frames to be replaced in subsequent messages. Send
		 * messages with image data in a while loop.
		 */
		resp.setContentType("multipart/x-mixed-replace; boundary=--boundary");
		resp.setStatus(HttpServletResponse.SC_OK);
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
				} catch (IOException ex) {
					loop = false;
				}

				Thread.yield();
			}
		}
	}
}