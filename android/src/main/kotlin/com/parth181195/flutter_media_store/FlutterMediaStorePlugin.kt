package com.parth181195.flutter_media_store

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import androidx.annotation.NonNull

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

import android.content.Intent
import android.provider.MediaStore
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.util.Log
import java.security.MessageDigest
import java.util.*

/** FlutterMediaStorePlugin */
class FlutterMediaStorePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.parth181195")
        context = flutterPluginBinding.applicationContext
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "flutter_media_store") {

            if (android.os.Build.VERSION.SDK_INT >= 29) {
//                val path = call.arguments as String
                val image = call.arguments as ByteArray
                result.success(saveImageToGallery(BitmapFactory.decodeByteArray(image, 0, image.size)))
//                result.success(saveFileToGalleryViaIntent(path))


            } else {

                val image = call.arguments as ByteArray
                result.success(saveImageToGallery(BitmapFactory.decodeByteArray(image, 0, image.size)))
//                result.success(saveFileToGalleryViaIntent(path))

            }

        } else {
            result.notImplemented()
        }
    }

//
//    private fun generateFile(extension: String = ""): File {
//        try {
//            val storePath = Environment.DIRECTORY_PICTURES + File.separator + getApplicationName()
//            val appDir = File(storePath)
//            println(storePath)
//            if (!appDir.exists()) {
//                val created = appDir.mkdirs()
//                println(created)
//                println("creating Directory")
//            } else {
//                println("Directory exist")
//            }
//
//            var fileName = System.currentTimeMillis().toString()
//            if (extension.isNotEmpty()) {
//                fileName += ("." + extension)
//            }
//            return File(appDir, fileName)
//        } catch (e: SecurityException) {
//            e.printStackTrace()
//        }
//        return File()
//    }

    fun generateSSHKey(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.getEncoder().encode(md.digest()))
                Log.i("AppLog", "key:$hashKey=")
            }
        } catch (e: Exception) {
            Log.e("AppLog", "error:", e)
        }

    }
    private fun saveImageToGallery(bmp: Bitmap): String {


        try {
            val storePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_PICTURES
            val appDir = File(storePath)
            println(storePath)
            if (!appDir.exists()) {
                val created = appDir.mkdirs()
                println(created)
                println("creating Directory")
            } else {
                println("Directory exist")
            }

            var fileName = System.currentTimeMillis().toString()
            fileName += (".png")
            val file = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_PICTURES, fileName)
            println(file.absolutePath)
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            val uri = Uri.fromFile(file)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            return uri.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            println(e.message)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun saveFileToGalleryViaIntent(filePath: String): String {
        return try {
            val originalFile = File(filePath)
            val uri = Uri.fromFile(originalFile)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            return uri.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }

    private fun getApplicationName(): String {

        var ai: ApplicationInfo? = null
        try {
            ai = context.packageManager.getApplicationInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
        }
        var appName: String
        appName = if (ai != null) {
            val charSequence = context.packageManager.getApplicationLabel(ai)
            StringBuilder(charSequence.length).append(charSequence).toString()
        } else {
            "image_gallery_saver"
        }
        return appName
    }
//    private fun saveFileToGalleryViaStore(filePath: String): String {
//        return try {
//            // val values = ContentValues();
//            // values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//            // values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//            // values.put(MediaStore.MediaColumns.DATA, uri);
//            // context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values).toString();
//            return ""
//        } catch (e: IOException) {
//            e.printStackTrace()
//            return ""
//        }
//    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.getActivity();
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
