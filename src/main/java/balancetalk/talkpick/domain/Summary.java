package balancetalk.talkpick.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
public class Summary {

    String summaryFirstLine;
    String summarySecondLine;
    String summaryThirdLine;
}
