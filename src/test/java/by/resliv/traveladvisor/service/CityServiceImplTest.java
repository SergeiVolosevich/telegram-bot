package by.resliv.traveladvisor.service;

import by.resliv.traveladvisor.dto.CityDTO;
import by.resliv.traveladvisor.entity.City;
import by.resliv.traveladvisor.exception.UniqueConstraintException;
import by.resliv.traveladvisor.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

    @InjectMocks
    private CityServiceImpl cityService;

    @Mock
    private CityRepository repository;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        cityService = new CityServiceImpl(repository, modelMapper);
    }

    @Test
    void findCityByName_ValidName_ShouldReturnCity() {
        List<City> cities = new ArrayList<>();

        cities.add(City.builder().id(1L).name("Минск").description("City").build());

        when(repository.findByNameContainingIgnoreCase(anyString())).thenReturn(cities);
        List<CityDTO> foundCities = cityService.findCityByName("Минск");

        assertEquals(1, foundCities.size());
        CityDTO city = foundCities.get(0);
        assertEquals(1L, city.getId());
        verify(repository, times(1)).findByNameContainingIgnoreCase(anyString());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll_PageRequest_ShouldReturnList() {
        List<City> cities = new ArrayList<>();

        cities.add(City.builder().id(1L).name("Minsk").description("One of the most beautiful cities").build());
        cities.add(City.builder().id(2L).name("Moscow").description("A city with a rich history").build());
        cities.add(City.builder().id(3L).name("Paris").description("City of Romantics").build());

        PageImpl<City> cityPage = new PageImpl<>(cities);

        when(repository.findAll(any(PageRequest.class))).thenReturn(cityPage);

        Page<CityDTO> foundCities = cityService.findAll(PageRequest.of(0, 3));

        assertEquals(3, foundCities.getContent().size());
        verify(repository, times(1)).findAll(any(PageRequest.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save_ValidCityDTO_ShouldReturnCity() {
        City city = City.builder().id(1L).name("Minsk").description("One of the most beautiful cities").build();

        when(repository.save(any(City.class))).thenReturn(city);
        when(repository.findByNameIgnoreCase("Minsk")).thenReturn(null);

        CityDTO savedCity = cityService.save(CityDTO.builder().name("Minsk")
                .description("One of the most beautiful cities").build());

        assertEquals(1L, savedCity.getId());
        assertEquals("Minsk", savedCity.getName());
        verify(repository, times(1)).save(any(City.class));
        verify(repository, times(1)).findByNameIgnoreCase("Minsk");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save_InvalidCity_ShouldThrowException() {
        CityDTO cityToSave = CityDTO.builder().name("Minsk").description("One of the most beautiful cities").build();
        City city = City.builder().id(1L).name("Minsk").description("One of the most beautiful cities").build();
        when(repository.findByNameIgnoreCase("Minsk")).thenReturn(city);

        assertThrows(UniqueConstraintException.class, () -> cityService.save(cityToSave));
        verify(repository, times(1)).findByNameIgnoreCase("Minsk");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update_ValidCityDTO_ShouldReturnCity() {
        City city = City.builder().id(1L).name("Minsk").description("One of the most beautiful cities").build();

        when(repository.save(any(City.class))).thenReturn(city);
        when(repository.findById(1L)).thenReturn(Optional.of(city));

        CityDTO updatedCity = cityService.update(CityDTO.builder().id(1L).name("Minsk")
                .description("One of the most beautiful cities").build());

        assertEquals(1L, updatedCity.getId());
        assertEquals("Minsk", updatedCity.getName());
        verify(repository, times(1)).save(any(City.class));
        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update_CityNotExist_ShouldThrowException() {
        CityDTO cityToSave = CityDTO.builder().id(1L).name("Minsk")
                .description("One of the most beautiful cities").build();

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cityService.update(cityToSave));
        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update_NotUniqueCityName_ShouldThrowException() {
        City city = City.builder().id(1L).name("Minsk").description("One of the most beautiful cities").build();
        CityDTO cityToSave = CityDTO.builder().id(1L).name("Moscow")
                .description("One of the most beautiful cities").build();

        when(repository.findById(1L)).thenReturn(Optional.of(city));
        when(repository.findByNameIgnoreCase("Moscow")).thenReturn(city);

        assertThrows(UniqueConstraintException.class, () -> cityService.update(cityToSave));
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).findByNameIgnoreCase("Moscow");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete_ShouldPass() {
        doNothing().when(repository).deleteById(1L);

        cityService.delete(1L);

        verify(repository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getById_CityExist_ShouldReturnCity() {
        City city = City.builder().id(1L).name("Minsk").description("One of the most beautiful cities").build();

        when(repository.findById(1L)).thenReturn(Optional.of(city));

        CityDTO foundCity = cityService.getById(1L);

        assertEquals(1L, foundCity.getId());
        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getById_CityNotExist_ShouldThrowException() {
        when(repository.findById(500L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> cityService.getById(500L));
        verify(repository, times(1)).findById(500L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll_ShouldReturnList() {
        List<City> cities = new ArrayList<>();

        cities.add(City.builder().id(1L).name("Minsk").description("One of the most beautiful cities").build());
        cities.add(City.builder().id(2L).name("Moscow").description("A city with a rich history").build());
        cities.add(City.builder().id(3L).name("Paris").description("City of Romantics").build());


        when(repository.findAll()).thenReturn(cities);
        List<CityDTO> foundCities = cityService.findAll();

        assertEquals(3, foundCities.size());
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }
}