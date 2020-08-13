package com.mining.platform.core.datasource.multitenant

import com.mining.platform.core.converter.UUIDConverter
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

/**
 * The MultiTenantFilter class will filter the HttpRequest to get tenant from
 * the request header and set it as request attribute
 *
 * @author luiz.bonfioli
 */
@Component
class MultiTenantFilter : Filter {
    /**
     * Method called when the filter is initialized
     */
    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
        // Method called when the filter is initialized
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val tenant = req.getHeader(MultiTenant.TENANT_KEY)
        val environment = req.getHeader(MultiTenant.ENVIRONMENT_KEY)
        if (tenant != null) {
            req.setAttribute(MultiTenant.TENANT_KEY, UUIDConverter.toUUID(tenant))
        } else {
            req.setAttribute(MultiTenant.TENANT_KEY, null)
        }
        if (environment != null) {
            req.setAttribute(MultiTenant.ENVIRONMENT_KEY, environment)
        } else {
            req.setAttribute(MultiTenant.ENVIRONMENT_KEY, null)
        }
        chain.doFilter(request, response)
    }

    override fun destroy() {
        // Method called when the filter has been destroyed
    }
}