package de.hsrm.objectify.camera;

import java.io.IOException;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Shows preview of camera and holds different lightning setups for taking
 * several photos
 * 
 * @author kwolf001
 * 
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "CameraPreview";
	private SurfaceHolder holder;
	private static Camera camera;
	private Size previewSize;
	private Size pictureSize;
	private int imageFormat;
	
	public CameraPreview(Context context) {
		super(context);
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	
	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}


	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}


	public void surfaceCreated(SurfaceHolder holder) {
		camera = openFrontFacingCamera();
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			camera.release();
			camera = null;
		}
	}
	
	public static Camera getCamera() {
		return camera;
	}
	
	public Size getPreviewSize() {
		return previewSize;
	}
	
	public Size getPictureSize() {
		return pictureSize;
	}
	
	public int getImageFormat() {
		return imageFormat;
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		camera.startPreview();
	}
	
	public void takePicture(ShutterCallback shutter, PictureCallback raw, PictureCallback jpeg) {
		camera.takePicture(shutter, raw, jpeg);
	}
	
	public void startPreview() {
		camera.startPreview();
	}
	
	/**
	 * Convenience method for opening front facing cameras on devices < 2.3
	 * 
	 * @return front facing camera
	 */
	public Camera openFrontFacingCamera() {
		Camera camera = null;

		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentApiVersion > android.os.Build.VERSION_CODES.FROYO) {
			// gingerbread (2.3) and above
			int cameraCount = 0;
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			cameraCount = Camera.getNumberOfCameras();
			for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					try {
						camera = Camera.open(camIdx);
					} catch (RuntimeException e) {
						Log.e(TAG, "Failed to open camera:" + e.getMessage());
					}
				}
			}
			// TODO remove hack for devices without front-facing cam
			if (camera == null) {
				camera = Camera.open();
				previewSize = new Size(camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
			}
		} else {
			// froyo (2.2)
			camera = Camera.open();		
			Camera.Parameters params = camera.getParameters();
			if (android.os.Build.PRODUCT.equals("GT-P1000")) {
				// we're running on samsung galaxy tab
				// only working size and picture format for samsung galaxy tab
				previewSize = new Size(800,600);
				pictureSize = new Size(800, 600);
				imageFormat = params.getSupportedPictureFormats().get(0);
				params.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
				params.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
				params.setPictureFormat(imageFormat);
				params.set("camera-id", 2); // using front-cam (2) instead
											// of back-cam (1)
			} else {
				Camera.Size s = params.getSupportedPreviewSizes().get(0);
				Camera.Size p = params.getSupportedPictureSizes().get(params.getSupportedPictureSizes().size()-1);
				for (Integer i : params.getSupportedPictureFormats()) {
					Log.d("format", i.toString());
				}
				imageFormat = params.getSupportedPictureFormats().get(0);
				previewSize = new Size(s.width, s.height);
				pictureSize = new Size(p.width, p.height);
				params.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
				params.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
				params.setPictureFormat(imageFormat);
			}
			camera.setParameters(params);
		}
		return camera;
	}

}
