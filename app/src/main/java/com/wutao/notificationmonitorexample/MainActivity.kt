package com.wutao.notificationmonitorexample

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.audiofx.EnvironmentalReverb
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var btnPermission:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPermission = findViewById<Button>(R.id.buttonPermissionRequest)
        var serviceIntent = Intent(this,NotificationMonitor::class.java)
        //启动服务
        startService(serviceIntent)
        if(isNotificationPermissionEnabled()){
            btnPermission.text = "已授权"
            btnPermission.setBackgroundColor(Color.GREEN)
        } else {
            btnPermission.text = "未授权"
            btnPermission.setBackgroundColor(Color.RED)
        }

        btnPermission.setOnClickListener(){
            if(!isNotificationPermissionEnabled()){
                showNotificationAccess()
            }
        }
    }
    //激活

    //显示通知使用权的对话框
    fun showNotificationAccess(){
        try {
            //Android 5 以上版本
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }else {
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun isNotificationPermissionEnabled():Boolean{
        var pkgName = packageName
        var flat = android.provider.Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        Log.i("===info===",flat)
        if(flat.isNotEmpty()){
            var names = flat.split(":")
            for(n in names){
                var cn = ComponentName.unflattenFromString(n)
                if(cn != null){
                    if(pkgName == cn.packageName){
                        return true
                    }
                }
            }
        }
        return false
    }

}

val ExcludePackages = arrayOf<String>("com.v2ray.ang")

public class NotificationMonitor:NotificationListenerService(){
    /**
     * 接收到通知消息
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        //super.onNotificationPosted(sbn)
        var extras = sbn?.notification?.extras
        //发生消息的包名
        var notificationPkg = sbn?.packageName
        //消息的标题
        var notificationTitle = extras?.getString(Notification.EXTRA_TITLE)
        //消息的内容
        var notificationText = extras?.getString(Notification.EXTRA_TEXT)
        //处理消息
        if(notificationPkg !in ExcludePackages)
        {
            Log.i("通知监听器","接收到消息：【$notificationTitle】$notificationText——来自包[$notificationPkg]。")
        }

    }

    /**
     * 通知消息被移除
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        //super.onNotificationRemoved(sbn)
        var extras = sbn?.notification?.extras
        //发生消息的包名
        var notificationPkg = sbn?.packageName
        //消息的标题
        var notificationTitle = extras?.getString(Notification.EXTRA_TITLE)
        //消息的内容
        var notificationText = extras?.getString(Notification.EXTRA_TEXT)
        //处理消息
        Log.i("通知监听器","被移除的通知：【$notificationTitle】$notificationText——来自包[$notificationPkg]。")
    }
}