package com.fitbank.web.http;

//import java.io.IOException;
//import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.collections.IteratorUtils;
//import org.apache.commons.fileupload.FileUploadBase;
//import org.apache.commons.fileupload.MultipartStream;
//import org.apache.commons.fileupload.servlet.ServletRequestContext;

/**
 * HttpServletRequest que puede manejar tanto multipar/form-data como text/plain
 *
 * TODO: falta implementar
 *
 * @author FitBank CI
 */
public class MultipartHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> values = new LinkedHashMap<String, String>();

    public MultipartHttpServletRequest(HttpServletRequest req) {
        super(req);

        /*if (FileUploadBase.isMultipartContent(new ServletRequestContext(req))) {
            try {
                MultipartStream multipartStream = new MultipartStream(input,
                        boundary);
                boolean nextPart = multipartStream.skipPreamble();
                OutputStream output;
                while (nextPart) {
                    header = chunks.readHeader();
                    // process headers
                    // create some output stream
                    multipartStream.readBodyPart(output);
                    nextPart = multipartStream.readBoundary();
                }
            } catch (MultipartStream.MalformedStreamException e) {
                // the stream failed to follow required syntax
            } catch (IOException e) {
                // a read or write error occurred
            }
        }*/
    }

    @Override
    public String getParameter(String name) {
        if (values.isEmpty()) {
            return super.getParameter(name);
        } else {
            return values.get(name);
        }
    }

    @Override
    public Map getParameterMap() {
        if (values.isEmpty()) {
            return super.getParameterMap();
        } else {
            return values;
        }
    }

    @Override
    public Enumeration getParameterNames() {
        if (values.isEmpty()) {
            return super.getParameterNames();
        } else {
            return IteratorUtils.asEnumeration(values.values().iterator());
        }
    }

    @Override
    public String[] getParameterValues(String name) {
        if (values.isEmpty()) {
            return super.getParameterValues(name);
        } else {
            return values.keySet().toArray(new String[0]);
        }
    }
}
