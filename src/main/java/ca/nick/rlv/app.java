package ca.nick.rlv;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.angryelectron.gphoto2.GPhoto2;

public class app {

	public static void main(String[] args) {
				
		// Connect to camera
		GPhoto2 camera = new GPhoto2();
		try {
			camera.open();
		} catch (IOException ex) {
			Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
			System.exit(-1);
		}
		
		// Diddle around with camera settings           
        try {
            String iso = camera.getConfig("iso"); // Where params are found by entering command "gphoto2 --list-config" in a terminal"
            System.out.println(iso);            
        } catch (IOException ex) {
            Logger.getLogger(app.class.getName()).log(Level.SEVERE, ex.getMessage());
            camera.close();
            System.exit(-1);
        }                                     
        
        // Cleanup
        camera.close();

	}

}
