package com.mining.platform.core.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap

/**
 *
 * This class responsible to handle response HTTP request.
 *
 * @author adriano.paula
 */
object ServerResponse {
    /**
     *
     * This method is responsible to handle success.
     *
     * @return
     */
    fun success(): ResponseEntity<HttpStatus> {
        return ResponseEntity(HttpStatus.OK)
    }

    /**
     *
     * This method is responsible to handle success and return an object.
     *
     * @param response
     * @return
     */
    fun <R> success(response: R): ResponseEntity<R> {
        return ResponseEntity(response, HttpStatus.OK)
    }

    /**
     *
     * This method is responsible to handle success and return an object.
     *
     * @param response
     * @param headers
     * @return
     */
    fun <R> success(response: R, headers: MultiValueMap<String, String>): ResponseEntity<R> {
        return ResponseEntity(response, headers, HttpStatus.OK)
    }

    /**
     *
     * This method is responsible to handle success and return an object list.
     *
     * @param response
     * @return
     */
    fun <R> success(response: List<R>): ResponseEntity<List<R>> {
        return ResponseEntity(response, HttpStatus.OK)
    }

    /**
     *
     * This method is responsible to handle HTTP error status.
     *
     * @param httpStatus
     * @return
     */
    fun error(httpStatus: HttpStatus?): ResponseEntity<HttpStatus> {
        return ResponseEntity(httpStatus!!)
    }

    /**
     *
     * This method is responsible to handle exception.
     *
     * @param ex
     * @return
     */
    fun error(ex: Exception?): ResponseEntity<Exception> {
        return ResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}