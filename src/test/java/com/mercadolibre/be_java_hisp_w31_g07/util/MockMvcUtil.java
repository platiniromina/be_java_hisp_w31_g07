package com.mercadolibre.be_java_hisp_w31_g07.util;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class MockMvcUtil {

    private final MockMvc mockMvc;

    public MockMvcUtil(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions performGetRequest(MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request).andDo(print());
    }

    public ResultActions performGet(String pathTemplate, Map<String, String> pathVars, Map<String, String> queryParams) throws Exception {
        String path = pathTemplate;

        for (Map.Entry<String, String> entry : pathVars.entrySet()) {
            path = path.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        MockHttpServletRequestBuilder request = get(path);
        queryParams.forEach(request::param);

        return performGetRequest(request);
    }

    public ResultActions performGet(String pathTemplate, Map<String, String> pathVars) throws Exception {
        return performGet(pathTemplate, pathVars, Map.of())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions performGet(String pathTemplate) throws Exception {
        return performGet(pathTemplate, Map.of(), Map.of())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions performPostRequest(String pathTemplate, Map<String, String> pathVars, Object content)
            throws Exception {
        String path = pathTemplate;

        for (Map.Entry<String, String> entry : pathVars.entrySet()) {
            path = path.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.generateFromDto(content)))
                .andDo(print());
    }

    public ResultActions performPutRequest(String pathTemplate, Map<String, String> pathVars, Object content)
            throws Exception {
        String path = pathTemplate;

        for (Map.Entry<String, String> entry : pathVars.entrySet()) {
            path = path.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return mockMvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.generateFromDto(content)))
                .andDo(print());
    }
}

