package com.outis.pwvault.mapper;

import com.outis.pwvault.dto.SecretDto;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TagMapper {

    @Named("mapFolder")
    public String mapFolder(Map<String, String> tags) {
        if (tags == null || !tags.containsKey("folder")) {
            return "";
        }
        return tags.get("folder");
    }

    @Named("mapName")
    public String mapName(Map<String, String> tags) {
        if (tags == null || !tags.containsKey("name")) {
            return "";
        }
        return tags.get("name");
    }

    @Named("mapTags")
    public Map<String,String> mapTags(SecretDto secretDto){
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        if(secretDto.name() != null){
            map.put("name", secretDto.name());
        }
        if(secretDto.folder() != null){
            map.put("folder", secretDto.folder());
        }
        return map;
    }
}
