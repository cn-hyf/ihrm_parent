package com.ihrm.domain.system;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 权限实体类，与后面的api、菜单、按钮权限都是一对一关系。
 */
@Entity
@Table(name = "pe_permission")
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert(true)
@DynamicUpdate(true)
public class Permission implements Serializable {
    private static final long serialVersionUID = -4990810027542971546L;
    /**
     * 主键
     */
    @Id
    private String id;
    /**
     * 权限名称
     */
    private String name;
    /**
     * 权限类型 1为菜单 2为功能 3为API
     */
    private Integer type;

    /**
     * 权限标志(在做权限控制的时候，不管是前端的菜单还是后端的api或者是按钮的配置，都是通过code进行比较来判断当前用户是否包含当前权限，如果有就过，没有就拦截)
     */
    private String code;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 一个权限
     *      菜单权限 对应 多个权限
     *      菜单 对应 api权限
     *      按钮 对应 api权限
     */
    private String pid;

    /**
     * 可见状态
     */
    private Integer enVisible;

    public Permission(String name, Integer type, String code, String description) {
        this.name = name;
        this.type = type;
        this.code = code;
        this.description = description;
    }


}
