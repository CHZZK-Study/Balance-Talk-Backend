package balancetalk.module.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5NDc1NTE4LCJleHAiOjE3MDk1MTg3MTh9.ZZXuN4OWM2HZjWOx7Pupl5NkRtjvd4qnK_txGdRy7G5_GdKgnyF3JfiUsenQgxsi1Y_-7C0dA85xabot2m1cag")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5NDc1NTE4LCJleHAiOjE3MTAwODAzMTh9.l87QBtVI5JJxpW0oiSWpZKX7JUESFgpZlLhW2R_cIsmf7GCEO1advBDN0csJP_PJ_bpPhQxjTd8pn0K33wBAog")
    private String refreshToken;
}
