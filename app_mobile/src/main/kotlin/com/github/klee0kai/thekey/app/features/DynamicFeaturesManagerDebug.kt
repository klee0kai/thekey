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
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

class DynamicFeaturesManagerDebug : DynamicFeaturesManager {

    private val BUF_SIZE = 4096
    private val manager = SplitInstallManagerFactory.create(DI.ctx());
    override val installedFeatures = MutableStateFlow<List<DynamicFeature>>(emptyList())
    private val packageInstaller: PackageInstaller = DI.ctx().packageManager.packageInstaller

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("install receive ${intent}")

            val sessionId = intent?.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, 0) ?: return
            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, 0)
            val pkgName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)

            val userConfirmIntent: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.extras?.getParcelable(Intent.EXTRA_INTENT, Intent::class.java)
            } else {
                intent.extras?.getParcelable(Intent.EXTRA_INTENT)
            }
            Timber.d("install $sessionId status is $status modules $pkgName intent $userConfirmIntent")
        }
    }

    init {
        ContextCompat.registerReceiver(DI.ctx(), receiver, IntentFilter(), ContextCompat.RECEIVER_NOT_EXPORTED)
        updateInstalledFeatures()
    }

    override fun install(feature: DynamicFeature) {
        Timber.d("install ${feature.moduleName} from ${findModuleApk(feature.moduleName)}")
        val apk = findModuleApk(feature.moduleName) ?: return
        val sessionParams = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_INHERIT_EXISTING)

        sessionParams.setAppPackageName(DI.ctx().packageName)
        sessionParams.setSize(apk.length())

        val sesID: Int = packageInstaller.createSession(sessionParams)
        val session: PackageInstaller.Session = packageInstaller.openSession(sesID)

        val buffer = ByteArray(BUF_SIZE)
        val fileSize: Long = apk.length()
        val outStream = session.openWrite(apk.getName(), 0, fileSize)
        val fileInputStream = FileInputStream(apk)
        var bytesRead: Int
        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
            outStream.write(buffer, 0, bytesRead)
        }
        session.fsync(outStream)
        outStream.close()

        Timber.d("success install ${feature.moduleName} in $sesID")

        val intent = Intent(DI.ctx(), receiver.javaClass)
        session.commit(PendingIntent.getBroadcast(DI.ctx(), sesID, intent, PendingIntent.FLAG_IMMUTABLE).intentSender)
        session.close()
    }

    private fun updateInstalledFeatures() {
        val installed = manager.installedModules
        installedFeatures.value = DynamicFeature.allFeatures()
            .filter { it.moduleName in installed }
    }

    private fun findModuleApk(moduleName: String): File? {
        // before run ./gradlew installDebug
        return File("/data/local/tmp/tkey_features")
            .walk(FileWalkDirection.TOP_DOWN)
            .find { file -> file.name.contains(moduleName) && file.name.endsWith(".apk") }
    }

}