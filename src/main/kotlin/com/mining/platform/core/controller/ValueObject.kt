package com.mining.platform.core.controller

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.mining.platform.core.datasource.EntityBase
import java.io.Serializable

/**
 *
 * @author luiz.bonfioli
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
interface ValueObject<E : EntityBase?> : Serializable {

    @get:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @set:JsonIgnore
    var entity: E
}