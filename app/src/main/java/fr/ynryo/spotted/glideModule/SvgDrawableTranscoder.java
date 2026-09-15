package fr.ynryo.spotted.glideModule;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.caverock.androidsvg.SVG;

public class SvgDrawableTranscoder implements ResourceTranscoder<SVG, PictureDrawable> {
    @Override
    public Resource<PictureDrawable> transcode(@NonNull Resource<SVG> toTranscode, @NonNull Options options) {
        SVG svg = toTranscode.get();
        float w = svg.getDocumentWidth();
        float h = svg.getDocumentHeight();

        // getDocumentWidth/Height retourne -1 si width/height ne sont pas déclarés dans le SVG.
        // Dans ce cas, on lit les dimensions depuis le viewBox.
        if (!(w > 0 && h > 0)) {
            android.graphics.RectF viewBox = svg.getDocumentViewBox();
            if (viewBox != null) {
                w = viewBox.width();
                h = viewBox.height();
            }
        }

        Picture picture = (w > 0 && h > 0)
                ? svg.renderToPicture((int) w, (int) h)
                : svg.renderToPicture();
        PictureDrawable drawable = new PictureDrawable(picture);
        return new SimpleResource<>(drawable);
    }
}