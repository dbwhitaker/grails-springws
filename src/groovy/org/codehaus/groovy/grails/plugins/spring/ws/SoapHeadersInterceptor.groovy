package org.codehaus.groovy.grails.plugins.spring.ws

import org.springframework.ws.server.EndpointInterceptor
import groovy.util.XmlSlurper
import groovy.util.Expando
import groovy.util.slurpersupport.GPathResult
import org.springframework.ws.context.MessageContext
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.sax.SAXResult
import org.springframework.xml.transform.TransformerObjectSupport
/**
 *
 * @author tareq.abedrabbo
 */
class SoapHeadersInterceptor extends TransformerObjectSupport implements EndpointInterceptor{

    def namespace
    Expando request = new Expando()
    Expando response = new Expando()
    Expando fault = new Expando()

    public boolean handleRequest(MessageContext messageContext, Object endPointClass) {
        log.debug "SoapHeadersInterceptor handle request"
        def header = messageContext.request.soapHeader
        for(element in header.examineAllHeaderElements()){
            def localPart = element.name.localPart
            def namespaceURI = element.name.namespaceURI
            if (namespace == namespaceURI) {
                if (! request.&localPart){
                    log.warn("unknown header: ${namespaceURI}:${localPart}")
                    return true
                }
                log.debug "processing header ${namespaceURI}:${localPart}"
                Source source = element.source
                XmlSlurper slurper = new XmlSlurper()
                Transformer transformer = createTransformer()
                SAXResult result = new SAXResult(slurper)
                transformer.transform(source, result)
                GPathResult document = slurper.document
                request."$localPart"(document)
            }
        }
        return true
    }
    public boolean handleResponse(MessageContext messageContext, Object endPointClass) {

    }
    public boolean handleFault(MessageContext messageContext, Object endPointClass) {

    }

    /**
     * Callback after completion of request and response (fault) processing. Will be called on any outcome of endpoint
     * invocation, thus allows for proper resource cleanup.
     * <p/>
     * Note: Will only be called if this interceptor's {@link #handleRequest}  method has successfully completed.
     * <p/>
     * As with the {@link #handleResponse} method, the method will be invoked on each interceptor in the chain in
     * reverse order, so the first interceptor will be the last to be invoked.
     *
     * @param messageContext contains both request and response messages, the response should contains a Fault
     * @param endpoint       chosen endpoint to invoke
     * @param ex exception thrown on handler execution, if any
     * @throws Exception in case of errors
     * @since 2.0.2
     */
    void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) throws Exception {

    }
}

