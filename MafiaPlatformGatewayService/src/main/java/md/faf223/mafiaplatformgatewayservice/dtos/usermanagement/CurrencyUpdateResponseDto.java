package md.faf223.mafiaplatformgatewayservice.dtos.usermanagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyUpdateResponseDto {
    private CurrencyUpdateData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyUpdateData {
        private Long id;
        private Integer newBalance;
        private Long transactionId;
        private String currency;
    }
}
