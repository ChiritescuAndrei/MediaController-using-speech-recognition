package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VideoPageController implements Initializable {
	
	@FXML
	private VBox vboxMedia;

	@FXML
	private Label labelDrop;

	@FXML
	private VBox showControls;
	@FXML
	private HBox hboxFullScreen;

	@FXML
	private Label videoINFO;
	
	@FXML
	private BorderPane borderParent;
	
	@FXML
	private MediaView mvVideo;
	private static MediaPlayer mpVideo;
	private Media mediaVideo;
	
	@FXML
	private Slider sliderTime;
	
	@FXML
	private HBox hboxControls;
	
	@FXML
	private Button buttonPPR;
	
	@FXML
	private HBox hboxVolume;
	
	@FXML
	private Slider sliderVolume;
	
	@FXML
	private Label labelVolume;
	
	@FXML
	private Label labelCurrentTime;
	
	@FXML
	private Label labelTotalTime;
	
	@FXML
	private Label labelFullScreen;
	
	@FXML
	private Label labelSpeed;
	
	@FXML
	private ScrollPane scrollPaneList;
	
	@FXML
	private GridPane gridPaneList;
	
	@FXML
	private HBox hboxList;
	
//	@FXML
//	private Slider sliderBrightness;
	
	private boolean atEndOfVideo = false;
	private boolean isPlaying = false;
	private boolean isMuted = true;
	
	private ImageView ivPlay;
	private ImageView ivPause;
	private ImageView ivRestart;
	private ImageView ivVolume;
	private ImageView ivFullScreen;
	private ImageView ivMute;
	private ImageView ivExit;
	private HBox lastRemovedVideoBox = null;
	private int lastRemovedVideoBoxIndex = -1;
	
	
	final File thumbnailFolder = new File("resources/thumbnails");
	final File videoFolder = new File("resources/videos");
	final int IV_SIZE = 25;
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		createMediaControls();
		playVideo("resources/videos/Melodie Greceasca.mp4");	
		gridPaneAdd();
		
	}
	
	private void playVideo(String path) {
		
		mediaVideo = new Media(new File(path).toURI().toString());
		mpVideo = new MediaPlayer(mediaVideo);
		mvVideo.setMediaPlayer(mpVideo);
		mpVideo.play();
		isPlaying = true;
		sliderVolume.setValue(0.4);
		sliderTime.setValue(0);
		
		ColorAdjust colorAdjust = new ColorAdjust();
		
//		De adaugat luminozitate
		
//		sliderBrightness.valueProperty().addListener((observable, oldValue, newValue) -> {
//		    colorAdjust.setBrightness(newValue.doubleValue());
//		    System.out.println(newValue.doubleValue());
//		});
//		mvVideo.setEffect(colorAdjust);
		
		buttonPPR.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				bindCurrentTimeLabel();
				Button buttonPlay = (Button) actionEvent.getSource();
				bindCurrentTimeLabel();
				
				if(atEndOfVideo) {
					sliderTime.setValue(0);
					atEndOfVideo = false;
					isPlaying = false;
				}				
				if(isPlaying) {
					buttonPlay.setGraphic(ivPlay);
					mpVideo.pause();
					isPlaying = false;
				} else {
					buttonPlay.setGraphic(ivPause);
					mpVideo.play();
					isPlaying = true;
				}
			}
		});
		
		hboxVolume.getChildren().remove(sliderVolume);
		hboxList.getChildren().remove(scrollPaneList);
		mpVideo.volumeProperty().bindBidirectional((sliderVolume.valueProperty()));   //binduiesc volumul cu sliderVolume
		
		bindCurrentTimeLabel();
		
		sliderVolume.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				mpVideo.setVolume(sliderVolume.getValue());
				if(mpVideo.getVolume() != 0.0) {
					labelVolume.setGraphic(ivVolume);
					isMuted = false;
				}else {
					labelVolume.setGraphic(ivMute);
					isMuted = true;
				}
				
			}
		});
		
		labelSpeed.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(labelSpeed.getText().equals("1X")) {
					labelSpeed.setText("2X");
					mpVideo.setRate(2.0);
				}else {
					labelSpeed.setText("1X");
					mpVideo.setRate(1.0);
				}
			}
		});
		
		labelVolume.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(isMuted) {
					labelVolume.setGraphic(ivVolume);
					sliderVolume.setValue(0.2);
					isMuted = false;
				}else {
					labelVolume.setGraphic(ivMute);
					sliderVolume.setValue(0);
					isMuted = true;
				}
			}
		});
						
		labelVolume.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(hboxVolume.lookup("#sliderVolume") == null) {
					hboxVolume.getChildren().add(sliderVolume);
					sliderVolume.setValue(mpVideo.getVolume());
				}
			}
		});
		
		hboxVolume.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				hboxVolume.getChildren().remove(sliderVolume);
			}
		});
		
		hboxList.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(hboxList.lookup("#scrollPaneList") == null) {
					hboxList.getChildren().add(scrollPaneList);
				}
			}
		});
		
		hboxList.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				hboxList.getChildren().remove(scrollPaneList);
			}
		});
		
		borderParent.sceneProperty().addListener(new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue< ? extends Scene > observableValue, Scene oldScene, Scene newScene) {
				 if (oldScene == null && newScene !=null) {
					 mvVideo.fitHeightProperty().bind(newScene.heightProperty().
							 	subtract(hboxControls.heightProperty().add(30)));	
					 
					
					mvVideo.fitWidthProperty().bind(newScene.widthProperty().
								subtract(hboxList.widthProperty()));
								
				 }
			}
		});
			
		labelFullScreen.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Label label = (Label) mouseEvent.getSource();
				Stage stage = (Stage) label.getScene().getWindow();
				if(stage.isFullScreen()) {
					stage.setFullScreen(false);
					labelFullScreen.setGraphic(ivFullScreen);
					hboxList.setVisible(true);
					hboxList.setManaged(true);
				}else {
					stage.setFullScreen(true);
					labelFullScreen.setGraphic(ivExit);
					hboxList.setVisible(false);
					hboxList.setManaged(false);
				}
				
				stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent keyEvent) {
						if(keyEvent.getCode() == KeyCode.ESCAPE) {
							labelFullScreen.setGraphic(ivFullScreen);
							hboxList.setVisible(true);
							hboxList.setManaged(true);
						}
					}
				});
			}
		});
	
		mpVideo.totalDurationProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
				bindCurrentTimeLabel();
				sliderTime.setMax(newDuration.toSeconds());
				labelTotalTime.setText(getTime(newDuration));
			}
		});
		
		sliderTime.valueChangingProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isChanging) {
				bindCurrentTimeLabel();
				if(!isChanging) {
					mpVideo.seek(Duration.seconds(sliderTime.getValue()));
				}
			}
		});
		
		sliderTime.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				bindCurrentTimeLabel();
				double currentTime = mpVideo.getCurrentTime().toSeconds();
				if(Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
					mpVideo.seek(Duration.seconds(newValue.doubleValue()));
				}
				labelMatchEndVideo(labelCurrentTime.getText(), labelTotalTime.getText());
			}	
		});

		mpVideo.currentTimeProperty().addListener(new ChangeListener<Duration>(){
			@Override
			public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
				bindCurrentTimeLabel();
				if(!sliderTime.isValueChanging()) {
					sliderTime.setValue(newTime.toSeconds());
				}
				labelMatchEndVideo(labelCurrentTime.getText(), labelTotalTime.getText());
			}
				
		});
		
		
		mpVideo.setOnEndOfMedia(new Runnable() {

			@Override
			public void run() {
				buttonPPR.setGraphic(ivRestart);
				atEndOfVideo = true;
				if(!labelCurrentTime.textProperty().equals(labelTotalTime.textProperty())) {
					labelCurrentTime.textProperty().unbind();
					labelCurrentTime.setText(getTime(mpVideo.getTotalDuration()) + " / ");
				}
				
			}
			
		});
		
		
}
			
	private void createMediaControls() {
		Image imagePlay = new Image(new File("resources/icons/play-button.png").toURI().toString());
		ivPlay = new ImageView(imagePlay);
		ivPlay.setFitHeight(IV_SIZE);
		ivPlay.setFitWidth(IV_SIZE);
		
		Image imageStop = new Image(new File("resources/icons/pause-button.png").toURI().toString());
		ivPause = new ImageView(imageStop);
		ivPause.setFitHeight(IV_SIZE);
		ivPause.setFitWidth(IV_SIZE);
		
		Image imageRestart = new Image(new File("resources/icons/refresh.png").toURI().toString());
		ivRestart = new ImageView(imageRestart);
		ivRestart.setFitHeight(IV_SIZE);
		ivRestart.setFitWidth(IV_SIZE);
		
		Image imageVol = new Image(new File("resources/icons/volume.png").toURI().toString());
		ivVolume = new ImageView(imageVol);
		ivVolume.setFitHeight(IV_SIZE);
		ivVolume.setFitWidth(IV_SIZE);
		
		Image imageMute = new Image(new File("resources/icons/volume-mute.png").toURI().toString());
		ivMute = new ImageView(imageMute);
		ivMute.setFitHeight(IV_SIZE);
		ivMute.setFitWidth(IV_SIZE);
		
		Image imageFull = new Image(new File("resources/icons/fullscreen.png").toURI().toString());
		ivFullScreen = new ImageView(imageFull);
		ivFullScreen.setFitHeight(IV_SIZE);
		ivFullScreen.setFitWidth(IV_SIZE);
		
		Image imageExit = new Image(new File("resources/icons/exit-fullscreen.png").toURI().toString());
		ivExit = new ImageView(imageExit);
		ivExit.setFitHeight(IV_SIZE);
		ivExit.setFitWidth(IV_SIZE);
		
		buttonPPR.setGraphic(ivPause);
		labelVolume.setGraphic(ivVolume);
		labelSpeed.setText("1X");
		labelFullScreen.setGraphic(ivFullScreen);
	}
	
	public void bindCurrentTimeLabel() {
			
			labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>(){
				@Override
				public String call() throws Exception{
					return getTime(mpVideo.getCurrentTime()) + " / ";
				}
			}, mpVideo.currentTimeProperty()));	
			
		}
	
	public String getTime(Duration time) {
		
		int hours = (int) time.toHours();
		int minutes = (int) time.toMinutes();
		int seconds = (int) time.toSeconds();
		
		if(seconds > 59) seconds = seconds % 60;
		if(minutes > 59) minutes = minutes % 60;
		if(hours > 59) hours = hours % 60;
		
		if(hours > 0) return String.format("%d:%02d:%02d",hours,minutes,seconds);
		else
			return String.format("%02d:%02d", minutes,seconds);
	}
	
	
	public void labelMatchEndVideo(String labelTime, String labelTotalTime) {
		for(int i = 0; i < labelTotalTime.length(); i++) {
			if(labelTime.charAt(i) != labelTotalTime.charAt(i)) {
			atEndOfVideo = false;
			if(isPlaying) buttonPPR.setGraphic(ivPause);
			else buttonPPR.setGraphic(ivPlay);
			break;
			} else {
				atEndOfVideo = true;
				buttonPPR.setGraphic(ivRestart);
			}
		}
	}
	
	private void gridPaneAdd() {
	    try {
	        Files.list(thumbnailFolder.toPath())
	             .filter(path -> path.toString().endsWith(".jpg"))
	             .forEach(thumbnailPath -> {
	                 String title = thumbnailPath.getFileName().toString().replaceFirst("[.][^.]+$", ""); 
	                 Path matchingVideoPath = videoFolder.toPath().resolve(title + ".mp4");
	                 
	                 if (Files.exists(matchingVideoPath)) {
	                     try {
	                         String duration = getVideoDuration(matchingVideoPath.toString()); 
	                         FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/videoDisplay.fxml"));
	                         HBox videoBox = loader.load();
	                         VideoController controller = loader.getController();
	                         controller.setData(thumbnailPath, title, duration);
	                         
	                         int row = gridPaneList.getChildren().size();
	                         
	                         gridPaneList.add(videoBox, 0, row);
	                         GridPane.setMargin(videoBox, new Insets(10, 10, 10, 10));
	                         
	                         videoBox.setOnMouseClicked(e -> {
	                             MediaPlayer currentMediaPlayer = mvVideo.getMediaPlayer();
	                             currentMediaPlayer.dispose();
	                             playVideo(matchingVideoPath.toString());
	                             createMediaControls();
	                             
	                             // Remove the clicked video box
	                             gridPaneList.getChildren().remove(videoBox);
	                             
	                             // Restore the last removed video box, if any
	                             if (lastRemovedVideoBox != null ) {
	                                 gridPaneList.add(lastRemovedVideoBox, 0, lastRemovedVideoBoxIndex);
	                                 GridPane.setMargin(lastRemovedVideoBox, new Insets(10, 10, 10, 10));
	                             }
	                             // Store the current video box as the last removed one
	                             lastRemovedVideoBox = videoBox;
	                             lastRemovedVideoBoxIndex = row;
	                         });

	                     } catch (IOException e) {
	                         e.printStackTrace();
	                     }
	                 }
	                
	             });
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
		
	private String getVideoDuration(String videoPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder("ffmpeg", "-i", videoPath);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            String duration = " ";
            // Updated regex pattern to capture only minutes and seconds
            Pattern durPattern = Pattern.compile("Duration: \\d{2}:(\\d{2}):(\\d{2})\\.\\d{2},");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = durPattern.matcher(line);
                if (m.find()) {
                    // Combining minutes and seconds
                    duration = m.group(1) + ":" + m.group(2);
                    break;
                }
            }
            process.waitFor();
            return duration;
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to get video duration for " + videoPath + ": " + e.getMessage());
            return "Error";
        }
    }

		
			 
}






























