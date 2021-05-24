package com.loftechs.sample.utils

import com.loftechs.sample.SampleApp
import java.io.File

object FileUtil {

    fun getOriginalFile(filename: String, needCreateFile: Boolean): File {
        return getFile("original", filename, needCreateFile)
    }

    fun getThumbnailFile(filename: String, needCreateFile: Boolean): File {
        return getFile("thumbnail", filename, needCreateFile)
    }

    fun getProfileFile(filename: String, needCreateFile: Boolean): File {
        return getFile("profile", filename, needCreateFile)
    }

    private fun getFile(fileDirectory: String, filename: String, needCreateFile: Boolean): File {
        val directoryFile = File(SampleApp.context.externalCacheDir, fileDirectory)
        return if (needCreateFile) {
            if (!directoryFile.exists()) {
                directoryFile.mkdirs()
            }

            File(directoryFile, filename).apply {
                if (!exists()) {
                    createNewFile()
                }
            }
        } else {
            File(directoryFile, filename)
        }
    }
}