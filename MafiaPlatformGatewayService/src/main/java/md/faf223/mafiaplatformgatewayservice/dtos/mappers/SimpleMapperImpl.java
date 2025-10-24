package md.faf223.mafiaplatformgatewayservice.dtos.mappers;

import java.util.List;

public abstract class SimpleMapperImpl<T, U> implements IMapper<T, U> {

    public abstract T mapToDto(U entity);

    public List<T> mapToDtoList(List<U> entityList) {
        return entityList.stream()
                .map(this::mapToDto)
                .toList();
    }

}
