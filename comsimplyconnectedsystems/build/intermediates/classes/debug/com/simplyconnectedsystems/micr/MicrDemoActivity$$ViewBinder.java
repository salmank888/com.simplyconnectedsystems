// Generated code from Butter Knife. Do not modify!
package com.simplyconnectedsystems.micr;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MicrDemoActivity$$ViewBinder<T extends com.simplyconnectedsystems.micr.MicrDemoActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492949, "field 'mImageViewer'");
    target.mImageViewer = finder.castView(view, 2131492949, "field 'mImageViewer'");
    view = finder.findRequiredView(source, 2131492950, "field 'mSurfaceView'");
    target.mSurfaceView = finder.castView(view, 2131492950, "field 'mSurfaceView'");
    view = finder.findRequiredView(source, 2131492952, "field 'toggleButton'");
    target.toggleButton = finder.castView(view, 2131492952, "field 'toggleButton'");
  }

  @Override public void unbind(T target) {
    target.mImageViewer = null;
    target.mSurfaceView = null;
    target.toggleButton = null;
  }
}
