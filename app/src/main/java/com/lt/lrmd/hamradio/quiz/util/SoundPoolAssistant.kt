package com.lt.lrmd.hamradio.quiz.util

import android.content.Context
import android.media.SoundPool
import com.lt.lrmd.hamradio.quiz.util.SoundPoolAssistant.VolumeMode
import android.util.SparseIntArray
import kotlin.Throws
import android.content.res.AssetFileDescriptor
import com.lt.lrmd.hamradio.quiz.util.SoundPoolAssistant
import android.media.AudioManager
import android.util.Log
import java.io.IOException
import java.util.LinkedHashMap

/**
 * Facade class to play sounds using a [MediaPlayer] (assets) or
 * [SoundPool] (resources)
 */
class SoundPoolAssistant(private val mContext: Context, maxStreams: Int, streamType: Int) {
    /**
     * Passed to ...
     */
    enum class VolumeMode {
        /**
         * Use the system volume settings.
         */
        SYSTEM,

        /**
         * Use the volume passed to [SoundPoolAssistant.setVolume].
         */
        FIXED,

        /**
         * Don't play any sounds.
         */
        MUTE
    }

    private val mSoundPool: SoundPool
    private var mVolumeMode = VolumeMode.SYSTEM
    private var mVolume = 0f
    private val mPathToSoundId: MutableMap<String, Int> = LinkedHashMap()
    private val mResIdToSoundId = SparseIntArray()
    fun setVolumeMode(mode: VolumeMode) {
        mVolumeMode = mode
    }

    fun setVolume(volume: Float) {
        require(!(volume < 0 || volume > 1)) { "volume must be >= 0 and <= 1" }
        mVolume = volume
    }

    /**
     *
     * @param assetFileName
     * @param reference
     */
    @Throws(IOException::class)
    fun load(path: String) {
        val afd = mContext.assets.openFd(path)
        mPathToSoundId[path] = mSoundPool.load(afd, 0)
    }

    /**
     *
     * @param resourceId
     * @param reference
     */
    fun load(resourceId: Int) {
        mResIdToSoundId.put(
            resourceId,
            mSoundPool.load(mContext, resourceId, 0)
        )
    }

    fun play(resId: Int) {
        val soundId = mResIdToSoundId[resId]
        soundId?.let { playSoundId(it) }
            ?: Log.w(
                TAG,
                "sound not loaded for resource id $resId"
            )
    }

    fun play(path: String) {
        val soundId = mPathToSoundId[path]
        soundId?.let { playSoundId(it) }
            ?: Log.w(TAG, "sound not loaded for path $path")
    }

    val isSoundEnabled: Boolean
        get() = actualVolume > 0

    private fun playSoundId(soundId: Int) {
        val vol = actualVolume
        if (vol != 0f) {
            mSoundPool.play(soundId, vol, vol, 0, 0, 1f)
        }
    }

    private val actualVolume: Float
        get() {
            if (mVolumeMode == VolumeMode.MUTE) return 0f
            if (mVolumeMode == VolumeMode.FIXED && mVolume >= 0f && mVolume <= 1f) return mVolume
            val am = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat() /
                    am.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        }

    companion object {
        private const val TAG = "SoundPoolAssistant"
    }

    init {
        mSoundPool = SoundPool(maxStreams, streamType, 0)
    }
}