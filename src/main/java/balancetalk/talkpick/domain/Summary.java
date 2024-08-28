package balancetalk.talkpick.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Summary {

    @Column(name = "summary_first_line")
    String firstLine;

    @Column(name = "summary_second_line")
    String secondLine;

    @Column(name = "summary_third_line")
    String thirdLine;
}
