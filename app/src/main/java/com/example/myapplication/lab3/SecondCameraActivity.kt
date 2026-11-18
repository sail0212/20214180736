package com.example.myapplication.lab3

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySecondCameraBinding
import java.io.File

/**
 * 第3个二级Activity：采用隐式 Intent（MediaStore.ACTION_IMAGE_CAPTURE）启动系统相机拍照，
 * 拍照成功后把照片显示在本页 ImageView 中。
 * 相机照片通过 FileProvider 提供的 content:// Uri 保存，兼容 Android 7.0+。
 */
class SecondCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondCameraBinding
    private var photoUri: Uri? = null

    // 运行时申请相机权限
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                takePhoto()
            } else {
                Toast.makeText(this, R.string.s3_permission_denied, Toast.LENGTH_SHORT).show()
            }
        }

    // 调用系统相机拍照，成功后把 Uri 对应的图片显示出来
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                binding.ivPhoto.setImageURI(photoUri)
            } else {
                Toast.makeText(this, R.string.s3_capture_failed, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.s3_title)

        // 横竖屏旋转后恢复上次拍摄的照片
        if (savedInstanceState != null) {
            photoUri = savedInstanceState.getString(KEY_PHOTO_URI)?.let { Uri.parse(it) }
            photoUri?.let { binding.ivPhoto.setImageURI(it) }
        }

        binding.btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                takePhoto()
            } else {
                requestPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takePhoto() {
        // 用 MediaStore 创建一张空图片，拿到 content Uri 交给相机写入
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, getString(R.string.s3_image_desc))
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        photoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        // 兼容方案：MediaStore 不可用时回退到应用缓存目录 + FileProvider
        if (photoUri == null) {
            val file = File(cacheDir, "capture_${System.currentTimeMillis()}.jpg")
            photoUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        }

        val intent = android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            // 授予相机应用对 content Uri 的临时写权限
            addFlags(android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        if (intent.resolveActivity(packageManager) != null) {
            takePicture.launch(photoUri!!)
        } else {
            Toast.makeText(this, R.string.s3_no_camera, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        photoUri?.let { outState.putString(KEY_PHOTO_URI, it.toString()) }
    }

    companion object {
        private const val KEY_PHOTO_URI = "key_photo_uri"
    }
}
