-- 创建广告状态表
CREATE TABLE advertising.ad_statuses
(
    id          INT AUTO_INCREMENT COMMENT '状态ID，主键，自增'
        PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL COMMENT '状态名称，唯一'
)
    COMMENT '广告状态表';

-- 创建上传文件表
CREATE TABLE advertising.uploaded_files
(
    id          CHAR(36)                           NOT NULL COMMENT '文件ID，主键，UUID'
        PRIMARY KEY,
    file_name   VARCHAR(255)                       NOT NULL,
    file_type   VARCHAR(50)                        NOT NULL,
    file_url    VARCHAR(255)                       NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL
);

-- 创建用户表
CREATE TABLE advertising.users
(
    id          CHAR(36)     NOT NULL COMMENT '用户ID，主键，UUID'
        PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL COMMENT '用户名，唯一',
    name        VARCHAR(100) NOT NULL COMMENT '用户姓名',
    password    VARCHAR(100) NOT NULL COMMENT '用户密码',
    role        VARCHAR(100) NOT NULL COMMENT '用户角色',
    CONSTRAINT username
        UNIQUE (username)
)
    COMMENT '用户表';

-- 创建广告表
CREATE TABLE advertising.ads
(
    id              CHAR(36)                           NOT NULL COMMENT '广告ID，主键，UUID'
        PRIMARY KEY,
    title           VARCHAR(255)                      NOT NULL COMMENT '广告标题',
    tags            VARCHAR(255)                      NULL     COMMENT '广告标签',
    description     TEXT                              NOT NULL COMMENT '广告描述',
    advertiser_id   CHAR(36)                          NOT NULL COMMENT '广告发布商ID，外键',
    price           DECIMAL(10, 2)                    NOT NULL COMMENT '广告价格',
    file_id         CHAR(36)                          NOT NULL COMMENT '文件ID，外键',
    status_id       INT                               NOT NULL COMMENT '广告状态ID，外键',
    created_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL COMMENT '广告创建时间',
    advertiser_name VARCHAR(255)                      NOT NULL COMMENT '发布商',
    distributed     INT                               NULL     COMMENT '广告分发数量',
    CONSTRAINT fk_advertiser
        FOREIGN KEY (advertiser_id) REFERENCES advertising.users (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_file
        FOREIGN KEY (file_id) REFERENCES advertising.uploaded_files (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_status
        FOREIGN KEY (status_id) REFERENCES advertising.ad_statuses (id)
)
    COMMENT '广告表';

-- 创建广告申请表
CREATE TABLE advertising.ad_applications
(
    id               CHAR(36)                           NOT NULL COMMENT '申请ID，主键，UUID'
        PRIMARY KEY,
    ad_id            CHAR(36)                           NOT NULL COMMENT '广告ID，外键',
    applicant_id     CHAR(36)                           NOT NULL COMMENT '申请者ID，外键',
    application_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL COMMENT '申请时间',
    CONSTRAINT fk_ad
        FOREIGN KEY (ad_id) REFERENCES advertising.ads (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_applicant
        FOREIGN KEY (applicant_id) REFERENCES advertising.users (id)
            ON DELETE CASCADE
)
    COMMENT '广告申请表';

-- 创建广告点击记录表
CREATE TABLE advertising.ad_clicks
(
    id                BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    user_id           CHAR(36)                           NOT NULL,
    ad_id             CHAR(36)                           NOT NULL,
    click_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
    client_id         CHAR(36)                           NOT NULL COMMENT '广告商用户的 UUID',
    new_interest_tags VARCHAR(255)                       NULL,
    CONSTRAINT ad_clicks_ibfk_1
        FOREIGN KEY (user_id) REFERENCES advertising.users (id)
            ON DELETE CASCADE,
    CONSTRAINT ad_clicks_ibfk_2
        FOREIGN KEY (ad_id) REFERENCES advertising.ads (id)
            ON DELETE CASCADE
)
    COMMENT '广告点击记录表';

-- 创建索引
CREATE INDEX ad_id
    ON advertising.ad_clicks (ad_id);

CREATE INDEX user_id
    ON advertising.ad_clicks (user_id);

-- 创建触发器
DELIMITER //

CREATE DEFINER = 'root'@'localhost' TRIGGER advertising.after_delete_ad
    AFTER DELETE
ON advertising.ads
    FOR EACH ROW
BEGIN
DELETE FROM advertising.ad_applications WHERE ad_id = OLD.id;
END//

CREATE DEFINER = 'root'@'localhost' TRIGGER advertising.delete_ad_file
    AFTER DELETE
ON advertising.ads
    FOR EACH ROW
BEGIN
DELETE FROM advertising.uploaded_files WHERE id = OLD.file_id;
END//

CREATE DEFINER = 'root'@'localhost' TRIGGER advertising.after_delete_user
    AFTER DELETE
ON advertising.users
    FOR EACH ROW
BEGIN
DELETE FROM advertising.ad_clicks WHERE user_id = OLD.id;
DELETE FROM advertising.ads WHERE advertiser_id = OLD.id;
END//

DELIMITER ;