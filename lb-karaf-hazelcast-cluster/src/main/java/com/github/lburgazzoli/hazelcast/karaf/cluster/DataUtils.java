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
package com.github.lburgazzoli.hazelcast.karaf.cluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class DataUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataUtils.class);
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
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String,String> decode(String data) {
        Map<String,String> rval = Maps.newHashMap();
        if(StringUtils.isNotBlank(data)) {
            try {
                LOGGER.debug("JsonDecode {}=<{}>",Map.class.getName(),data);
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
    public static String encode(Map<String,String> data) {
        String jsonData = null;

        if(data != null) {
            try {
                jsonData = MAPPER.writeValueAsString(data);
                LOGGER.debug("JsonDecode {}=<{}>",data.getClass().getName(),jsonData);
            } catch (IOException e) {
                LOGGER.warn("JsonEncode - Exception",e);
            }
        }

        return jsonData;
    }

}
