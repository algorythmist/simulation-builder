package com.tecacet.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Helper class for loading properties from a configuration file to a bean
 */
public class PropertiesLoader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String filename;

    /**
     * Properties Loader for a given file
     *
     * @param filename filename with properties
     */
    public PropertiesLoader(String filename) {
        this.filename = filename;
    }

    /**
     * Read the properties from the file. If the names of the properties
     * correspond to fields in the bean, the property value is mapped to the
     * bean
     *
     * @param bean the target bean
     */
    public void readInputParameters(Object bean) {
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(new Parameters().properties()
                                .setFileName(filename)
                                .setThrowExceptionOnMissing(true));
        PropertiesConfiguration parameters;
        try {
            parameters = builder.getConfiguration();
        } catch (ConfigurationException ioe) {
            logger.warn("Failed to load parameters from file " + filename + ". Using defaults.");
            return;
        }
        Iterator<String> keys = parameters.getKeys();
        while (keys.hasNext()) {
            String property = keys.next();
            Object value = parameters.getProperty(property);
            try {
                BeanUtils.copyProperty(bean, property, value);
                //TODO this is silent if there is no setter - make it say something
                logger.debug("{} = {}", property, BeanUtils.getProperty(bean, property));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
