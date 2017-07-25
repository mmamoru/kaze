package kaze.server;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import kaze.App;

public class Jetty {
  //-> settings
  ////-> app
  private Handler appHand;
  public Jetty() { super(); }
  public Jetty(App a) { this.appHand=new Ap(a); }
  public Jetty(Handler ap) { this.appHand=ap; }
  ////-> thread
  private int thMax=200, thMin=8, thTime=60000;
  public void thread(int max, int min, int timeout) {
    thMax=max; thMin=min; thTime=timeout;
  }
  ////-> http
  private int httpTime=30000;
  public void http(int timeout) { httpTime=timeout; }
  ////-> session
  private SessionHandler ssnHand;
  public void session(int timeoutSec) {  // -1: no timeout
    ssnHand = new SessionHandler();
    ssnHand.setMaxInactiveInterval(timeoutSec);
  }
  ////-> static files
  private ResourceHandler rscHand;
  private void location(Resource root) {
    rscHand = new ResourceHandler();
    rscHand.setDirectoriesListed(false);  // security
    rscHand.setBaseResource(root);
  }
  public void location(String classpath) {
    location(Resource.newClassPathResource(classpath));
  }
  public void location(File dir) {
    location(Resource.newResource(dir));
  }
  ////-> error handler
  private ErrorHandler errHand;
  public void error(ErrorHandler handler) {
    this.errHand=handler;
  }

  //-> start
  public void listen(int port) {
    listen(null, port);
  }
  public void listen(String host, int port) {
    Server svr = new Server(
      new QueuedThreadPool(thMax, thMin, thTime)
    );
    HttpConfiguration conf = new HttpConfiguration();
    conf.setSendServerVersion(false);  // security
    ServerConnector http = new ServerConnector(
      svr, new HttpConnectionFactory(conf)
    );
    http.setHost(host);
    http.setPort(port);
    http.setIdleTimeout(httpTime);
    svr.addConnector(http);
    svr.setHandler(handlers());
    svr.setErrorHandler(errHandler());
    try {
      svr.start();
      svr.join();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  private HandlerList handlers() {
    ArrayList<Handler> list = new ArrayList<>(3);
    if (ssnHand != null) list.add(ssnHand);
    if (appHand != null) list.add(appHand);
    if (rscHand != null) list.add(rscHand);
    HandlerList hands = new HandlerList();
    hands.setHandlers(
      list.toArray(new Handler[list.size()])
    );
    return hands;
  }
  private ErrorHandler errHandler() {
    if (errHand == null) return new Err();
    else return errHand;
  }

  private static class Ap extends AbstractHandler {
    private App app;
    public Ap(App app) { this.app=app; }
    @Override public void handle(
        String target, Request baseReq,
        HttpServletRequest sreq, HttpServletResponse sres)
        throws IOException, ServletException
    {
      try {
        boolean run = app.run(sreq, sres);
        baseReq.setHandled(run);
      } catch (Exception e) {
        sres.sendError(500);
        throw new ServletException(e);
      }
    }
  }
  private static class Err extends ErrorHandler {
    @Override public void handleErrorPage(
        HttpServletRequest req, Writer out, int code, String msg
    ) throws IOException {
      out.write(HttpStatus.getMessage(code));
    }
  }
}
