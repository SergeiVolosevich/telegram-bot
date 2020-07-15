package by.resliv.traveladvisor.service;

import by.resliv.traveladvisor.dto.CityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CityService extends Service<CityDTO> {
    List<CityDTO> findCityByName(String cityName);

    Page<CityDTO> findAll(Pageable pageable);
}
