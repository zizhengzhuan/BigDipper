package com.z3pipe.bigdipper.model;

import com.z3pipe.bigdipper.R;
import com.z3pipe.bigdipper.util.StringUtil;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 9031729432214553367L;
    private String id;
    private String gid;
    private String trueName;
    private String groupId;
    private String groupName;
    private String groupCode;
    private String groupLev;
    private String ecode;
    private String role;
    private String roleCode;
    private String company;
    private String leader;
    private boolean isLeader;
    private String password;
    //是否在线 true在线；false 离线
    private String state;
    private String type;
    //巡检时间
    private String patroltime;
    //巡检速度
    private String speed;
    //巡检平均速度
    private String avrspeed;

    //是否已经签到

    private boolean isWatch;

    private boolean canSign;

    private String signOutTime;

    public String getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(String signOutTime) {
        this.signOutTime = signOutTime;
    }

    private String loginName;

    private String phone;
    private String email;
    /**
     * 登录ibps平台成功之后返回的token
     * ibps平台获取信息均需要在请求头中加入token
     */
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupLev() {
        return groupLev;
    }

    public void setGroupLev(String groupLev) {
        this.groupLev = groupLev;
    }

    public String getEcode() {
        return ecode;
    }

    public void setEcode(String ecode) {
        this.ecode = ecode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getCompany() {
        if(StringUtil.isBlank(company)) {
            return groupName;
        }
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isWatch() {
        return isWatch;
    }

    public void setWatch(boolean isWatch) {
        this.isWatch = isWatch;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPatroltime() {
        return patroltime;
    }

    public void setPatroltime(String patroltime) {
        this.patroltime = patroltime;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvrspeed() {
        return avrspeed;
    }

    public void setAvrspeed(String avrspeed) {
        this.avrspeed = avrspeed;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isCanSign() {
        return canSign;
    }

    public void setCanSign(boolean canSign) {
        this.canSign = canSign;
    }
}
