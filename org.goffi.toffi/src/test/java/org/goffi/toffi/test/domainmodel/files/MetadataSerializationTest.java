package org.goffi.toffi.test.domainmodel.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.core.domainmodel.Constants;
import org.goffi.toffi.domainmodel.files.DirectoryMetadata;
import org.goffi.toffi.domainmodel.files.FileMetadata;
import org.junit.Assert;
import org.junit.Test;

public class MetadataSerializationTest {

    private static final Logger LOG = LogManager.getLogger(DirectoryEncoderTest.class);

    @Test
    public void testToJson1() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        DirectoryMetadata directoryMetadata = new DirectoryMetadata();
        directoryMetadata.setDirectoryRealName("test1");

        directoryMetadata.getFiles().add(
                FileMetadata.builder()
                        .fileMask("file1_marshmallow")
                        .fileRealName("file1_realname")
                        .lastModified(123)
                        .sha1Sum("123".getBytes(Constants.DEFAULT_CHARSET))
                        .build());
        directoryMetadata.getFiles().add(
                FileMetadata.builder()
                        .fileMask("file2_marshmallow")
                        .fileRealName("file2_realname")
                        .lastModified(124)
                        .sha1Sum("125".getBytes(Constants.DEFAULT_CHARSET))
                        .build());

        String json = objectMapper.writeValueAsString(directoryMetadata);

        LOG.debug("Before json serialization DirectoryMetadata object - " + directoryMetadata);
        LOG.debug("Json serialized string from DirectoryMetadata object - " + json);

        DirectoryMetadata directoryMetadata2 = objectMapper.readValue(json, DirectoryMetadata.class);
        LOG.debug("After json deserialization DirectoryMetadata object - " + directoryMetadata2);

        Assert.assertEquals(directoryMetadata, directoryMetadata2);
    }
}
