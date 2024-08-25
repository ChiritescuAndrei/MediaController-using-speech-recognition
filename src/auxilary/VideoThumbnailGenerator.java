package auxilary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class VideoThumbnailGenerator {

    public static Path generateThumbnailForVideo(String videoPath, Path thumbnailDirectory) throws IOException {
        Path videoFilePath = Paths.get(videoPath);
        String baseName = videoFilePath.getFileName().toString().replaceFirst("[.][^.]+$", "");
        Path outputPath = thumbnailDirectory.resolve(baseName + ".jpg");

        if (!Files.exists(thumbnailDirectory)) {
            Files.createDirectories(thumbnailDirectory);
        }

        List<String> command = Arrays.asList(
            "ffmpeg", "-i", videoFilePath.toString(),
            "-ss", "00:00:07", "-frames:v", "1",
            "-vf", "scale=320:-1", outputPath.toString()
        );

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Failed to generate thumbnail, ffmpeg exit code " + exitCode);
            }
            System.out.println("Thumbnail generated successfully: " + outputPath);
            return outputPath; // Return the path of the generated thumbnail
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thumbnail generation interrupted", e);
        }
    }
}
