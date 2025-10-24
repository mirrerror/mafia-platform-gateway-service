package md.faf223.mafiaplatformgatewayservice.dtos.mappers;

import java.util.List;

public abstract class SimpleBidirectionalMapperImpl<T, U> extends SimpleMapperImpl<T, U> implements IBidirectionalMapper<T, U> {

    public abstract U mapToEntity(T dto);

    public List<U> mapToEntityList(List<T> dtoList) {
        return dtoList.stream()
                .map(this::mapToEntity)
                .toList();
    }

}
