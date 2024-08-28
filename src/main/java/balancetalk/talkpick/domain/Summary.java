package balancetalk.talkpick.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Embeddable
@Getter
public class Summary {

    @Size(max = 120)
    @Column(name = "summary_first_line")
    String firstLine;

    @Size(max = 120)
    @Column(name = "summary_second_line")
    String secondLine;

    @Size(max = 120)
    @Column(name = "summary_third_line")
    String thirdLine;
}
