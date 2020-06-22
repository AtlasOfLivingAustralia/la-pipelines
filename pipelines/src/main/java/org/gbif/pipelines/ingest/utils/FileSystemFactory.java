package org.gbif.pipelines.ingest.utils;

import com.google.common.base.Strings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.File;
import java.net.URI;

@Slf4j
public class FileSystemFactory {

    private static final String HDFS_PREFIX = "hdfs://ha-nn";

    private static volatile FileSystemFactory instance;

    private final FileSystem localFs;
    private final FileSystem hdfsFs;

    private static final Object MUTEX = new Object();

    @SneakyThrows
    private FileSystemFactory(String hdfsSiteConfig, String hdfsPrefix) {
        if (!Strings.isNullOrEmpty(hdfsSiteConfig)) {

            Configuration configuration = getHdfsConfiguration(hdfsSiteConfig);
            String hdfsPrefixToUse = configuration.get("fs.default.name");
            if (hdfsPrefixToUse == null){
                hdfsPrefixToUse = configuration.get("fs.defaultFS");
            }

            if(hdfsPrefixToUse == null){
                hdfsFs = FileSystem.get(URI.create(hdfsPrefix), getHdfsConfiguration(hdfsSiteConfig));
            } else {
                hdfsFs = FileSystem.get(URI.create(hdfsPrefixToUse), getHdfsConfiguration(hdfsSiteConfig));
            }
        } else {
            hdfsFs = null;
        }
        localFs = FileSystem.get(getHdfsConfiguration(hdfsSiteConfig));
    }
//
//    @SneakyThrows
//    private FileSystemFactory(String hdfsSiteConfig, String coreSiteConfig, String hdfsPrefix) {
//        if (!Strings.isNullOrEmpty(hdfsSiteConfig)) {
//
//            Configuration configuration = getHdfsConfiguration(hdfsSiteConfig, coreSiteConfig);
//            String hdfsPrefixToUse = configuration.get("fs.default.name");
//            if (hdfsPrefixToUse == null){
//                hdfsPrefixToUse = configuration.get("fs.defaultFS");
//            }
//
//            hdfsFs = FileSystem.get(URI.create(hdfsPrefixToUse), configuration);
//        } else {
//            hdfsFs = null;
//        }
//        localFs = FileSystem.get(getHdfsConfiguration(hdfsSiteConfig, coreSiteConfig));
//    }

    public static FileSystemFactory getInstance(String hdfsSiteConfig, String hdfsPrefix) {
        if (instance == null) {
            synchronized (MUTEX) {
                if (instance == null) {
                    instance = new FileSystemFactory(hdfsSiteConfig, hdfsPrefix);
                }
            }
        }
        return instance;
    }

    /** Use predefined HDFS_PREFIX = "hdfs://ha-nn" */
    public static FileSystemFactory getInstance(String hdfsSiteConfig) {
        return getInstance(hdfsSiteConfig, HDFS_PREFIX);
    }

    public static FileSystemFactory create(String hdfsSiteConfig, String hdfsPrefix){
        return new FileSystemFactory(hdfsSiteConfig, hdfsPrefix);
    }

    /** Use predefined HDFS_PREFIX = "hdfs://ha-nn" */
    public static FileSystemFactory create(String hdfsSiteConfig){
        return new FileSystemFactory(hdfsSiteConfig, HDFS_PREFIX);
    }

    public FileSystem getFs(String path, String hdfsPrefix) {
        return path != null && path.startsWith(hdfsPrefix) ? hdfsFs : localFs;
    }

    /** Use predefined HDFS_PREFIX = "hdfs://ha-nn" */
    public FileSystem getFs(String path) {
        return getFs(path, HDFS_PREFIX);
    }

    public FileSystem getLocalFs() {
        return localFs;
    }

    public FileSystem getHdfsFs() {
        return hdfsFs;
    }

    /**
     * Creates an instances of a {@link Configuration} using a xml HDFS configuration file.
     *
     * @param hdfsSiteConfig path to the hdfs-site.xml or HDFS config file
     * @return a {@link Configuration} based on the provided config file
     */
    @SneakyThrows
    private static Configuration getHdfsConfiguration(String hdfsSiteConfig) {
        Configuration config = new Configuration();

        // check if the hdfs-site.xml is provided
        if (!Strings.isNullOrEmpty(hdfsSiteConfig)) {
            File hdfsSite = new File(hdfsSiteConfig);
            if (hdfsSite.exists() && hdfsSite.isFile()) {
                log.info("using hdfs-site.xml");
                config.addResource(hdfsSite.toURI().toURL());
            } else {
                log.warn("hdfs-site.xml does not exist");
            }
        } else {
            log.info("hdfs-site.xml not provided");
        }

//        if (!Strings.isNullOrEmpty(coreSiteConfig)) {
//            File coreSite = new File(coreSiteConfig);
//            if (coreSite.exists() && coreSite.isFile()) {
//                log.info("using core-site.xml");
//                config.addResource(coreSite.toURI().toURL());
//            } else {
//                log.warn("core-site.xml does not exist");
//            }
//        } else {
//            log.info("core-site.xml not provided");
//        }
        return config;
    }
}