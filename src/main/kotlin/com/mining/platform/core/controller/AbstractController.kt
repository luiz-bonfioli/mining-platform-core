package com.mining.platform.core.controller

import com.mining.platform.core.datasource.EntityBase
import com.mining.platform.core.service.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KClass

/**
 * The controller base abstract class
 *
 * @author luiz.bonfioli
 */
@CrossOrigin(origins = ["*"])
abstract class AbstractController<E : EntityBase, VO : ValueObject<E>, S : DataService<E>> {

    @Autowired
    private lateinit var request: HttpServletRequest

    @Autowired
    private lateinit var service: S

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

//    @GetMapping("/")
//    fun findAll(@RequestParam("page") page: Int, @RequestParam("size") size: Int, @RequestParam(value = "sort", required = false) sortProperties: Array<String?>?, @RequestParam(value = "direction", required = false) sortDirection: Sort.Direction?): ResponseEntity<List<VO?>?>? {
//        val pageRequest = if (sortProperties != null) PageRequest.of(page, size, Sort.by(sortDirection, *sortProperties)) else PageRequest.of(page, size)
//        val pageSlice = service.findAll(pageRequest)
//        val entityList = pageSlice.content
//        val valueObjectList = ValueObjectConverter.convert(entityList, valueObjectClass)
//        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
//        headers.add("X-Total-Elements", pageSlice.totalElements.toString())
//        headers.add("X-Total-Pages", pageSlice.totalPages.toString())
//        return ServerResponse.success(valueObjectList, headers)
//    }

//    @GetMapping("/search")
//    fun findAllParams(@RequestParam requestParams: MutableMap<String?, String?>, @RequestParam(value = "sort", required = false) sortProperties: Array<String?>?, @RequestParam(value = "direction", required = false) sortDirection: Sort.Direction?): ResponseEntity<List<VO?>?>? {
//        val page = requestParams["page"].toInt()
//        requestParams.remove("page")
//        val size = requestParams["size"].toInt()
//        requestParams.remove("size")
//        val search = requestParams["search"]
//        requestParams.remove("search")
//        val pageRequest = if (sortProperties != null) PageRequest.of(page, size, Sort.by(sortDirection, *sortProperties)) else PageRequest.of(page, size)
//        val pageSlice = service.findAll(pageRequest, search, requestParams)
//        val entityList = pageSlice.content
//        val valueObjectList = ValueObjectConverter.convert(entityList, valueObjectClass)
//        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
//        headers.add("X-Total-Elements", pageSlice.totalElements.toString())
//        headers.add("X-Total-Pages", pageSlice.totalPages.toString())
//        return ServerResponse.success(valueObjectList, headers)
//    }

    @GetMapping("/all")
    fun findAll(): ResponseEntity<Collection<VO>> {
        val entityList = service.findAll()
        val valueObjectList = ValueObjectConverter.convert(entityList, valueObjectClass)
        return ServerResponse.success(valueObjectList)
    }

    protected abstract val valueObjectClass: KClass<VO>

    companion object {
        /**
         * The logger instance for this class
         */
        protected val logger = Logger.getLogger(AbstractController::class.java.name)
    }

}