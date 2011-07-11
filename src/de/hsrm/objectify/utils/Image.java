package de.hsrm.objectify.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;

/**
 * This class behaves mostly as a {@link Bitmap}, but extends its functionality
 * by some convenience methods.
 * 
 * @author kwolf001
 * 
 */
public class Image {

	private Bitmap bitmap;
	
	public Image(int width, int height, Config config) {
		bitmap = Bitmap.createBitmap(width, height, config);
	}
	
	public Image(Bitmap bitmap) {
		this.bitmap = Bitmap.createBitmap(bitmap);
	}
	
	public void setPixel(int x, int y, int color) {
		bitmap.setPixel(x, y, color);
	}
	
	public int[] getPixels() {
		int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, getWidth(), getHeight());
		return pixels;
	}

	public int getWidth() {
		return bitmap.getWidth();
	}

	public int getHeight() {
		return bitmap.getHeight();
	}

	public int getPixel(int x, int y) {
		return bitmap.getPixel(x, y);
	}

	public Config getConfig() {
		return bitmap.getConfig();
	}

	public void compress(CompressFormat format, int quality, BufferedOutputStream bos) {
		bitmap.compress(format, quality, bos);
	}
	
	public void compress(CompressFormat format, int quality, ByteArrayOutputStream baos) {
		bitmap.compress(format, quality, baos);
	}
	
	public float[][] getIntensity() {
		float[][] map = new float[getWidth()][getHeight()];
		int[] pixels = getPixels();
		int idx = 0;
		for (int x=0; x<getWidth(); x++) {
			for (int y=0; y<getHeight(); y++) {
				float intensity = getGreyscale(pixels[idx]);
				map[x][y] = intensity;
				idx += 1;
			}
		}
		return map;
	}
	
	public float getIntensity(int x, int y) {
		int pixel = bitmap.getPixel(x, y);
		return getGreyscale(pixel);
	}
	
	// TODO: Debugging. Wieder rausnehmen und Uli zeigen wg. KuriositŠt
	public float[][] getIntensity2() {
		float[][] map = new float[getWidth()][getHeight()];
		for (int x=0; x<getWidth(); x++) {
			for (int y=0; y<getHeight(); y++) {
				float intensity = getGreyscale(bitmap.getPixel(x, y));
				map[x][y] = intensity;
			}
		}
		return map;
	}
	
	private float getGreyscale(int pixelColor) {
		int red = (pixelColor >> 16) & 0xFF;
		int green = (pixelColor >> 8) & 0xFF;
		int blue = (pixelColor >> 0) & 0xFF;
		if (red==0 || green==0 || blue==0) {
			return 0;
		} else {
			return ((red + green + blue) / 3.0f) / 255.0f;
		}
	}

}
