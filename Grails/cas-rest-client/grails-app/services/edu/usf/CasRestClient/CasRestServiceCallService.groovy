package edu.usf.CasRestClient

import groovyx.net.http.*
import grails.converters.*

class CasRestServiceCallService {

    def casTicketRetrievalService

    def executeCasService(String serviceUrl, String method = "GET",def query=[:], String springSecurityBaseUrl="") {
        return withRest(uri: serviceUrl) {
            def headers = [:]
            if(springSecurityBaseUrl) {
                headers.Cookie = "JSESSIONID=${casTicketRetrievalService.getSpringSecuritySessionId(serviceUrl)};"
                log.info "SpringSecurity Cookie is ${headers.Cookie}"
            } else {
                query.ticket = casTicketRetrievalService.getServiceTicket(serviceUrl)
                log.info "ServiceTicket is ${query.ticket}"
            }
            log.info "Query is ${query.toString()}"
            method = (method.toUpperCase().trim() in ["GET","POST","PUT","DELETE"])?method.toUpperCase().trim():"GET"
            switch(method) {
                case "GET":
                    return get(query: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { resp, reader -> 
                        log.info resp.statusLine.statusCode + " " + resp.statusLine + " " + resp.contentType
                        switch(resp.statusLine.statusCode) {
                            case 200: 
                                return reader.toString()
                                break
                            default:
                                break
                        }
                        return null
                    }                
                    break
                case "POST":
                    return post(body: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { resp, reader -> 
                        log.info resp.statusLine.statusCode + " " + resp.statusLine + " " + resp.contentType
                        switch(resp.statusLine.statusCode) {
                            case 200: 
                                return reader.toString()
                                break
                            default:
                                break
                        }
                        return null
                    }                
                    break
                case "PUT":
                    return put(body: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { resp, reader -> 
                        log.info resp.statusLine.statusCode + " " + resp.statusLine + " " + resp.contentType
                        switch(resp.statusLine.statusCode) {
                            case 200: 
                                return reader.toString()
                                break
                            default:
                                break
                        }
                        return null
                    }                
                    break
                case "DELETE":
                    return delete(query: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { resp, reader -> 
                        log.info resp.statusLine.statusCode + " " + resp.statusLine + " " + resp.contentType
                        switch(resp.statusLine.statusCode) {
                            case 200: 
                                return reader.toString()
                                break
                            default:
                                break
                        }
                        return null
                    }                
                    break
            }            
        }

    }
}
