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
package com.github.lburgazzoli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
     * @param data
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T decode(String data,Class<T> type) {
        T rval = null;

        try {
            LOGGER.debug("JsonDecode {}=<{}>",type.getName(),data);
            rval = MAPPER.readValue(data, type);
        } catch (IOException e) {
            LOGGER.warn("JsonDecode - Exception",e);
        }

        return null;
    }

    /**
     *
     * @param data
     * @return
     */
    public static String encode(Object data) {
        String jsonData = null;

        try {
            jsonData = MAPPER.writeValueAsString(data);
            LOGGER.debug("JsonDecode {}=<{}>",data.getClass().getName(),jsonData);
        } catch (IOException e) {
            LOGGER.warn("JsonEncode - Exception",e);
        }

        return jsonData;
    }

}
