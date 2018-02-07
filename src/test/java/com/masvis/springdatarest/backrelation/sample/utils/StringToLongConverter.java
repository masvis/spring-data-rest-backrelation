package com.masvis.springdatarest.backrelation.sample.utils;

import org.springframework.core.convert.converter.Converter;

public class StringToLongConverter implements Converter<String, Long> {
    @Override
    public Long convert(String source) {
        int lastSlash = source.lastIndexOf('/');
        if (lastSlash != -1)
            source = source.substring(lastSlash + 1);
        return Long.parseLong(source);
    }
}
