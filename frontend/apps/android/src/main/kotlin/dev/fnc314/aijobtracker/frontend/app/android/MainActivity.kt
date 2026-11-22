package dev.fnc314.aijobtracker.frontend.app.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import dev.fnc314.aijobtracker.frontend.app.core.App
import dev.fnc314.aijobtracker.frontend.core.data.ApplicationContextHolder
import dev.fnc314.aijobtracker.frontend.core.data.platform.PlatformDeviceInfoProvider
import dev.fnc314.aijobtracker.frontend.core.data.source.NotificationRemoteDataSource
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ApplicationContextHolder.application = application
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        handleIncomingNotificationIntent(intent)

        setContent {
          App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingNotificationIntent(intent)
    }

    private fun handleIncomingNotificationIntent(intent: Intent?) {
        if (intent == null) return
        val proposalId = intent.getStringExtra(JobTrackerFirebaseMessagingService.EXTRA_PROPOSAL_ID)
        val route = intent.getStringExtra(JobTrackerFirebaseMessagingService.EXTRA_NOTIFICATION_ROUTE)

        if (route != null || proposalId != null) {
            lifecycleScope.launch {
                val platformDeviceInfoProvider: PlatformDeviceInfoProvider = getKoin().get()
                val notificationRemoteDataSource: NotificationRemoteDataSource = getKoin().get()
                val deviceInfo = platformDeviceInfoProvider.getDeviceInfo()
                notificationRemoteDataSource.sendHeartbeat(
                    deviceInfo = deviceInfo,
                    targetProposalId = proposalId,
                )
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
  App()
}