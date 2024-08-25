package controllers;

import java.nio.file.Path;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class VideoController {
	
	@FXML
    private Text titlu;
	
	@FXML
	private Text duration;

    @FXML
    private ImageView thumb;
    

    public void setData(Path thumbnailsPath, String title, String videoDuration) {
      
       thumb.setImage(new Image(thumbnailsPath.toUri().toString()));
       titlu.setText(title);
       duration.setText(videoDuration);
       

    }

    
}

