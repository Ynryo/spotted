package fr.ynryo.spotted.glideModule;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * Utility class for loading SVG images via Glide.
 * Handles aspect-ratio-aware sizing automatically.
 */
public class SvgLoader {

    private SvgLoader() {
    }

    /**
     * Load an SVG from a URL into an ImageView, resizing it to fit its parent height
     * while preserving aspect ratio and capping the width.
     *
     * @param context    Android context
     * @param url        URL of the SVG to load
     * @param imageView  Target ImageView
     * @param fallbackDp Fallback height in dp if the parent has not been laid out yet
     * @param maxWidthDp Maximum allowed width in dp
     */
    public static void loadSvg(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, int fallbackDp, int maxWidthDp) {

        int parentHeight = imageView.getParent() instanceof View ? ((View) imageView.getParent()).getHeight() : 0;

        float density = context.getResources().getDisplayMetrics().density;
        int targetHeightPx = parentHeight > 0 ? parentHeight : (int) (fallbackDp * density);
        int maxWidthPx = (int) (maxWidthDp * density);

        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(context).as(PictureDrawable.class).load(url).diskCacheStrategy(DiskCacheStrategy.DATA).into(new CustomTarget<PictureDrawable>() {
            @Override
            public void onResourceReady(@NonNull PictureDrawable resource, Transition<? super PictureDrawable> transition) {
                int picW = resource.getIntrinsicWidth();
                int picH = resource.getIntrinsicHeight();

                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                lp.height = targetHeightPx;
                if (picH > 0 && picW > 0) {
                    lp.width = Math.min((int) ((float) picW / picH * targetHeightPx), maxWidthPx);
                } else {
                    lp.width = targetHeightPx; // fallback carré
                }
                imageView.setLayoutParams(lp);
                imageView.setImageDrawable(resource);
            }

            @Override
            public void onLoadCleared(Drawable placeholder) {
                imageView.setImageDrawable(placeholder);
            }
        });
    }
}
