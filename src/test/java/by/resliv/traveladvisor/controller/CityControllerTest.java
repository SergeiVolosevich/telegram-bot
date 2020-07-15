package by.resliv.traveladvisor.controller;

import by.resliv.traveladvisor.dto.CityDTO;
import by.resliv.traveladvisor.service.CityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityService cityService;

    @Test
    void getById_ValidId_ShouldReturnCity() throws Exception {
        CityDTO city = CityDTO.builder().id(1L).name("Minsk").description("Minsk city").build();
        when(cityService.getById(anyLong())).thenReturn(city);
        mockMvc.perform(get("/cities/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Minsk")))
                .andExpect(jsonPath("$.description", is("Minsk city")));
        verify(cityService, times(1)).getById(1L);
        verifyNoMoreInteractions(cityService);
    }

    @Test
    void getById_InvalidId_ShouldReturnCityStatusCode400() throws Exception {
        mockMvc.perform(get("/cities/{id}", "not a number"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("Bad Request")))
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages.[0]", is("Invalid request")))
                .andExpect(jsonPath("$.path", is("uri=/cities/not%20a%20number")));
        verifyNoInteractions(cityService);
    }

    @Test
    void save_ValidRequestBody_ShouldReturnSavedCity() throws Exception {
        CityDTO inputCity = CityDTO.builder().name("Minsk").description("A very beautiful city with a rich history." +
                " You can take a walk in numerous parks or go to a restaurant and try dishes from national cuisine")
                .build();
        CityDTO outputCity = CityDTO.builder().id(1L).name("Minsk").description("A very beautiful city with a rich " +
                "history. You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                "cuisine").build();

        when(cityService.save(inputCity)).thenReturn(outputCity);

        mockMvc.perform(post("/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(inputCity)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/cities/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Minsk")))
                .andExpect(jsonPath("$.description", is("A very beautiful city with a rich history." +
                        " You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                        "cuisine")));
        ArgumentCaptor<CityDTO> captor = ArgumentCaptor.forClass(CityDTO.class);
        verify(cityService, times(1)).save(captor.capture());
        verifyNoMoreInteractions(cityService);
        CityDTO captureNews = captor.getValue();
        assertNull(captureNews.getId());
    }

    @Test
    void save_InvalidRequestBody_ShouldReturnHttpStatus400() throws Exception {
        CityDTO inputCity = CityDTO.builder().name("M").description("A very beautiful city with a rich history.")
                .build();

        mockMvc.perform(post("/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(inputCity)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Bad Request")))
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages.[*]", containsInAnyOrder("name: Please provide a " +
                        "correct value for city name.", "description: Length of full text of news must be between 50" +
                        " - 2000 characters")));
        verifyNoInteractions(cityService);
    }

    @Test
    void save_InvalidJsonFormat_ShouldReturnHttpStatus400() throws Exception {
        String json = "{\n" +
                "\t\"id\":\"not a number\",\n" +
                "\t\"name\":\"Name-test\",\n" +
                "\t\"description\":\"Description-test\"\n" +
                "}";

        mockMvc.perform(post("/cities")
                .content(json.getBytes())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("Bad Request")))
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages.[0]", is("Malformed JSON request")));
        verifyNoInteractions(cityService);
    }

    @Test
    void update_ValidRequestBody_ShouldReturnUpdatedCity() throws Exception {
        CityDTO inputCity = CityDTO.builder().id(1L).name("Minsk").description("A very beautiful city with a rich " +
                "history. You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                "cuisine")
                .build();
        CityDTO outputCity = CityDTO.builder().id(1L).name("Minsk").description("A very beautiful city with a rich " +
                "history. You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                "cuisine")
                .build();

        when(cityService.update(inputCity)).thenReturn(outputCity);

        mockMvc.perform(put("/cities/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(inputCity)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Minsk")))
                .andExpect(jsonPath("$.description", is("A very beautiful city with a rich history." +
                        " You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                        "cuisine")));
        ArgumentCaptor<CityDTO> captor = ArgumentCaptor.forClass(CityDTO.class);
        verify(cityService, times(1)).update(captor.capture());
        verifyNoMoreInteractions(cityService);
        CityDTO captureNews = captor.getValue();
        assertEquals(1L, captureNews.getId().longValue());
    }

    @Test
    void update_InvalidRequestBody_ShouldReturnHttpStatus400() throws Exception {
        CityDTO inputCity = CityDTO.builder().name("M").description("A very beautiful city with a rich history.")
                .build();

        mockMvc.perform(put("/cities/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(inputCity)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Bad Request")))
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.messages", hasSize(3)))
                .andExpect(jsonPath("$.messages.[*]", containsInAnyOrder("name: Please provide a " +
                        "correct value for city name.", "description: Length of full text of news must be between 50" +
                        " - 2000 characters", "id: Please provide a correct value for id")));
        verifyNoInteractions(cityService);
    }

    @Test
    void delete_ShouldReturnStatus204() throws Exception {
        long cityId = 1L;
        doNothing().when(cityService).delete(cityId);
        mockMvc.perform(delete("/cities/{id}", cityId))
                .andExpect(status().isNoContent());
        verify(cityService, times(1)).delete(cityId);
        verifyNoMoreInteractions(cityService);
    }

    @Test
    void findAll_ValidRequestParams_ShouldReturnListOfCities() throws Exception {
        CityDTO city1 = CityDTO.builder().id(1L).name("Minsk").description("A very beautiful city with a rich " +
                "history. You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                "cuisine")
                .build();
        CityDTO city2 = CityDTO.builder().id(2L).name("Moscow").description("A very beautiful city with a rich " +
                "history. You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                "cuisine").build();

        List<CityDTO> cities = new ArrayList<>();
        cities.add(city1);
        cities.add(city2);

        PageImpl<CityDTO> page = new PageImpl<>(cities, PageRequest.of(0, 10), 2);

        when(cityService.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(page);
        mockMvc.perform(get("/cities?page=0&size=10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*]", hasSize(2)));
        verify(cityService, times(1)).findAll(ArgumentMatchers.any(PageRequest.class));
        verifyNoMoreInteractions(cityService);
    }

    @Test
    void findAll_InvalidRequestParams_ShouldReturnStatus404() throws Exception {
        CityDTO city1 = CityDTO.builder().id(1L).name("Minsk").description("A very beautiful city with a rich " +
                "history. You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                "cuisine")
                .build();
        CityDTO city2 = CityDTO.builder().id(2L).name("Moscow").description("A very beautiful city with a rich " +
                "history. You can take a walk in numerous parks or go to a restaurant and try dishes from national " +
                "cuisine").build();

        List<CityDTO> cities = new ArrayList<>();
        cities.add(city1);
        cities.add(city2);

        PageImpl<CityDTO> page = new PageImpl<>(cities, PageRequest.of(0, 10), 2);

        when(cityService.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/cities?page=10&size=10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("Not Found")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages.[0]", is("Resource on page 10 does not exist.")));
        verify(cityService, times(1)).findAll(ArgumentMatchers.any(PageRequest.class));
        verifyNoMoreInteractions(cityService);
    }

    @Test
    void findAll_NoResultFound_ShouldReturnStatus204() throws Exception {
        List<CityDTO> cities = new ArrayList<>();
        PageImpl<CityDTO> page = new PageImpl<>(cities, PageRequest.of(0, 10), 2);

        when(cityService.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/cities?page=0&size=10"))
                .andExpect(status().isNoContent());
        verify(cityService, times(1)).findAll(ArgumentMatchers.any(PageRequest.class));
        verifyNoMoreInteractions(cityService);
    }
}