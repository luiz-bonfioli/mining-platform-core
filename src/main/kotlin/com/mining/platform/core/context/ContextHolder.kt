package com.mining.platform.core.context

/**
 *
 * @author luiz.bonfioli
 */
object ContextHolder {
    private val CONTEXT = ThreadLocal<Context>()

	var context: Context?
        get() = CONTEXT.get()
        set(context) {
            CONTEXT.set(context)
        }

    fun clear() {
        CONTEXT.remove()
    }
}