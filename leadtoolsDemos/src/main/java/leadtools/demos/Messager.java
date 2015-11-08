package leadtools.demos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Looper;
import android.widget.Toast;

public class Messager {
   public static void showMessage(Context context, String message, String title, OnClickListener listener) {
      boolean threadHasLooper = threadHasLooper();
      if(!threadHasLooper)
         Looper.prepare();

      AlertDialog.Builder msgDlgBuilder  = new AlertDialog.Builder(context);

      msgDlgBuilder.setMessage(message);
      msgDlgBuilder.setTitle(title);
      msgDlgBuilder.setPositiveButton("OK", listener);
      AlertDialog msgDlg = msgDlgBuilder.create();
      msgDlg.setCanceledOnTouchOutside(false);
      msgDlg.show();
      
      if(!threadHasLooper) {
         Looper.loop();
         Looper.myLooper().quit();
      }
   }

   public static void showError(Context context, String message, String title) {
      boolean threadHasLooper = threadHasLooper();
      if(!threadHasLooper)
         Looper.prepare();

      AlertDialog.Builder errorDlgBuilder  = new AlertDialog.Builder(context);

      errorDlgBuilder.setMessage(message);
      errorDlgBuilder.setTitle(title);
      errorDlgBuilder.setPositiveButton("OK", null);
      AlertDialog errorDlg = errorDlgBuilder.create();
      errorDlg.setCanceledOnTouchOutside(false);
      errorDlg.show();
      
      if(!threadHasLooper) {
         Looper.loop();
         Looper.myLooper().quit();
      }
   }
   
   public static void showNotification(Context context, String message) {
      boolean threadHasLooper = threadHasLooper();
      if(!threadHasLooper)
         Looper.prepare();
      
      Toast.makeText(context, message, Toast.LENGTH_LONG).show();
      
      if(!threadHasLooper) {
         Looper.loop();
         Looper.myLooper().quit();
      }
   }
   
   public static void showKernelExpiredMessage(Context context, OnDismissListener listener) {
      AlertDialog.Builder kernelExpiredDlgBuilder  = new AlertDialog.Builder(context);

      kernelExpiredDlgBuilder.setMessage("Your license file is missing, invalid or expired. LEADTOOLS will not function. Please contact LEAD Sales for information on obtaining a valid license.");
      kernelExpiredDlgBuilder.setTitle("No LEADTOOLS License");
      kernelExpiredDlgBuilder.setPositiveButton("OK", null);
      AlertDialog kernelExpiredDlg = kernelExpiredDlgBuilder.create();
      kernelExpiredDlg.setCanceledOnTouchOutside(false);
      kernelExpiredDlg.setOnDismissListener(listener);
      kernelExpiredDlg.show();
   }
   
   private static boolean threadHasLooper() {
      return Looper.myLooper() != null;
   }
}
