/*
 * Copyright (C) 2011-2015 GUIGUI Simon, fyhertz@gmail.com
 *
 * This file is part of libstreaming (https://github.com/fyhertz/libstreaming)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.majorkernelpanic.streaming

import android.content.Context
import android.hardware.Camera.CameraInfo
import android.preference.PreferenceManager
import net.majorkernelpanic.streaming.audio.AACStream
import net.majorkernelpanic.streaming.audio.AMRNBStream
import net.majorkernelpanic.streaming.audio.AudioQuality
import net.majorkernelpanic.streaming.gl.SurfaceView
import net.majorkernelpanic.streaming.video.H263Stream
import net.majorkernelpanic.streaming.video.H264Stream
import net.majorkernelpanic.streaming.video.VideoQuality

/**
 * Call [.getInstance] to get access to the SessionBuilder.
 */
class SessionBuilder private constructor() { // Removes the default public constructor
    /** Returns the VideoQuality set with [.setVideoQuality].  */
    // Default configuration
    var videoQuality = VideoQuality.DEFAULT_VIDEO_QUALITY
        private set

    /** Returns the AudioQuality set with [.setAudioQuality].  */
    var audioQuality = AudioQuality.DEFAULT_AUDIO_QUALITY
        private set

    /** Returns the context set with [.setContext] */
    var context: Context? = null
        private set

    /** Returns the video encoder set with [.setVideoEncoder].  */
    var videoEncoder = VIDEO_H263
        private set

    /** Returns the audio encoder set with [.setAudioEncoder].  */
    var audioEncoder = AUDIO_AMRNB
        private set

    /** Returns the id of the [android.hardware.Camera] set with [.setCamera].  */
    var camera = CameraInfo.CAMERA_FACING_BACK
        private set

    /** Returns the time to live set with [.setTimeToLive].  */
    var timeToLive = 64
        private set
    private var mOrientation = 0

    /** Returns the flash state set with [.setFlashEnabled].  */
    var flashState = false
        private set

    /** Returns the SurfaceView set with [.setSurfaceView].  */
    var surfaceView: SurfaceView? = null
        private set

    /** Returns the origin ip address set with [.setOrigin].  */
    var origin: String? = null
        private set

    /** Returns the destination ip address set with [.setDestination].  */
    var destination: String? = null
        private set
    private var mCallback: Session.Callback? = null

    /**
     * Creates a new [Session].
     * @return The new Session
     */
    fun build(): Session {
        val session: Session
        session = Session()
        session.setOrigin(origin)
        session.destination = destination
        session.setTimeToLive(timeToLive)
        session.callback = mCallback
        when (audioEncoder) {
            AUDIO_AAC -> {
                val stream = AACStream()
                session.addAudioTrack(stream)
                if (context != null) stream.setPreferences(PreferenceManager.getDefaultSharedPreferences(context))
            }
            AUDIO_AMRNB -> session.addAudioTrack(AMRNBStream())
        }
        when (videoEncoder) {
            VIDEO_H263 -> session.addVideoTrack(H263Stream(camera))
            VIDEO_H264 -> {
                val stream = H264Stream(camera)
                if (context != null) stream.setPreferences(PreferenceManager.getDefaultSharedPreferences(context))
                session.addVideoTrack(stream)
            }
        }
        if (session.videoTrack != null) {
            val video = session.videoTrack
            video.flashState = flashState
            video.videoQuality = videoQuality
            video.setSurfaceView(surfaceView)
            video.setPreviewOrientation(mOrientation)
            video.setDestinationPorts(5006)
        }
        if (session.audioTrack != null) {
            val audio = session.audioTrack
            audio.audioQuality = audioQuality
            audio.setDestinationPorts(5004)
        }
        return session
    }

    /**
     * Access to the context is needed for the H264Stream class to store some stuff in the SharedPreferences.
     * Note that you should pass the Application context, not the context of an Activity.
     */
    fun setContext(context: Context?): SessionBuilder {
        this.context = context
        return this
    }

    /** Sets the destination of the session.  */
    fun setDestination(destination: String?): SessionBuilder {
        this.destination = destination
        return this
    }

    /** Sets the origin of the session. It appears in the SDP of the session.  */
    fun setOrigin(origin: String?): SessionBuilder {
        this.origin = origin
        return this
    }

    /** Sets the video stream quality.  */
    fun setVideoQuality(quality: VideoQuality): SessionBuilder {
        videoQuality = quality.clone()
        return this
    }

    /** Sets the audio encoder.  */
    fun setAudioEncoder(encoder: Int): SessionBuilder {
        audioEncoder = encoder
        return this
    }

    /** Sets the audio quality.  */
    fun setAudioQuality(quality: AudioQuality): SessionBuilder {
        audioQuality = quality.clone()
        return this
    }

    /** Sets the default video encoder.  */
    fun setVideoEncoder(encoder: Int): SessionBuilder {
        videoEncoder = encoder
        return this
    }

    fun setFlashEnabled(enabled: Boolean): SessionBuilder {
        flashState = enabled
        return this
    }

    fun setCamera(camera: Int): SessionBuilder {
        this.camera = camera
        return this
    }

    fun setTimeToLive(ttl: Int): SessionBuilder {
        timeToLive = ttl
        return this
    }

    /**
     * Sets the SurfaceView required to preview the video stream.
     */
    fun setSurfaceView(surfaceView: SurfaceView?): SessionBuilder {
        this.surfaceView = surfaceView
        return this
    }

    /**
     * Sets the orientation of the preview.
     * @param orientation The orientation of the preview
     */
    fun setPreviewOrientation(orientation: Int): SessionBuilder {
        mOrientation = orientation
        return this
    }

    fun setCallback(callback: Session.Callback?): SessionBuilder {
        mCallback = callback
        return this
    }

    /** Returns a new [SessionBuilder] with the same configuration.  */
    fun clone(): SessionBuilder {
        return SessionBuilder()
            .setDestination(destination)
            .setOrigin(origin)
            .setSurfaceView(surfaceView)
            .setPreviewOrientation(mOrientation)
            .setVideoQuality(videoQuality)
            .setVideoEncoder(videoEncoder)
            .setFlashEnabled(flashState)
            .setCamera(camera)
            .setTimeToLive(timeToLive)
            .setAudioEncoder(audioEncoder)
            .setAudioQuality(audioQuality)
            .setContext(context)
            .setCallback(mCallback)
    }

    companion object {
        const val TAG = "SessionBuilder"

        /** Can be used with [.setVideoEncoder].  */
        const val VIDEO_NONE = 0

        /** Can be used with [.setVideoEncoder].  */
        const val VIDEO_H264 = 1

        /** Can be used with [.setVideoEncoder].  */
        const val VIDEO_H263 = 2

        /** Can be used with [.setAudioEncoder].  */
        const val AUDIO_NONE = 0

        /** Can be used with [.setAudioEncoder].  */
        const val AUDIO_AMRNB = 3

        /** Can be used with [.setAudioEncoder].  */
        const val AUDIO_AAC = 5

        // The SessionManager implements the singleton pattern
        @Volatile
        private var sInstance: SessionBuilder? = null

        /**
         * Returns a reference to the [SessionBuilder].
         * @return The reference to the [SessionBuilder]
         */
        @JvmStatic
        val instance: SessionBuilder
            get() {
                if (sInstance == null) {
                    synchronized(SessionBuilder::class.java) {
                        if (sInstance == null) {
                            sInstance = SessionBuilder()
                        }
                    }
                }
                return sInstance!!
            }
    }
}