package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaginatedResponse implements IResponse {

    private List<?> contents;
    private long totalElements;
    private int totalPages;

    public static PaginatedResponse fromPage(Page<?> page) {
        return PaginatedResponse.builder()
                .contents(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

}