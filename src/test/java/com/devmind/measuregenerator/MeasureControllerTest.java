package com.devmind.measuregenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.RequestDispatcher;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MeasureController.class)
public class MeasureControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getMeasuresShouldFailWhenStartGreaterThanEnd() throws Exception{
        this.mvc.perform(get("/measures")
                                                    .param("start", "2018-09-02T22:00:00.000Z")
                                                    .param("end", "2018-09-01T22:00:00.000Z")
                                                    .param("min", "1250000")
                                                    .param("max", "5250000")
                                                    .param("step", "3600"))
                .andExpect(status().isBadRequest())
                .andExpect(request().attribute(RequestDispatcher.ERROR_MESSAGE, "start must be less than end"));
    }

    @Test
    public void getMeasuresShouldFailWhenStepIsLessThanOne() throws Exception{
        this.mvc.perform(get("/measures")
                                 .param("start", "2018-09-01T22:00:00.000Z")
                                 .param("end", "2018-09-02T22:00:00.000Z")
                                 .param("min", "1250000")
                                 .param("max", "5250000")
                                 .param("step", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(request().attribute(RequestDispatcher.ERROR_MESSAGE, "step is a value > 0"));
    }

    @Test
    public void getMeasuresShouldFailWhenMaxIsLessThanMin() throws Exception{
        this.mvc.perform(get("/measures")
                                 .param("start", "2018-09-01T22:00:00.000Z")
                                 .param("end", "2018-09-02T22:00:00.000Z")
                                 .param("min", "7250000")
                                 .param("max", "5250000")
                                 .param("step", "3600"))
                .andExpect(status().isBadRequest())
                .andExpect(request().attribute(RequestDispatcher.ERROR_MESSAGE, "min must be less than max"));
    }

    @Test
    public void getMeasures() throws Exception{
        MvcResult result = this.mvc.perform(get("/measures")
                                 .param("start", "2018-09-01T22:00:00.000Z")
                                 .param("end", "2018-09-02T22:00:00.000Z")
                                 .param("min", "1250000")
                                 .param("max", "5250000")
                                 .param("step", "3600")
        ).andReturn();

        List<MeasureDTO> measures = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(),
                                                                         MeasureDTO[].class));

        assertThat(measures).hasSize(24);
    }


}