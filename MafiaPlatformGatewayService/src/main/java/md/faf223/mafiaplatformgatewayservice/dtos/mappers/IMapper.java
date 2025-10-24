package md.faf223.mafiaplatformgatewayservice.dtos.mappers;

import java.util.List;

public interface IMapper<T, U> {

    T mapToDto(U entity);
    List<T> mapToDtoList(List<U> entityList);

}
