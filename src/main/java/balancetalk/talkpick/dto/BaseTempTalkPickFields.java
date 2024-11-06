package balancetalk.talkpick.dto;

import jakarta.validation.constraints.NotBlank;

public class BaseTempTalkPickFields extends BaseTalkPickFields {

    public BaseTempTalkPickFields(String title, String content, String optionA, String optionB, String sourceUrl) {
        super(title, content, optionA, optionB, sourceUrl);
    }

    @NotBlank(message = "제목은 공백을 허용하지 않습니다.")
    public String getTitle() {
        return super.getTitle();
    }

    @NotBlank(message = "본문 내용은 공백을 허용하지 않습니다.")
    public String getContent() {
        return super.getContent();
    }

    @NotBlank(message = "선택지 이름은 공백을 허용하지 않습니다.")
    public String getOptionA() {
        return super.getOptionA();
    }

    @NotBlank(message = "선택지 이름은 공백을 허용하지 않습니다.")
    public String getOptionB() {
        return super.getOptionA();
    }
}
