package com.tecacet.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for loading properties from a configuration file to a bean
 * 
 */
public class PropertiesLoader {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String filename;

    /**
     * Properties Loader for a given file
     * 
     * @param filename
     */
    public PropertiesLoader(String filename) {
        this.filename = filename;
    }

    /**
     * Read the properties from the file. If the names of the properties
     * correspond to fields in the bean, the property value is mapped to the
     * bean
     * 
     * @param bean
     *            the target bean
     */
    public void readInputParameters(Object bean) {
        PropertiesConfiguration parameters = new PropertiesConfiguration();
        try {
            parameters.load(filename);
        } catch (ConfigurationException ioe) {
            logger.warn("Failed to load parameters from file " + filename + ". Using defaults.");
        }
        Iterator<String> keys = parameters.getKeys();
        while (keys.hasNext()) {
            String property = keys.next();
            Object value = parameters.getProperty(property);
            try {
                BeanUtils.copyProperty(bean, property, value);
                //TODO this is silent if there is no setter - make it say something
                logger.debug(property + " = " + BeanUtils.getProperty(bean, property));
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
