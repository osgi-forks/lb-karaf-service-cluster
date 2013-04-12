/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.karaf.hazelcast.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class JsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper MAPPER = setupMapper();

    /**
     *
     * @return
     */
    private static ObjectMapper setupMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

    /**
     *
     * @param path
     * @return
     */
    public static <K,V> Map<K,V> readFile(String path) {
        Map<K,V> rval = Maps.newHashMap();
        try {
            rval = MAPPER.readValue(new File(path),Map.class);
        } catch (IOException e) {
            LOGGER.warn("JsonDecode - Exception",e);
        }

        return rval;
    }

    /**
     *
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K,V> Map<K,V> decode(String data) {
        Map<K,V> rval = Maps.newHashMap();
        if(StringUtils.isNotBlank(data)) {
            try {
                rval = MAPPER.readValue(data,Map.class);
            } catch (IOException e) {
                LOGGER.warn("JsonDecode - Exception",e);
            }
        }

        return rval;
    }

    /**
     *
     * @param data
     * @return
     */
    public static <K,V> String encode(Map<K,V> data) {
        String jsonData = null;

        if(data != null) {
            try {
                jsonData = MAPPER.writeValueAsString(data);
            } catch (IOException e) {
                LOGGER.warn("JsonEncode - Exception",e);
            }
        }

        return jsonData;
    }

}
