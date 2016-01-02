package com.simplyconnectedsystems.micr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.simplyconnectedsystems.utility.Constants;
import com.simplyconnectedsystems.utility.UtilsClass;
import com.sk.simplyconnectedsystems.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import leadtools.L_ERROR;
import leadtools.LeadRect;
import leadtools.LeadSize;
import leadtools.RasterImage;
import leadtools.codecs.RasterCodecs;
import leadtools.controls.ImageViewerNewImageResetOptions;
import leadtools.controls.ImageViewerSizeMode;
import leadtools.controls.RasterImageViewer;
import leadtools.converters.ConvertFromImageOptions;
import leadtools.converters.ConvertToImageOptions;
import leadtools.converters.RasterImageConverter;
import leadtools.demos.DeviceUtils;
import leadtools.demos.Messager;
import leadtools.demos.Progress;
import leadtools.demos.Support;
import leadtools.demos.Utils;
import leadtools.forms.ocr.OcrAutoPreprocessPageCommand;
import leadtools.forms.ocr.OcrDocument;
import leadtools.forms.ocr.OcrEngine;
import leadtools.forms.ocr.OcrEngineManager;
import leadtools.forms.ocr.OcrEngineType;
import leadtools.forms.ocr.OcrMicrData;
import leadtools.forms.ocr.OcrPage;
import leadtools.forms.ocr.OcrPageCharacters;
import leadtools.forms.ocr.OcrPageType;
import leadtools.forms.ocr.OcrSettingManager;
import leadtools.forms.ocr.OcrWord;
import leadtools.forms.ocr.OcrZone;
import leadtools.forms.ocr.OcrZoneCharacters;
import leadtools.forms.ocr.OcrZoneFillMethod;
import leadtools.forms.ocr.OcrZoneType;
import leadtools.imageprocessing.core.MICRCodeDetectionCommand;
import leadtools.imageprocessing.core.MinimumCommand;

public class MicrDemoActivity extends Activity implements SurfaceHolder.Callback {
   private static final long IMAGE_PROCESS_DELAY = 1; //10;

   private static final long AUTO_FOCUS_DELAY = 3000;

   private static final String OCR_TEMP_DIRECTORY = Environment.getExternalStorageDirectory() + "/LEADTOOLS Demos/MICR_DEMO/";
   private static final String OCR_RUNTIME_DIRECTORY = OCR_TEMP_DIRECTORY + "OCRRuntime/";
   private static final String CAPTURED_IMAGE_TEMP_DIRECTORY = Environment.getExternalStorageDirectory() + "/LEADTOOLS Demos/MICR_DEMO/";
   private static final int MicrExtractionMode_Strict = 0;
   private static final int MicrExtractionMode_Relaxed = 1;

   private boolean mStartLiveCapture;

   private Rect mLiveCaptureRect;
   private Paint mLiveCapturePaint;
   private Paint mLiveCapturePaintMicr;
   private Paint mTextPaint;
   private Paint mRectPaint;
   private Camera mCamera; // for live capture

   @Bind(R.id.imageviewer) RasterImageViewer mImageViewer;
   private View mOverlayView;
   @Bind(R.id.surfaceview) SurfaceView mSurfaceView;
   private SurfaceHolder mSurfaceHolder;
   
   private ProgressDialog mProgressDlg;
   private boolean mShowProgress;

   private float mHorizontalMargins;
   private float mVerticalMargins;
   
   private float mInstructionLabelWidth;
   private float mInstructionLabelHeight;
   private float mInstructionLabelLeft;
   private float mInstructionLabelTop;
   
   private float mCameraGuideLeft;
   private float mCameraGuideTop;
   private float mCameraGuideWidth;
   private float mCameraGuideHeight;
   private float mVerticalStrokeWidth;
   private float mMicrClearBandHeight;
   private Rect mMicrAreaBounds;
   private Rect mOcrAreaBounds;
   private RectF mMicrNoteBounds;
   private static final String mMicrNoteLine1 = "Make sure the MICR code fits within the MICR Area guides and OCR fits in it's Area guides";
   private static final String mMicrNoteLine2 = "Use Toggle Button to turn on/off OCR. Also ensure there is plenty of light on the cheque.";
   private static final String mMicrAreaNote = "MICR Area";
   private static final String mOCRAreaNote = "OCR Area";
   private int mMicrNoteTop;
   private int mMicrNoteLeftLine1;
   private int mMicrNoteLeftLine2;
   private int mMicrAreaNoteLeft;
   private int mOcrAreaNoteLeft;
   private boolean mDrawMicrGuides;
   private OcrEngine mOcrEngine;
   private int mMicrVideoExtractionMode;
   private int mMicrPictureExtractionMode;
   private LeadRect mMicrReadAreaBounds;
   private LeadRect mOCRReadAreaBounds;
   private TextView routingTxtView;
   private TextView accountTxtView;
   private TextView checkNumberTxtView;
   private boolean detectOCR = false;
   @Bind (R.id.toggleButton)ToggleButton toggleButton;
   private EditText resultsEditText;


   @SuppressWarnings("deprecation")
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.main_micr);
      ButterKnife.bind(this);

      // Set License
      Support.setLicense(this);
      
      if(Support.isKernelExpired()) {
         Messager.showKernelExpiredMessage(this, new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
               finish();
            }
         });
         return;
      }

      // Init overlay rectangle
      mLiveCaptureRect = new Rect();
      mMicrAreaBounds = new Rect();
      mMicrNoteBounds = new RectF();
      mLiveCapturePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      mLiveCapturePaint.setColor(Color.RED);
      mLiveCapturePaint.setStrokeWidth(3);
      mLiveCapturePaint.setStyle(Style.STROKE);

      mLiveCapturePaintMicr = new Paint(Paint.ANTI_ALIAS_FLAG);
      mLiveCapturePaintMicr.setColor(Color.RED);
      mLiveCapturePaintMicr.setStrokeWidth(3);
      mLiveCapturePaintMicr.setStyle(Style.STROKE);
      mLiveCapturePaintMicr.setPathEffect(new DashPathEffect(new float[]{15, 20}, 0));
      
      mTextPaint = new Paint(); 
      mTextPaint.setColor(Color.WHITE); 
      mTextPaint.setTextSize(20);
      
      mRectPaint = new Paint(); 
      mRectPaint.setColor(Color.DKGRAY);
      mRectPaint.setAlpha(128);

      toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

         @Override
         public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

            if(isChecked)
               detectOCR = true;
            else
               detectOCR = false;
         }
      });
      RelativeLayout viewsContainer = (RelativeLayout) findViewById(R.id.relativelayout_viewerscontainer);
      
      // Set conversion options
      mImageViewer.setConvertToImageOptions(ConvertToImageOptions.LINK_IMAGE.getValue());
      
      mImageViewer.setNewImageResetOptions(ImageViewerNewImageResetOptions.NONE.getValue());
      mImageViewer.setSizeMode(ImageViewerSizeMode.FIT);
      // Set Dpi
      mImageViewer.setUseDpi(true);
      DisplayMetrics metrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(metrics);
      mImageViewer.setScreenDpiX(metrics.densityDpi);
      mImageViewer.setScreenDpiY(metrics.densityDpi);
      mDrawMicrGuides = false;
      
      // Init Overlay View
      mOverlayView = new View(this) {
        @Override
        public void onDraw(Canvas canvas) {
           super.onDraw(canvas);
          
           if (mDrawMicrGuides) {
              canvas.drawRoundRect(mMicrNoteBounds, 10, 10, mRectPaint);
              canvas.drawText(mMicrNoteLine1, mMicrNoteLeftLine1, mMicrNoteTop, mTextPaint);
              canvas.drawText(mMicrNoteLine2, mMicrNoteLeftLine2, mMicrNoteTop * 2, mTextPaint);
              canvas.drawText(mMicrAreaNote, mMicrAreaNoteLeft, (int)(mMicrAreaBounds.top + (mMicrAreaBounds.height() / 3.0) * 2.0), mTextPaint);
              
              canvas.drawLine(mLiveCaptureRect.left, mLiveCaptureRect.top, mLiveCaptureRect.right, mLiveCaptureRect.top, mLiveCapturePaint);
              canvas.drawLine(mLiveCaptureRect.left, mLiveCaptureRect.top, mLiveCaptureRect.left, mLiveCaptureRect.top + mVerticalStrokeWidth, mLiveCapturePaint);
              canvas.drawLine(mLiveCaptureRect.right, mLiveCaptureRect.top, mLiveCaptureRect.right, mLiveCaptureRect.top + mVerticalStrokeWidth, mLiveCapturePaint);
              canvas.drawLine(mLiveCaptureRect.left, mLiveCaptureRect.bottom, mLiveCaptureRect.left, mLiveCaptureRect.bottom - mVerticalStrokeWidth, mLiveCapturePaint);
              canvas.drawLine(mLiveCaptureRect.right, mLiveCaptureRect.bottom, mLiveCaptureRect.right, mLiveCaptureRect.bottom - mVerticalStrokeWidth, mLiveCapturePaint);
              canvas.drawLine(mLiveCaptureRect.left, mLiveCaptureRect.bottom, mLiveCaptureRect.right, mLiveCaptureRect.bottom, mLiveCapturePaint);
              
              canvas.drawLine(mMicrAreaBounds.left, mMicrAreaBounds.top, mMicrAreaBounds.right, mMicrAreaBounds.top, mLiveCapturePaintMicr);

              canvas.drawLine(mOcrAreaBounds.left, mOcrAreaBounds.top, mOcrAreaBounds.left, mOcrAreaBounds.bottom, mLiveCapturePaintMicr);
              canvas.drawLine(mOcrAreaBounds.left, mOcrAreaBounds.bottom, mOcrAreaBounds.right, mOcrAreaBounds.bottom, mLiveCapturePaintMicr);
              canvas.drawLine(mOcrAreaBounds.right, mOcrAreaBounds.bottom, mOcrAreaBounds.right, mOcrAreaBounds.top, mLiveCapturePaintMicr);

              canvas.drawText(mOCRAreaNote, mOcrAreaNoteLeft, (int)(mOcrAreaBounds.top + (mOcrAreaBounds.height() / 3.0) * 2.0), mTextPaint);

           }
        }
      };
      viewsContainer.addView(mOverlayView);
      
      mSurfaceHolder = mSurfaceView.getHolder();
      mSurfaceHolder.addCallback(this);
      // Set the holder type if the API is lower than 11
      if(Build.VERSION.SDK_INT < 11)
         mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

      if (UtilsClass.requestPermission(MicrDemoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, "Request for Writing External Storage", Constants.PermissionRequestCodes.WRITE_EXTERNAL_STORAGE)) {
         copyLanguageFiles();

         mMicrVideoExtractionMode = MicrExtractionMode_Strict;
         mMicrPictureExtractionMode = MicrExtractionMode_Relaxed;
         init();

         setMicrExtractMode();

      }

      if (UtilsClass.requestPermission(MicrDemoActivity.this, Manifest.permission.CAMERA, "Request for Camera", Constants.PermissionRequestCodes.SYSTEM_CAMERA)) {
         if(DeviceUtils.hasCamera(this))
            startLiveCapture();

      }

   }

   private boolean copyLanguageFiles() {
      try {
         Utils.createDirectory(OCR_RUNTIME_DIRECTORY);
         String assetsDirectoryName = "ocr_runtime";
         String[] languagesFilesNames = getAssets().list(assetsDirectoryName);
         if(languagesFilesNames.length == 0)
            return false;

         for(String languageFile: languagesFilesNames) {
            String name = String.format("%s%s", OCR_RUNTIME_DIRECTORY, languageFile);
            File file = new File(name);
            if(!file.exists()) {
               // copy the language file
               InputStream is = getAssets().open(assetsDirectoryName + "/" + languageFile);
               OutputStream os = new FileOutputStream(name);
               byte[] buffer = new byte[1024];
               int read;
               while ((read = is.read(buffer)) != -1) {
                  os.write(buffer, 0, read);
               }
               os.close();
               is.close();
            }
         }
         
         return true;
      } catch(Exception ex) {
         return false;
      }
   }

   private void init() {
      try {
         RasterCodecs codecsForOCR = new RasterCodecs(Utils.getSharedLibsPath(this));

         mOcrEngine = OcrEngineManager.createEngine(OcrEngineType.Advantage);
         mOcrEngine.startup(codecsForOCR, "", OCR_RUNTIME_DIRECTORY, Utils.getSharedLibsPath(this));
         OcrSettingManager settingsManager = mOcrEngine.getSettingManager();
         settingsManager.setBooleanValue("Recognition.Preprocess.MobileImagePreprocess", true);
         settingsManager.setBooleanValue("Recognition.ShareOriginalImage", true);
         settingsManager.setBooleanValue("Recognition.ModifyProcessingImage", true);
         settingsManager.setBooleanValue("Recognition.Preprocess.UseZoningEngine", false);
      } catch(Exception ex) {
         Messager.showError(this, ex.getMessage(), "Error");
      }
   }
   
   @Override
   protected void onResume() {
      super.onResume();
     
      // Restart live capture
      if(mStartLiveCapture) {
         startLiveCapture();
      }
   }

   @Override
   protected void onPause() {
      super.onPause();

      if(isFinishing() && mImageViewer != null)
         mImageViewer.setImage(null);
      
      // Stop live capture
      if(mStartLiveCapture) {
         stopLiveCapture(false);
         mStartLiveCapture = true;
      }
   }
   
   @Override
   public void onBackPressed() {
      super.onBackPressed();
      stopLiveCapture(false);
   }

   public void onSelectImage(View v) {
      int id = v.getId();

      if(id == R.id.btn_image_live_capture) {
         if(mCamera != null)
            stopLiveCapture(true);
         else {
            if(!DeviceUtils.hasCamera(this)) {
               Messager.showError(this, "The device doesn't have a camera", null);
               return;
            }

            startLiveCapture();
         }
      }

   }


   private void startLiveCapture() {
      // Clear Image
      setImage(null);

      // Start live capture
      mStartLiveCapture = true;

      mSurfaceView.setVisibility(View.VISIBLE);
      setMicrExtractMode();
   }
    
   private void stopLiveCapture(boolean showProgress) {
      if(mStartLiveCapture) {
         // Clear Image
         setImage(null);
         
         mShowProgress = showProgress;
         mOverlayView.setVisibility(View.INVISIBLE);
         mSurfaceView.setVisibility(View.GONE);

      }
      
//      setMicrExtractMode();
   }

   private void setImage(RasterImage image) {
      try {
         mImageViewer.setImage(image);
      } catch(Exception ex) {
         Messager.showError(this, ex.getMessage(), "");
      }
   }
   
   private void initCameraParameters(LeadSize size) {
      try {
         Parameters parameters = mCamera.getParameters();

         try {
            List<Integer> supportedPreviewFormatsList = parameters.getSupportedPreviewFormats();
            // Check for "JPEG" and "RGB_565" support
            if(supportedPreviewFormatsList != null && supportedPreviewFormatsList.size() < 0) {
               if(supportedPreviewFormatsList.contains(ImageFormat.JPEG))
                  parameters.setPreviewFormat(ImageFormat.JPEG);
               else if(supportedPreviewFormatsList.contains(ImageFormat.RGB_565))
                  parameters.setPreviewFormat(ImageFormat.RGB_565);
            }
         } catch(Exception ex) {
            Log.w("SetPreviewFormat", ex.getMessage());
         }

         try {
            List<Size> supportedPreviewSizesList = parameters.getSupportedPreviewSizes();
            Size previewSize = null;
            int sizeDiff = 0;
            if(supportedPreviewSizesList != null && supportedPreviewSizesList.size() > 0) {
               for(int i = 1; i < supportedPreviewSizesList.size(); i++) {
                  Size temp = supportedPreviewSizesList.get(i);
                  int diff = Math.abs(temp.width - size.getWidth()) + Math.abs(temp.height - size.getHeight());
                  if (diff < sizeDiff || previewSize == null) {
                     sizeDiff = diff;
                     previewSize = temp;
                  }
               }
            }
            
            if(previewSize != null) {
               parameters.setPreviewSize(previewSize.width, previewSize.height);

               // Update Rectangles
               LeadRect bounds = new LeadRect(0, 0, size.getWidth(), size.getHeight());
               
               mHorizontalMargins = bounds.getWidth() / 15;
               mVerticalMargins = bounds.getHeight() / 15;
               
               mInstructionLabelWidth = bounds.getWidth() - (mHorizontalMargins * 2);
               mInstructionLabelHeight = (bounds.getHeight() / 8);
               mInstructionLabelLeft = mHorizontalMargins;
               mInstructionLabelTop = mVerticalMargins / 2;
               
               mCameraGuideLeft = mInstructionLabelLeft;
               mCameraGuideTop = mInstructionLabelTop + mInstructionLabelHeight + (mVerticalMargins / 2);
               mCameraGuideWidth = mInstructionLabelWidth;
               mCameraGuideHeight = bounds.getHeight() - mCameraGuideTop - mVerticalMargins;
               
               mVerticalStrokeWidth = (float)(mCameraGuideHeight / 4.0);
               
               mMicrClearBandHeight = mCameraGuideHeight / 5;
               
               mLiveCaptureRect = new Rect((int)mCameraGuideLeft, (int)mCameraGuideTop, (int)(mCameraGuideWidth + mCameraGuideLeft), (int)(mCameraGuideHeight + mCameraGuideTop));
               
               mMicrNoteTop = (int)(mLiveCaptureRect.top / 3.0);
               mMicrNoteLeftLine1 = (int)(((mLiveCaptureRect.width()) / 2 + mHorizontalMargins)  - (mTextPaint.measureText(mMicrNoteLine1) / 2));
               mMicrNoteLeftLine2 = (int)(((mLiveCaptureRect.width()) / 2 + mHorizontalMargins)  - (mTextPaint.measureText(mMicrNoteLine2) / 2));
               mMicrAreaNoteLeft = (int)(((mLiveCaptureRect.width()) / 2 + mHorizontalMargins)  - (mTextPaint.measureText(mMicrAreaNote) / 2));

               mDrawMicrGuides = true;

               int micrTop = (int)(mCameraGuideHeight + mCameraGuideTop - mMicrClearBandHeight);
               mMicrAreaBounds = new Rect((int)mCameraGuideLeft, micrTop, mLiveCaptureRect.right, (int)(mMicrClearBandHeight + micrTop));
               mMicrNoteBounds = new RectF(mLiveCaptureRect.left, mMicrNoteTop - mTextPaint.getTextSize(), mLiveCaptureRect.right, mLiveCaptureRect.top - 10);

               mOcrAreaBounds = new Rect((int)mCameraGuideLeft, (int)mCameraGuideTop, mLiveCaptureRect.right - ((int)mCameraGuideLeft + mLiveCaptureRect.right)/3, (int)(mLiveCaptureRect.top + mVerticalStrokeWidth));
               mOcrAreaNoteLeft = (int)(((mOcrAreaBounds.width()) / 2 + mHorizontalMargins)  - (mTextPaint.measureText(mOCRAreaNote) / 2));

               float ratioX = (float)size.getWidth() / previewSize.width;
               float ratioY = (float)size.getHeight() / previewSize.height;

               mMicrReadAreaBounds = LeadRect.fromLTRB(
                       (int) (mMicrAreaBounds.left / ratioX),
                       (int) (mMicrAreaBounds.top / ratioY),
                       (int) (mMicrAreaBounds.right / ratioX),
                       (int) (mMicrAreaBounds.bottom / ratioY));

               mOCRReadAreaBounds = LeadRect.fromLTRB(
                       (int)(mOcrAreaBounds.left / ratioX),
                       (int)(mOcrAreaBounds.top / ratioY),
                       (int)(mOcrAreaBounds.right / ratioX),
                       (int)(mOcrAreaBounds.bottom / ratioY));
            }
         } catch(Exception ex) {
            Log.w("SetPreviewSize", ex.getMessage());
            // hide the OverlayView and clear the crop rectangle
            mOverlayView.setVisibility(View.INVISIBLE);
         }

         try {
            List<String> supportedFlashModesList = parameters.getSupportedFlashModes();
            if (supportedFlashModesList != null && supportedFlashModesList.contains(Parameters.FLASH_MODE_OFF))
               parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
         } catch(Exception ex) {
            Log.w("SetFlashMode", ex.getMessage());
         }

         mCamera.setParameters(parameters);
      } catch(Exception ex) {
         Log.w("SetParameters", ex.getMessage());
      }
   }

   @Override
   public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      if(!mStartLiveCapture || mCamera == null)
         return;
      
      try {
         
         initCameraParameters(new LeadSize(width, height));
         mCamera.setDisplayOrientation(0);
         mCamera.startPreview();
         
         // Process Image (Delay 10 ms)
         mCamera.setOneShotPreviewCallback(new PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
               // Check if live capture stopped
               if (!mStartLiveCapture || mCamera == null)
                  return;

               try {
                  Parameters parameters = camera.getParameters();

                  // Convert to Bitmap
                  Size previewSize = parameters.getPreviewSize();
                  byte[] imgData = null;

                  switch (parameters.getPreviewFormat()) {
                     // If the camera support "JPEG" or "RGB_565" use the data direct, if not use the default "NV21"
                     case ImageFormat.JPEG:
                     case ImageFormat.RGB_565:
                        imgData = data;
                        break;

                     default:
                        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);
                        imgData = baos.toByteArray();
                        baos = null;
                        yuvimage = null;
                        break;
                  }

                  Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                  RasterImage rasterImage = RasterImageConverter.convertFromBitmap(bitmap, ConvertFromImageOptions.NONE.getValue());

                  MicrResutls micrRes = detectAndRecognizeMicr(rasterImage, mMicrReadAreaBounds);

                  String ocrRes = null;
                  if (detectOCR)
                     ocrRes = detectAndRecognizeOCR(rasterImage, mOCRReadAreaBounds);

                  boolean found = false;
                  if (micrRes != null && micrRes.isMicrCodeDetected() && detectOCR && ocrRes != null) {
                     // Set start value to 'true' to restart live capture...
                     displayResults(micrRes, rasterImage, mMicrReadAreaBounds, ocrRes);
                     found = true;
                  } else if (micrRes != null && micrRes.isMicrCodeDetected() && !detectOCR) {
                     // Set start value to 'true' to restart live capture...
                     displayResults(micrRes, rasterImage, mMicrReadAreaBounds, "");
                     found = true;
                  }

                  // Free Data
                  rasterImage.dispose();
                  rasterImage = null;
                  bitmap = null;

                  if (!found) {
                     final PreviewCallback previewCallBack = this;
                     Timer oneShotTimer = new Timer();
                     oneShotTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                           if (!mStartLiveCapture || mCamera == null)
                              return;
                           mCamera.setOneShotPreviewCallback(previewCallBack);
                        }
                     }, IMAGE_PROCESS_DELAY);
                  }
               } catch (Exception ex) {
                  stopLiveCapture(true);
                  Messager.showError(MicrDemoActivity.this, ex.getMessage(), "Live Capture");
               }
            }
         });

         // Do AutoFocus every 3 seconds
         mCamera.autoFocus(new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
               final AutoFocusCallback autoFocusCallback = this;

               Timer autoFocusTimer = new Timer();
               autoFocusTimer.schedule(new TimerTask() {
                  public void run() {
                     if (!mStartLiveCapture || mCamera == null)
                        return;

                     mCamera.autoFocus(autoFocusCallback);
                  }
               }, AUTO_FOCUS_DELAY);
            }
         });
      } catch (Exception ex) {
         Messager.showError(MicrDemoActivity.this, "Couldn't start live capture", "Live Capture");
         stopLiveCapture(true);
      }
   }

   @Override
   public void surfaceCreated(SurfaceHolder holder) {
      if(!mStartLiveCapture || mCamera != null)
         return;

      try {
         mCamera = Camera.open();
         // if the device doesn't have a back-facing camera; "Camera.open()" will return null
         if(mCamera == null && Build.VERSION.SDK_INT >= 9) {
            // "Camera.open (int cameraId)" supported in API 9+
            mCamera = Camera.open(Camera.getNumberOfCameras() - 1);
         }
         mCamera.setPreviewDisplay(mSurfaceHolder);
         // show overlay view
         mOverlayView.setVisibility(View.VISIBLE);
      } catch (Exception ex) {
         Messager.showError(MicrDemoActivity.this, "Couldn't start live capture", "Live Capture");
         stopLiveCapture(true);
      }
   }

   @Override
   public void surfaceDestroyed(SurfaceHolder holder) {
      if(!mStartLiveCapture || mCamera == null)
         return;

      mStartLiveCapture = false;
      if(mShowProgress) {
         mProgressDlg = Progress.show(this, null, "Stopping Live Capture");
         Thread stopCameraThread = new Thread(new Runnable() {
            @Override
            public void run() {
               stopCamera();
               Progress.close(mProgressDlg);
            }
         });
         stopCameraThread.start();
      } else {
         stopCamera();
      }
   }
   
   private void stopCamera() {
      try {
         mCamera.cancelAutoFocus();
         mCamera.stopPreview();
         mCamera.setPreviewDisplay(null);
         mCamera.release();
      } catch (Exception ex) {
         Messager.showError(this, ex.getMessage(), "Live Capture");
      }
      mCamera = null;
   }
   
   private String getMicrZoneText(OcrZoneCharacters zoneCharacters) {
      List<OcrWord> words = zoneCharacters.getWords();
      if(words.size() > 0) {
         String micrLineText = new String();
         for (int i = 0; i < words.size(); i++) {
            OcrWord word = words.get(i);
            micrLineText += word.getValue();
            micrLineText += " ";
         }
         
         return micrLineText;
      }
      
      return null;
   }
   
   char[] micr_chars = {
         (char)0x2446, (char)0x2447, (char)0x2448, (char)0x2449, // MICR characters
         (char)0x0030, (char)0x0031, (char)0x0032, (char)0x0033,
         (char)0x0034, (char)0x0035, (char)0x0036, (char)0x0037,
         (char)0x0038, (char)0x0039};

   private String getMicrString(String micrText) {
      // We need to convert it, because in this font, a to d are the MICR symbols
      char[] newChars = new char[micrText.length()];
      for (int i = 0; i < newChars.length; i++)
      {
         char c = micrText.charAt(i);

         // check if it is a MICR symbol, convert it to a to d
         if (c >= 0x2446 && c <= 0x2449) {
            // Make it a + the difference
            newChars[i] = (char)('a' + (c - 0x2446));
         }
         else {
            // Add it as is
            newChars[i] = c;
         }
      }
      
      return new String(newChars);
   }
   
   boolean isStrictMode() {
      if (mStartLiveCapture) 
         return mMicrVideoExtractionMode == MicrExtractionMode_Strict;
      else
         return mMicrPictureExtractionMode == MicrExtractionMode_Strict;
   }
   
   private void setMicrExtractMode() {
      boolean isStrictMode = isStrictMode();

      OcrSettingManager settingsManager = mOcrEngine.getSettingManager();
      settingsManager.setBooleanValue("Recognition.CharacterFilter.PostprocessMICR", isStrictMode);
      settingsManager.setBooleanValue("Recognition.Fonts.RecognizeFontAttributes", isStrictMode);
   }
   
   public void displayMICRSettings(final boolean isLiveCaptureStarted) {
      LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      final View settingsView = layoutInflater.inflate(R.layout.micr_settings, null);
      
      {
         final Spinner micrVideoExtarctionModeSpinner = ButterKnife.findById(settingsView, R.id.spnr_video_micr_extraction_mode);
         micrVideoExtarctionModeSpinner.setSelection(mMicrVideoExtractionMode);
         micrVideoExtarctionModeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(position == 0)
                  mMicrVideoExtractionMode = MicrExtractionMode_Strict;
               else
                  mMicrVideoExtractionMode = MicrExtractionMode_Relaxed;
   
               setMicrExtractMode();
            }
   
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
         });
      }
      
      {
         final Spinner micrPictureExtarctionModeSpinner = ButterKnife.findById(settingsView, R.id.spnr_picture_micr_extraction_mode);
         micrPictureExtarctionModeSpinner.setSelection(mMicrPictureExtractionMode);
         micrPictureExtarctionModeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(position == 0)
                  mMicrPictureExtractionMode = MicrExtractionMode_Strict;
               else
                  mMicrPictureExtractionMode = MicrExtractionMode_Relaxed;
   
               setMicrExtractMode();
            }
   
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
         });
      }

      AlertDialog.Builder resultsDlgBuilder = new AlertDialog.Builder(this);
      resultsDlgBuilder.setView(settingsView);
      final AlertDialog resultsDlg = resultsDlgBuilder.create();
      resultsDlg.setTitle(R.string.settings_micr_extraction_mode);
      
      resultsDlg.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", new Dialog.OnClickListener() {         
         @Override
         public void onClick(DialogInterface dialog, int which) {
            resultsDlg.dismiss();
            
            if (isLiveCaptureStarted)
               startLiveCapture();
         }
      });

      resultsDlg.show();
   }

   private MicrResutls detectAndRecognizeMicr(RasterImage image, LeadRect searchingZoneValue) {
      OcrDocument ocrDocument = null;
      boolean micrCodeDetected = false;
      boolean showErrorMessage = !mStartLiveCapture;
      MicrResutls micrRes = new MicrResutls(null, null, searchingZoneValue, micrCodeDetected, showErrorMessage);
      
      try {
         LeadRect searchingZone;
         if (searchingZoneValue.isEmpty())
            searchingZone = new LeadRect(0, 0, image.getWidth(), image.getHeight());
         else
            searchingZone = searchingZoneValue;
         
         // Create LTOcrDocument and add the image into it.
         ocrDocument = mOcrEngine.getDocumentManager().createDocument();
         OcrPage ocrPage = ocrDocument.getPages().addPage(image, null);
         if(ocrPage == null)
            return micrRes;
         
         // Auto deskew the image before calling the MICR detection function
         ocrPage.autoPreprocess(OcrAutoPreprocessPageCommand.DESKEW, null);
         
         // Get the processed image from the engine
         RasterImage rasterImage = ocrPage.getRasterImage(OcrPageType.PROCESSING);
         
         double maxPageCoordinate = Math.max((double)ocrPage.getWidth(), (double)ocrPage.getHeight());
         double maxImageCoordinate = Math.max((double)rasterImage.getWidth(), (double)rasterImage.getHeight());
         double ratio = Math.min(maxPageCoordinate, maxImageCoordinate) / Math.max(maxPageCoordinate, maxImageCoordinate);
         if(ratio != 1.0) {
            searchingZone = new LeadRect((int)(searchingZone.getX() * ratio), (int)(searchingZone.getY() * ratio), (int)(searchingZone.getWidth() * ratio), (int)(searchingZone.getHeight() * ratio));
            ocrPage.setRasterImage(rasterImage);
            micrRes.setScale(ratio);
         }

         MICRCodeDetectionCommand micrCommand = new MICRCodeDetectionCommand();
         micrCommand.setSearchingZone(searchingZone);
         int ret = micrCommand.run(rasterImage);

         if(ret == L_ERROR.SUCCESS.getValue() && !micrCommand.getMICRZone().isEmpty()) {
            // We were able to determine the MICR area inside the image, so read the MICR code using the OCR engine
            
            // Add one zone to the OCR page with the same detected MICR zone rectangle
            OcrZone zone = new OcrZone();
            zone.setBounds(micrCommand.getMICRZone());
            zone.setFillMethod(OcrZoneFillMethod.MICR);
            ocrPage.getZones().add(zone);
         
            ocrPage.recognize(null);
            
            OcrPageCharacters pageCharacters = ocrPage.getRecognizedCharacters();
            if(pageCharacters == null || pageCharacters.size() <= 0)
               return micrRes;

            OcrZoneCharacters characters = pageCharacters.get(0);
            if(isStrictMode() && characters.size() < 18)
               return micrRes;
            
            String micrLineText = getMicrZoneText(characters);
            if(micrLineText != null && micrLineText.length() > 0) {
               OcrMicrData micrData = characters.extractMicrData();
               if (isStrictMode() && (micrData == null || micrData.getAccount().length() <= 0 || micrData.getRouting().length() <= 0))
                  return micrRes;
               
               micrCodeDetected = true;
               
               micrRes.setMicrData(micrData);
               micrRes.setMicrText(micrLineText);
               micrRes.setArea(searchingZone);
               micrRes.setMicrCodeDetected(micrCodeDetected);
               micrRes.setShowErrorMessage(showErrorMessage);
               return micrRes;
            }
         }
      }
      finally {
         // Clear the OcrDocument pages
         if(ocrDocument != null)
            ocrDocument.getPages().clear();
      }
      
      return micrRes;
   }

   
   public void displayResults(MicrResutls micrRes, RasterImage image, LeadRect rc, final String results) {
      boolean bImageCloned = false;
      RasterImage micrImage = image;
      if (!rc.isEmpty()) {
         double scale = micrRes.getScale() < 1.0 ? 1.0 : micrRes.getScale();
         micrImage = image.clone(new LeadRect((int)(rc.getLeft() / scale),
               (int)(rc.getTop() / scale), 
               (int)(rc.getWidth() / scale), 
               (int)(rc.getHeight() / scale)));
         bImageCloned = true;
      }
            
      OcrMicrData micrData = micrRes.getMicrData();
      String micrText = micrRes.getMicrText();
         
      LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      final View resultsView = layoutInflater.inflate(R.layout.micr_results, null);

      final ImageView imageView = ButterKnife.findById(resultsView, R.id.imageview_micr);
      int height = mImageViewer.getHeight() / 4;
      if(micrImage.getHeight() < height)
         height = micrImage.getHeight();
      imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height));
      imageView.setImageBitmap(RasterImageConverter.convertToBitmap(micrImage, ConvertToImageOptions.NONE.getValue()));
      if(bImageCloned)
         micrImage.dispose();
      
      {
         final TextView micrLineResultsTxtView = ButterKnife.findById(resultsView, R.id.txtview_micr_line_results);
         Typeface type = Typeface.createFromAsset(getAssets(),"fonts/micrenc.ttf");
         micrLineResultsTxtView.setTypeface(type);
         micrLineResultsTxtView.setText(getMicrString(micrText));
      }
      
      {
         String strAuxiliary = micrData.getAuxiliary();
         final TextView auxiliaryOnUsTxtView = ButterKnife.findById(resultsView, R.id.txtview_auxiliary_on_us);
         auxiliaryOnUsTxtView.setText(strAuxiliary.length() > 0 ? strAuxiliary : "N/A");
      }
      
      {
         char[] epc = new char[1];
         epc[0] = micrData.getEpc();
         final TextView epcTxtView = ButterKnife.findById(resultsView, R.id.txtview_epc);
         if (epc[0] != ' ')
            epcTxtView.setText(new String(epc));
         else
            epcTxtView.setText("N/A");
      }
      
      {
         String strRouting = micrData.getRouting();
         routingTxtView = ButterKnife.findById(resultsView, R.id.txtview_routing);
         routingTxtView.setText(strRouting.length() > 0 ? strRouting : "N/A");
      }

      {
         String strAcount = micrData.getAccount();
         accountTxtView = ButterKnife.findById(resultsView, R.id.txtview_account);
         accountTxtView.setText(strAcount.length() > 0 ? strAcount: "N/A");
      }

      {
         String strCheckNumber = micrData.getCheckNumber();
         checkNumberTxtView = ButterKnife.findById(resultsView, R.id.txtview_check_number);
         checkNumberTxtView.setText(strCheckNumber.length() > 0 ? strCheckNumber : "N/A");
      }

      {
         String strAmount = micrData.getAmount();
         final TextView amountTxtView = ButterKnife.findById(resultsView, R.id.txtview_amount);
         amountTxtView.setText(strAmount.length() > 0 ? strAmount : "N/A");
      }

      if(detectOCR) {
         resultsEditText = ButterKnife.findById(resultsView, R.id.edittext_ocr_results);
         resultsEditText.setKeyListener(null);
         resultsEditText.setScrollContainer(true);
         resultsEditText.setText(results);
      }
      
      AlertDialog.Builder resultsDlgBuilder = new AlertDialog.Builder(this);
      resultsDlgBuilder.setView(resultsView);
      final AlertDialog resultsDlg = resultsDlgBuilder.create();
      resultsDlg.setTitle(R.string.micr_result);

      resultsDlg.setButton(AlertDialog.BUTTON_POSITIVE, "Accept", new Dialog.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
           if(!routingTxtView.getText().toString().equalsIgnoreCase("N/A") && !accountTxtView.getText().toString().equalsIgnoreCase("N/A") && !checkNumberTxtView.getText().toString().equalsIgnoreCase("N/A")) {
              resultsDlg.dismiss();
              Intent returnIntent = new Intent();
              returnIntent.putExtra(Constants.ROUTING_NUMBER, routingTxtView.getText().toString());
              returnIntent.putExtra(Constants.ACCOUNT_NUMBER, accountTxtView.getText().toString());
              returnIntent.putExtra(Constants.CHEQUE_NUMBER, checkNumberTxtView.getText().toString());

              if(detectOCR && !results.equalsIgnoreCase("")) {
                 returnIntent.putExtra(Constants.OWNER_NAME, resultsEditText.getText().toString().split("\n")[0]);
                 returnIntent.putExtra(Constants.OWNER_STREET, resultsEditText.getText().toString().split("\n")[1]);
                 returnIntent.putExtra(Constants.OWNER_STREET1, resultsEditText.getText().toString().split("\n")[2]);
                 returnIntent.putExtra(Constants.OWNER_CITY, resultsEditText.getText().toString().split("\n")[3]);
                 returnIntent.putExtra(Constants.OCR_INFO, "true");

              }else
                 returnIntent.putExtra(Constants.OCR_INFO, "false");


              setResult(RESULT_OK, returnIntent);
              finish();

           }
            else
              Toast.makeText(getApplicationContext(), "Invalid Data", Toast.LENGTH_LONG).show();
         }
      });

      resultsDlg.setButton(AlertDialog.BUTTON_NEGATIVE, "Retry", new Dialog.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            resultsDlg.dismiss();
         }
      });

      if (mStartLiveCapture) {
         resultsDlg.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
               startLiveCapture();
            }
         });
      }
      
      resultsDlg.show();
      
      if (mStartLiveCapture)
         stopLiveCapture(true);
   }
   
   public void onSettings(View v) {
      // Stop live capture
      displayMICRSettings(mStartLiveCapture);
      if(mStartLiveCapture) {
         stopLiveCapture(true);
      }
   }
   
   private class MicrResutls {
      private String micrText;
      private OcrMicrData micrData;
      private LeadRect mArea;
      private boolean mMicrCodeDetected;
      private boolean mShowErrorMessage;
      private double mScale;
      
      public MicrResutls(OcrMicrData data, String text, LeadRect area, boolean micrDetected, boolean showErrMsg) {
         micrText = text;
         micrData = data;
         mArea = area.clone();
         mMicrCodeDetected = micrDetected;
         mShowErrorMessage = showErrMsg;
         mScale = 1.0;
      }
      
      public OcrMicrData getMicrData() {
         return micrData;
      }
      
      public void setMicrData(OcrMicrData value) {
         micrData = value;
      }
      
      public String getMicrText() {
         return micrText;
      }

      public void setMicrText(String value) {
         micrText = value;
      }
      
      public LeadRect getArea() {
         return mArea;
      }

      public void setArea(LeadRect value) {
         mArea = value.clone();
      }
      
      public boolean isMicrCodeDetected() {
         return mMicrCodeDetected;
      }

      public void setMicrCodeDetected(boolean value) {
         mMicrCodeDetected = value;
      }
      
      public boolean canShowErrorMessage() {
         return mShowErrorMessage;
      }

      public void setShowErrorMessage(boolean value) {
         mShowErrorMessage = value;
      }
      
      public double getScale() {
         return mScale;
      }
      
      public void setScale(double value) {
         mScale = value;
      }
   }
   
   private class RecognizeTextTaskParams {
      private RasterImage mImage;
      private LeadRect mRect;

      public RecognizeTextTaskParams(RasterImage image, LeadRect rc) {
         mImage = image;
         mRect = rc;
      }
      
      public RasterImage getImage() {
         return mImage;
      }
      
      public LeadRect getRect() {
         return mRect;
      }
   }


   private String detectAndRecognizeOCR(RasterImage image, LeadRect searchingZoneValue) {
      OcrDocument ocrDocument = null;
      String ocrRes = "";
      final boolean mAbort = false;
      try {

         LeadRect searchingZone;
         if (searchingZoneValue.isEmpty())
            searchingZone = new LeadRect(0, 0, image.getWidth(), image.getHeight());
         else
            searchingZone = searchingZoneValue;

         // Create LTOcrDocument and add the image into it.
         ocrDocument = mOcrEngine.getDocumentManager().createDocument();
         OcrPage ocrPage = ocrDocument.getPages().addPage(image, null);

         if(ocrPage == null)
            return ocrRes;

         // Auto deskew the image before calling the MICR detection function
         ocrPage.autoPreprocess(OcrAutoPreprocessPageCommand.DESKEW, null);

         // Get the processed image from the engine
         RasterImage rasterImage = ocrPage.getRasterImage(OcrPageType.PROCESSING);

         ocrPage.setRasterImage(rasterImage);

         // Prepare the command
         MinimumCommand command = new MinimumCommand();
         //Apply the Minimum filter.
         command.setDimension(2);
         command.run(rasterImage);

         OcrZone zone = new OcrZone();
         zone.setBounds(searchingZone);
         zone.setZoneType(OcrZoneType.TEXT);
         ocrPage.getZones().add(zone);

         if(mAbort)
            return null;

         ocrRes = ocrPage.recognizeText(null);
      }
      finally {
         // Clear the OcrDocument pages
         if(ocrDocument != null)
            ocrDocument.getPages().clear();
      }

      return ocrRes;
   }

   /**
    * callback function for requests
    * @param requestCode the request code you have sent for requestingpermission
    * @param permissions which permissions were asked.
    * @param grantResults permissions granted or not
    */
   @Override
   public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String permissions[], @NonNull int[] grantResults) {
      switch (requestCode) {

         case Constants.PermissionRequestCodes.SYSTEM_CAMERA:{
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // permission was granted, yay! Do the
               // contacts-related task you need to do.
               Toast.makeText(MicrDemoActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
//               StaticFunctions.createSnackBar(mCoordinatorLayout,"Permission Granted","DISMISS",Snackbar.LENGTH_LONG);
            } else {
               Toast.makeText(MicrDemoActivity.this, "Permission Camera Denied", Toast.LENGTH_LONG).show();
//               StaticFunctions.createSnackBar(mCoordinatorLayout,"Permission Read Phone/Calls Denied","DISMISS",Snackbar.LENGTH_INDEFINITE);
               // permission denied, boo! Disable the
               // functionality that depends on this permission.
            }
         }

         case Constants.PermissionRequestCodes.WRITE_EXTERNAL_STORAGE:{
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // permission was granted, yay! Do the
               // contacts-related task you need to do.
               Toast.makeText(MicrDemoActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
//               StaticFunctions.createSnackBar(mCoordinatorLayout,"Permission Granted","DISMISS",Snackbar.LENGTH_LONG);
            } else {
               Toast.makeText(MicrDemoActivity.this, "Permission Write External Storage Denied", Toast.LENGTH_LONG).show();
//               StaticFunctions.createSnackBar(mCoordinatorLayout,"Permission Read Phone/Calls Denied","DISMISS",Snackbar.LENGTH_INDEFINITE);
               // permission denied, boo! Disable the
               // functionality that depends on this permission.
            }
         }

         // other 'case' lines to check for other
         // permissions this app might request
      }
   }


}
