package com.io.iotask.service.mapper;

import com.io.iotask.repository.entity.Record;
import com.io.iotask.web.model.dto.RecordDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecordMapper {
    RecordDto toDto(Record source);

    Record toEntity(RecordDto recordDto);

    void updateRecordFromDto(RecordDto source, @MappingTarget Record target);
}
