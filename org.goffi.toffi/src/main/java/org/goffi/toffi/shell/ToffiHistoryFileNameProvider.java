package org.goffi.toffi.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ToffiHistoryFileNameProvider extends DefaultHistoryFileNameProvider {

    @Override
    public String getHistoryFileName() {
        return "toffi-history.log";
    }

    @Override
    public String getProviderName() {
        return ToffiHistoryFileNameProvider.class.getName();
    }
}
