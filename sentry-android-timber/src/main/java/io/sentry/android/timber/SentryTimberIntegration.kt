package io.sentry.android.timber

import io.sentry.IHub
import io.sentry.ILogger
import io.sentry.Integration
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.android.timber.BuildConfig.SENTRY_TIMBER_SDK_NAME
import io.sentry.android.timber.BuildConfig.VERSION_NAME
import io.sentry.protocol.SdkVersion
import timber.log.Timber
import java.io.Closeable

/**
 * Sentry integration for Timber.
 */
class SentryTimberIntegration(
    val minEventLevel: SentryLevel = SentryLevel.ERROR,
    val minBreadcrumbLevel: SentryLevel = SentryLevel.INFO
) : Integration, Closeable {
    private lateinit var tree: SentryTimberTree
    private lateinit var logger: ILogger

    override fun register(hub: IHub, options: SentryOptions) {
        createSdkVersion(options)
        logger = options.logger

        tree = SentryTimberTree(hub, minEventLevel, minBreadcrumbLevel)
        Timber.plant(tree)

        logger.log(SentryLevel.DEBUG, "SentryTimberIntegration installed.")
    }

    override fun close() {
        if (this::tree.isInitialized) {
            Timber.uproot(tree)

            if (this::logger.isInitialized) {
                logger.log(SentryLevel.DEBUG, "SentryTimberIntegration removed.")
            }
        }
    }

    private fun createSdkVersion(options: SentryOptions): SdkVersion {
        var sdkVersion = options.sdkVersion

        val name = SENTRY_TIMBER_SDK_NAME
        val version = VERSION_NAME
        sdkVersion = SdkVersion.updateSdkVersion(sdkVersion, name, version)

        sdkVersion.addPackage("maven:io.sentry:sentry-android-timber", VERSION_NAME)

        return sdkVersion
    }
}
