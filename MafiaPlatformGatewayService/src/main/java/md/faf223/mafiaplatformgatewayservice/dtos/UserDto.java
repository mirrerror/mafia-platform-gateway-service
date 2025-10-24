package md.faf223.mafiaplatformgatewayservice.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class UserDto extends LimitedUserDto {

    private String email;

}
