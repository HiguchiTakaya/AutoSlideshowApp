package jp.techacademy.takaya.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.pm.PackageManager
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0
    var cnt = 0

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preference = PreferenceManager.getDefaultSharedPreferences(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        }

        Log.d("ANDROID", "CNT"+cnt.toString())

        start_button.setOnClickListener {
            val rnb0=object : Runnable{
                override fun run() {
                    mHandler.postDelayed(this,500)
                    Log.d("ANDROID", "test")
                }
            }
            mHandler.post(rnb0)

            val resolver = contentResolver
            val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null = 全項目）
                null, // フィルタ条件（null = フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート (nullソートなし）
            )


            if (cursor!!.moveToFirst()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
                Log.d("ANDROID", "URI : " + imageUri.toString())
                cursor.moveToNext()
//                val fieldIndex2 = cursor.getColumnIndex(MediaStore.Images.Media._ID)
//                val id2 = cursor.getLong(fieldIndex2)
//                val imageUri2 = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id2)
//                imageView.setImageURI(imageUri2)
//                Log.d("ANDROID", "URI : " + imageUri2.toString())


                if (mTimer == null){
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 0.1
                            cnt++
                            mHandler.post {
                                timer.text = cnt.toString()
                            }
                            Log.d("ANDROID", "CNT"+cnt.toString())

                        }
                    }, 5000, 5000) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定

                } else if(mTimer != null){
                    mTimer!!.cancel()
                    mTimer = null
                }
            }
            cursor.close()
        }

        pause_button.setOnClickListener {
            if (mTimer != null){
                mTimer!!.cancel()
                mTimer = null
            }
        }

        reset_button.setOnClickListener {
            mTimerSec = 0.0
            timer.text = String.format("%.1f", mTimerSec)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )


    if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }
        cursor.close()
    }


}