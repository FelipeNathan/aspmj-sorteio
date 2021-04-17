package com.aspmj.sorteio.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.UrlPathHelper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebMvc
class SpringWebConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/")
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/")
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/")
        registry.addResourceHandler("/locales/**").addResourceLocations("classpath:/static/locales/")
        registry.addResourceHandler("/webfonts/**").addResourceLocations("classpath:/static/webfonts/")
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/login").setViewName("login")
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(object : HandlerInterceptor {

            override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
                val requestUrl = UrlPathHelper().getLookupPathForRequest(request)

                return if (requestUrl == "/login" && isAuthenticated()) {
                    var urlRedirect = request.getHeader("Referer") ?: "/sorteios"
                    if (urlRedirect == requestUrl)
                        urlRedirect = "/sorteios"

                    response.status = HttpStatus.TEMPORARY_REDIRECT.value()
                    response.addHeader("Location", response.encodeRedirectURL(request.contextPath + urlRedirect))
                    false
                } else true
            }

            private fun isAuthenticated(): Boolean {
                val authentication = SecurityContextHolder.getContext().authentication
                return (authentication != null && authentication !is AnonymousAuthenticationToken && authentication.isAuthenticated)
            }
        })
    }
}