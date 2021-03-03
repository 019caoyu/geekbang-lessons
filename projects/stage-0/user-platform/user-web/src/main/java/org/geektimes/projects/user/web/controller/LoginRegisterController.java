package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.service.DefaultUserServiceImpl;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.sql.DBConnectionManager;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/login")
public class LoginRegisterController implements PageController {

    private UserService userService = new DefaultUserServiceImpl(new DatabaseUserRepository(new DBConnectionManager()));

    @POST
    @Path("/register")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String inputEmail = request.getParameter("inputEmail");
        String inputPwd = request.getParameter("inputPassword");
        User user = new User();
        user.setName(inputEmail);
        user.setEmail(inputEmail);
        user.setPassword(inputPwd);
        user.setPhoneNumber("");

        if ( userService.queryUserByNameAndPassword(inputEmail,inputPwd).isNotExistUser()){
            if (userService.register(user)){
                return "register-successful.jsp";
            }
        }else{
            return "login-successful.jsp";
        }

        return "index.jsp";

    }

}
