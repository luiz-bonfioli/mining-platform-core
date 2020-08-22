package com.mining.platform.core.controller

import com.mining.platform.core.converter.EntityConverter
import com.mining.platform.core.converter.ValueObjectConverter
import com.mining.platform.core.datasource.EntityBase
import com.mining.platform.core.service.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap
import kotlin.reflect.KClass

/**
 * The controller base abstract class
 *
 * @author luiz.bonfioli
 */
@CrossOrigin(origins = ["*"], exposedHeaders = ["X-Total-Elements", "X-Total-Pages"])
abstract class AbstractController<E : EntityBase, VO : ValueObject<E>, S : DataService<E>> {

    companion object {
        protected val logger: Logger = Logger.getLogger(AbstractController::class.qualifiedName)
    }

    @Autowired
    private lateinit var request: HttpServletRequest

    @Autowired
    protected lateinit var service: S

    @PostMapping
    fun save(@RequestBody valueObject: VO): ResponseEntity<VO> {
        val entity = valueObject.entity
        val saved = service.save(entity)
        return ServerResponse.success(ValueObjectConverter.convert(saved, valueObjectClass))
    }

    @PostMapping("save-all")
    fun saveAll(@RequestBody valueObjectList: Collection<VO>): ResponseEntity<Collection<VO>> {
        val entityList = service.saveAll(EntityConverter.convert(valueObjectList))
        return ServerResponse.success(ValueObjectConverter.convert(entityList, valueObjectClass))
    }

    @PutMapping
    fun update(@RequestBody valueObject: VO): ResponseEntity<VO> {
        val entity = valueObject.entity
        val updated = service.update(entity)
        return ServerResponse.success(ValueObjectConverter.convert(updated, valueObjectClass))
    }

    @PutMapping("update-all")
    fun updateAll(@RequestBody valueObjectList: Collection<VO>?): ResponseEntity<Collection<VO>> {
        val entityList = service.updateAll(EntityConverter.convert(valueObjectList))
        return ServerResponse.success(ValueObjectConverter.convert(entityList, valueObjectClass))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID?): ResponseEntity<HttpStatus> {
        service.deleteById(id)
        return ServerResponse.success()
    }

    @GetMapping("/{id}")
    fun find(@PathVariable id: UUID): ResponseEntity<VO> {
        val entity = service.find(id)
        return ServerResponse.success(ValueObjectConverter.convert(entity, valueObjectClass))
    }

    @GetMapping("/find-by-params")
    fun findByParams(@RequestParam("page") page: Int,
                     @RequestParam("size") size: Int,
                     @RequestParam("sort") sort: Array<String>?,
                     @RequestParam("direction") direction: Sort.Direction = Sort.Direction.ASC,
                     @RequestParam("search") search: Array<String>?): ResponseEntity<Collection<VO>> {
        val pageRequest = sort?.let { PageRequest.of(page, size, Sort.by(direction, *sort)) }
                ?: PageRequest.of(page, size)

        val pageSlice = service.findByParams(pageRequest, createSearchMap(search))
        val entityList = pageSlice.content
        val valueObjectList = ValueObjectConverter.convert(entityList, valueObjectClass)

        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("X-Total-Elements", pageSlice.totalElements.toString())
        headers.add("X-Total-Pages", pageSlice.totalPages.toString())

        return ServerResponse.success(valueObjectList, headers)
    }

    private fun createSearchMap(search: Array<String>?): Map<String, String> {
        val searchMap = HashMap<String, String>()
        search?.forEach {
            val parameter = it.split(":")
            searchMap[parameter[0]] = parameter[1]
        }
        return searchMap
    }

    @GetMapping("/all")
    fun findAll(): ResponseEntity<Collection<VO>> {
        val entityList = service.findAll()
        val valueObjectList = ValueObjectConverter.convert(entityList, valueObjectClass)
        return ServerResponse.success(valueObjectList)
    }

    protected abstract val valueObjectClass: KClass<VO>
}