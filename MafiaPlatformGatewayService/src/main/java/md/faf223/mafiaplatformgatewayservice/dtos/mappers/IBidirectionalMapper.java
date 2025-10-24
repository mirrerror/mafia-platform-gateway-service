package md.faf223.mafiaplatformgatewayservice.dtos.mappers;

import java.util.List;

public interface IBidirectionalMapper<T, U> extends IMapper<T, U> {

    U mapToEntity(T dto);
    List<U> mapToEntityList(List<T> dtoList);

}
