package cn.edu.zju.isst.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.zju.isst.admin.controller.AppConfig;
import cn.edu.zju.isst.collection.CollectionEntity;
import cn.edu.zju.isst.dao.annotation.Column;
import cn.edu.zju.isst.dao.annotation.Entity;
import cn.edu.zju.isst.dao.annotation.ID;

@Entity("collected_news")
public class CollectedNews {
    @ID
    @Column
    private int id;
    
    @Column("source_id")
    private int sourceId;
    
    @Column("archive_id")
    private int archiveId;
    
    @Column
    private String title;
    
    @Column
    private String content;
    
    @Column("created_at")
    private Date createdAt;
    
    @Column("post_time")
    private Date postTime;

    @Column
    private String url;
    
    public CollectedNews() {
    }
    
    public CollectedNews(CollectionEntity entity) {
        sourceId = entity.getId();
        title = entity.getTitle();
        content = entity.getContent();
        postTime = entity.getPostTime();
        url = entity.getUrl();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
    
    public int getArchiveId() {
        return archiveId;
    }
    
    public void setArchiveId(int archiveId) {
        this.archiveId = archiveId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        try {
            this.createdAt = new SimpleDateFormat(AppConfig.dateTimeFormatter).parse(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    public Date getPostTime() {
        return postTime;
    }
    
    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }
    
    public void setPostTime(String postTime) {
        try {
            this.postTime = new SimpleDateFormat().parse(postTime);
        } catch (ParseException e) {
            this.postTime = new Date(System.currentTimeMillis());
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}