package com.example.service

import android.content.Context
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.IRtcEngineEventHandler

class AgoraManager(context: Context, appId: String) {
    private val rtcEngine: RtcEngine

    init {
        val config = RtcEngineConfig()
        config.mContext = context
        config.mAppId = appId
        config.mEventHandler = object : IRtcEngineEventHandler() {
            // Handle events here
        }
        rtcEngine = RtcEngine.create(config)
    }

    fun joinChannel(token: String, channelName: String, uid: Int) {
        rtcEngine.joinChannel(token, channelName, null, uid)
    }

    fun leaveChannel() {
        rtcEngine.leaveChannel()
    }

    fun destroy() {
        RtcEngine.destroy()
    }
}
