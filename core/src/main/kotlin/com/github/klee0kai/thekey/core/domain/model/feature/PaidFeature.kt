package com.github.klee0kai.thekey.core.domain.model.feature

enum class PaidFeature {
    /**
     * Storage color group is unlimited.
     * For free account limit is PAID_GROUPS_LIMIT
     *
     * @see PaidLimits.PAID_GROUPS_LIMIT
     */
    UNLIMITED_STORAGE_GROUPS,

    /**
     * Only a limited number of favorite storages are displayed in the side navigation.
     * @see PaidLimits.PAID_FAVORITE_STORAGE_LIMITS
     */
    UNLIMITED_FAVORITE_STORAGES,

    /**
     * Only a limited number of loggined storages are displayed in the side navigation.
     *
     * @see PaidLimits.PAID_AUTHORIZED_STORAGE_LIMITS
     */
    UNLIMITED_AUTHORIZED_STORAGES,

    /**
     * Unlimited password history period
     */
    UNLIMITED_HIST_PERIOD;
}

object PaidLimits {

    /**
     * Only a limited number of color groups are available for a free account.
     */
    const val PAID_GROUPS_LIMIT = 3

    /**
     * Only a limited number of favorite storages are displayed in the side navigation.
     */
    const val PAID_FAVORITE_STORAGE_LIMITS = 3

    /**
     * Only a limited number of loggined storages are displayed in the side navigation.
     */
    const val PAID_AUTHORIZED_STORAGE_LIMITS = 3

}
