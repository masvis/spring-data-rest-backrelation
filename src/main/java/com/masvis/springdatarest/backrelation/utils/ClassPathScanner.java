package com.masvis.springdatarest.backrelation.utils;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

/**
 * A simple class scanner, used to get indipendent classes in a package.
 *
 * @author Ruggero Russo
 * @author Sante Stanisci
 */
public class ClassPathScanner extends ClassPathScanningCandidateComponentProvider {
    /*
     * (non-Javadoc)
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#ClassPathScanningCandidateComponentProvider(boolean)
     */
    ClassPathScanner(final boolean useDefaultFilters) {
        super(useDefaultFilters);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#isCandidateComponent(org.springframework.beans.factory.annotation.AnnotatedBeanDefinition)
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent();
    }
}