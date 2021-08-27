package com.fitbank.ifg.servidorhttp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.fitbank.ifg.iFG;
import com.fitbank.util.Debug;
import com.fitbank.web.servlets.Procesador;

public class ServidorHttp implements Runnable {

    private Thread thread = null;
    private Server server = null;
    private final iFG ifg;

    public ServidorHttp(iFG ifg) {
        this(ifg, 1025 + (int) Math.round(Math.random() * 10000));
    }

    public ServidorHttp(final iFG ifg, int port) {
        this.ifg = ifg;
        server = new Server(port);

        AbstractHandler classLoaderHandler = new AbstractHandler() {
            public void handle(String target, Request arg1,
                    HttpServletRequest request, HttpServletResponse response)
                    throws IOException, ServletException {
                if (target.equals("/")) {
                    target = "/index.html";
                }

                InputStream inputStream = Thread.currentThread()
                        .getContextClassLoader().getResourceAsStream(
                                "com/fitbank/web" + target);

                if (inputStream != null) {
                    String ext = target.substring(target.lastIndexOf('.'));
                    ServletOutputStream outputStream = response
                            .getOutputStream();

                    response.setContentType(new MimeTypes().getMimeByExtension(
                            ext).toString());
                    response.setCharacterEncoding("UTF-8");

                    response.setStatus(HttpServletResponse.SC_OK);

                    while (inputStream.available() > 0) {
                        outputStream.write(inputStream.read());
                    }

                    ((Request) request).setHandled(true);
                }
            }
        };

        ServletContextHandler servletContextHandler = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");

        ServletHolder procesador = new ServletHolder(new Procesador());

        servletContextHandler.addServlet(procesador, "/proc/*");
        servletContextHandler.addServlet(procesador, "/js/fitbank/proc/*");
        servletContextHandler.addServlet(procesador, "/reportes/*");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { servletContextHandler,
                classLoaderHandler, new DefaultHandler() });

        server.setHandler(handlers);
    }

    public String getHost() {
        return "127.0.0.1"; // FIXME: server.getConnectors()[0].getHost();
    }

    public int getPuerto() {
        return server.getConnectors()[0].getPort();
    }

    public String getUri() {
        return "/proc/sig?pt=" + ifg.getWebPageActual().getURI();
    }

    public URL getUrl() {
        try {
            return new URL("http://" + getHost() + ":" + getPuerto() + getUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        boolean start = false;

        synchronized (server) {
            if (thread == null) {
                thread = new Thread(this);
                start = true;
            }
        }

        if (start) {
            thread.start();
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            Debug.error(e);
        }
    }

    public void run() {
        try {
            server.start();
        } catch (Exception e) {
            Debug.error(e);
        }
    }

}
