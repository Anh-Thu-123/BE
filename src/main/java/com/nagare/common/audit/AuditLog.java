package com.nagare.common.audit;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** auditLogs: ai da lam gi. Bat buoc voi thao tac tren ho so visa, tien va tai khoan. TTL 365 ngay. */
@Document(collection = "auditLogs")
public class AuditLog {

    @Id
    private String id;
    private String actorId;
    private String actorUsername;
    private String action;
    private String entity;
    private String entityId;
    private String summary;
    private String ip;
    private String userAgent;

    @Indexed(name = "at_ttl", expireAfterSeconds = 31536000)
    private Instant at = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
    public String getActorUsername() { return actorUsername; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public Instant getAt() { return at; }
    public void setAt(Instant at) { this.at = at; }
}
