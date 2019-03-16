module org.goffi.toffi {
    requires spring.core;
    requires spring.context;
    requires spring.beans;
    requires spring.shell;
    requires jline;
    requires org.apache.logging.log4j;
    requires org.apache.commons.codec;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires jackson.annotations;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.goffi.core;
    opens org.goffi.toffi to spring.core, spring.context, spring.beans;
    opens org.goffi.toffi.shell to spring.core, spring.context, spring.beans;
    opens org.goffi.toffi.shell.commands to spring.core, spring.context, spring.beans;
}