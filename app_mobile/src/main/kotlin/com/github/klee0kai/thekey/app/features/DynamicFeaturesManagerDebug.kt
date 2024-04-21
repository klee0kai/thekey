package com.github.klee0kai.thekey.app.features

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.os.Build
import androidx.core.content.ContextCompat
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

class DynamicFeaturesManagerDebug : DynamicFeaturesManager {

    private val scope = DI.defaultThreadScope()
    private val installTracker = InstallTracker()
    private val manager = SplitInstallManagerFactory.create(DI.ctx());
    private val packageInstaller: PackageInstaller = DI.ctx().packageManager.packageInstaller
    private val router = DI.router()

    override val features = installTracker.features.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("install receive $intent")
            val sessionId = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, 0)
            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, 0)
            val statusMessage = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
            val pkgName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)

            val userConfirmIntent: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.extras?.getParcelable(Intent.EXTRA_INTENT, Intent::class.java)
            } else {
                intent.extras?.getParcelable(Intent.EXTRA_INTENT)
            }
            Timber.d("install $sessionId status is $status modules $pkgName intent $userConfirmIntent")
            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    if (userConfirmIntent != null) {
                        router.navigate(
                            userConfirmIntent
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                        Timber.d("request user action")
                    }
                }

                PackageInstaller.STATUS_SUCCESS -> {
                    installTracker.update()
                }

                PackageInstaller.STATUS_FAILURE,
                PackageInstaller.STATUS_FAILURE_BLOCKED,
                PackageInstaller.STATUS_FAILURE_ABORTED,
                PackageInstaller.STATUS_FAILURE_CONFLICT,
                PackageInstaller.STATUS_FAILURE_STORAGE,
                PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
                PackageInstaller.STATUS_FAILURE_TIMEOUT -> {
                    Timber.w("install failure $status - $statusMessage")
                }

                else -> {
                    Timber.e("non processed status $status")
                }
            }
        }
    }

    init {
        ContextCompat.registerReceiver(
            DI.ctx(),
            receiver,
            IntentFilter().apply {
                addAction(INSTALL_ACTION)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun install(feature: DynamicFeature) {
        Timber.d("install ${feature.moduleName} from ${findModuleApk(feature.moduleName)}")
        val apk = findModuleApk(feature.moduleName) ?: return
        val sessionParams = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_INHERIT_EXISTING)

        sessionParams.setAppPackageName(DI.ctx().packageName)
        sessionParams.setSize(apk.length())

        val sessionId = packageInstaller.createSession(sessionParams)
        val session = packageInstaller.openSession(sessionId)

        val buffer = ByteArray(BUF_SIZE)
        val outStream = session.openWrite(apk.getName(), 0, apk.length())
        val fileInputStream = FileInputStream(apk)
        var bytesRead = 0
        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
            outStream.write(buffer, 0, bytesRead)
        }
        session.fsync(outStream)
        outStream.close()

        Timber.d("commit install ${feature.moduleName} in $sessionId")
        session.commit(
            PendingIntent.getBroadcast(
                DI.ctx(),
                sessionId,
                Intent(INSTALL_ACTION),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            ).intentSender
        )

        session.close()
    }

    override fun uninstall(feature: DynamicFeature) {
        Timber.d("uninstall ${feature.moduleName} ")
        manager.deferredUninstall(listOf(feature.moduleName))
    }

    private fun findModuleApk(moduleName: String): File? {
        // before run ./gradlew installDebug
        return File("/data/local/tmp/tkey_features")
            .walk(FileWalkDirection.TOP_DOWN)
            .find { file -> file.name.contains(moduleName) && file.name.endsWith(".apk") }
    }

    companion object {
        private const val BUF_SIZE = 4096
        private const val INSTALL_ACTION = "DYNAMIC_FEATURE_INSTALL_ACTION"
    }

}