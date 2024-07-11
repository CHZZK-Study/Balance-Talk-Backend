package balancetalk.file.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long resourceId;

    @NotNull
    @Positive
    private Long size;

    @NotBlank
    @Size(max = 50)
    private String uploadName;

    @NotBlank
    @Size(max = 50)
    private String storedName;

    @NotBlank
    @Size(max = 255)
    private String path;

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @Enumerated(value = EnumType.STRING)
    private FileFormat fileFormat;
}
