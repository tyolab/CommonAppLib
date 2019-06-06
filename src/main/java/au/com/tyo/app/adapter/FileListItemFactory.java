/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import au.com.tyo.android.adapter.CommonItemFactory;
import au.com.tyo.android.images.utils.BitmapUtils;
import au.com.tyo.android.utils.DrawableUtils;
import au.com.tyo.app.R;
import au.com.tyo.data.ContentTypes;
import au.com.tyo.utils.FileFormatter;

public class FileListItemFactory extends CommonItemFactory<File> {

    final int THUMBNAIL_SIZE = 64;

    private Drawable fileIconDrawable;

    private Drawable folderIconDrawable;

    private Drawable musicIconDrawable;

    private Drawable videoIconDrawable;

    /**
     * Tint Color for the default icons (file, folder)
     */
    private int tintColor;

    public FileListItemFactory(Context context) {
        super(context, R.layout.image_text_list_cell2);
        init(context);
    }

    public FileListItemFactory(Context context, int resId) {
        super(context, resId);
        init(context);
    }

    private void init(Context context) {
        tintColor = Color.LTGRAY;

        fileIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_insert_drive_file_black_24dp, tintColor);
        folderIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_folder_black_24dp, tintColor);
        videoIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_videocam_black_24dp, tintColor);
        musicIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_music_note_black_24dp, tintColor);
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }

    @Override
    protected CharSequence getText1(File obj) {
        return obj.getName();
    }

    @Override
    protected Drawable getImageViewDrawable(File obj) {
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
    protected CharSequence getText2(File obj) {
        return FileFormatter.byteSizeToString(obj.length(),1);
    }

}
