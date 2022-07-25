package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.JwtUtils;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.system.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//1.解决跨域
@CrossOrigin
//2.声明restContoller
@RestController
//3.设置父路径
@RequestMapping(value="/sys")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 新增员工
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Result save(@RequestBody User user) {
        //1.设置保存的企业id
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        //2.调用service完成保存企业
        userService.save(user);
        //3.构造返回结果
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 查询企业的员工（分页查询，动态拼接查询条件）
     * 指定企业id
     * 请求参数：Integer page,Integer size,String hasDept,String department,String companyId
     *          把hasDept(是否分配部门 0未分配，1已分配)，departmentId（部门id），companyId（企业id）封装成map集合
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Result findAll(int page, int size, @RequestParam Map map) {//前端的请求方法是get，所以可以把请求参数放在参数列表
        //1.获取当前的企业id
        map.put("companyId",companyId);
        //2.完成查询
        Page<User> pageUser = userService.findAll(map,page,size);
        //3.构造返回结果（PageResult模型是用于返回有分页的查询结果）
        PageResult pageResult = new PageResult(pageUser.getTotalElements(),pageUser.getContent());
        return new Result(ResultCode.SUCCESS, pageResult);
    }

    /**
     * 根据ID查询user
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable(value = "id") String id) {
        User user = userService.findById(id);
        UserResult userResult = new UserResult(user);   //给员工用复选框分配角色的回显
        return new Result(ResultCode.SUCCESS, userResult);
    }

    /**
     * 修改User
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable(value = "id") String id, @RequestBody User user) {
        //1.设置修改的部门id
        user.setId(id);
        //2.调用service更新
        userService.update(user);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据id删除
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable(value = "id") String id) {
        userService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 给员工分配角色
     */
    @RequestMapping(value = "/user/assignRoles", method = RequestMethod.PUT)
    public Result save(@RequestBody Map<String,Object> map) {
        //1.获取被分配的用户id
        String userId = (String) map.get("id");
        //2.获取到角色的id列表
        List<String> roleIds = (List<String>) map.get("roleIds");
        //3.调用service完成角色分配
        userService.assignRoles(userId,roleIds);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 一、用户登录
     * 1.通过service根据mobile查询用户
     * 2.比较password
     * 3.生成jwt信息
     */
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public Result login(@RequestBody Map<String,String> loginMap) {//loginMap是在请求体中，需要把它构造成对象才能被获取
        String mobile = loginMap.get("mobile"); //在请求体中获取mobile
        String password = loginMap.get("password");
        User user = userService.findByMobile(mobile);//根据手机号查找用户
        //登录失败
        if(user == null || !user.getPassword().equals(password)) {
            return new Result(ResultCode.MOBILEORPASSWORDERROR);//用户名或密码错误
        }else {
            //登录成功
            Map<String,Object> map = new HashMap<>();
            map.put("companyId",user.getCompanyId());
            map.put("companyName",user.getCompanyName());
            String token = jwtUtils.createJwt(user.getId(), user.getUsername(), map);
            return new Result(ResultCode.SUCCESS,token);
        }
    }

    /**
     * 二、用户登录成功后，获取用户信息
     *      1.获取用户id
     *      2.根据用户id查询用户
     *      3.构建返回值对象
     *      4.响应
     */
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public Result profile(HttpServletRequest request) throws Exception {
        //请求中获取key为Authorization的头信息
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        //前后端约定头信息内容以 Bearer+空格+token 形式组成
        String token = authorization.replace("Bearer ", "");
        //比较并获取claims
        Claims claims = jwtUtils.parseJwt(token);
        if(claims == null) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        String userId = claims.getId();
        User user = userService.findById(userId);
        return new Result(ResultCode.SUCCESS,new ProfileResult(user));
    }
}
