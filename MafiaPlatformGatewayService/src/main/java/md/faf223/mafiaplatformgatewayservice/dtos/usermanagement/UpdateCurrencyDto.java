package md.faf223.mafiaplatformgatewayservice.dtos.usermanagement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCurrencyDto {
    @NotBlank(message = "Currency type is required")
    private String currency; // "diamonds" or "coins"
    
    @NotNull(message = "Amount is required")
    private Integer amount;
    
    @NotBlank(message = "Operation is required")
    private String operation; // "add", "subtract", or "set"
}
