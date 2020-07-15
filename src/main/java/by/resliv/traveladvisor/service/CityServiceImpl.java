package by.resliv.traveladvisor.service;

import by.resliv.traveladvisor.ApplicationConstants;
import by.resliv.traveladvisor.dto.CityDTO;
import by.resliv.traveladvisor.entity.City;
import by.resliv.traveladvisor.exception.UniqueConstraintException;
import by.resliv.traveladvisor.repository.CityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
@Transactional
public class CityServiceImpl implements CityService {

    private static final String CITY = "City";
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CityDTO> findCityByName(String cityName) {
        List<City> foundCities = cityRepository.findByNameContainingIgnoreCase(cityName);
        return foundCities.stream()
                .map(element -> modelMapper.map(element, CityDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<CityDTO> findAll(Pageable pageable) {
        Page<City> resultPage = cityRepository.findAll(pageable);
        return resultPage.map(city -> modelMapper.map(city, CityDTO.class));
    }

    @Override
    public CityDTO save(CityDTO cityDTO) {
        City city = modelMapper.map(cityDTO, City.class);
        checkUniqueCityName(city);
        City savedCity = cityRepository.save(city);
        return modelMapper.map(savedCity, CityDTO.class);
    }

    private void checkUniqueCityName(City city) {
        String name = city.getName();
        City foundCity = cityRepository.findByNameIgnoreCase(name);
        if (Objects.nonNull(foundCity)) {
            String msg = MessageFormat.format(ApplicationConstants.UNIQUE_KEY_CONSTRAINT_TEMPLATE, "City",
                    "name - " + name);
            throw new UniqueConstraintException(msg);
        }
    }

    @Override
    public CityDTO update(CityDTO cityDTO) {
        City city = modelMapper.map(cityDTO, City.class);
        CityDTO foundCity = getById(city.getId());
        if (!foundCity.getName().equals(city.getName())) {
            checkUniqueCityName(city);
        }
        City updatedCity = cityRepository.save(city);
        return modelMapper.map(updatedCity, CityDTO.class);
    }

    @Override
    public void delete(long id) {
        cityRepository.deleteById(id);
    }

    @Override
    public CityDTO getById(long id) {
        return cityRepository.findById(id)
                .map(optionalCity -> modelMapper.map(optionalCity, CityDTO.class))
                .orElseThrow(() -> {
                    log.error(ApplicationConstants.FAILED_TO_FIND_ENTITY_BY_ID, CITY, id, CITY);
                    String msg = MessageFormat.format(ApplicationConstants.ENTITY_NOT_EXIST_TEMPLATE,
                            CITY, id);
                    return new EntityNotFoundException(msg);
                });
    }

    @Override
    public List<CityDTO> findAll() {
        List<City> cities = cityRepository.findAll();
        return cities.stream().map(city -> modelMapper.map(city, CityDTO.class)).collect(Collectors.toList());
    }
}
