module org.goffi.core {
    requires org.bouncycastle.provider;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires org.apache.logging.log4j;

    exports org.goffi.core.concurrent;
    exports org.goffi.core.domainmodel;
    exports org.goffi.core.domainmodel.crypto;
    exports org.goffi.core.domainmodel.crypto.exceptions;
    exports org.goffi.core.domainmodel.crypto.impl;
    exports org.goffi.core.domainmodel.crypto.impl.v2;
    exports org.goffi.core.domainmodel.exceptions;
    exports org.goffi.core.domainmodel.text;
}