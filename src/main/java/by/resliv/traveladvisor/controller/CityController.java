package by.resliv.traveladvisor.controller;


import by.resliv.traveladvisor.ApplicationConstants;
import by.resliv.traveladvisor.dto.CityDTO;
import by.resliv.traveladvisor.dto.ExistedEntity;
import by.resliv.traveladvisor.dto.NewEntity;
import by.resliv.traveladvisor.service.CityService;
import by.resliv.traveladvisor.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

@RestController
@RequestMapping(value = "/cities")
@Validated
public class CityController {

    private static final String NUMBER_PATTERN = "^[1-9]\\d*$";
    private static final String PAGE_PATTERN = "^[0-9]\\d*$";
    private static final String VALIDATION_MSG_FOR_ID = "Invalid request";

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping(value = "/{id}")
    public CityDTO getById(@PathVariable("id") @Pattern(regexp = NUMBER_PATTERN,
            message = VALIDATION_MSG_FOR_ID, flags = Pattern.Flag.CASE_INSENSITIVE) String id) {
        return cityService.getById(Long.parseLong(id));
    }


    @PostMapping()
    public ResponseEntity<CityDTO> save(@Validated(NewEntity.class) @RequestBody CityDTO cityDTO,
                                        UriComponentsBuilder ucb) {
        CityDTO savedCity = cityService.save(cityDTO);
        long id = savedCity.getId();
        URI locationUrl = RequestUtil.buildLocationURL(ucb, "/cities/", String.valueOf(id));
        return ResponseEntity.created(locationUrl).body(savedCity);
    }

    @PutMapping(value = "/{id}")
    public CityDTO update(@PathVariable("id")
                          @Pattern(regexp = NUMBER_PATTERN, message = VALIDATION_MSG_FOR_ID,
                                  flags = Pattern.Flag.CASE_INSENSITIVE) String id, @Validated(ExistedEntity.class)
                          @RequestBody CityDTO cityDTO, UriComponentsBuilder ucb) {
        return cityService.update(cityDTO);

    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Pattern(regexp = NUMBER_PATTERN,
            message = VALIDATION_MSG_FOR_ID, flags = Pattern.Flag.CASE_INSENSITIVE) String id) {
        cityService.delete(Long.parseLong(id));
    }


    @GetMapping(params = {"page", "size"})
    public ResponseEntity<List<CityDTO>> findAll(@RequestParam("page") @Pattern(regexp = PAGE_PATTERN,
            message = VALIDATION_MSG_FOR_ID, flags = Pattern.Flag.CASE_INSENSITIVE) String page,
                                                 @RequestParam("size")
                                                 @Pattern(regexp = NUMBER_PATTERN,
                                                         message = VALIDATION_MSG_FOR_ID,
                                                         flags = Pattern.Flag.CASE_INSENSITIVE) String size,
                                                 UriComponentsBuilder ucb) {
        int pageValue = Integer.parseInt(page);
        int sizeValue = Integer.parseInt(size);
        Page<CityDTO> resultPage = cityService.findAll(PageRequest.of(pageValue, sizeValue));
        int totalPages = resultPage.getTotalPages();
        if (pageValue > totalPages) {
            String msg = MessageFormat.format(ApplicationConstants.RESOURCE_NOT_FOUND_BY_PAGE, page);
            throw new EntityNotFoundException(msg);
        }
        if (resultPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        RequestUtil.addTotalCountHeader(httpHeaders, String.valueOf(resultPage.getTotalElements()));
        RequestUtil.addLinkHeader(ucb, httpHeaders, "/cities/", resultPage);
        return new ResponseEntity<>(resultPage.getContent(), httpHeaders, HttpStatus.OK);
    }
}
