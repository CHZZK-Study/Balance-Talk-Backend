package balancetalk.talkpick.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Embeddable
@Getter
public class Summary {

    private static final int MAX_SIZE = 120;

    @Size(max = MAX_SIZE)
    @Column(name = "summary_first_line")
    String firstLine;

    @Size(max = MAX_SIZE)
    @Column(name = "summary_second_line")
    String secondLine;

    @Size(max = MAX_SIZE)
    @Column(name = "summary_third_line")
    String thirdLine;

    public boolean isOverSize() {
        return firstLine.length() > MAX_SIZE || secondLine.length() > MAX_SIZE || thirdLine.length() > MAX_SIZE;
    }
}
