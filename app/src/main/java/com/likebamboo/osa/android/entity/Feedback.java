
package com.likebamboo.osa.android.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * 问题表
 *
 * @author wentaoli
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Feedback {
    /**
     * 联系方式
     */
    private String contact = "";

    /**
     * 描述
     */
    private String description = "";

    /**
     * 添加时间
     */
    @JsonProperty("add_time")
    private String addTime = "";

    /**
     * 反馈的问题列表
     */
    @JsonProperty("issue")
    private ArrayList<String> issues = null;

    @JsonProperty("blogId")
    private Long blogId = 0L;

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getIssues() {
        return issues;
    }

    public void setIssues(ArrayList<String> issuesArr) {
        this.issues = issuesArr;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    @Override
    public String toString() {
        return "Feedback [contact=" + contact + ", description=" + description + ", addTime=" + addTime + ", issues=" + issues + "]";
    }

}
