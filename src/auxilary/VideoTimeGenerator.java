package auxilary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoTimeGenerator {

    public ArrayList<String> collectVideoDurations(String directoryPath) {
        ArrayList<String> durations = new ArrayList<>();
        Path videoDirectory = Paths.get(directoryPath);

        if (!Files.exists(videoDirectory) || !Files.isDirectory(videoDirectory)) {
            System.err.println("Directory does not exist or is not accessible: " + videoDirectory);
            return durations;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(videoDirectory, "*.mp4")) {
            for (Path file : stream) {
                String duration = getVideoDuration(file.toAbsolutePath().toString());
                durations.add(duration);
            }
        } catch (IOException e) {
            System.err.println("Error reading the video directory: " + e.getMessage());
        }

        return durations;
    }

    private static String getVideoDuration(String videoPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder("ffmpeg", "-i", videoPath);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            String duration = "Unknown";
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

    public static void main(String[] args) {
        VideoTimeGenerator collector = new VideoTimeGenerator();
        ArrayList<String> videoDurations = collector.collectVideoDurations("resources/videos");
        videoDurations.forEach(System.out::println);  // Print all video durations
    }
}
