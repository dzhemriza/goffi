module org.goffi.my.vault {
    requires javafx.controls;
    requires miglayout.javafx;
    requires org.apache.logging.log4j;
    requires org.apache.commons.codec;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires jackson.annotations;
    requires org.goffi.core;
    requires org.goffi.fx.core;
    opens org.goffi.my.vault.gui to javafx.controls, javafx.graphics, javafx.base;
    opens org.goffi.my.vault.services to com.fasterxml.jackson.databind, javafx.base;
    opens org.goffi.my.vault.services.impl to com.fasterxml.jackson.databind;
    opens org.goffi.my.vault.model to com.fasterxml.jackson.databind;
}