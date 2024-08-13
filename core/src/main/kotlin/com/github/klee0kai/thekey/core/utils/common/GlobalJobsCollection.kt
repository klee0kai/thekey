package com.github.klee0kai.thekey.core.utils.common

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize

object GlobalJobsCollection {

    val globalJobs = MutableStateFlow(emptyList<GlobalJob>())

    inline fun <R> trackJob(
        @StringRes descriptionRes: Int = 0,
        block: () -> R,
    ): R {
        val runId = Dummy.dummyId
        try {
            if (descriptionRes != 0) {
                globalJobs.update { jobs ->
                    jobs + listOf(
                        GlobalJob(
                            unicId = runId,
                            descriptionRes = descriptionRes
                        )
                    )
                }
            }
            return block()
        } finally {
            if (descriptionRes != 0) {
                globalJobs.update { jobs ->
                    jobs.filter { it.unicId != runId }
                }
            }
        }
    }

}

@Parcelize
data class GlobalJob(
    val unicId: Long = 0,
    val descriptionRes: Int = 0,
) : Parcelable