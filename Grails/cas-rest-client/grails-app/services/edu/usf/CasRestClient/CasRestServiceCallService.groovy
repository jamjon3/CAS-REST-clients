package edu.usf.CasRestClient

import groovyx.net.http.*
import grails.converters.*

class CasRestServiceCallService {

    def casTicketRetrievalService

    def executeCasService(String serviceUrl, String method = "GET",String username,String password,def headers=[:],def query=[:], String springSecurityBaseUrl="") {
        return withRest(uri: serviceUrl) {
            log.info "Query is ${query.toString()}"
            method = (method.toUpperCase().trim() in ["GET","POST","PUT","DELETE"])?method.toUpperCase().trim():"GET"
            switch(method) {
                case "GET":                    
                    return get(query: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { resp, reader -> 
                        log.info resp.statusLine.statusCode + " " + resp.statusLine + " " + resp.contentType
                        if(!"${resp.context['http.target_host'].toString()}${resp.context['http.request'].requestLine.uri}".startsWith(serviceUrl)) {
                            List pathTokens = resp.context['http.request'].requestLine.uri.tokenize("login")
                            String casServerUrl
                            if(pathTokens.size()) {
                                switch (pathTokens.get(0)) {
                                    case "/":
                                        casServerUrl = resp.context['http.target_host'].toString()
                                        break
                                    default:
                                        casServerUrl = "${resp.context['http.target_host'].toString()}${pathTokens.get(0)[0..-2]}"
                                        break;
                                }
                                if(springSecurityBaseUrl) {
                                    headers.Cookie = "JSESSIONID=${casTicketRetrievalService.getSpringSecuritySessionId(serviceUrl,casServerUrl,springSecurityBaseUrl,username,password)};"
                                    log.info "SpringSecurity Cookie is ${headers.Cookie}"
                                } else {                                    
                                    query.ticket = casTicketRetrievalService.getServiceTicket(serviceUrl,casServerUrl,username,password)
                                    // headers.'Set-Cookie' = "PHPSESSID=${query.ticket};"
                                    log.info "ServiceTicket is ${query.ticket}"
                                }
                                return get(query: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { sresp, sreader -> 
                                    log.info sresp.statusLine.statusCode + " " + sresp.statusLine + " " + sresp.contentType
                                    switch(sresp.statusLine.statusCode) {
                                        case 200: 
                                            return sreader.toString()
                                            break
                                        default:
                                            break
                                    }
                                    return null                                
                                }                
                            }                                                        
                        }                    
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
                        if(!"${resp.context['http.target_host'].toString()}${resp.context['http.request'].requestLine.uri}".startsWith(serviceUrl)) {
                            List pathTokens = resp.context['http.request'].requestLine.uri.tokenize("login")
                            String casServerUrl
                            if(pathTokens.size()) {
                                switch (pathTokens.get(0)) {
                                    case "/":
                                        casServerUrl = resp.context['http.target_host'].toString()
                                        break
                                    default:
                                        casServerUrl = "${resp.context['http.target_host'].toString()}${pathTokens.get(0)[0..-2]}"
                                        break;
                                }
                                if(springSecurityBaseUrl) {
                                    headers.Cookie = "JSESSIONID=${casTicketRetrievalService.getSpringSecuritySessionId(serviceUrl,casServerUrl,springSecurityBaseUrl,username,password)};"
                                    log.info "SpringSecurity Cookie is ${headers.Cookie}"
                                } else {
                                    query.ticket = casTicketRetrievalService.getServiceTicket(serviceUrl,casServerUrl,username,password)
                                    log.info "ServiceTicket is ${query.ticket}"
                                }
                                return post(body: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { sresp, sreader -> 
                                    log.info sresp.statusLine.statusCode + " " + sresp.statusLine + " " + sresp.contentType
                                    switch(sresp.statusLine.statusCode) {
                                        case 200: 
                                            return sreader.toString()
                                            break
                                        default:
                                            break
                                    }
                                    return null                                
                                }                
                            }                            
                            
                        }                    
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
                        if(!"${resp.context['http.target_host'].toString()}${resp.context['http.request'].requestLine.uri}".startsWith(serviceUrl)) {
                            List pathTokens = resp.context['http.request'].requestLine.uri.tokenize("login")
                            String casServerUrl
                            if(pathTokens.size()) {
                                switch (pathTokens.get(0)) {
                                    case "/":
                                        casServerUrl = resp.context['http.target_host'].toString()
                                        break
                                    default:
                                        casServerUrl = "${resp.context['http.target_host'].toString()}${pathTokens.get(0)[0..-2]}"
                                        break;
                                }
                                if(springSecurityBaseUrl) {
                                    headers.Cookie = "JSESSIONID=${casTicketRetrievalService.getSpringSecuritySessionId(serviceUrl,casServerUrl,springSecurityBaseUrl,username,password)};"
                                    log.info "SpringSecurity Cookie is ${headers.Cookie}"
                                } else {
                                    query.ticket = casTicketRetrievalService.getServiceTicket(serviceUrl,casServerUrl,username,password)
                                    log.info "ServiceTicket is ${query.ticket}"
                                }
                                return put(body: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { sresp, sreader -> 
                                    log.info sresp.statusLine.statusCode + " " + sresp.statusLine + " " + sresp.contentType
                                    switch(sresp.statusLine.statusCode) {
                                        case 200: 
                                            return sreader.toString()
                                            break
                                        default:
                                            break
                                    }
                                    return null                                
                                }                
                            }
                        }                    
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
                        if(!"${resp.context['http.target_host'].toString()}${resp.context['http.request'].requestLine.uri}".startsWith(serviceUrl)) {
                            List pathTokens = resp.context['http.request'].requestLine.uri.tokenize("login")
                            String casServerUrl
                            if(pathTokens.size()) {
                                switch (pathTokens.get(0)) {
                                    case "/":
                                        casServerUrl = resp.context['http.target_host'].toString()
                                        break
                                    default:
                                        casServerUrl = "${resp.context['http.target_host'].toString()}${pathTokens.get(0)[0..-2]}"
                                        break;
                                }
                                if(springSecurityBaseUrl) {
                                    headers.Cookie = "JSESSIONID=${casTicketRetrievalService.getSpringSecuritySessionId(serviceUrl,casServerUrl,springSecurityBaseUrl,username,password)};"
                                    log.info "SpringSecurity Cookie is ${headers.Cookie}"
                                } else {
                                    query.ticket = casTicketRetrievalService.getServiceTicket(serviceUrl,casServerUrl,username,password)
                                    log.info "ServiceTicket is ${query.ticket}"
                                }
                                return delete(query: query,headers: headers, requestContentType : 'application/x-www-form-urlencoded') { sresp, sreader -> 
                                    log.info sresp.statusLine.statusCode + " " + sresp.statusLine + " " + sresp.contentType
                                    switch(sresp.statusLine.statusCode) {
                                        case 200: 
                                            return sreader.toString()
                                            break
                                        default:
                                            break
                                    }
                                    return null                                
                                }                
                            }                            
                        }                                            
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
