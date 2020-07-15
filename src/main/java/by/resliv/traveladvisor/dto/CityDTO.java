package by.resliv.traveladvisor.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CityDTO implements Serializable {

    private static final long serialVersionUID = 2805375697368544426L;

    @Null(message = "Id field must be absent", groups = NewEntity.class)
    @NotNull(message = "Please provide a correct value for id", groups = ExistedEntity.class)
    @Min(value = 1, message = "Please provide a correct value for id", groups = ExistedEntity.class)
    private Long id;

    @NotBlank(message = "Please provide a city name", groups = {NewEntity.class, ExistedEntity.class})
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z-]{3,100}$", message = "Please provide a correct value for city name.",
            groups = {NewEntity.class, ExistedEntity.class})
    private String name;

    @NotBlank(message = "Please provide a city description", groups = {NewEntity.class, ExistedEntity.class})
    @Length(min = 50, max = 2000, message = "Length of city description must be between 50 - 2000 characters",
            groups = {NewEntity.class, ExistedEntity.class})
    private String description;
}
