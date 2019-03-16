module org.goffi.text.encoder {
    requires javafx.controls;
    requires miglayout.javafx;
    requires org.apache.logging.log4j;
    requires org.apache.commons.codec;
    requires org.apache.commons.lang3;
    requires org.goffi.core;
    requires org.goffi.fx.core;
    opens org.goffi.text.encoder.gui to javafx.graphics;
}