package cn.lili.security.buyer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.lili.cache.Cache;
import cn.lili.cache.CachePrefix;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.enums.PermissionEnum;
import cn.lili.common.security.enums.SecurityEnum;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.security.token.SecretKeyUtil;
import cn.lili.common.utils.ResponseUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.naming.NoPermissionException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 认证结果过滤器
 *
 * @author Chopper
 * @version v4.1
 * @since 2020/11/17 3:37 下午
 * @since
 */
@Slf4j
public class BuyerAuthenticationFilter extends BasicAuthenticationFilter {


    /**
     * 缓存
     */
    @Autowired
    private Cache cache;

    /**
     * 自定义构造器
     *
     * @param authenticationManager
     * @param cache
     */
    public BuyerAuthenticationFilter(AuthenticationManager authenticationManager,
                                     Cache cache) {
        super(authenticationManager);
        this.cache = cache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //从header中获取jwt
        String jwt = request.getHeader(SecurityEnum.HEADER_TOKEN.getValue());
        try {
            //如果没有token 则return
            if (StrUtil.isBlank(jwt)) {
                chain.doFilter(request, response);
                return;
            }
            //获取用户信息，存入context
            UsernamePasswordAuthenticationToken authentication = getAuthentication(jwt, response);
            //自定义权限过滤
            if (authentication != null) {
                if (cache.hasKey(CachePrefix.ACCESS_TOKEN.getPrefix(UserEnums.MANAGER) + jwt)) {
                    customAuthentication(request, response, authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("BuyerAuthenticationFilter-> member authentication exception:", e);
        }
        chain.doFilter(request, response);
    }

    /**
     * 解析用户
     *
     * @param jwt
     * @param response
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String jwt, HttpServletResponse response) {

        try {
            Claims claims
                    = Jwts.parser()
                    .setSigningKey(SecretKeyUtil.generalKeyByDecoders())
                    .parseClaimsJws(jwt).getBody();
            //获取存储在claims中的用户信息
            String json = claims.get(SecurityEnum.USER_CONTEXT.getValue()).toString();
            AuthUser authUser = new Gson().fromJson(json, AuthUser.class);

            //校验redis中是否有权限
            if (cache.hasKey(CachePrefix.ACCESS_TOKEN.getPrefix(UserEnums.MEMBER) + jwt)
                    || cache.hasKey(CachePrefix.ACCESS_TOKEN.getPrefix(UserEnums.STORE) + jwt)
                    || !cache.keys("*" + jwt).isEmpty()
                    || cache.hasKey(CachePrefix.ACCESS_TOKEN.getPrefix(UserEnums.MANAGER) + jwt)) {
                //构造返回信息
                List<GrantedAuthority> auths = new ArrayList<>();
                auths.add(new SimpleGrantedAuthority("ROLE_" + authUser.getRole().name()));
                //构造返回信息
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser.getUsername(), null, auths);
                authentication.setDetails(authUser);
                return authentication;
            }
            ResponseUtil.output(response, 403, ResponseUtil.resultMap(false, 403, "登录已失效，请重新登录"));
            return null;
        } catch (ExpiredJwtException e) {
            log.debug("user analysis exception:", e);
        } catch (Exception e) {
            log.error("user analysis exception:", e);
        }
        return null;
    }


    /**
     * 自定义权限过滤
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 用户信息
     */
    private void customAuthentication(HttpServletRequest request, HttpServletResponse response, UsernamePasswordAuthenticationToken authentication) throws NoPermissionException {
        AuthUser authUser = (AuthUser) authentication.getDetails();
        String requestUrl = request.getRequestURI();


        //如果不是超级管理员， 则鉴权
        if (Boolean.FALSE.equals(authUser.getIsSuper())) {
            //获取缓存中的权限
            Map<String, List<String>> permission = (Map<String, List<String>>) cache.get(CachePrefix.PERMISSION_LIST.getPrefix(UserEnums.MANAGER) + authUser.getId());

            //获取数据(GET 请求)权限
            if (request.getMethod().equals(RequestMethod.GET.name())) {
                //如果用户的超级权限和查阅权限都不包含当前请求的api
                if (match(permission.get(PermissionEnum.SUPER.name()), requestUrl) ||
                        match(permission.get(PermissionEnum.QUERY.name()), requestUrl)) {
                } else {
                    ResponseUtil.output(response, ResponseUtil.resultMap(false, 400, "权限不足"));
                    log.error("当前请求路径：{},所拥有权限：{}", requestUrl, JSONUtil.toJsonStr(permission));
                    throw new NoPermissionException("权限不足");
                }
            }
            //非get请求（数据操作） 判定鉴权
            else {
                if (!match(permission.get(PermissionEnum.SUPER.name()), requestUrl)) {
                    ResponseUtil.output(response, ResponseUtil.resultMap(false, 400, "权限不足"));
                    log.error("当前请求路径：{},所拥有权限：{}", requestUrl, JSONUtil.toJsonStr(permission));
                    throw new NoPermissionException("权限不足");
                }
            }
        }
    }

    /**
     * 校验权限
     *
     * @param permissions 权限集合
     * @param url         请求地址
     * @return 是否拥有权限
     */
    boolean match(List<String> permissions, String url) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        return PatternMatchUtils.simpleMatch(permissions.toArray(new String[0]), url);
    }
}

