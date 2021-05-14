package org.geektimes.configuration.microprofile.config.servlet.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class ConfigServletRequestListener implements ServletRequestListener {

    private static final ThreadLocal<Config> configThreadLocal = new ThreadLocal<>();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ClassLoader classLoader = getClassLoader(sre.getServletRequest());
        Config config = getProviderConfig(classLoader);
        configThreadLocal.set(config);
    }

    private Config getProviderConfig(ClassLoader classLoader) {
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        return configProviderResolver.getConfig(classLoader);
    }

    private ClassLoader getClassLoader( ServletRequest request) {
        ServletContext servletContext = request.getServletContext();
        return servletContext.getClassLoader();
    }

    public static Config getConfig() {
        return configThreadLocal.get();
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // 防止 OOM
        configThreadLocal.remove();
    }

}
