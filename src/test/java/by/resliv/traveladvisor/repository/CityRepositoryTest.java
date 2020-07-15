package by.resliv.traveladvisor.repository;

import by.resliv.traveladvisor.entity.City;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles("dev")
class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    void findByNameIgnoreCaseContaining_ValidName_ShouldReturnCity() {
        List<City> cities = cityRepository.findByNameContainingIgnoreCase("минск");
        assertEquals(1, cities.size());
        City city = cities.get(0);
        assertEquals(1, city.getId());
        assertEquals("Минск", city.getName());
    }

    @Test
    void findByNameIgnoreCaseContaining_ValidName_ShouldReturnList() {
        List<City> cities = cityRepository.findByNameContainingIgnoreCase("м");
        assertEquals(5, cities.size());
    }

    @Test
    void findByNameIgnoreCaseContaining_InvalidName_ShouldThrowException() {
        List<City> cities = cityRepository.findByNameContainingIgnoreCase("123");
        assertEquals(0, cities.size());
    }

    @Test
    void findByNameIgnoreCase_ValidName_ShouldReturnCity() {
        City city = cityRepository.findByNameIgnoreCase("минск");
        assertNotNull(city);
    }

    @Test
    void findByNameIgnoreCase_InvalidName_ShouldReturnCity() {
        City city = cityRepository.findByNameIgnoreCase("123");
        assertNull(city);
    }
}