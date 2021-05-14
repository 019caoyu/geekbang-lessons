package org.geektimes.projects.user.web.controller;

import org.eclipse.microprofile.config.Config;
import org.geektimes.configuration.microprofile.config.servlet.listener.ConfigServletRequestListener;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/config")
public class ConfigTestController implements PageController {

    @GET
    @POST
    @Path("/test") // /hello/world -> HelloWorldController
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        Config config = ConfigServletRequestListener.getConfig();
        String propertyName = "application.name";
        System.out.println(config.getConfigValue(propertyName).getValue());
        return "index.jsp";
    }
}
