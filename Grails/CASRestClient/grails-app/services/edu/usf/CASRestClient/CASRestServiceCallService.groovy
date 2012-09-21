package edu.usf.CASRestClient

import groovyx.net.http.*
import edu.usf.CASRestClient.CASTicketRetrievalService
import grails.converters.*
// import static groovyx.net.http.Method.*
// import static groovyx.net.http.ContentType.*

class CASRestServiceCallService {

    def executeCASService(String serviceUrl, String method = "GET",def query=[:], String springSecurityBaseUrl="") {
        CASTicketRetrievalService cASTicketRetrievalService = new CASTicketRetrievalService()
        return withRest(uri: serviceUrl) {
            def headers = [:]
            if(springSecurityBaseUrl) {
                headers.Cookie = "JSESSIONID=${cASTicketRetrievalService.getSpringSecuritySessionId(serviceUrl)};"
                log.warn "SpringSecurity Cookie is ${headers.Cookie}"
            } else {
                query.ticket = cASTicketRetrievalService.getServiceTicket(serviceUrl)
                // headers.Cookie = "PHPSESSID=${query.ticket};"
                // println query.ticket
                log.warn "ServiceTicket is ${query.ticket}"
            }
            log.warn "Query is ${query.toString()}"
            method = (method.toUpperCase().trim() in ["GET","POST","PUT","DELETE"])?method.toUpperCase().trim():"GET"
            switch(method) {
                case "GET":
                    // return get(query: query,headers: headers, requestContentType : URLENC) { resp, reader -> 
                    return get(query: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { resp, reader -> 
                        log.warn resp.statusLine.statusCode + " " + resp.statusLine + " " + resp.contentType
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
                        log.warn resp.statusLine.statusCode + " " + resp.statusLine + " " + resp.contentType
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
                        log.warn resp.statusLine.statusCode + " " + resp.statusLine + " " + resp.contentType
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
                        println(resp.statusLine.statusCode + " " + resp.statusLine + resp.contentType)
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
