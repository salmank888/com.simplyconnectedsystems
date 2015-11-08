package leadtools.demos;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

public class DeviceUtils {
   
   public static boolean hasCamera(Context context) {
      PackageManager manager = context.getPackageManager();
      boolean hasCamera = manager.hasSystemFeature(PackageManager.FEATURE_CAMERA);

      // "PackageManager.FEATURE_CAMERA_FRONT" supported in API 9+
      if(!hasCamera && Build.VERSION.SDK_INT >= 9)
         hasCamera = manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
      
      return hasCamera;
   }
   
   public static boolean isMediaMounted() {
      return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
   }
}
