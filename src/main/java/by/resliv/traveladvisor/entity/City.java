package by.resliv.traveladvisor.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@GenericGenerator(
        name = "ID_GENERATOR",
        strategy = "enhanced-sequence",
        parameters = {
                @Parameter(
                        name = "sequence_name",
                        value = "id_sequence"
                ),
                @Parameter(
                        name = "initial_value",
                        value = "21"
                )
        }
)
@DynamicInsert
@DynamicUpdate
@Table(name = "cities")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class City {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    private Long id;
    @Column(unique = true)
    private String name;

    private String description;
}
