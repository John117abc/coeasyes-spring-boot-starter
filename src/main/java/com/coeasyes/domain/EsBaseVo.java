package com.coeasyes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @program: manage-microservice
 * @description: esBasevo
 * @author: jiangchengxuan
 * @created: 2023/11/06 18:17
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsBaseVo extends EsBaseData{
}
