package cn.last.dao;

import cn.last.pojo.DataDictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataDictionaryMapper {
    List<DataDictionary> getDataDictionaryList(@Param("typeCode") String typeCode);

}
