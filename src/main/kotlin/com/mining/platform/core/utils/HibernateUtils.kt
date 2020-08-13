package com.mining.platform.core.utils

import org.hibernate.Hibernate

/**
 *
 * @author luiz.bonfioli
 */
object HibernateUtils {
    /**
     *
     * @param proxy
     * @return
     */
    fun isNotNullAndInitialized(proxy: Any?): Boolean {
        return proxy != null && Hibernate.isInitialized(proxy)
    }

}