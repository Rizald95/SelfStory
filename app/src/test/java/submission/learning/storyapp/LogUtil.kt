package submission.learning.storyapp

import android.util.Log
import org.mockito.Mockito

object LogUtil {
    fun mockLog() {
        Mockito.mockStatic(Log::class.java).use { mockedLog ->
            mockedLog.`when`<Any> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }
                .thenReturn(true)
        }
    }
}