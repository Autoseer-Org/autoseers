package com.innovara.autoseers.home

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.innovara.autoseers.R
import java.io.File

class HomeFileProvider: FileProvider(
    R.xml.file_provider_path
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val imagePath = File(context.filesDir, "/images")
            imagePath.mkdir()
            val image = File(imagePath, "report_image")
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(context, authority, image)
        }
    }
}