package edu.usf.CasRestClient

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.codehaus.groovy.grails.commons.GrailsApplication

class CasTicketRetrievalService {
    def grailsApplication
    
    String getServiceTicket(String serviceUrl,String casServerUrl,String username="",String password="") {
        String ticketsPath = (grailsApplication.config.casRestClient.cas.ticketsPath)?grailsApplication.config.casRestClient.cas.ticketsPath:"/v1/tickets"
        return withRest(uri: casServerUrl) {
            return post(path: ticketsPath,body: [ 
                                                username: (!!grailsApplication.config.casRestClient.cas.username)?grailsApplication.config.casRestClient.cas.username:username,
                                                password: (!!grailsApplication.config.casRestClient.cas.password)?grailsApplication.config.casRestClient.cas.password:password], 
                                                requestContentType : 'application/x-www-form-urlencoded', 
                                                contentType : 'text/plain') { tgtresp, tgtreader ->
                String tgtcontent = tgtreader.text
                switch(tgtresp.statusLine.statusCode) {
                    case 201:
                        Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(tgtcontent)
                        if (matcher.matches()) {
                            String ticketGrantingTicket = matcher.group(1)
                            if(ticketGrantingTicket) {
                                log.warn "TGT is ${ticketGrantingTicket}"
                                return withRest(uri: casServerUrl) {
                                    return post(path: ticketsPath+"/"+ticketGrantingTicket,body: [
                                                                                        service: serviceUrl], 
                                                                                        requestContentType : 'application/x-www-form-urlencoded', 
                                                                                        contentType : 'text/plain') { stresp, streader ->
                                        String serviceTicket = streader.text
                                        switch(stresp.statusLine.statusCode) {
                                            case 200:
                                                if(serviceTicket) {
                                                    return serviceTicket
                                                }
                                                break
                                            default:
                                                log.warn "Invalid response code (${stresp.statusLine.statusCode}) from CAS server!"
                                                log.warn "Response (1k): ${(serviceTicket.size() < 1024)?serviceTicket:stcontent[0..1024]}"                        
                                                break
                                        }
                                        return null
                                    }
                                }                                
                            }
                        }
                        log.warn "Successful ticket granting request, but no ticket found!"
                        log.warn "Response (1k): ${(tgtcontent.size() < 1024)?tgtcontent:tgtcontent[0..1024]}"                        
                        break
                    default:
                        log.warn "Invalid response code (${resp.statusLine.statusCode}) from CAS server!"
                        log.warn "Response (1k): ${(tgtcontent.size() < 1024)?tgtcontent:tgtcontent[0..1024]}"
                        log.warn "Status Message: ${resp.statusLine}"                    
                        break
                }
                return null
            }
        }
    }
    
    String getSpringSecuritySessionId(String serviceUrl,String casServerUrl,String springSecurityBaseUrl,String username = "",String password = "") {
        return withRest(uri: springSecurityBaseUrl) {
            return post(path: "/j_spring_cas_security_check",body: [ticket: CasTicketRetrievalService.getServiceTicket(serviceUrl,casServerUrl,
                                                                        (!!grailsApplication.config.casRestClient.cas.username)?grailsApplication.config.casRestClient.cas.username:username,
                                                                        (!!grailsApplication.config.casRestClient.cas.password)?grailsApplication.config.casRestClient.cas.password:password)
                                                                    ], 
                                                                    requestContentType : 'application/x-www-form-urlencoded') { ssresp, ssreader ->
                switch(ssresp.statusLine.statusCode) {
                    case 200:
                        String cookies = ssresp.headers.'Set-Cookie'
                        if(cookies) {
                            matcher = Pattern.compile(".*JSESSIONID=(.*?)(;|\$)").matcher(cookies)
                            if (matcher.matches()) {
                                restRequest.headers.Cookie = "PHPSESSID=${stcontent}; JSESSIONID=${matcher.group(1)};"    
                                if(restRequest.method in ["GET","DELETE"]) {
                                    restRequest.query.ticket = stcontent
                                } else {
                                    restRequest.body.ticket = stcontent
                                }                                                                        
                                return targetRestService(restRequest)
                            }
                        }
                        return null
                        break
                    default:
                        return null
                        break
                }
                return null
            }
        }
        
    }
}
