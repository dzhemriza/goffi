package org.goffi.toffi.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BannerProvider extends DefaultBannerProvider {

    @Override
    public String getBanner() {
        return " _         __  __ _ " +
                OsUtils.LINE_SEPARATOR +
                "| |       / _|/ _(_)" +
                OsUtils.LINE_SEPARATOR +
                "| |_ ___ | |_| |_ _ " +
                OsUtils.LINE_SEPARATOR +
                "| __/ _ \\|  _|  _| |" +
                OsUtils.LINE_SEPARATOR +
                "| || (_) | | | | | |" +
                OsUtils.LINE_SEPARATOR +
                " \\__\\___/|_| |_| |_|" +
                OsUtils.LINE_SEPARATOR;
    }

    @Override
    public String getWelcomeMessage() {
        return "Welcome please type help\n";
    }

    @Override
    public String getProviderName() {
        return "toffi";
    }
}
