package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/user/")
public class UserController {
    /**
     * 用户登录功能
     */
    @Autowired
    private IUserService iUserService;


        @RequestMapping(value = "login.do", method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse<User> login(String username, String password, HttpSession session){
            //controller->service->mybatis-dao
            ServerResponse<User> response = iUserService.login(username,password);
            if(response.isSuccess()){
                session.setAttribute(Const.CURRENT_USER,response.getData());
            }
            return response;

    }

        @RequestMapping(value = "logout.do", method = RequestMethod.GET)
        @ResponseBody
        //下面的代码返回的泛型不是User对象了，而是替换成了String，此处为何如此设计呢？返回String又能显示什么呢？不得而知
        public ServerResponse<String> logout(HttpSession session) {
            session.removeAttribute(Const.CURRENT_USER);
            return ServerResponse.createBySuccess();
        }

        @RequestMapping(value = "register.do",method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse<String> register(User user){
            return iUserService.register(user);
        }

        @RequestMapping(value = "checkValid.do",method = RequestMethod.GET)
        @ResponseBody
        public ServerResponse<String> checkValid(String str,String type){
            return iUserService.checkValid(str,type);
        }

        @RequestMapping(value = "getUserInfo.do",method = RequestMethod.GET)
        @ResponseBody
        public ServerResponse<User> getUserInfo(HttpSession session){
            User user = (User) session.getAttribute(Const.CURRENT_USER);
            if(user != null){
                return ServerResponse.createBySuccess(user);
            }
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        @RequestMapping(value = "forgetGetQuestion.do",method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse<String> forgetGetQuestion(String username){
            return iUserService.selectQuestion(username);
        }

        @RequestMapping(value = "forgetCheckAnswer.do",method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
            return iUserService.forgetCheckAnswer(username,question,answer);
        }

        @RequestMapping(value = "forgetResetPassword.do",method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){

            return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
        }

        @RequestMapping(value = "resetPassword.do",method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse<String> resetPassword(HttpSession session,String passwordold,String passwordNew){
            User user = (User)session.getAttribute(Const.CURRENT_USER);
            if(user == null){
                return ServerResponse.createByErrorMessage("用户未登录");
            }
            return iUserService.resetPassword(passwordold,passwordNew,user);
        }
        @RequestMapping(value = "updateInformation.do",method = RequestMethod.POST)
        @ResponseBody
        public ServerResponse<User> updateInformation(HttpSession session,User user){
            User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
            if(currentUser == null){
                return ServerResponse.createByErrorMessage("用户未登录");
            }
            //此句代码：说明从前端传回的更新数据缺少的元素（id）是需要session中的原有id来替换的，后端唯一标识不能简单的改变！！
            user.setId(currentUser.getId());
            user.setUsername(currentUser.getUsername());

            //下面代码逻辑就是你自己考虑不周到了！！！不能直接return，因为成功的话需要把user更新到session中去的！
            //return iUserService.updateInformation(user);
            ServerResponse<User> response = iUserService.updateInformation(user);
            if(response.isSuccess()){
                session.setAttribute(Const.CURRENT_USER,response.getData());
            }
            return response;

        }


}
