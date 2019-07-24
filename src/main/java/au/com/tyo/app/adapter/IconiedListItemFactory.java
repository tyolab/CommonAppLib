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

public class IconiedListItemFactory<ItemType> extends CommonItemFactory<ItemType> {

    public static final int THUMBNAIL_SIZE = 64;

    protected Drawable fileIconDrawable;

    protected Drawable folderIconDrawable;

    protected Drawable musicIconDrawable;

    protected Drawable videoIconDrawable;

    protected Drawable imageIconDrawable;

    /**
     * Tint Color for the default icons (file, folder)
     */
    private int tintColor;

    public IconiedListItemFactory(Context context) {
        super(context, R.layout.image_text_list_cell2);
        init(context);
    }

    public IconiedListItemFactory(Context context, int resId) {
        super(context, resId);
        init(context);
    }

    private void init(Context context) {
        tintColor = context.getResources().getColor(R.color.iconied_item_tint); // Color.LTGRAY;

        fileIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_insert_drive_file_black_24dp, tintColor);
        folderIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_folder_black_24dp, tintColor);
        videoIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_play_circle_outline_black_24dp, tintColor);
        musicIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_music_note_black_24dp, tintColor);
        imageIconDrawable = DrawableUtils.createTintedVectorDrawable(context, R.drawable.ic_photo_black_24dp, tintColor);
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }

    public Drawable getFolderIconDrawable() {
        return folderIconDrawable;
    }

    public Drawable getFileIconDrawable() {
        return fileIconDrawable;
    }

    public Drawable getMusicIconDrawable() {
        return musicIconDrawable;
    }

    public Drawable getVideoIconDrawable() {
        return videoIconDrawable;
    }

    public Drawable getImageIconDrawable() {
        return imageIconDrawable;
    }
}
