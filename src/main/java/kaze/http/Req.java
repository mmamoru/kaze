package kaze.http;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import kaze.fw.lib.Jackson;
import kaze.http.req.Json;
import kaze.http.req.Params;
import kaze.http.req.Uri;

public class Req {

	public HttpServletRequest servletReq;
	private Map<String, Integer> uriIndex;
	
	public Req(
	    HttpServletRequest sr,
	    Map<String, Integer> uriIndex
	) {
    this.servletReq = sr;
    this.uriIndex = uriIndex;
	}
	
  public String param(String name) {
    return servletReq.getParameter(name);
  }

  public <T> T param(String name, Class<T> to) {
    return Jackson.convert(param(name), to);
  }
  
  public Params params() {
    return new Params(servletReq);
  }
  
  public Json json() {
    return new Json(servletReq);
  }
  
  public Uri uri() {
    return new Uri(servletReq.getRequestURI(), uriIndex);
  }
  
  public String[] params(String name) {
    return servletReq.getParameterValues(name);
  }
  
  public <T> T params(String name, Class<T> to) {
    return Jackson.convert(params(name), to);
  }
}