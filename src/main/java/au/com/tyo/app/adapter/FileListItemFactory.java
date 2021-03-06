/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import au.com.tyo.app.R;
import au.com.tyo.data.ContentTypes;
import au.com.tyo.io.WildcardFileStack;
import au.com.tyo.utils.FileFormatter;

public class FileListItemFactory extends IconiedListItemFactory<File> {

    public FileListItemFactory(Context context) {
        super(context);
    }

    public FileListItemFactory(Context context, int resId) {
        super(context, resId);
    }

    @Override
    public CharSequence getText2(File obj) {
        return FileFormatter.byteSizeToString(obj.length(),1);
    }

    @Override
    public CharSequence getText1(File obj) {
        return obj.getName();
    }

    @Override
    public Drawable getImageViewDrawable(File obj) {
        if (obj.isDirectory())
            return folderIconDrawable;

        String name = obj.getName();
        String ext = ContentTypes.getExtension(name);

        if (ContentTypes.isVideo(ext))
            return videoIconDrawable;
        else if (ContentTypes.isAudio(ext))
            return musicIconDrawable;
        else if (ContentTypes.isImage(ext)) {
            try {
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeStream(new FileInputStream(obj)),
                        THUMBNAIL_SIZE, THUMBNAIL_SIZE);
                return new BitmapDrawable(getContext().getResources(), thumbImage);
            } catch (FileNotFoundException e) {
                return null;
            }
        }

        return fileIconDrawable;
    }

    @Override
    protected void setText2(final File item, final TextView tv2) {
        if (item.isDirectory()) {
            if (null != tv2) {
                tv2.post(new Runnable() {
                    @Override
                    public void run() {
                        WildcardFileStack wildcardFileStack = new WildcardFileStack(item);
                        wildcardFileStack.listFiles();
                        tv2.setText("" + wildcardFileStack.size() + ' ' + tv2.getContext().getString(R.string.items));
                    }
                });
            }

        }
        else
            super.setText2(item, tv2);
    }
}
