package bazmar.lastwar.autofl.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import bazmar.lastwar.autofl.data.Stats;
import bazmar.lastwar.autofl.image.Image;
import bazmar.lastwar.autofl.io.Frame;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class Utils {

	private static final String STATS_FL_TEXT = FileManager.readFile("stats.html");

	public static void pause(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String readableDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd-HHmmss");
		return sdf.format(new Date());
	}

	public static String readableDateMs() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd-HHmmssSSS");
		return sdf.format(new Date());
	}

	public static Object humanReadableDate() {
		return humanReadableDate(new Date());
	}

	public static Object humanReadableDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
		return sdf.format(date);
	}

	public static Image bufferedImageToImage(BufferedImage bufferedImage) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", os);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		return new Image(is);
	}

	public static BufferedImage convertToBufferedImage(Image fxImage) {
		int width = (int) fxImage.getWidth();
		int height = (int) fxImage.getHeight();

		// Create a BufferedImage
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Get the PixelReader from the JavaFX Image
		PixelReader pixelReader = fxImage.getPixelReader();

		// Iterate through the pixels of the JavaFX Image and set them in the
		// BufferedImage
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Read color from JavaFX image
				Color color = pixelReader.getColor(x, y);

				// Convert Color to ARGB format and set in BufferedImage
				int argb = (int) (color.getOpacity() * 255) << 24 | // Alpha
						(int) (color.getRed() * 255) << 16 | // Red
						(int) (color.getGreen() * 255) << 8 | // Green
						(int) (color.getBlue() * 255); // Blue

				bufferedImage.setRGB(x, y, argb);
			}
		}

		return bufferedImage;
	}

	public static Date getNextMinute(int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes);

		return cal.getTime();
	}

	public static Date getPastMinute(int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -minutes);

		return cal.getTime();
	}

	public static void updateLogs() {
		try {
			File logs = new File("./logs/lastwar.log");
			Frame.updateLogs(LastLinesReader.readLastLines(logs, 25));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String generateHtmlStats(Stats stats) {

		return STATS_FL_TEXT.formatted(stats.isFlInitialized(), stats.getMoyenneFl(), stats.getCountFL(),
				stats.getCountRecovery(), stats.getLastFlAction(), stats.getStartDateStats(), stats.getCountFLAdd(),
				stats.getCountFLTitle(), stats.getCountStrat(), stats.getCountSecu(), stats.getCountDev(),
				stats.getCountScience(), stats.getCountInter(), stats.getCountMilitary(), stats.getCountAdmin(),
				stats.getCountFLKick(), stats.getCountKickStrat(), stats.getCountKickSecu(), stats.getCountKickDev(),
				stats.getCountKickScience(), stats.getCountKickInter(), stats.getCountKickMilitary(),
				stats.getCountKickAdmin(), stats.getNextStratKickCheck(), stats.getNextSecuKickCheck(),
				stats.getNextDevKickCheck(), stats.getNextScienceKickCheck(), stats.getNextInterKickCheck(),
				stats.getNextMilitaryKickCheck(), stats.getNextAdminKickCheck());
	}

}
