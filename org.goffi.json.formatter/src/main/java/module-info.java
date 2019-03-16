module org.goffi.json.formatter {
    requires javafx.controls;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires org.goffi.fx.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    opens org.goffi.json.formatter.gui to javafx.graphics;
}