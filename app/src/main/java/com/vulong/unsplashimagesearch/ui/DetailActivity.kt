package com.vulong.unsplashimagesearch.ui

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vulong.unsplashimagesearch.R
import com.vulong.unsplashimagesearch.data.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStream


class DetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var currentPhoto: Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        currentPhoto = intent.getParcelableExtra<Photo>("photo_click")!!

        findViewById<TextView>(R.id.text_description).text =
            "${currentPhoto?.description}"
        findViewById<TextView>(R.id.text_date_published).text =
            "Published on: ${currentPhoto?.createdAt?.substring(0, 10)}"
        findViewById<TextView>(R.id.text_username_author).text =
            "By: ${currentPhoto?.user?.username}"

        Glide.with(this)
            .load(currentPhoto?.urls?.small)
            .placeholder(R.drawable.loading)
            .into(findViewById<ImageView>(R.id.image_photo_selected));

        findViewById<Button>(R.id.button_download_full).setOnClickListener(this)
        findViewById<Button>(R.id.button_download_small).setOnClickListener(this)
        findViewById<Button>(R.id.button_download_thumb).setOnClickListener(this)

    }

    //todo
    override fun onClick(v: View?) {

        findViewById<ScrollView>(R.id.layout_container_detail_photo).visibility = View.GONE
        findViewById<LinearLayout>(R.id.layout_downloading).visibility = View.VISIBLE
        var typeSave: String? = null
        when (v?.id) {
            R.id.button_download_full -> {
                typeSave = "FULL"
            }
            R.id.button_download_small -> {
                typeSave = "SMALL"
            }
            R.id.button_download_thumb -> {
                typeSave = "THUMB"
            }
        }

        if (typeSave != null) {
            saveImage(typeSave)
        }

    }

    fun saveImage(typeSave: String) {
        GlobalScope.launch {
            saveBitmapToPNGFile(getBitmap(typeSave), typeSave)
        }
    }

    fun getBitmap(type: String): Bitmap {
        var URL = ""
        if (type.equals("FULL")) URL = currentPhoto.urls.full
        if (type.equals("SMALL")) URL = currentPhoto.urls.small
        if (type.equals("THUMB")) URL = currentPhoto.urls.thumb
        return Glide.with(this@DetailActivity)
            .asBitmap()
            .load(URL)
            .submit()
            .get()
    }

    suspend fun saveBitmapToPNGFile(bitmap: Bitmap, type: String) {
        var fos: OutputStream? = null
        var imageUri: Uri? = null
        var returnMessage = ""
        var displayName = ""
        if (type.equals("FULL")) displayName = "FULL_SIZE_${currentPhoto.id}.png"
        if (type.equals("THUMB")) displayName = "THUMB_${currentPhoto.id}.png"
        if (type.equals("SMALL")) displayName = "SMALL_${currentPhoto.id}.png"

        val resolver: ContentResolver = applicationContext.getContentResolver()
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "${displayName}")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
//            muc Download not allowed for content://media/external/images/media; allowed directories are [DCIM, Pictures]

            //se luu anh vao DCIM
            Environment.DIRECTORY_DCIM.toString()
                    + File.separator + "VU LONG"
        )
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (imageUri == null) throw IOException("Failed to create new MediaStore record.")

        fos = resolver.openOutputStream(imageUri)

        returnMessage = "Image saved in:\nDCIM/VU LONG/$displayName"
        if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
            returnMessage = "Failed to save this image"
//            throw IOException("Failed to save bitmap.");
        }

        fos?.flush()

        withContext(Dispatchers.Main) {
            Toast.makeText(this@DetailActivity, returnMessage, Toast.LENGTH_LONG).show()
            findViewById<ScrollView>(R.id.layout_container_detail_photo).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.layout_downloading).visibility = View.GONE
        }

    }


}