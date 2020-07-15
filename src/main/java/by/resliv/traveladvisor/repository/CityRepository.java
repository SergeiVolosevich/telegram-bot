package by.resliv.traveladvisor.repository;

import by.resliv.traveladvisor.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByNameContainingIgnoreCase(String cityName);
    City findByNameIgnoreCase(String cityName);
}
