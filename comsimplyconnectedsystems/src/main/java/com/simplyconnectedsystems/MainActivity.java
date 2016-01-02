package com.simplyconnectedsystems;

import com.simplyconnectedsystems.utility.ACHPreferanceHelper;
import com.simplyconnectedsystems.utility.ColorTool;
import com.simplyconnectedsystems.utility.CreditPreferenceHelper;
import com.simplyconnectedsystems.utility.GeneralPreferenceHelper;
import com.sk.simplyconnectedsystems.R;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final int RESULT_SETTINGS = 1;

    private boolean _isACH = false;

    private CreditPreferenceHelper _preferanceHelperCC;
    private ACHPreferanceHelper _preferanceHelperACH;
    private GeneralPreferenceHelper _preferanceHelperGeneral;

    private UniMagTopDialog dlgErrorTopShow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        ImageView iv = (ImageView) findViewById(R.id.image);
        if (iv != null) {
            iv.setOnTouchListener(this);
        }

        _preferanceHelperCC = new CreditPreferenceHelper(this.getApplicationContext());
        _preferanceHelperACH = new ACHPreferanceHelper(this.getApplicationContext());
        _preferanceHelperGeneral = new GeneralPreferenceHelper(this.getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(_preferanceHelperGeneral.getCompanyName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_settingsCC) {
            Intent i = new Intent(this, CCUserSettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
        } else if (item.getItemId() == R.id.menu_settingsACH) {
            Intent i = new Intent(this, ACHUserSettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
        }
        else if (item.getItemId() == R.id.menu_settingsGeneral) {
            Intent i = new Intent(this, GeneralUserSettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
        }
        if (item.getItemId() == R.id.menu_close) {
            finish();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    private void showErrorTopDialog(String strTitle, String errorMessage, final boolean changeAllowedMode) {
        try {
            if (dlgErrorTopShow == null) {
                dlgErrorTopShow = new UniMagTopDialog(this);
            }

            dlgErrorTopShow.setCancelable(false);

            Window win = dlgErrorTopShow.getWindow();
            win.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dlgErrorTopShow.setTitle(strTitle);
            dlgErrorTopShow.setContentView(R.layout.dlgerrortopview);
            TextView myTV = (TextView) dlgErrorTopShow
                    .findViewById(R.id.TView_Info);
            Button myBtn = (Button) dlgErrorTopShow
                    .findViewById(R.id.btnCancel);

            myTV.setText(errorMessage);
            myBtn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    if(changeAllowedMode){
                        Intent i = new Intent(MainActivity.this, GeneralUserSettingsActivity.class);
                        startActivityForResult(i, RESULT_SETTINGS);
                    }else {
                        if (_isACH) {
                            Intent i = new Intent(MainActivity.this, ACHUserSettingsActivity.class);
                            startActivityForResult(i, RESULT_SETTINGS);
                            _isACH = false;
                        } else {
                            Intent i = new Intent(MainActivity.this, CCUserSettingsActivity.class);
                            startActivityForResult(i, RESULT_SETTINGS);
                        }
                    }
                    dlgErrorTopShow.dismiss();
                }
            });

            dlgErrorTopShow.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                        return false;
                    }
                    return true;
                }
            });
            dlgErrorTopShow.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    ;

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        boolean handledHere = false;

        final int action = ev.getAction();

        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();
//				int nextImage = -1;			// resource id of the next image to display

        // If we cannot find the imageView, return.
        ImageView imageView = (ImageView) v.findViewById(R.id.image);
        if (imageView == null) return false;

        // When the action is Down, see if we should show the "pressed" image for the default image.
        // We do this when the default image is showing. That condition is detectable by looking at the
        // tag of the view. If it is null or contains the resource number of the default image, display the pressed image.
        Integer tagNum = (Integer) imageView.getTag();
        int currentResource = (tagNum == null) ? R.drawable.vector : tagNum.intValue();

        // Now that we know the current resource being displayed we can handle the DOWN and UP events.

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (currentResource == R.drawable.vector) {
//							nextImage = R.drawable.p2_ship_pressed;
                    handledHere = true;
       /*
       } else if (currentResource != R.drawable.p2_ship_default) {
         nextImage = R.drawable.p2_ship_default;
         handledHere = true;
       */
                } else handledHere = true;
                break;

            case MotionEvent.ACTION_UP:
                // On the UP, we do the click action.
                // The hidden image (image_areas) has three different hotspots on it.
                // The colors are red, blue, and yellow.
                // Use image_areas to determine which region the user touched.
                int touchColor = getHotspotColor(R.id.image_shadow, evX, evY);

                // Compare the touchColor to the expected values. Switch to a different image, depending on what color was touched.
                // Note that we use a Color Tool object to test whether the observed color is close enough to the real color to
                // count as a match. We do this because colors on the screen do not match the map exactly because of scaling and
                // varying pixel density.
                ColorTool ct = new ColorTool();
                int tolerance = 25;
//						nextImage = R.drawable.p2_ship_default;
                if (ct.closeMatch(Color.RED, touchColor, tolerance)) {
                    if (!_preferanceHelperACH.getIsValidSettings()) {
                        _isACH = true;
                        showErrorTopDialog("Settings Required", "Please review ACH Settings to continue.", false);
                    } else if(Integer.valueOf(_preferanceHelperGeneral.getAllowedMode()) == 1 || Integer.valueOf(_preferanceHelperGeneral.getAllowedMode()) == 2) {
                        Intent intent = new Intent(MainActivity.this
                                .getApplicationContext(), SaleActivity.class);
                        intent.putExtra("saleType", "check");
                        MainActivity.this.startActivity(intent);
                    }
                    else
                        showErrorTopDialog("Mode Change Required", "Please review General Settings to continue.", true);

                } else if (ct.closeMatch(Color.GREEN, touchColor, tolerance)) {
                    if (!_preferanceHelperCC.getIsValidSettings()) {
                        _isACH = false;
                        showErrorTopDialog("Settings Required", "Please review CC Settings to continue.", false);
                    } else if(Integer.valueOf(_preferanceHelperGeneral.getAllowedMode()) == 1 || Integer.valueOf(_preferanceHelperGeneral.getAllowedMode()) == 3) {
                        Intent intent = new Intent(MainActivity.this
                                .getApplicationContext(), SaleActivity.class);
                        intent.putExtra("saleType", "credit");
                        MainActivity.this.startActivity(intent);
                    }else
                        showErrorTopDialog("Mode Change Required", "Please review General Settings to continue.", true);

                }
//						else if (ct.closeMatch (Color.YELLOW, touchColor, tolerance)) nextImage = R.drawable.p2_ship_no_star;
//						else if (ct.closeMatch (Color.WHITE, touchColor, tolerance)) nextImage = R.drawable.p2_ship_default;

                // If the next image is the same as the last image, go back to the default.
                // toast ("Current image: " + currentResource + " next: " + nextImage);
//						if (currentResource == nextImage) {
//							nextImage = R.drawable.p2_ship_default;
//						}
                handledHere = true;
                break;

            default:
                handledHere = false;
        } // end switch

        if (handledHere) {

//					if (nextImage > 0) {
//						imageView.setImageResource (nextImage);
//						imageView.setTag (nextImage);
//					}
        }
        return handledHere;
    }

    /**
     * Get the color from the hotspot image at point x-y.
     */

    public int getHotspotColor(int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById(hotspotId);
        if (img == null) {
            Log.d("ImageAreasActivity", "Hot spot image not found");
            return 0;
        } else {
            img.setDrawingCacheEnabled(true);
            Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
            if (hotspots == null) {
                Log.d("ImageAreasActivity", "Hot spot bitmap was not created");
                return 0;
            } else {
                img.setDrawingCacheEnabled(false);
                return hotspots.getPixel(x, y);
            }
        }
    }

    private class UniMagTopDialog extends Dialog {

        public UniMagTopDialog(Context context) {
            super(context);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK
                    || KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {
                return false;
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onKeyMultiple(int keyCode, int repeatCount,
                                     KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK
                    || KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {

                return false;
            }
            return super.onKeyMultiple(keyCode, repeatCount, event);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK
                    || KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {
                return false;
            }
            return super.onKeyUp(keyCode, event);
        }
    }

}
