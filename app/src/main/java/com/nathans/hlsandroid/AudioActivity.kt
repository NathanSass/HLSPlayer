package com.nathans.hlsandroid

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.FilteringManifestParser
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser
import com.google.android.exoplayer2.source.hls.playlist.RenditionKey
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util

class AudioActivity : AppCompatActivity() {

    private val userAgent: String by lazy { Util.getUserAgent(this, getString(R.string.app_name)) }

    private val exoPlayer: SimpleExoPlayer by lazy {
        val renderersFactory = DefaultRenderersFactory(this,
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        val trackSelector = DefaultTrackSelector()

        return@lazy ExoPlayerFactory.newSimpleInstance(
                renderersFactory,
                trackSelector)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaSource: MediaSource = constructMediaSource()
        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = true
    }

    private fun buildDataSourceFactory(): DataSource.Factory {
        val httpDataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory(userAgent)
        val upstreamFactory = DefaultDataSourceFactory(this, null, httpDataSourceFactory)
        val downloadCache = SimpleCache(externalCacheDir, NoOpCacheEvictor())
        return buildCacheReadOnlyDataSource(upstreamFactory, downloadCache)
    }

    private fun buildCacheReadOnlyDataSource(upstreamFactory: DefaultDataSourceFactory, cache: Cache): CacheDataSourceFactory {
        return CacheDataSourceFactory(
                cache,
                upstreamFactory,
                FileDataSourceFactory(),
                /* cacheWriteDataSinkFactory= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                /* eventListener= */ null)
    }

    private fun constructMediaSource(): MediaSource {
        val uri = Uri.parse("https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8")
        return HlsMediaSource.Factory(buildDataSourceFactory())
                .setPlaylistParser(
                        FilteringManifestParser<HlsPlaylist, RenditionKey>(
                                HlsPlaylistParser(), null))
                .createMediaSource(uri)
    }
}